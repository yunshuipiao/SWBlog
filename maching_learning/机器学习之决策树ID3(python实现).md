机器学习中，决策树是一个预测模型；代表对象属性和对象值之间的一种映射关系。树中每个节点表示某个对象，而每个分叉表示某个可能的属性，每个叶子节点则对应从根节点到该叶子节点所经历的路径所表示的对象的值。决策树只有单一输出，若想要复数输出，可以建立独立的决策树以处理不同输入。
数据挖掘中常用到决策树，可以用于分析数据，也可以用于预测。

### 简单理解
![image.png](http://upload-images.jianshu.io/upload_images/1794675-a013d57c6ef804d2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  
如上图，
前两个是属性，可以记为`['no surfacing','flippers']`。则可以简单的构建决策树如下：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-9002927a79b2f111.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  
根据两个属性可以判断是否属于鱼类。

那么首先决定选择哪个属性作为最开始的分类？最简单的是ID3。改进的C4.5， CART后面再进行了解。

### 决策树和ID3
决策树与树结构类似，具有树形结构。每个内部节点表示一个属性的测试，每个分支代表一个测试输出，每个叶子节点代表一种类别。如上图一样。
分类树（决策树）常用于机器学习的分类，是一种监督学习方法。由树的分支对该类型的对象依靠属性进行分类。每个决策树可以依靠对源数据库分割进行数据测试，递归修剪树。知道一个单独的类被应用于某一分支，不能进行分割，递归完成。
特点：
* 多层次的决策树形式易于理解。
* 只适用于标称行数据，连续性数据处理的不好。

#### ID3算法
上面介绍，怎么在一系列属性中首先选择哪个属性进行分类。简单理解，如果哪个属性比较混乱，直接就可以得到所属类别。比如上面属性`水下是否可以生存`，不能生存的可以分类为 不是鱼。
那么怎么去量化，得到这个属性呢？
ID3算法的核心是**信息墒**，通过计算每个属性的信息增益，认为增益高的是好属性，易于分类。每次划分选取信息增益最高的属性作为划分标准，进行重复，直至生成一个能完美分类训练样历的决策树。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-f5b67bd9a3944dd4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  
上面信息增益的算法不是很好理解，后面看代码很容易。

### ID3算法和决策树的流程
1. 数据准备：需要对数值型数据进行离散化
2. ID3算法构建决策树：
 * 如果数据类别完全相同，则停止划分。
* 否则，继续划分：
    * 计算信息墒和信息增益来选择最好的数据集划分方法
    * 划分数据集
    * 创建分支节点
    * 对每个分支进行判定类别相同。相同停止划分，不同则按照上述方法进行划分。


### python代码实现

利用上面例子创建数据集
```
def createDataSet():
    dataSet = [[1, 1, 'yes'], [1, 1, 'yes'], [1, 0, 'no'], [0, 1, 'no'], [0, 1, 'no']]
    labels = ['no sufacing', 'flippers']
    return dataSet, labels
```

计算信息墒,对应第一个公式
```
def calcShannonEnt(dataSet):
    numEntries = len(dataSet)
    # 为分类创建字典
    labelCounts = {}
    for featVec in dataSet:
        currentLabel = featVec[-1]
        if currentLabel not in labelCounts.keys():
            labelCounts.setdefault(currentLabel, 0)
        labelCounts[currentLabel] += 1

    # 计算香农墒
    shannonEnt = 0.0
    for key in labelCounts:
        prob = float(labelCounts[key]) / numEntries
        shannonEnt += prob * math.log2(1 / prob)
    return shannonEnt
```

计算最大信息增益(公式2)， 划分数据集。
```
# 定义按照某个特征进行划分的函数 splitDataSet
# 输入三个变量（带划分数据集， 特征，分类值)
def splitDataSet(dataSet, axis, value):
    retDataSet = []
    for featVec in dataSet:
        if featVec[axis] == value:
            reduceFeatVec = featVec[:axis]
            reduceFeatVec.extend(featVec[axis + 1:])
            retDataSet.append(reduceFeatVec)
    return retDataSet  #返回不含划分特征的子集

#  定义按照最大信息增益划分数据的函数
def chooseBestFeatureToSplit(dataSet):
    numFeature = len(dataSet[0]) - 1
    print(numFeature)
    baseEntropy = calcShannonEnt(dataSet)
    bestInforGain = 0
    bestFeature = -1

    for i in range(numFeature):
        featList = [number[i] for number in dataSet] #得到某个特征下所有值
        uniqualVals = set(featList) #set无重复的属性特征值
        newEntrogy = 0

        #求和
        for value in uniqualVals:
            subDataSet = splitDataSet(dataSet, i, value)
            prob = len(subDataSet) / float(len(dataSet)) #即p(t)
            newEntrogy += prob * calcShannonEnt(subDataSet) #对各子集求香农墒

        infoGain = baseEntropy - newEntrogy #计算信息增益
        print(infoGain)

        # 最大信息增益
        if infoGain > bestInforGain:
            bestInforGain = infoGain
            bestFeature = i
    return bestFeature
```

简单测试：  
```
if __name__ == '__main__':
    dataSet, labels = createDataSet()
    r = chooseBestFeatureToSplit(dataSet)
    print(r)
# 输出
# 2
# 0.41997309402197514
# 0.17095059445466865
# 0
```
如上，可以看到共有两个属性`['no surfacing','flippers']`和其信息增益，因此选择较大的特征(下标0)对数据集进行划分(见开始图)，重复步骤，知道只剩下一个类别。

创建决策树构造函数
```
# 投票表决代码
def majorityCnt(classList):
    classCount = {}
    for vote in classList:
        if vote not in classCount.keys():
            classCount.setdefault(vote, 0)
        classCount[vote] += 1
    sortedClassCount = sorted(classCount.items(), key=lambda i:i[1], reverse=True)
    return sortedClassCount[0][0]

def createTree(dataSet, labels):
    classList = [example[-1] for example in dataSet]
    # print(dataSet)
    # print(classList)
    # 类别相同，停止划分
    if classList.count(classList[0]) == len(classList):
        return classList[0]

    # 判断是否遍历完所有的特征,是，返回个数最多的类别
    if len(dataSet[0]) == 1:
        return majorityCnt(classList)

    #按照信息增益最高选择分类特征属性
    bestFeat = chooseBestFeatureToSplit(dataSet) #分类编号
    bestFeatLabel = labels[bestFeat]  #该特征的label
    myTree = {bestFeatLabel: {}}
    del (labels[bestFeat]) #移除该label

    featValues = [example[bestFeat] for example in dataSet]
    uniqueVals = set(featValues)
    for value in uniqueVals:
        subLabels = labels[:]  #子集合
        #构建数据的子集合，并进行递归
        myTree[bestFeatLabel][value] = createTree(splitDataSet(dataSet, bestFeat, value), subLabels)
    return myTree
```
代码里面有注视，尝试去理解每一步的执行，对决策树有一个基本的了解。

```
if __name__ == '__main__':
    dataSet, labels = createDataSet()
    r = chooseBestFeatureToSplit(dataSet)
    # print(r)
    myTree = createTree(dataSet, labels)
    print(myTree)
#  --> {'no sufacing': {0: 'no', 1: {'flippers': {0: 'no', 1: 'yes'}}}}
```
可以看到输出结果是一个嵌套的字典，手动可以画出决策树，与开头的图相吻合。

#### 将决策树用于分类
构建决策树分类函数：
```
def classify(inputTree, featLabels, testVec):
    """
    :param inputTree: 决策树
    :param featLabels: 属性特征标签
    :param testVec: 测试数据
    :return: 所属分类
    """
    firstStr = list(inputTree.keys())[0] #树的第一个属性
    sendDict = inputTree[firstStr]

    featIndex = featLabels.index(firstStr)
    classLabel = None
    for key in sendDict.keys():

        if testVec[featIndex] == key:
            if type(sendDict[key]).__name__ == 'dict':
                classLabel = classify(sendDict[key], featLabels, testVec)
            else:
                classLabel = sendDict[key]
    return classLabel
```
可以看到函数分别根据属性值对测试数据进行一步一步分类，直至到叶子节点，得到正确的分类。

另外，可以将决策树进行存储，与kNN不一样的是，决策树构造好不用重复计算，下次可以直接使用.
```
def storeTree(inputTree,filename):
    import pickle
    fw=open(filename,'wb') #pickle默认方式是二进制，需要制定'wb'
    pickle.dump(inputTree,fw)
    fw.close()

def grabTree(filename):
    import pickle
    fr=open(filename,'rb')#需要制定'rb'，以byte形式读取
    return pickle.load(fr)
```

完整决策树代码请查看github：
[github:decision_tree](https://github.com/yunshuipiao/cheatsheets-ai-code/tree/master/machine_learning_algorithm/decision_tree)

### 总结
* 决策树： ID3， C4.5， CART
* 信息论：信息墒，信息增益
* python对象存储

参考资料：
[机器学习之决策树(ID3)算法与Python实现](http://blog.csdn.net/moxigandashu/article/details/71305273)
