# 如果一个样本在特征空间中的K个最相似（即特征空间中最邻近，用上面的距离公式描述）的样本中的大多数属于某一个类别，
# 则该样本也属于这个类别。该方法在定类决策上只依据最邻近的一个或者几个样本的类别来决定待分样本所属的类别。
# 步骤：
# 1计算想要分类的点到其余点的距离
# 2按距离升序排列，并选出前K（KNN的K）个点，也就是距离样本点最近的K个点
# 3加权平均，得到答案

import pandas as pd
from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import cross_val_score, KFold
from sklearn.metrics import accuracy_score

# GC [0, 35, 36, 37, 38, 23] st-drmb target 39 #spect target 0   [16, 8, 13]
# GC[2, 35, 36, 23, 6, 38] BAMB   #spect [16, 17, 13]
def KNN(data):
    K = 10
    print(data)
    X = data.iloc[:, :-1]
    y = data.iloc[:, -1]

    # 创建KNN分类器
    knn = KNeighborsClassifier(n_neighbors=10)

    # 创建十折交叉验证对象
    kfold = KFold(n_splits=10, shuffle=True, random_state=42)

    # 进行十折交叉验证
    scores = cross_val_score(knn, X, y, cv=kfold)

    # 打印每折交叉验证的准确率
    for fold, score in enumerate(scores):
        print(f"Fold {fold + 1}: Accuracy = {score}")
    average = scores.mean()
    # 打印平均准确率
    print("Mean Accuracy:", average)
    return average

    # # 70%训练30%测试
    # train_set = data[:int(len(data) * 0.7)].values
    # test_set = data[int(len(data) * 0.7):].values
    # train_x = train_set[:, 2:10]
    # train_y = train_set[:, 1]
    # test_x = test_set[:, 2:10]
    # test_y = test_set[:, 1]
    #
    #
    # # 归一化
    # scaler = MinMaxScaler()
    # train_x = scaler.fit_transform(train_x)
    # test_x = scaler.transform(test_x)
    # knn = KNeighborsClassifier(n_neighbors=K)
    # knn.fit(train_x, train_y)
    # score = knn.score(test_x, test_y)
    # print("准确率为:", score)
    # return score

# if __name__ == '__main__':
#     # data = pd.read_csv('data/Prostate_Cancer.csv')
#     # data = pd.read_csv('C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/GastricCancer.csv')
#     # data = pd.read_csv('C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/stdrmb GC.csv')
#     # data = pd.read_csv('C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/bamb.csv')
#     data = pd.read_csv('C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/SPECT_2671.csv')
#     # data = pd.read_csv('/CBD/data/realData/SPECT_stdrmb.csv')
#     KNN(data)
