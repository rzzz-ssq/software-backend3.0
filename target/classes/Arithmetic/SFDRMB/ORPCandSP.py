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

# 使用OR规则恢复特征
def ORPCandSP(data, PCS, SPS, target, pcc_out, out_put, alaph, k_or, is_discrete):
    # print('接收到的pcc_out', pcc_out)
    # print('接收到的out_put', out_put)
    # 得到其他数据列下标
    number, kVar = np.shape(data)
    # 获取到F=D\{PCST ∪ SPST}集合
    d_sub = [i for i in range(kVar) if i != target]
    MB = list(set(PCS).union(set(SPS)))
    MB = set(MB)
    other_cols = set(d_sub) - set(MB)
    ci_all = 0
    # 转换成数组
    idxs = []
    # 存储top前k_or%个特征的列名字
    top_feature = []
    # 接收返回值
    or_pct = []
    or_spt = []
    for col in sorted(list(other_cols)):
        idxs.append(int(col))
    idxs.append(target)
    # print('idxs', idxs)
    # 根据idxs特征列存储数据表中对应的特征列数据
    subset_data = data.iloc[:, idxs]
    # print('--------------------')
    # 获取最后一列的名字，也即标签列的名字
    last_col_index = subset_data.iloc[:, -1].name
    # print(last_col_index)
    # 获取最后一列，即标签列与其他特征的皮尔逊相关性系数
    pcc_last_col = subset_data.corr()[last_col_index]
    # print(pcc_last_col)
    # 获取subset_data表中特征列的名字信息，方便下一步求绝对值后一一对应
    col_names = subset_data.columns.tolist()
    # print(col_names)
    # 相关关系计算绝对值，并与特征一一对应
    last_col_result = dict(map(lambda x, y: [x, abs(y)], col_names, pcc_last_col))
    # print(last_col_result)
    # 将绝对值后的相关关系进行排序
    pcc_last_col_sort = sorted(last_col_result.items(), key=lambda t: t[1], reverse=True)
    # print(pcc_last_col_sort)
    # 相关性由大到小取前k_or%个特征
    n = int(len(idxs) * k_or)
    # print(n)
    top_percent = pcc_last_col_sort[:n]
    # print('top_percent', top_percent)
    # 获取排序后的特征名字
    for i in range(len(top_percent)):
        top_feature.append(top_percent[i][0])
    # print(top_feature)
    # 根据名字将其转换成data数据集中的下标no_target_indices
    col_data_names = data.columns.tolist()
    target_indices = [col_data_names.index(col) for col in top_feature]
    no_target_indices = target_indices[1:]
    # print(target_indices)
    # print(no_target_indices)

    # 获得排序后的前k_or%特征，基于OR规则恢复
    for i in no_target_indices:
        CPC, spouseT, i_conn, c = IdenMB(data, i, pcc_out, alaph, is_discrete)
        # print('i_conn', i_conn)
        # print('i_conn列', i_conn[][target])
        # print('子节点的cpc:', CPC)
        # print('sp:', spouseT)
        # print(target)
        # 如果target在i的pc集中，则将i加入到or_PCT
        if target in CPC:
            or_pct.append(i)
            out_put[target][i] = pcc_out[target][i]
            # print('out_put[target][i]', out_put[target][i])
        # 如果target在i的sp集中，则将i加入到or_SPT
        if target in spouseT:
            or_spt.append(i)
            # 获取i_conn中target列数据，将列中有值的数据，赋值给out_put对应的位置
            # i_conn第i行
            # out_put[:, target] = i_conn[:, target]
        # print('ci_all:', ci_all)
    return or_pct, or_spt, out_put


# data = pd.read_csv("C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/GastricCancer.csv")
# target = 39
# alaph = 0.01
# k_or = 0.5
#
# PCS, SPS, ci_number = IdenMB(data, target, alaph, is_discrete=False)
# print('PC:', PCS)
# print('SP:', SPS)
# # print('ci_number',ci_number)
# or_PCT, or_SPT = ORPCandSP(data, PCS, SPS, target, alaph, k_or,is_discrete=False)
# print('or_PCT:', or_PCT)
# print('or_SPT:', or_SPT)
