
"""
 @Time    : 2023/4/21 09:45
 @File    : SF_DRMB.py
 """
import numpy as np
import pandas as pd
from numpy import zeros, mat
from SFDRMB.IdenMB import IdenMB
from SFDRMB.ORPCandSP import ORPCandSP
from SFDRMB.ANDPCandSP import ANDPCandSP
import mysql.connector
import argparse
#
# # 创建参数解析器
# parser = argparse.ArgumentParser()
# parser.add_argument("--tableName", type=str, default=None)
#
# # 解析命令行参数
# args = parser.parse_args()
# tableName=args.tableName



# SF_DRMB共分三个阶段: 阶段1：同时发现PC和SP，阶段2：恢复可能遗漏的节点，阶段3删除阶段1假阳性节点
def SF_DRMB(data, target, alaph, k_or, k_and_pc, k_and_sp, is_discrete):
    # 计算所有列的相关性系数,结果保存3位
    pcc_last_col = data.corr()
    rounded_result_array = np.round(pcc_last_col, 3)
    absolute_correlation_matrix = np.abs(rounded_result_array)
    pcc_out = np.array(absolute_correlation_matrix)

    # pcc_out = np.array(absolute_correlation_matrix)
    # one_out = pcc_out[target]
    # 阶段1：发现PC和SP
    PCS, SPS, out_put,a = IdenMB(data, target, pcc_out, alaph, is_discrete)
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
    return MB, out_put2

if __name__ == '__main__':
    data_path = pd.read_csv("../data/heart.csv")
    # data_path = pd.read_csv("../data/GastricCancer.csv")
    # data_path = pd.read_csv("F:\Code\pyCausalMB\SFDRMB\my_array1.csv")
    # data_path = pd.read_csv("../data/ObesityDataSet_raw_and_data_sinthetic.csv")
    # 目标特征列
    target = 13
    # 接收的三个参数
    k_or = 0.15
    k_and_pc = 0.3
    k_and_sp = 0.75
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
    MB, out_put = SF_DRMB(data_path, target, alaph, k_or, k_and_pc, k_and_sp, is_discrete)
    # end_time = time.process_time()
    print('MBs is: ', str(MB))
    print('关系矩阵 is: ', out_put[target])
    # np.savetxt('my_array.txt', out_put[target], delimiter=',', fmt='%.3f')

