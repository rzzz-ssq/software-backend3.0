# 如果一个样本在特征空间中的K个最相似（即特征空间中最邻近，用上面的距离公式描述）的样本中的大多数属于某一个类别，
# 则该样本也属于这个类别。该方法在定类决策上只依据最邻近的一个或者几个样本的类别来决定待分样本所属的类别。
# 步骤：
# 1计算想要分类的点到其余点的距离
# 2按距离升序排列，并选出前K（KNN的K）个点，也就是距离样本点最近的K个点
# 3加权平均，得到答案

from sklearn.neighbors import KNeighborsClassifier
from sklearn.preprocessing import MinMaxScaler
from sklearn.model_selection import cross_val_score, KFold, train_test_split
from sklearn.metrics import accuracy_score

def KNN(data):
    K = 10
    print(data)
    X = data.iloc[:, :-1]
    y = data.iloc[:, -1]

    # 划分训练集和测试集
    X_train1, X_test1, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)

    scaler = MinMaxScaler()
    X_train = scaler.fit_transform(X_train1)
    X_test = scaler.fit_transform(X_test1)


    # 创建KNN分类器
    knn = KNeighborsClassifier(n_neighbors=K)

    # 创建十折交叉验证对象
    kfold = KFold(n_splits=10, shuffle=True, random_state=42)

    # 进行十折交叉验证
    scores = cross_val_score(knn, X_train, y_train, cv=kfold)
    # scores = cross_val_score(knn, X, y, cv=kfold)

    # 打印每折交叉验证的准确率
    for fold, score in enumerate(scores):
        # print(f"Fold {fold + 1}: Accuracy = {score}")
        print(score)
    average = scores.mean()
    # 打印平均准确率
    print("Mean Accuracy:", average)

    # 在训练集上训练模型
    knn.fit(X_train, y_train)

    # 在测试集上进行预测
    y_pred = knn.predict(X_test)

    # 计算准确率
    accuracy = accuracy_score(y_test, y_pred)
    print("测试集准确率：", accuracy)

    return average

# if __name__ == '__main__':
#     # data = pd.read_csv('data/Prostate_Cancer.csv')
#     # data = pd.read_csv('C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/GastricCancer.csv')
#     # data = pd.read_csv('C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/stdrmb GC.csv')
#     # data = pd.read_csv('C:/Users/hp-pc/PycharmProjects/pyCausalFS/CBD/data/realData/bamb.csv')
#     # data = pd.read_csv('/CBD/data/realData/SPECT_stdrmb.csv')
#     data = pd.read_csv("../../../data/realData/ObesityDataSet.csv")
#     KNN(data)
# 0.7405660377358491
# 0.7440758293838863
# 0.7203791469194313
# 0.7298578199052133
# 0.7298578199052133
# 0.7298578199052133
# 0.7203791469194313
# 0.7298578199052133
# 0.7772511848341233
# 0.8246445497630331
# Mean Accuracy: 0.7446727175176607

