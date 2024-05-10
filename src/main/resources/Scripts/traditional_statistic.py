import argparse

import numpy as np
import pandas as pd
from scipy.stats import chi2_contingency, pearsonr, kendalltau, spearmanr
import json
from sklearn.impute import SimpleImputer

from sqlalchemy import create_engine
import psycopg2


def read_data_from_postgresql(tableName):
    # 假设你已经获得了数据库的连接参数，例如：host、user、password、database等
    # 请根据你自己的实际情况来填写这些参数
    host = "10.16.48.219"
    user = "pg"
    password = "111111"
    database = "software4V2"

    # 创建数据库连接字符串
    db_uri = f"postgresql://{user}:{password}@{host}/{database}"

    # 创建 SQLAlchemy 引擎
    engine = create_engine(db_uri)

    # 读取数据到DataFrame
    # query = f"SELECT * FROM {tableName} WHERE \"{tar}\" IS NOT NULL"
    # for field in caculateList:
    #     query += f" AND \"{field}\" IS NOT NULL"
    # data = pd.read_sql(query, engine)
    # query = f'SELECT "age", "sexcode", "MPV", "P_LCR", "PDW", "PCT", "EO_num", "BASO_num", "RBC", "HGB", "NEUT_per", "LYMPH_per", "WBC", "NEUT_num", "HCT", "LYMPH_num", "MONO_per", "EO_per", "MONO_num", "BASO_per", "RDW_SD", "RDW_CV", "PLT", "temperature", "nationcode", "maritalstatuscode", "occupationcategorycode", "nationalitycode", "edubackgroundcode", "age" FROM merge;'
    query = f'SELECT * FROM "{tableName}";'
    data = pd.read_sql(query, engine)
    base_data = data
    max_numeric_length = 10  # 设定阈值为10
    for col in data.columns:
        if data[col].dtype == 'object':
            try:
                if (len(str(data[col].iloc[0])) < max_numeric_length):
                    data[col] = pd.to_numeric(data[col], errors='ignore')  # 将无法转换的值设为 NaN
                    # data[col] = data[col].fillna("")  # 将 NaN 值替换为空字符串
            except ValueError:
                # 如果无法转换为浮点数，则保持为字符串类型
                pass
        # 如果列中全都是空值，则将其转换为字符串类型
        if data[col].isnull().all():
            data[col] = data[col].astype(str)

    numeric_cols = [col for col in data.columns if data[col].dtype != 'object']

    # 使用 SimpleImputer 对缺失值进行处理，只对数值列进行填充
    df_median = SimpleImputer(missing_values=np.nan, strategy='median', copy=False)
    data_imputed = df_median.fit_transform(data[numeric_cols])

    # 转换回 DataFrame，重新设置列名
    data_imputed = pd.DataFrame(data_imputed, columns=numeric_cols)

    base_data.update(data_imputed)

    return base_data


# Function to calculate Pearson, Kendall, and Spearman correlations
def calculate_correlations(data, features, labels):
    results = {}
    for label in labels:
        label_results = {}
        for feature in features:
            if data[feature].dtype in ['int64', 'float64'] and data[label].dtype in ['int64', 'float64']:
                pearson_corr, _ = pearsonr(data[feature].dropna(), data[label].dropna())
                kendall_corr, _ = kendalltau(data[feature].dropna(), data[label].dropna())
                spearman_corr, _ = spearmanr(data[feature].dropna(), data[label].dropna())
                label_results[feature] = {
                    'Pearson': round(pearson_corr, 3),
                    'Kendall': round(kendall_corr, 3),
                    'Spearman': round(spearman_corr, 3)
                }
        results[label] = label_results
    return results

# Function to calculate Chi-squared test for categorical features with multiple target columns
def chi_squared_test(data, features, labels):
    results = {}
    for label in labels:
        label_results = {}
        for feature in features:
            contingency_table = pd.crosstab(data[feature], data[label])
            chi2, p_value, _, _ = chi2_contingency(contingency_table)
            label_results[feature] = {'Chi-Squared': round(chi2,3), 'p-value': round(p_value,3)}
        results[label] = label_results
    return results



# Print the results


if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument("--tableName", type=str, default=None)
    parser.add_argument("--targetcolumn", nargs='+', type=str, default=None)
    parser.add_argument("--calculatedColumns", nargs='+', type=str, default=None)

    # 解析命令行参数
    args = parser.parse_args()
    tableName = args.tableName
    features = (args.calculatedColumns[0].split(" "))
    labels = args.targetcolumn[0].split(" ")
    data = read_data_from_postgresql(tableName)



    correlation_results = calculate_correlations(data, features, labels)
    chi_squared_results = chi_squared_test(data, features, labels)
    all_results = {
        'Correlation Results': correlation_results,
        'Chi-Squared Test Results': chi_squared_results
    }

    # 将结果转换为 JSON 格式
    json_results = json.dumps(all_results, ensure_ascii=False,indent=4)
    # 打印 JSON 字符串
    print(json_results)
