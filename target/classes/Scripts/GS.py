import argparse
import json
import numpy as np
import pandas as pd
from scipy.stats import chi2_contingency
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
    query = f'SELECT * FROM "{tableName}";'  # 使用双引号包围表名
    cursor.execute(query)
    rows = cursor.fetchall()
    # 获取字段名
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

# 基于 GS 算法的特征选择，支持多标签
def grow_shrink_feature_selection_multi_label(X, y, significance_level=0.05):
    results = {}

    for label in y.columns:
        selected_features = []
        candidate_features = X.columns.tolist()
        y_label = y[label]

        # 增长阶段
        for feature in candidate_features:
            contingency_table = pd.crosstab(X[feature], y_label)
            _, p_value, _, _ = chi2_contingency(contingency_table)
            if p_value < significance_level:
                selected_features.append(feature)

        # 收缩阶段
        for feature in selected_features.copy():
            remaining_features = [f for f in selected_features if f != feature]
            if remaining_features:
                contingency_table = pd.crosstab(X[remaining_features].sum(axis=1), y_label)
                _, p_value, _, _ = chi2_contingency(contingency_table)
                if p_value >= significance_level:
                    selected_features.remove(feature)

        results[label] = selected_features

    return results

# 命令行参数解析
parser = argparse.ArgumentParser()
parser.add_argument("--tableName", type=str, required=True, help="数据库中的表名")
parser.add_argument("--targetcolumns", nargs='+', type=str, required=True, help="目标列名称列表")
parser.add_argument("--calculatedColumns", nargs='+', type=str, required=True, help="用于计算的特征列名称")
parser.add_argument("--significanceLevel", type=float, default=0.05, help="显著性水平")

args = parser.parse_args()

# 从数据库读取数据
tableName = args.tableName
feature_columns= args.calculatedColumns[0].split(" ")
target_columns= args.targetcolumns[0].split(" ")
significance_level = args.significanceLevel

# 加载数据并选择指定的特征和目标列
data = read_data_from_postgresql(tableName)
data_filtered = data[feature_columns + target_columns]
X = data_filtered[feature_columns]
y = data_filtered[target_columns]

# 进行 Grow-Shrink 特征选择
selected_features = grow_shrink_feature_selection_multi_label(X, y, significance_level)

# 重新组织数据以匹配第二个代码的输出格式
results = {}
for label in target_columns:
    results[label] = {"targetcol": label, "res": []}

# 将每个标签的选定特征及其权重添加到结果中
for label, features in selected_features.items():
    for feature in features:
        results[label]["res"].append({feature: None})  # 特征值为None表示权重不计算

# 处理输出，确保无特征的标签包含空的 "res"
output = [value for value in results.values() if value["res"]]

target_cols = [item["targetcol"] for item in output]

# 对于没有相关特征的标签，确保包含空的 "res"
for tarName in target_columns:
    if tarName not in target_cols:
        output.append({"targetcol": tarName, "res": []})

# 转换为 JSON 格式并输出
json_output = json.dumps(output, ensure_ascii=False, indent=4)
print(json_output)
