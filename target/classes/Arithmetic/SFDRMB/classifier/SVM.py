from sklearn.metrics import accuracy_score
from sklearn.svm import SVC
from sklearn.model_selection import cross_val_score, train_test_split
from sklearn.preprocessing import MinMaxScaler

def SVM(data):
    X = data.iloc[:, :-1]
    y = data.iloc[:, -1]
    X_train1, X_test1, y_train, y_test = train_test_split(X, y, test_size=0.3, random_state=42)
    # 归一化特征数据
    scaler = MinMaxScaler()
    X_train = scaler.fit_transform(X_train1)
    X_test = scaler.fit_transform(X_test1)

    param_range = [0.1, 1, 10, 100]

    # 初始化最优参数和准确率
    best_c = None
    best_accuracy = 0.0

    # 遍历参数范围
    for c in param_range:
        # 创建SVM分类器
        svm = SVC(kernel='linear', C=c)

        # 进行十折交叉验证
        scores = cross_val_score(svm, X_train, y_train, cv=10)
        # scores = cross_val_score(svm, X, y, cv=10)
        print('----------', scores)
        # 计算平均准确率
        accuracy = scores.mean()
        print('---accuracy-------',  accuracy)
        # 检查是否为最优参数
        if accuracy > best_accuracy:
            best_accuracy = accuracy
            best_c = c

    # 输出最优参数和对应的准确率
    print("最优参数C：", best_c)
    print("最优准确率：", best_accuracy)

    svm = SVC(kernel='linear', C=best_c)
    svm.fit(X_train, y_train)

    # 在测试集上进行预测
    y_pred = svm.predict(X_test)

    # 计算准确率
    accuracy = accuracy_score(y_test, y_pred)
    print("测试集准确率：", accuracy)

    return accuracy

# if __name__ == '__main__':
#     data = pd.read_csv("../../../data/realData/ObesityDataSet.csv")
#     # data = pd.read_csv("../../../data/realData/test1.csv")
#     # data = pd.read_csv("../../../data/realData/ObesityDataSet_raw_and_data_sinthetic.csv")
#     SVMtest(data)
