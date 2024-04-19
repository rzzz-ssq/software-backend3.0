# coding=utf-8
# /usr/bin/env python
"""
date: 2019/7/8 8:41
desc: 
"""
import argparse
import os
import sys
from sklearn.impute import SimpleImputer
import mysql.connector
from sqlalchemy import create_engine
import psycopg2
current_folder = os.path.dirname(os.path.abspath(__file__))
# 获取 SFDRMB 所在的目录路径
sfdrmb_folder = os.path.join(current_folder, 'IAMB')
# 将 SFDRMB 目录路径添加到 Python 模块搜索路径中
sys.path.append(sfdrmb_folder)

import json
import time

import numpy as np
from condition_independence_test import cond_indep_test

def IAMB(data, list_t, alaph, is_discrete=True):
    number, kVar = np.shape(data)
    ci_number = 0
    # forward circulate phase
    length_targets = len(list_t)
    ResMB = [[]] * length_targets

    for i, target in enumerate(list_t):
        # print("i:", i, "---target:", target)
        # print("ResMB", ResMB)
        CMB = []
        circulate_Flag = True
        while circulate_Flag:
            # if not change, forward phase of IAMB is finished.
            circulate_Flag = False
            # tem_dep pre-set infinite negative.
            temp_dep = -(float)("inf")
            y = None
            variables = [i for i in range(kVar) if i != target and i not in CMB]

            for x in variables:
                ci_number += 1
                pval, dep = cond_indep_test(data, target, x, CMB, is_discrete)
                # print("target is:",target,",x is: ", x," CMB is: ", CMB," ,pval is: ",pval," ,dep is: ", dep)

                # chose maxsize of f(X:T|CMB)
                if pval <= alaph:
                    if dep > temp_dep:
                        temp_dep=dep
                        y=x
            # if not condition independence the node,appended to CMB
            if y is not None:
                # print('appended is :'+str(y))
                CMB.append(y)
                circulate_Flag = True

        # backward circulate phase
        CMB_temp = CMB.copy()
        for x in CMB_temp:
            # exclude variable which need test p-value
            condition_Variables=[i for i in CMB if i != x]
            ci_number += 1
            pval, dep = cond_indep_test(data, target, x, condition_Variables, is_discrete)
            # print("target is:", target, ",x is: ", x, " condition_Variables is: ", condition_Variables, " ,pval is: ", pval, " ,dep is: ", dep)
            if pval > alaph:
                # print("removed variables is: " + str(x))
                CMB.remove(x)

        ResMB[i] = CMB.copy()
        # print("第", i, "次:", ResMB)
        # print("第", i, "次---:", list(set(ResMB)))
    # return list(set(CMB)), ci_number
    return ResMB, ci_number

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
    query = f"SELECT * FROM `{tableName}`"
    data = pd.read_sql(query, connection)
    # data_filled = data.fillna(0)

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


import  pandas as pd

if __name__ == '__main__':
    # data_path = pd.read_csv("../data/GastricCancer.csv")
    # data_path = pd.read_csv("../data/ObesityDataSet_raw_and_data_sinthetic.csv") #target = 16
    parser = argparse.ArgumentParser()
    parser.add_argument("--tableName", type=str, default=None)
    parser.add_argument("--targetcolumn",  nargs='+', type=str, default=None)
    parser.add_argument("--calculatedColumns", nargs='+', type=str, default=None)

    # 解析命令行参数
    args = parser.parse_args()
    tableName = args.tableName

    #目标列
    tar = args.targetcolumn
    targ = tar[0]
    numbers_list = targ.split()
    targe = [int(num) for num in numbers_list]
    target = []
    for x in targe:
        target.append(x - 1)

    # 参数列
    # 将获取的参数转换为整数列表
    calculatedColumns = args.calculatedColumns
    # 字符串传化数组
    input_string = calculatedColumns[0]
    numbers_list = input_string.split()
    idxs = [int(num) for num in numbers_list]
    int_list = []
    # 重新生成参与运算的新表
    # for col in calculatedColumns:
    #     idxs.append(int(col))
    for x in idxs:
        int_list.append(x - 1)
    # 接收target 数组
    # target = [1, 2, 3, 4, 36, 35, 37, 38]
    #读取文件
    data_path = read_data_from_postgresql(tableName)
    new_data_target = data_path.iloc[:, target]
    new_data_calculatedColumns = data_path.iloc[:, int_list]
    concatenated_data = pd.concat([new_data_target, new_data_calculatedColumns], axis=1)
    target_res = []
    target_init = len(target)

    target_res = list(range(target_init))

    # no = concatenated_data.iloc[:,target_res];
    # np.savetxt('F:\Code\pyCauIAMB\concatenated_data.txt',concatenated_data, delimiter=',', fmt='%.1f')
    # np.savetxt('F:\Code\pyCauIAMB\no.txt',no, delimiter=',', fmt='%.1f')

    # _, num_para = np.shape(data_path)
    # print("num_para", num_para)
    # list_t = input("target variable index: ").split(",")
    # target = []
    # if list_t[0] == "all":
    #     target = [i for i in range(num_para)]
    # else:
    #     for i in list_t:
    #         target.append(int(i))
    # print("target", target)

    # 算法内固定参数
    alaph = 0.01
    is_discrete = 1
    if is_discrete == "1":
        is_discrete = True
    elif is_discrete == "0":
        is_discrete = False

    use_time = 0
    start_time = time.process_time()
    MB, ci_num = IAMB(concatenated_data, target_res, alaph, is_discrete=True)
    end_time = time.process_time()
    use_time += (end_time - start_time)
    # 接收的MB是一个二维数组
    ResMB_names = []
    for sublist in MB:
        # print("sublist", sublist)
        subset_data_1 = concatenated_data.iloc[:, sublist]
        # print("subset_data_1", subset_data_1)
        field_names = subset_data_1.columns.tolist()
        # print("field_names", field_names)
        ResMB_names.append(field_names.copy())

    # print('ci_num is: ', ci_num)
    # print('use_time is: ', use_time)
    # print("MB is:", MB)

    # input1 = json.dumps(target_res)
    # input = json.dumps(ResMB_names)

    res = [{"ResMB_names":ResMB_names},{"ci_num": ci_num}, {"use_time": use_time}]

    # 将列表转换为 JSON 格式
    input1 = json.dumps(res, ensure_ascii=False)
    # print(input)
    print(input1)
    # # 数组转换成json字符串
    # json_data = json.dumps(field_names)
    # print('MB name is: ', json_data)
