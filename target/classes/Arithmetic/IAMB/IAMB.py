# coding=utf-8
# /usr/bin/env python
"""
date: 2019/7/8 8:41
desc: 
"""
import os
import sys

current_folder = os.path.dirname(os.path.abspath(__file__))
# 获取 SFDRMB 所在的目录路径
sfdrmb_folder = os.path.join(current_folder, 'IAMB')
# 将 SFDRMB 目录路径添加到 Python 模块搜索路径中
sys.path.append(sfdrmb_folder)
import json
import  pandas as pd
import numpy as np
from condition_independence_test import cond_indep_test


def IAMB(data, target, alaph, is_discrete=True):
    number, kVar = np.shape(data)
    CMB = []
    ci_number = 0
    # forward circulate phase
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
        pval, dep = cond_indep_test(data,target, x, condition_Variables, is_discrete)
        # print("target is:", target, ",x is: ", x, " condition_Variables is: ", condition_Variables, " ,pval is: ", pval, " ,dep is: ", dep)
        if pval > alaph:
            # print("removed variables is: " + str(x))
            CMB.remove(x)
    # print(list(set(CMB)))
    return list(set(CMB)), ci_number



if __name__ == '__main__':
    data_path = pd.read_csv("../data/GastricCancer.csv")
    # data_path = pd.read_csv("../data/ObesityDataSet_raw_and_data_sinthetic.csv") #target = 16
    # 目标特征列
    target = 38


    # 算法内固定参数
    alaph = 0.01
    is_discrete = 1
    if is_discrete == "1":
        is_discrete = True
    elif is_discrete == "0":
        is_discrete = False
    # start_time = time.process_time()
    MB, ci_num = IAMB(data_path, target, alaph, is_discrete=True)
    # end_time = time.process_time()
    # print('MB is: ', str(MB))
    # print('ci_num is: ', ci_num)
    subset_data_1 = data_path.iloc[:, MB]
    # print(subset_data_1)
    field_names = subset_data_1.columns.tolist()
    # 数组转换成json字符串
    json_data = json.dumps(field_names)
    print(json_data)
