
"""
 @Time    : 2023/4/21 09:45
 @File    : SF_DRMB.py
 """
import json
import os
import sys
from sklearn.impute import SimpleImputer
import numpy as np
import pandas as pd
from numpy import zeros, mat
# 获取当前文件所在的目录路径
current_folder = os.path.dirname(os.path.abspath(__file__))
# 获取 SFDRMB 所在的目录路径
sfdrmb_folder = os.path.join(current_folder, 'SFDRMB')
# 将 SFDRMB 目录路径添加到 Python 模块搜索路径中
sys.path.append(sfdrmb_folder)

from IdenMB import IdenMB
from ORPCandSP import ORPCandSP
from ANDPCandSP import ANDPCandSP
import mysql.connector
import argparse
import pandas as pd
import psycopg2
from sqlalchemy import create_engine
import mysql.connector





# 定义一个从MySQL中读取数据的函数
def read_data_from_mysql(tableName):
    # 假设你已经获得了数据库的连接参数，例如：host、user、password、database等
    # 请根据你自己的实际情况来填写这些参数
    host = "10.16.48.219"
    user = "root"
    password = "111111"
    database = "software4"
    # 连接到MySQL数据库
    connection = mysql.connector.connect(host=host, user=user, password=password, database=database)
    # 读取数据到DataFrame
    query =  f"SELECT * FROM `{tableName}`"
    data = pd.read_sql(query, connection)
    # 关闭数据库连接
    connection.close()
    return data

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
    #query = f'SELECT "age", "sexcode", "MPV", "P_LCR", "PDW", "PCT", "EO_num", "BASO_num", "RBC", "HGB", "NEUT_per", "LYMPH_per", "WBC", "NEUT_num", "HCT", "LYMPH_num", "MONO_per", "EO_per", "MONO_num", "BASO_per", "RDW_SD", "RDW_CV", "PLT", "temperature", "nationcode", "maritalstatuscode", "occupationcategorycode", "nationalitycode", "edubackgroundcode", "age" FROM merge;'
    query = f'SELECT * FROM {tableName}'
    data = pd.read_sql(query, engine)
    base_data = data
    max_numeric_length = 10  # 设定阈值为10
    for col in data.columns:
        if data[col].dtype == 'object':
            try:
                if(len(str(data[col].iloc[0]))<max_numeric_length):
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
    data_imputed = pd.DataFrame(data_imputed,columns=numeric_cols)

    base_data.update(data_imputed)

    return base_data

# SF_DRMB共分三个阶段: 阶段1：同时发现PC和SP，阶段2：恢复可能遗漏的节点，阶段3删除阶段1假阳性节点
def SF_DRMB(data, target, alaph, k_or, k_and_pc, k_and_sp, is_discrete):
    # 计算所有列的相关性系数,结果保存3位
    pcc_last_col = data.corr()
    rounded_result_array = np.round(pcc_last_col, 3)
    absolute_correlation_matrix = np.abs(rounded_result_array)
    pcc_out = np.array(absolute_correlation_matrix)

    # 阶段1：发现PC和SP
    PCS, SPS, out_put, aa = IdenMB(data, target, pcc_out, alaph, is_discrete)
    # print('阶段1 已被调用：')
    # print('阶段1 PCS：', PCS)
    # print('阶段1 SPS：', SPS)
    # print('阶段1 接收到的out_put：', out_put)
    # 阶段2：基于OR规则恢复特征
    or_PC, or_SP, out_put1 = ORPCandSP(data, PCS, SPS, target, pcc_out, out_put, alaph, k_or, is_discrete)
    # 阶段3：基于AND规则删除假阳性节点
    and_PC, and_SP, out_put2 = ANDPCandSP(data, PCS, SPS, target, pcc_out, out_put1, alaph, k_and_pc, k_and_sp, is_discrete)

    # print('阶段3 and_PC:', and_PC)
    # print('阶段3 and_SP:', and_SP)
    # print('阶段2 or_PC:', or_PC)
    # print('阶段2 or_SP:', or_SP)
    PC = list(set(or_PC).union(set(and_PC)))
    SP = list(set(or_SP).union(set(and_SP)))
    MB = list(set(PC).union(set(SP)))
    # print('MB的数量：', len(MB))
    # print('MB：', MB)
    # ci_num = ci_number + ci_or + ci_and
    return MB, out_put2, aa

if __name__ == '__main__':

    # # 创建参数解析器
    # parser = argparse.ArgumentParser()
    # parser.add_argument("--tableName", type=str, default=None)
    # parser.add_argument("--targetcolumn",type=int, default=None)
    # parser.add_argument("--K_OR", type=float, default=None)
    # parser.add_argument("--K_and_pc", type=float, default=None)
    # parser.add_argument("--K_and_sp", type=float, default=None)
    # parser.add_argument("--calculatedColumns", nargs='+', type=int, default=None)  # 接收多个值的参数
    #
    # # 解析命令行参数
    # args = parser.parse_args()
    # tableName = args.tableName
    # target = args.targetcolumn
    # k_or = args.K_OR
    # k_and_pc = args.K_and_pc
    # k_and_sp = args.K_and_sp
    # calculatedColumns = args.calculatedColumns  # 该参数为一个列表，包含传递过来的多个值

    parser = argparse.ArgumentParser()
    parser.add_argument("--tableName", type=str, default=None)
    parser.add_argument("--targetcolumn", nargs='+', type=str, default=None)
    parser.add_argument("--K_OR", type=float, default=None)
    parser.add_argument("--K_and_pc", type=float, default=None)
    parser.add_argument("--K_and_sp", type=float, default=None)
    parser.add_argument("--calculatedColumns", nargs='+', type=str, default=None)




    # 解析命令行参数
    args = parser.parse_args()
    tableName = args.tableName

    tar = args.targetcolumn
    targ = tar[0]
    numbers_list = targ.split()
    targe = [int(num) for num in numbers_list]
    target = []
    for x in targe:
        target.append(x - 1)

    target_res = []
    target_init = len(target)

    target_res = list(range(target_init))


    k_or = args.K_OR
    k_and_pc = args.K_and_pc
    k_and_sp = args.K_and_sp
    # 将获取的参数转换为整数列表
    calculatedColumns = args.calculatedColumns
    #字符串传化数组
    input_string = calculatedColumns[0]
    numbers_list = input_string.split()
    idxs = [int(num) for num in numbers_list]
    # str_list = str(int_list)
    # print('tableName:', tableName)
    # print('target:', target)
    # print('k_or:', k_or)
    # print('k_and_pc:', k_and_pc)
    # print('k_and_sp:', k_and_sp)
    # print('calculatedColumns:', calculatedColumns)

    data_path =read_data_from_postgresql(tableName)

    new_data_path1 = data_path.iloc[:, target]
    int_list = []
    # 重新生成参与运算的新表
    # for col in calculatedColumns:
    #     idxs.append(int(col))
    for x in idxs:
        int_list.append(x - 1)

    new_data_path = data_path.iloc[:, int_list]

    concatenated_data = pd.concat([new_data_path1, new_data_path], axis=1)
    # concatenated_data.iloc[:, [0, 1]] = concatenated_data.iloc[:, [1, 0]]
    # concatenated_data = pd.concat([new_data_path, new_data_path1], axis=1)

    # # 获取DataFrame的列数（列数量）
    # num_columns = concatenated_data.shape[1]
    #
    # # 获取最后一列的列索引（整数值）
    # target = num_columns - 1

    # data_path = pd.read_csv("../data/GastricCancer.csv")
    # data_path = pd.read_csv("../data/ObesityDataSet_raw_and_data_sinthetic.csv")
    # 目标特征列
    # target = 38
    # 接收的三个参数
    # k_or = 0.15
    # k_and_pc = 0.3
    # k_and_sp = 0.75

    # target = 16
    # k_or = 0.15
    # k_and_pc = 0.2
    # k_and_sp = 0.4
    # 算法内固定参数
    alaph = 0.01
    is_discrete = 1
    if is_discrete == "1":
        is_discrete = True
    elif is_discrete == "0":
        is_discrete = False


    # start_time = time.process_time()
    MB, out_put, b = SF_DRMB(concatenated_data, 0, alaph, k_or, k_and_pc, k_and_sp, is_discrete)
    # end_time = time.process_time()
    subset_data = concatenated_data.iloc[:, MB]
    field_names = subset_data.columns.tolist()

    # result=[
    #     {"MBs is:": field_names}
    # ]
    # np.savetxt('F:\Code\pyCausalMB\SFDRMB\my_array1.csv', concatenated_data, delimiter=',', fmt='%.3f')
    json_output = json.dumps(field_names)
    print(json_output)
    # np.savetxt('my_array.txt', out_put[target], delimiter=',', fmt='%.3f')

