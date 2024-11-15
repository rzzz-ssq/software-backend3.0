import argparse
import json
import numpy as np
import pandas as pd
from sklearn.feature_selection import mutual_info_classif
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

# 基于互信息的特征选择
def select_features_by_mutual_info(X, y, threshold=0.1):
    """
    基于互信息的特征选择，支持多个目标标签。

    参数：
    - X: 特征矩阵，DataFrame 格式，每一列是一个特征
    - y: 标签，DataFrame 格式，包含一个或多个标签列
    - threshold: 互信息的阈值，选择互信息得分大于该阈值的特征

    返回：
    - results: 包含每个标签与其相关特征的字典
    """
    results = {}

    # 针对每个标签列分别计算互信息
    for label in y.columns:
        mi_scores = mutual_info_classif(X, y[label])

        # 选择互信息分数高于阈值的特征
        selected_features = {feature: score for feature, score in zip(X.columns, mi_scores) if score > threshold}
        results[label] = selected_features

    return results

# 命令行参数解析
parser = argparse.ArgumentParser()
parser.add_argument("--tableName", type=str, required=True, help="数据库中的表名")
parser.add_argument("--targetcolumns", nargs='+', type=str, required=True, help="目标列名称列表")
parser.add_argument("--calculatedColumns", nargs='+', type=str, required=True, help="用于计算的特征列名称")
parser.add_argument("--threshold", type=float, default=0.1, help="互信息阈值")

args = parser.parse_args()

# 从数据库读取数据
tableName = args.tableName
threshold = args.threshold

features = args.calculatedColumns[0].split(" ")
labels = args.targetcolumns[0].split(" ")

data = read_data_from_postgresql(tableName)
data_filtered = data[labels + features]
X = data_filtered[features]
y = data_filtered[labels]

# 进行互信息特征选择
selected_features = select_features_by_mutual_info(X, y, threshold)

# 格式化输出结果，使其与第一个脚本的输出一致
output = []

# 处理每个目标列的特征
for label in labels:
    res_list = [{"targetcol": label, "res": []}]
    if label in selected_features:
        feature_res = [{"targetcol": label, "res": [{feature: score} for feature, score in selected_features[label].items()]}]
        res_list = feature_res

    output.extend(res_list)

# 如果某个标签没有相关特征，仍需添加空的 "res" 列表
for label in labels:
    if not any(entry["targetcol"] == label for entry in output):
        output.append({"targetcol": label, "res": []})

# 转换为 JSON 格式并输出
json_output = json.dumps(output, ensure_ascii=False, indent=4)
print(json_output)
