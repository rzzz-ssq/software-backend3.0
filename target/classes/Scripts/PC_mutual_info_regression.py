import argparse
import json
import numpy as np
import pandas as pd
from pgmpy.estimators import PC
from sklearn.feature_selection import mutual_info_regression
from sklearn.impute import SimpleImputer
from sqlalchemy import create_engine
import psycopg2
# 加载数据
def read_data_from_postgresql(tableName):
    host = "10.16.48.219"
    user = "pg"
    password = "111111"
    database = "software4V2"
    port="5432"
    conn = psycopg2.connect(database=database, user=user, password=password, host=host, port=port,
                            options='-c client_encoding=utf-8')
    cursor = conn.cursor()

    # 执行SQL查询，获取表数据
    query = f'SELECT * FROM "{tableName}";'  # 使用双引号包围表名
    cursor.execute(query)
    rows = cursor.fetchall()
    # 获取字段名
    field_names = [desc[0] for desc in cursor.description]

    # 将查询结果转换为DataFrame对象
    data = pd.DataFrame(rows, columns=field_names)

    # 数据预处理逻辑
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

# 命令行参数解析
parser = argparse.ArgumentParser()
parser.add_argument("--tableName", type=str, default=None)
parser.add_argument("--targetcolumn", nargs='+', type=str, default=None)
parser.add_argument("--calculatedColumns", nargs='+', type=str, default=None)
args = parser.parse_args()

tableName = args.tableName
features = args.calculatedColumns[0].split(" ")
labels = args.targetcolumn[0].split(" ")

data = read_data_from_postgresql(tableName)
data_filtered = data[labels + features]

# PC 算法
pc = PC(data_filtered)
max_cond_vars = len(labels + features) - 1
significance_level = 0.05
model = pc.estimate(return_type="dag", variant="stable", significance_level=significance_level, max_cond_vars=max_cond_vars)
filtered_edges = [edge for edge in model.edges() if edge[0] in labels or edge[1] in labels]

# 计算互信息
edge_weights = {}
for edge in filtered_edges:
    if edge[0] in data_filtered.columns and edge[1] in data_filtered.columns:
        X = data_filtered[edge[0]].values.reshape(-1, 1)
        y = data_filtered[edge[1]].values
        mi = mutual_info_regression(X, y)
        edge_weights[edge] = mi[0]

# 重组数据为指定格式
results = {}
for label in labels:
    results[label] = {"targetcol": label, "res": []}

for edge, weight in edge_weights.items():
    if edge[0] in labels:
        results[edge[0]]["res"].append({edge[1]: weight})
    elif edge[1] in labels:
        results[edge[1]]["res"].append({edge[0]: weight})

output = [value for value in results.values() if value["res"]]

target_cols = [item["targetcol"] for item in output]

for tarName in labels:
    if(tarName not in target_cols):
        output.append({"targetcol": tarName, "res": []})
json_output = json.dumps(output, ensure_ascii=False, indent=4)
print(json_output)
