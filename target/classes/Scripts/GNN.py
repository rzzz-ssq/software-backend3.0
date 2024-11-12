import argparse
import json
import numpy as np
import pandas as pd
import torch
import torch.nn as nn
import torch.optim as optim
from torch_geometric.data import Data
from torch_geometric.nn import GCNConv
from sklearn.impute import SimpleImputer
import psycopg2

# 从 PostgreSQL 读取数据
def read_data_from_postgresql(tableName):
    host = "10.16.48.219"
    user = "pg"
    password = "111111"
    database = "software4V2"
    port = "5432"
    conn = psycopg2.connect(database=database, user=user, password=password, host=host, port=port,
                            options='-c client_encoding=utf-8')
    cursor = conn.cursor()

    # 执行 SQL 查询，获取表数据
    query = f'SELECT * FROM "{tableName}";'
    cursor.execute(query)
    rows = cursor.fetchall()
    field_names = [desc[0] for desc in cursor.description]

    # 将查询结果转换为 DataFrame 对象
    data = pd.DataFrame(rows, columns=field_names)

    # 数据预处理：转换数值列并填充缺失值
    max_numeric_length = 10
    for col in data.columns:
        if data[col].dtype == 'object':
            try:
                if len(str(data[col].iloc[0])) < max_numeric_length:
                    data[col] = pd.to_numeric(data[col], errors='ignore')
            except ValueError:
                pass
        if data[col].isnull().all():
            data[col] = data[col].astype(str)

    numeric_cols = [col for col in data.columns if data[col].dtype != 'object']
    df_median = SimpleImputer(missing_values=np.nan, strategy='median', copy=False)
    data_imputed = df_median.fit_transform(data[numeric_cols])
    data_imputed = pd.DataFrame(data_imputed, columns=numeric_cols)
    data.update(data_imputed)

    cursor.close()
    conn.close()
    return data

# GCN 模型定义
class GCNFeatureSelector(nn.Module):
    def __init__(self, num_features, hidden_dim):
        super(GCNFeatureSelector, self).__init__()
        self.conv1 = GCNConv(num_features, hidden_dim)
        self.conv2 = GCNConv(hidden_dim, num_features)

    def forward(self, data):
        x, edge_index = data.x, data.edge_index
        x = self.conv1(x, edge_index).relu()
        x = self.conv2(x, edge_index)
        return x

# 特征选择的 GNN 方法
def gnn_feature_selection(X, y, hidden_dim=16, num_epochs=100, learning_rate=0.01):
    results = {}
    num_features = X.shape[1]

    # 构建邻接矩阵
    edge_index = torch.tensor([[i, j] for i in range(num_features) for j in range(num_features) if i != j], dtype=torch.long).t().contiguous()

    # 将数据转换为 PyTorch Geometric 格式
    X_tensor = torch.tensor(X.values, dtype=torch.float)
    for label in y.columns:
        y_label = torch.tensor(y[label].values, dtype=torch.float)

        # 使用 GCN 模型
        data = Data(x=X_tensor, edge_index=edge_index)
        model = GCNFeatureSelector(num_features, hidden_dim)
        optimizer = optim.Adam(model.parameters(), lr=learning_rate)
        criterion = nn.BCEWithLogitsLoss()

        # 模型训练
        model.train()
        for epoch in range(num_epochs):
            optimizer.zero_grad()
            out = model(data)
            loss = criterion(out.mean(dim=1), y_label)  # 保证维度一致
            loss.backward()
            optimizer.step()

        # 获取每个特征的重要性分数
        with torch.no_grad():
            model.eval()
            feature_scores = out.mean(dim=0).numpy()  # 确保输出维度与特征数一致

        # 根据分数选择重要的特征
        top_features = np.array(X.columns)[feature_scores > feature_scores.mean()]
        results[label] = top_features.tolist()

    return results

# 命令行参数解析
parser = argparse.ArgumentParser()
parser.add_argument("--tableName", type=str, required=True, help="数据库中的表名")
parser.add_argument("--targetcolumns", nargs='+', type=str, required=True, help="目标列名称列表")
parser.add_argument("--calculatedColumns", nargs='+', type=str, required=True, help="用于计算的特征列名称")
parser.add_argument("--hiddenDim", type=int, default=16, help="GNN 隐藏层维度")
parser.add_argument("--numEpochs", type=int, default=100, help="训练的迭代次数")
parser.add_argument("--learningRate", type=float, default=0.01, help="学习率")

args = parser.parse_args()

# 从数据库读取数据
tableName = args.tableName
target_columns = args.targetcolumns
feature_columns = args.calculatedColumns
hidden_dim = args.hiddenDim
num_epochs = args.numEpochs
learning_rate = args.learningRate

# 加载数据并选择指定的特征和目标列
data = read_data_from_postgresql(tableName)
data_filtered = data[feature_columns + target_columns]
X = data_filtered[feature_columns]
y = data_filtered[target_columns]

# 进行 GNN 特征选择
selected_features = gnn_feature_selection(X, y, hidden_dim=hidden_dim, num_epochs=num_epochs, learning_rate=learning_rate)

# 格式化输出结果
output = [{"targetcol": target, "selected_features": features} for target, features in selected_features.items()]

# 转换为 JSON 格式并输出
json_output = json.dumps(output, ensure_ascii=False, indent=4)
print(json_output)
