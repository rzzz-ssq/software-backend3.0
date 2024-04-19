import os
import sys

import numpy as np
# 获取当前文件所在的目录路径
current_folder = os.path.dirname(os.path.abspath(__file__))
# 获取 SFDRMB 所在的目录路径
sfdrmb_folder = os.path.join(current_folder, 'SFDRMB')
# 将 SFDRMB 目录路径添加到 Python 模块搜索路径中
sys.path.append(sfdrmb_folder)

from IdenMB import IdenMB

def ANDPCandSP(data, PCS, SPS, target, pcc_out, out_put, alaph, k_and_pc, k_and_sp, is_discrete):

    ci_number = 0
    ci_pc = 0
    ci_sp = 0
    # 将and_PC和and_SP赋予初值
    and_PC = PCS
    and_SP = SPS
    # 存储top前k_and%个特征的列名字
    top_feature_pc = []
    top_feature_sp = []
    idxs = []
    idxs_sp = []
    # print('初始and_PC:', and_PC)
    # print('初始and_SP:', and_SP)
    if and_PC:
        # print('执行查找PC---------------------')
        # 1.取and_PC中前k_and_pc%个相关性最小的特征赋给P
        # 转换成数组
        for col in sorted(list(and_PC)):
            idxs.append(int(col))
        idxs.append(target)
        # print('idxs', idxs)
        # 根据idxs特征列存储数据表中对应的特征列数据
        subset_data_1 = data.iloc[:, idxs]
        # print(subset_data_1)
        # print('--------------------')
        # 获取最后一列的名字，也即标签列的名字
        last_col_index = subset_data_1.iloc[:, -1].name
        # print(last_col_index)
        # 获取最后一列，即标签列与其他特征的皮尔逊相关性系数
        pc_last_col = subset_data_1.corr()[last_col_index]
        # print(pc_last_col)
        # 获取subset_data表中特征列的名字信息，方便下一步求绝对值后一一对应
        col_names = subset_data_1.columns.tolist()
        # print(col_names)
        # 相关关系计算绝对值，并与特征一一对应
        last_col_result = dict(map(lambda x, y: [x, abs(y)], col_names, pc_last_col))
        # print(last_col_result)
        # 将绝对值后的相关关系进行排序
        pcc_last_col_sort = sorted(last_col_result.items(), key=lambda t: t[1], reverse=False)
        # print(pcc_last_col_sort)
        # 相关性由小到大取前k_and_pc%个特征
        n = int(len(idxs) * k_and_pc)
        # print('and父子准备进行删除的特征个数为：', n)
        top_percent = pcc_last_col_sort[:n]
        # print(top_percent)

        # 获取排序后的特征名字
        for i in range(len(top_percent)):
            top_feature_pc.append(top_percent[i][0])
        # print(top_feature_pc)
        # 根据名字将其转换成data数据集中的下标no_target_indices
        col_data_names = data.columns.tolist()
        target_indices = [col_data_names.index(col) for col in top_feature_pc]
        if k_and_pc == 1:
            no_target_indices = target_indices[:-1]
        else:
            no_target_indices = target_indices
        # print('target_indices:', target_indices)
        # print(no_target_indices)
        # 2.循环遍历no_target_indices中的特征下标，计算其中每个节点x的PCi
        for i in no_target_indices:
            CPC, spouseT, ci_pc, a = IdenMB(data, i, pcc_out, alaph, is_discrete)
            # print('子节点的cpc:', CPC)
            # print('sp:', spouseT)
            # print(target)
        # 3.如果T不属于PCzi，则将x从and_PC中移除，得到and_PC集合
            if target not in CPC:
                and_PC.discard(i)
                out_put[target][i] = 0
        #         print('执行了并移除了', i)
        #         print('------------------------')
        # print('最终and_PC为：', and_PC)

    if and_SP:
        # print('执行删除配偶')
        # 1.取and_SP中前k_and_sp%个相关性最小的特征赋给
        # 转换成数组
        for col in sorted(list(and_SP)):
            idxs_sp.append(int(col))
        idxs_sp.append(target)
        # print('idxs_sp', idxs_sp)
        # 根据idxs特征列存储数据表中对应的特征列数据
        subset_data_2 = data.iloc[:, idxs_sp]
        # print(subset_data_2)
        # print('--------------------')
        # 获取最后一列的名字，也即标签列的名字
        last_col_index_sp = subset_data_2.iloc[:, -1].name
        # print(last_col_index_sp)
        # 获取最后一列，即标签列与其他特征的皮尔逊相关性系数
        sp_last_col = subset_data_2.corr()[last_col_index_sp]
        # print(sp_last_col)
        # 获取subset_data表中特征列的名字信息，方便下一步求绝对值后一一对应
        col_names_sp = subset_data_2.columns.tolist()
        # print(col_names_sp)
        # 相关关系计算绝对值，并与特征一一对应
        last_col_result_sp = dict(map(lambda x, y: [x, abs(y)], col_names_sp, sp_last_col))
        # print(last_col_result_sp)
        # 将绝对值后的相关关系进行排序
        spc_last_col_sort = sorted(last_col_result_sp.items(), key=lambda t: t[1], reverse=False)
        # print(spc_last_col_sort)
        # 相关性由小到大取前k_or%个特征
        s = int(len(idxs_sp) * k_and_sp)
        # print('and 配偶进行恢复的特征个数为：', s)
        top_percent_sp = spc_last_col_sort[:s]
        # print(top_percent_sp)

        # 获取排序后的特征名字
        for i in range(len(top_percent_sp)):
            top_feature_sp.append(top_percent_sp[i][0])
        # print(top_feature_sp)
        # 根据名字将其转换成data数据集中的下标no_target_indices
        col_data_names = data.columns.tolist()
        target_indices_sp = [col_data_names.index(col) for col in top_feature_sp]
        if k_and_sp == 1:
            no_target_indices_sp = target_indices_sp[:-1]
        else:
            no_target_indices_sp = target_indices_sp
        # print(target_indices_sp)
        # print(no_target_indices_sp)
        # 2.循环遍历no_target_indices中的特征下标，计算其中每个节点x的PCi
        for j in no_target_indices_sp:
            CPC, spouseT, ci_sp, s = IdenMB(data, j, pcc_out, alaph, is_discrete)
            # print('子节点的cpc:', CPC)
            # print('sp:', spouseT)
            # print(target)
            # 3.如果T不属于PCzi，则将x从and_PC中移除，得到and_PC集合
            if target not in spouseT:
                and_SP.discard(j)
                # 对应的第j列不为0的赋值为空
                # print('out_put[:, j]之前', out_put[:, j])
                # out_put[:, j] = 0
                # print('out_put[:, j]之后', out_put[:, j])
                # print('执行了并移除了', j)
                # print('------------------------')
        # print('最终and_SP为：', and_SP)
        ci_number = ci_pc + ci_sp
    # 相关性由小到大取前k_or%个特征
    return and_PC, and_SP, out_put


# data = pd.read_csv("C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/GastricCancer.csv")
# target = 39
# alaph = 0.01
# k_and_pc = 0.5
# k_and_sp = 0.5
#
# PCS, SPS, ci_number = IdenMB(data, target, alaph, is_discrete=False)
# # print('PCS:', PCS)
# # print('SPS:', SPS)
# and_PC, and_SP = ANDPCandSP(data, PCS, SPS, target, alaph, k_and_pc, k_and_sp, is_discrete=False)
# print('and_PC:', list(set(and_PC)))
# print('and_SP:', list(set(and_SP)))

# print('ci_number',ci_number)
