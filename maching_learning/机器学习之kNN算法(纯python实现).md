前面文章分别简单介绍了线性回归，逻辑回归，贝叶斯分类，并且用python简单实现。这篇文章介绍更简单的 knn， k-近邻算法（kNN，k-NearestNeighbor）。  
`k-近邻算法（kNN，k-NearestNeighbor），是最简单的机器学习分类算法之一，其核心思想在于用距离目标最近的k个样本数据的分类来代表目标的分类（这k个样本数据和目标数据最为相似）。
`

### 原理
kNN算法的核心思想是用距离最近(多种衡量距离的方式)的k个样本数据来代表目标数据的分类。

具体讲，存在**训练样本集**， 每个样本都包含数据特征和所属分类值。
输入新的数据，将该数据和训练样本集汇中每一个样本比较，找到距离最近的k个，在k个数据中，出现次数做多的那个分类，即可作为新数据的分类。

![image.png](http://upload-images.jianshu.io/upload_images/1794675-4235d7f648c53ccf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  
如上图：
需要判断绿色是什么形状。当k等于3时，属于三角。当k等于5是，属于方形。
因此该方法具有一下特点：
* 监督学习：训练样本集中含有分类信息
* 算法简单， 易于理解实现
* 结果收到k值的影响，k一般不超过20.
* 计算量大，需要计算与样本集中每个样本的距离。
* 训练样本集不平衡导致结果不准确问题

接下来用oython 做个简单实现， 并且尝试用于约会网站配对。

### python简单实现

```
def classify(inX, dataSet, labels, k):
    """
    定义knn算法分类器函数
    :param inX: 测试数据
    :param dataSet: 训练数据
    :param labels: 分类类别
    :param k: k值
    :return: 所属分类
    """

    dataSetSize = dataSet.shape[0]  #shape（m, n）m列n个特征
    diffMat = np.tile(inX, (dataSetSize, 1)) - dataSet
    sqDiffMat = diffMat ** 2
    sqDistances = sqDiffMat.sum(axis=1)
    distances = sqDistances ** 0.5  #欧式距离
    sortedDistIndicies = distances.argsort()  #排序并返回index

    classCount = {}
    for i in range(k):
        voteIlabel = labels[sortedDistIndicies[i]]
        classCount[voteIlabel] = classCount.get(voteIlabel, 0) + 1 #default 0

    sortedClassCount = sorted(classCount.items(), key=lambda d:d[1], reverse=True)
    return sortedClassCount[0][0]
```
算法的步骤上面有详细的介绍，上面的计算是矩阵运算，下面一个函数是代数运算，做个比较理解。

```
def classify_two(inX, dataSet, labels, k):
    m, n = dataSet.shape   # shape（m, n）m列n个特征
    # 计算测试数据到每个点的欧式距离
    distances = []
    for i in range(m):
        sum = 0
        for j in range(n):
            sum += (inX[j] - dataSet[i][j]) ** 2
        distances.append(sum ** 0.5)

    sortDist = sorted(distances)

    # k 个最近的值所属的类别
    classCount = {}
    for i in range(k):
        voteLabel = labels[ distances.index(sortDist[i])]
        classCount[voteLabel] = classCount.get(voteLabel, 0) + 1 # 0:map default
    sortedClass = sorted(classCount.items(), key=lambda d:d[1], reverse=True)
    return sortedClass[0][0]
```

有了上面的分类器，下面进行最简单的实验来预测一下：
```
def createDataSet():
    group = np.array([[1, 1.1], [1, 1], [0, 0], [0, 0.1]])
    labels = ['A', 'A', 'B', 'B']
    return group, labels
```
上面是一个简单的训练样本集。
```
if __name__ == '__main__':
    dataSet, labels = createDataSet()
    r = classify_two([0, 0.2], dataSet, labels, 3)
    print(r)
```
执行上述函数：可以看到输出B， [0 ,0.2]应该归入b类。

上面就是一个最简单的kNN分类器，下面有个例子。

### kNN用于判断婚恋网站中人的受欢迎程度
训练样本集中部分数据如下：
```
40920	8.326976	0.953952	3
14488	7.153469	1.673904	2
26052	1.441871	0.805124	1
75136	13.147394	0.428964	1
38344	1.669788	0.134296	1
```
第一列表示每年获得的飞行常客里程数， 第二列表示玩视频游戏所耗时间百分比， 第三类表示每周消费的冰淇淋公升数。第四列表示分类结果，1， 2， 3 分别是 不喜欢，魅力一般，极具魅力。

1. 将数据转换成numpy。
```
# 文本转换成numpy
def file2matrix(filepath="datingSet.csv"):
    dataSet = np.loadtxt(filepath)
    returnMat = dataSet[:, 0:-1]
    classlabelVector = dataSet[:, -1:]
    return returnMat, classlabelVector
```

2.  首先对数据有个感知，知道是哪些特征影响分类，进行可视化数据分析。
```
# 2， 3列数据进行分析
def show_2_3_fig():
    data, cls = file2matrix()
    fig = plt.figure()
    ax = fig.add_subplot(111)
    ax.scatter(data[:, 1], data[: ,2], c=cls)
    plt.xlabel("playing game")
    plt.ylabel("Icm Cream")
    plt.show()
```
![image.png](http://upload-images.jianshu.io/upload_images/1794675-6335468443e2b541.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)    
如上图可以看到并无明显的分类。  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-b43552ce62995d87.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  

![image.png](http://upload-images.jianshu.io/upload_images/1794675-6edde0efc1e80707.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  

可以看到不同的人根据特征有明显的区分。因此可以使用kNN算法来进行分类和预测。

2. 由于后面要用到距离比较，因此数据之前的影响较大， 比如飞机里程和冰淇淋数目之间的差距太大。因此需要对数据进行**归一化处理**。
```
# 数据归一化
def autoNorm(dataSet):
    minVal = dataSet.min(0)
    maxVal = dataSet.max(0)
    ranges = maxVal - minVal

    normDataSet = np.zeros(dataSet.shape)
    m, n = dataSet.shape  # 行， 特征
    normDataSet = dataSet - minVal
    normDataSet = normDataSet / ranges
    return normDataSet, ranges, minVal
```

3. 衡量算法的准确性
knn算法可以用正确率或者错误率来衡量。错误率为0，表示分类很好。
因此可以将训练样本中的10%用于测试，90%用于训练。
```
# 定义测试算法的函数
def datingClassTest(h=0.1):
    hoRatio = h
    datingDataMat, datingLabels = file2matrix()
    normMat, ranges, minVals = autoNorm(datingDataMat)
    m, n = normMat.shape
    numTestVecs = int(m * hoRatio)  #测试数据行数
    errorCount = 0  # 错误分类数


    # 用前10%的数据做测试
    for i in range(numTestVecs):
        classifierResult = classify(normMat[i, :], normMat[numTestVecs:m, :], datingLabels[numTestVecs:m],  3)
        # print('the classifier came back with: %d,the real answer is: %d' % (int(classifierResult), int(datingLabels[i])))
        if classifierResult != datingLabels[i]:
            errorCount += 1
    print("the total error rate is: %f" % (errorCount / float(numTestVecs)))
```

调整不同的测试比例，对比结果。

4. 使用knn进行预测。
有了训练样本和分类器，对新数据可以进行预测。模拟数据并进行预测如下：
```
# 简单进行预测
def classifypersion():
    resultList = ["none", 'not at all','in small doses','in large doses']
    # 模拟数据
    ffmiles = 15360
    playing_game = 8.545204
    ice_name = 1.340429

    datingDataMat, datingLabels = file2matrix()
    normMat, ranges, minVals = autoNorm(datingDataMat)
    inArr = np.array([ffmiles, playing_game, ice_name])
    # 预测数据归一化
    inArr = (inArr - minVals) / ranges
    classifierResult = classify(inArr, normMat, datingLabels, 3)
    print(resultList[int(classifierResult)])
```

可以看到基本的得到所属的分类。

完成代码和数据请参考：  
[github:kNN](https://github.com/yunshuipiao/cheatsheets-ai-code/tree/master/machine_learning_algorithm/kNN)

### 总结
* kNN
* 监督学习
*  数据可视化
* 数据归一化，不影响计算





