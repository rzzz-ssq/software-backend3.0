#!/usr/bin/env python
# encoding: utf-8
"""
 @Time    : 2023/4/25 15:18
 @File    : IdenMB.py
 """
import os
import sys

import numpy as np
import pandas as pd
from numpy import zeros, mat
# 获取当前文件所在的目录路径
current_folder = os.path.dirname(os.path.abspath(__file__))
# 获取 SFDRMB 所在的目录路径
sfdrmb_folder = os.path.join(current_folder, 'SFDRMB')
# 将 SFDRMB 目录路径添加到 Python 模块搜索路径中
sys.path.append(sfdrmb_folder)

from condition_independence_test import cond_indep_test
from subsets import subsets

def IdenMB(data, target, pcc_out, alaph, is_discrete=False):
    df = pd.DataFrame(data)
    num_col = df.shape[1]
    out_put = mat(zeros((num_col, num_col)))
    data_print = np.array(out_put)
    # print('data_print', data_print)
    # 计算独立性检验次数
    ci_number = 0
    number, kVar = np.shape(data)
    # target = t - 1
    # (9609, 40)包括特征
    max_k = 3
    # 父子集合
    CPC = []
    # 除了目标特征T之外的特征集合
    TMP = [i for i in range(kVar) if i != target]
    # 分离集
    sepset = [[] for i in range(kVar)]
    # 存储与目标节点条件无关的变量结合
    INDT = []
    # 配偶集合
    CSPT = [[] for i in range(kVar)]
    #
    variDepSet = []
    # 存储SP的特征和相关性大小
    pval_CSPT = []
    # 临时的配偶与父子集合
    SP = [[] for i in range(kVar)]
    PC = []
    # TMP = [int(x) for x in TMP]
    # 初始化，空集条件下计算x与T的独立性，不独立的特征加入到variDepSet
    for x in TMP:
        # print('x是：', x)
        ci_number += 1
        pval_f, dep_f = cond_indep_test(data, target, x, [], is_discrete)
        # print('pval_f:', pval_f)
        # print('dep_f:', dep_f)
        if pval_f > alaph:
            sepset[x] = []
            INDT.append(x)
        else:
            variDepSet.append([x, dep_f])
            # 往data_print中添加信息
            # print('此时pcc_out[target][x]:', pcc_out[target][x])

            # data_print[target][x] = pcc_out[target][x]
            # 添加判断data_print矩阵的维度和形状
            # print("target:", target)
            # print("x:", x)
            # print("data_print shape:", data_print.shape)
            # print("pcc_out shape:", pcc_out.shape)
            # if data_print.ndim == 2 and data_print.shape[0] > target and data_print.shape[1] > x:
            #     data_print[target][x] = pcc_out[target][x]
            # else:
            #     # 处理维度不匹配的情况
            #     print("维度不匹配，无法赋值：", target, x)
            #     print("data_print shape:", data_print.shape)
            #     print("pcc_out shape:", pcc_out.shape)

            # print('pcc_out[target][x]', pcc_out[target][x])
            # print('data_print[target][x]', data_print[target][x])

            # print('此时111data_print[target][x]:', data_print[target][x])
            # print('此时data_print:', data_print)
    variDepSet = sorted(variDepSet, key=lambda x: x[1], reverse=True)
    # print('排序后variDepSet：', variDepSet)

    """step one: Find the candidate set of PC and candidate set of spouse"""
    for variIndex in variDepSet:
        # print('variIndex is:', variIndex)
        # 从variDepSet集合中选择相关性最大的一个特征
        A = variIndex[0]
        # A_value = variIndex[1]
        # print("A is: " + str(A))
        # print("A_value is: " + str(A_value))
        Slength = len(CPC)
        if Slength > max_k:
            Slength = 3
        breakFlag = False
        # step 1-1 如果存在Z ⊆ CPCT 使得A和T是Z的独立条件，CSPT{X}置位空，把条件集Z加入到SepT{X}中
        for j in range(Slength + 1):
            # 创建并返回一个新对象
            ZSubsets = subsets(CPC, j)
            for Z in ZSubsets:
                ci_number += 1
                convari = [i for i in Z]
                pval_TAZ, dep_TAZ = cond_indep_test(
                    data, target, A, convari, is_discrete)
                if pval_TAZ > alaph:
                    sepset[A] = convari
                    CSPT[A] = []
                    breakFlag = True
                    indFlag = True
                    # 将不属于的特征A从data_print去掉
                    # print('不属于的特征A从data_print去掉', A)
                    data_print[target][A] = 0
                    # print('进行一次去除操作，此时data_print', data_print)
                    # 同时判断A节点是不是配偶节点
                    # print('A', A)
                    # print('sepset[A]', sepset[A])
                    # print('判断A节点是不是配偶节点：', A)
                    # 此时A不是父子节点，判断A节点是不是配偶节点
                    # print('此时A不是父子节点，判断A节点是不是配偶节点', A)
                    for pcIndex in CPC:
                        # print('当前pcIndex', pcIndex)
                        conv = [i for i in sepset[A]]
                        # print('sepset[A]条件集为conv', conv)
                        conv.append(pcIndex)
                        conv = list(set(conv))
                        ci_number += 1
                        pval_sp, _ = cond_indep_test(
                            data, target, A, conv, is_discrete)
                        # 通过P值与学习率判断，将满足条件的C加入到配偶集合中
                        if pval_sp <= alaph:
                            CSPT[pcIndex].append(A)
                            # print('条件依赖的条件集为conv', conv)

                            # 将属于的添加到data_print
                            # data_print[pcIndex][A] = pcc_out[pcIndex][A]
                            # print('判断配偶后data_print', data_print[pcIndex][A])
                            # pval_CSPT.append([A, pval_sp])
                            indFlag = False
                    if indFlag:
                        INDT.append(A)
                    break
            if breakFlag:
                break
        # step 1-2 否则，将A加到CPCT中,并执行寻找目标节点可能的配偶
        if not breakFlag:
            # print('否则将A节点加入到PC集合中：', A)
            CPC_ReA = CPC.copy()
            B_index = len(CPC_ReA)
            CPC.append(A)

            # data_print[target][A] = pcc_out[target][A]
            # print('pcc_out[target][A]', pcc_out[target][A])
            # print('CPC_ReA：', CPC_ReA)
            breakF = False
            # 循环遍历CPC父子集合
            while B_index > 0:
                B_index -= 1
                B = CPC_ReA[B_index]
                # print('B', B)
                flag1 = False
                # ST-DRMB被触发来检查CPCT \{A}中的每个特征以去除假阳性
                # print('这里B', B)
                conditionSet = [i for i in CPC_ReA if i != B]
                Clength = len(conditionSet)
                if Clength > max_k:
                    Clength = max_k
                for j in range(Clength + 1):
                    CSubsets = subsets(conditionSet, j)
                    # print('这里调用的',CSubsets)
                    # print('这里conditionSet', conditionSet)
                    for Z in CSubsets:
                        ci_number += 1
                        convari = [i for i in Z]
                        pval_TBZ, dep_TBZ = cond_indep_test(
                            data, target, B, convari, is_discrete)
                        # print("pval_TBZ: " + str(pval_TBZ))
                        if pval_TBZ > alaph:
                            if B in CPC:
                                CPC.remove(B)

                                # 将CPC假的节点从data_print移除
                                data_print[target][B] = 0
                                # print('移除节点B：', B)
                                # print('此时data_print[target][B]', data_print)

                            CSPT[B] = []
                            sepset[B] = convari
                            flag1 = True
                            if B == A:
                                breakF = True
                    if flag1:
                        break
                if breakF:
                    break
            # 添加T的配偶
            CSPT[A] = []
            # print("sepset: " + str(sepset))
            # 遍历文件的所有特征列
            # for C in range(kVar):
            # print('INDT:', INDT)
            for C in INDT:
                # if C == target or C in CPC:
                #     continue
                #     确定目标变量C的位置
                # 从U\{T}\CPCT中寻找target（C）的配偶
                # 根据定理1，判定加入配偶集的条件（septC 并A）
                # print('C:', C)
                # print('sepset[C]:', sepset[C])
                conditionSet = [i for i in sepset[C]]
                conditionSet.append(A)
                conditionSet = list(set(conditionSet))

                ci_number += 1
                pval_CAT, dep_C = cond_indep_test(
                    data, target, C, conditionSet, is_discrete)
                # 通过P值与学习率判断，将满足条件的C加入到配偶集合中
                if pval_CAT <= alaph:
                    CSPT[A].append(C)

                    # 将符合要求的特征加入到data_print
                    # data_print[A][C] = pcc_out[A][C]
                    # print(C, '是', A, '的父子')
                    # print('data_print[A][C]', data_print[A][C])
                    # print('data_print', data_print)

                    # pval_CSPT.append([C, pval_CAT])
            # print('寻找T的可能的配偶：', CSPT[A])
    # pval_CSPT = sorted(pval_CSPT, key=lambda x: x[1], reverse=False)
    # print('执行了1次')
    # print('Iden阶段删除配偶前CPC', CPC)
    # print('Iden阶段删除配偶前CSPT', CSPT)
    # # print(len(CSPT))
    # # print('pval_CSPT', pval_CSPT)
    # # step 1-3 找到PC和SP后，去除假的SP
    # for i in range(len(CSPT)):
    #     if CSPT[i] != []:
    #         # 去除假阳性SP节点
    #         index_sp = len(CSPT[i])
    #         # print('CSPT[i]', CSPT[i])
    #         while index_sp > 0:
    #             index_sp -= 1
    #             x = CSPT[i][index_sp]
    #             # print('x是', x)
    #             ZAllconditionSet = [i for i in CSPT[i] if i != x]
    #             # print('ZAllconditionSet是', ZAllconditionSet)
    #             ZAllSubsets = list(set(CPC).union(set(ZAllconditionSet)))
    #             print('set(CPC)', set(CPC))
    #             print('ZAllSubsets', ZAllSubsets)
    #             Zalength = len(ZAllSubsets)
    #             # print('Zalength',Zalength)
    #             if Zalength > max_k:
    #                 Zalength = max_k
    #             for j in range(Zalength + 1):
    #                 ZaSubsets = subsets(ZAllSubsets, j)
    #                 # print("ZaSubsets is: " + str(ZaSubsets))
    #                 for Z in ZaSubsets:
    #                     Z = [i for i in Z]
    #                     if A not in Z:
    #                         Z.append(A)
    #                 ci_number += 1
    #                 pval_TXZ, _ = cond_indep_test(
    #                     data, target, x, Z, is_discrete)
    #                 if pval_TXZ > alaph:
    #                     print('进入sp删除阶段')
    #                     CSPT[i].remove(x)
    #                     INDT.append(x)
    #                     print('Iden阶段删除配偶后CSPT', CSPT)
    #                     break

    spouseT = [j for i in CPC for j in CSPT[i]]
    CPC = set(CPC)
    spouseT = set(spouseT)
    # print('Iden阶段PC集合为', CPC)
    # print('Iden阶段SP集合为', spouseT)
    # MB = list(set(CPC).union(set(spouseT)))
    # print(MB)
    #  return ci_number
    return CPC, spouseT, data_print, TMP


# import  pandas as pd
# # import time
# data = pd.read_csv("../data/GastricCancer.csv")
# print("the file read")
# target = 38
# alaph = 0.01
#
# is_discrete = False
# # start_time = time.process_time()
# CPC, spouseT, data_pr = IdenMB(data, target, alaph, is_discrete)
# # end_time = time.process_time()
#
# MB = list(set(CPC).union(set(spouseT)))
#
# # print('算法运行时间',end_time - start_time)
# print('PC集合为', CPC)
# print('SP集合为', spouseT)
# print("MBs is: ", str(MB))
# print('data_pr', data_pr)



