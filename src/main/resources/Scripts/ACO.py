import argparse
import json
import numpy as np
import pandas as pd
from sklearn.feature_selection import mutual_info_classif
from sklearn.impute import SimpleImputer
import psycopg2
import random

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

# 蚁群优化特征选择算法
def ant_colony_optimization_multi_label(X, y, num_ants=10, num_iterations=20, evaporation_rate=0.5, alpha=1, beta=1):
    """
    多标签的蚁群优化算法进行特征选择

    参数：
    - X: 特征矩阵，DataFrame 格式，每一列是一个特征
    - y: 标签矩阵，DataFrame 格式，每一列是一个标签
    - num_ants: 每次迭代的蚂蚁数量
    - num_iterations: 迭代次数
    - evaporation_rate: 信息素蒸发率
    - alpha: 信息素重要性因子
    - beta: 启发信息重要性因子

    返回：
    - results: 包含每个标签及其相关特征的字典
    """
    num_features = X.shape[1]
    pheromone_matrix = np.ones((num_features,))  # 初始化信息素矩阵
    results = {}

    for label in y.columns:
        y_label = y[label]
        best_features = []
        best_score = -np.inf

        for iteration in range(num_iterations):
            ant_solutions = []

            # 每只蚂蚁构建一个特征子集
            for ant in range(num_ants):
                selected_features = []
                for feature_index in range(num_features):
                    # 根据信息素和启发信息选择特征
                    probability = (pheromone_matrix[feature_index] ** alpha) * (np.random.rand() ** beta)
                    if probability > 0.5:  # 设置一个选择概率的阈值
                        selected_features.append(X.columns[feature_index])

                # 计算特征子集的得分（使用互信息来衡量特征子集与标签的相关性）
                if selected_features:
                    mi_scores = mutual_info_classif(X[selected_features], y_label)
                    score = np.sum(mi_scores)
                    ant_solutions.append((selected_features, score))

                    # 更新全局最佳解
                    if score > best_score:
                        best_features = selected_features
                        best_score = score

            # 信息素更新
            pheromone_matrix *= (1 - evaporation_rate)  # 信息素蒸发
            for features, score in ant_solutions:
                for feature in features:
                    index = X.columns.get_loc(feature)
                    pheromone_matrix[index] += score  # 根据得分加强路径信息素

        # 将最佳特征子集记录下来
        results[label] = best_features

    return results

# 命令行参数解析
parser = argparse.ArgumentParser()
parser.add_argument("--tableName", type=str, required=True, help="数据库中的表名")
parser.add_argument("--targetcolumns", nargs='+', type=str, required=True, help="目标列名称列表")
parser.add_argument("--calculatedColumns", nargs='+', type=str, required=True, help="用于计算的特征列名称")
parser.add_argument("--numAnts", type=int, default=10, help="每次迭代的蚂蚁数量")
parser.add_argument("--numIterations", type=int, default=20, help="迭代次数")
parser.add_argument("--evaporationRate", type=float, default=0.5, help="信息素蒸发率")

args = parser.parse_args()

# 从数据库读取数据
tableName = args.tableName
target_columns = args.targetcolumns
feature_columns = args.calculatedColumns
num_ants = args.numAnts
num_iterations = args.numIterations
evaporation_rate = args.evaporationRate

# 加载数据并选择指定的特征和目标列
data = read_data_from_postgresql(tableName)
data_filtered = data[feature_columns + target_columns]
X = data_filtered[feature_columns]
y = data_filtered[target_columns]

# 进行多标签蚁群优化特征选择
selected_features = ant_colony_optimization_multi_label(
    X, y, num_ants=num_ants, num_iterations=num_iterations, evaporation_rate=evaporation_rate
)

# 格式化输出结果
output = [{"targetcol": target, "selected_features": features} for target, features in selected_features.items()]

# 转换为 JSON 格式并输出
json_output = json.dumps(output, ensure_ascii=False, indent=4)
print(json_output)
