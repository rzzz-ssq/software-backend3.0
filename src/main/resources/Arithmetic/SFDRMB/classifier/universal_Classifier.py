import numpy as np
import time
import pandas as pd

from SFDRMB.SF_DRMB import SF_DRMB
from SFDRMB.classifier.KNN import KNN
from SFDRMB.classifier.SVM import SVM

def classifier(method, sub_data_path):
    print('进入分类器----------------')
    print('接收到的data：', np.shape(sub_data_path))
    if method == "KNN":
        start_time = time.process_time()
        print('进入KNN')
        precision = KNN(sub_data_path)
        end_time = time.process_time()
    elif method == "SVM":
        start_time = time.process_time()
        print('进入SVM')
        precision = SVM(sub_data_path)
        end_time = time.process_time()
    else:
        raise Exception("method input error!")
    use_time = end_time - start_time
    return precision, use_time

def evaluation(
        al_method,
        path,
        target,
        isdiscrete,
        alaph=0.01):
    if al_method == "SF_DRMB":
        start_time = time.process_time()
        MB, ci_num = SF_DRMB(path, target, alaph, k_or, k_and_pc, k_and_sp, isdiscrete)
        end_time = time.process_time()
    else:
        raise Exception("method input error!")
    use_time = end_time - start_time
    return MB, ci_num, use_time

if __name__ == '__main__':
    # 输入分类器名字
    method = input("classifier name: ")
    # 输入数据集
    data_path = input("data: ")
    if data_path == "default":
        data_path = pd.read_csv("../../data/GastricCancer.csv")
        # data_path = pd.read_csv("../../data/ObesityDataSet_raw_and_data_sinthetic.csv")
    almethod = input("algorithm name: ")
    # target = 38
    # 输入目标节点
    target = int(input("target variable index: "))
    alpha = float(input("alpha: "))
    isdiscrete = input("is_discrete: ")
    if isdiscrete == "1":
        isdiscrete = True
    elif isdiscrete == "0":
        isdiscrete = False
    # alaph = 0.01
    print('程序已进入,请稍等......')
    #  肥胖数据集参数，初始PC长度3，SP长度1，此时没有进行and删除
    # k_or = 0.15
    # k_and_pc = 0.2
    # k_and_sp = 0.4
    k_or = 0.15
    k_and_pc = 0.3
    k_and_sp = 0.75
    # 算法选择
    print('执行MB选择算法')
    MB, ci_number, al_time = evaluation(almethod, data_path, target, isdiscrete, alpha)
    # 算法运行时间
    print('MB算法运行时间:', al_time)
    print('MB算法CITs:', ci_number)
    print("MBs is: " + str(MB))

    # 对输出的MB集合进行真实数据集的分类
    # 紧实度
    compactness = len(MB)
    print('执行分类算法')
    if compactness > 0:
        idxs = []
        for col in sorted(list(MB)):
            idxs.append(int(col))
        idxs.append(target)
        # 根据idxs特征列存储数据表中对应的特征列数据
        subset_data = data_path.iloc[:, idxs]
        print('subset_data_1', subset_data)
        print(np.shape(subset_data))
        # 获取subset_data表中特征列的名字信息
        col_names = subset_data.columns.tolist()
        print(col_names)
        Precision, cl_time = classifier(method, subset_data)
        print('分类预测时间：', cl_time)
        time = al_time + cl_time

        print("Precision is: " + str("%.2f" % Precision))
        print("Compactness is: " + str("%.2f" % compactness))
        print("ci_number is: " + str("%.2f" % ci_number))
        print("Running time is: " + str("%.2f" % time))
    else:
        print('未找到相关因果特征集合')