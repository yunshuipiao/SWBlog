`朴素贝叶斯（Naive Bayesian）是最为广泛使用的分类方法，它以概率论为基础，是基于贝叶斯定理和特征条件独立假设的分类方法。`

### 原理
朴素贝叶斯（Naive Bayesian）是基于贝叶斯定理和特征条件独立假设原则的分类方法。通过给出的特征计算分类的概率，选取概率大的情况进行分类。也是基于**概率论**的一种机器学习分类方法。分类目标确定，属于监督学习。
 
```
通过概率来衡量事件发生的可能性。概率论和统计学恰好是两个相反的概念，统计学是抽取部分样本进行统计来估算总体的情况，而概率论是通过总体情况来估计单个事件或者部分事情的发生情况。因此，概率论需要已知的数据去预测未知的事件。 
例如，我们看到天气乌云密布，电闪雷鸣并阵阵狂风，在这样的天气特征(F)下，我们推断下雨的概率比不下雨的概率大，也就是p(下雨)>p(不下雨),所以认为待会儿会下雨。这个从经验上看对概率进行判断。 
而气象局通过多年长期积累的数据，经过计算，今天下雨的概率p(下雨)=85%,p(不下雨)=15%,同样的，p(下雨）>p(不下雨)，因此今天的天气预报肯定预报下雨。这是通过一定的方法计算概率从而对下雨事件进行判断。
```

为什么叫**朴素**贝叶斯：简单，易于操作，基于特征独立性假设，也即各个特征彼此独立，互相不影响发生。

#### 条件概率
某个事件已发生的情况下另外一个事件发生的概率。计算公式如下：**P(A|B)=P(A∩B) / P(B)**
简单理解：画维恩图，两个圆圈相交的部分就是A发生B也发生了，因为求的是B发生下A发生的概率。B相当于一个新的样本空间。AB/B即可。

概率相乘法则：P(A∩B)=P(A)P(B|A) or P(A∩B)=P(B)P(A|B)
独立事件的概率：P(A∩B)=P(A)P(B)

#### 贝叶斯定理
如果有穷k个互斥事件，B1， B2，，，Bk 并且 P(B1)+P(B2)+⋅⋅⋅+P(Bk)=1和一个可以观测到的事件A，那么有：   ![image.png](http://upload-images.jianshu.io/upload_images/1794675-3dfea3e2b4d767df.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)

### 分类原理
基于概率论，二分类问题如下：
如果p1 > p2, 分入类别1； 否则分入类别2。

其次，贝叶斯定理，有
`p(ci|x,y) = p(x,y|ci) * p(ci) / p(x,y)`
x, y 表示特征变量，如下例子中的单词。Ci表示类别。p(ci |  x, y) 即表示在特征x， y出现的情况下，分入类别Ci的概率。结合如上：
p(ci |  x, y) > p(cj |  x, y), 分入类别i， 否则分入类别j。

`贝叶斯定理最大的好处是可以用已知的三个概率去计算未知的概率，而如果仅仅是为了比较p(ci|x,y)和p(cj|x,y)的大小，只需要已知两个概率即可，分母相同，比较p(x,y|ci)p(ci)和p(x,y|cj)p(cj)即可。`

#### 特征条件独立性假设原则
朴素贝叶斯常用与对文档分类。根据文档中出现的词汇，判断文章属于什么类别。将词汇出现的特征条件用**词向量W**表示，由多个值组成，值的个数和训练集中的词汇表个数相同。
上面的贝叶斯公式可以表示为：
  `p(ci|ω)=p(ω|ci) * p(ci) / p(ω)`
各个单词的出现不会相互影响，则`p(ω|ci) = p(ω0|ci)*p(ω1|ci)*...* p(ωk|ci)`

### 算法实现

```
import numpy as np
np.seterr(divide='ignore', invalid='ignore')  #消除向量中除以0的警告
# 获取数据
def loadDataSet():
    postingList = [['my', 'dog', 'has', 'flea', 'problems', 'help', 'please'],
                   ['maybe', 'not', 'take', 'him', 'to', 'dog', 'park', 'stupid'],
                   ['my', 'dalmation', 'is', 'so', 'cute', 'I', 'love', 'him'],
                   ['stop', 'posting', 'stupid', 'worthless', 'garbage'],
                   ['mr', 'licks', 'ate', 'my', 'steak', 'how', 'to', 'stop', 'him'],
                   ['quit', 'buying', 'worthless', 'dog', 'food', 'stupid']]
    classVec = [0, 1, 0, 1, 0, 1] #1表示侮辱性言论，0表示正常
    return postingList, classVec
```

根据文档词汇构建词向量：
```
def createVocabList(dataSet):
    vocabSet = set([])
    for document in dataSet:
        vocabSet = vocabSet | set(document)
    return list(vocabSet)

# 对输入的词汇表构建词向量
def setOfWords2Vec(vocabList, inputSet):
    returnVec = np.zeros(len(vocabList)) #生成零向量的array
    for word in inputSet:
        if word in vocabList:
            returnVec[vocabList.index(word)] = 1 #有单词，该位置填充1
        else:
            print("the word: %s is not in my Vocabulary" % word)
            # pass
    return returnVec  #返回0，1的向量

if __name__ == '__main__':
    listPosts, listClasses = loadDataSet()
    myVocabList = createVocabList(listPosts)
    print(myVocabList)
 
```
输出结果如下：
`['flea', 'ate', 'how', 'licks', 'quit', 'problems', 'dog', 'I', 'garbage', 'help', 'is', 'cute', 'steak', 'to', 'worthless', 'please', 'has', 'posting', 'buying', 'love', 'food', 'so', 'my', 'take', 'dalmation', 'stop', 'park', 'not', 'stupid', 'him', 'mr', 'maybe']`, 表示不同类别言论去重后得到的词向量。
`[ 1.  0.  0.  0.  0.  0.  1.  0.  0.  0.  1.  1.  1.  0.  0.  0.  1.  0. 0.  0.  0.  0.  0.  0.  0.  0.  1.  0.  0.  0.  0.  0.] `: 表示词汇集1中的单词是否在词向量中出现。

如上，这个方法只记录了每个词是否出现，并没有记录出现次数，成为**词集模型**。如果记录词出现的次数，这样的词向量构建方法称为**词袋模型**，如下。本文只使用词集模型。
```
# 词袋模型
def bagofWords2VecMN(vocabList, inputSet):
    returnVec = [0] * len(vocabList)
    for word in inputSet:
        if word in vocabList:
            returnVec[vocabList.index(word)] += 1
    return vocabList #返回非负整数的词向量
```

运用词向量计算概率：
```
def trainNB0(trainMatrix, trainCategory):
    numTrainDocs = len(trainMatrix)  #文档数目
    numWord = len(trainMatrix[0])  #词汇表数目
    print(numTrainDocs, numWord)
    pAbusive = sum(trainCategory) / len(trainCategory) #p1, 出现侮辱性评论的概率 [0, 1, 0, 1, 0, 1]
    p0Num = np.zeros(numWord)
    p1Num = np.zeros(numWord)

    p0Demon = 0
    p1Demon = 0

    for i in range(numTrainDocs):
        if trainCategory[i] == 0:
            p0Num += trainMatrix[i] #向量相加
            p0Demon += sum(trainMatrix[i]) #向量中1累加其和
        else:
            p1Num += trainMatrix[i]
            p1Demon += sum(trainMatrix[i])
    p0Vec = p0Num / p0Demon
    p1Vec = p1Num / p1Demon

    return p0Vec, p1Vec, pAbusive

if __name__ == '__main__':
    listPosts, listClasses = loadDataSet()
    myVocabList = createVocabList(listPosts)
    trainMat = []
    trainMat = []
    for postinDoc in listPosts:
        trainMat.append(setOfWords2Vec(myVocabList, postinDoc))
    print(trainMat)
    p0Vec, p1Vec, pAbusive = trainNB0(trainMat, listClasses)
    print(p0Vec, p1Vec, pAbusive)
```
输出结果稍微有点多，慢慢来看：
`trainMat`:表示数据中六个给定的特征在词集模型中的出现情况。
```
array([ 0.,  0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  1.,  1.,  0.,
        0.,  0.,  1.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,
        0.,  0.,  0.,  0.,  1.,  1.]), array([ 0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  1.,
        0.,  1.,  0.,  1.,  1.,  1.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,
        0.,  0.,  1.,  0.,  0.,  0.]), array([ 1.,  0.,  0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,
        1.,  0.,  0.,  1.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,
        1.,  1.,  0.,  0.,  0.,  1.]), array([ 0.,  1.,  1.,  1.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,
        0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  0.,  1.,  0.,  0.,
        0.,  0.,  0.,  0.,  0.,  0.]), array([ 0.,  1.,  0.,  0.,  0.,  0.,  0.,  1.,  0.,  1.,  0.,  0.,  0.,
        0.,  0.,  0.,  1.,  0.,  0.,  1.,  0.,  1.,  0.,  0.,  0.,  0.,
        0.,  0.,  1.,  1.,  0.,  1.]), array([ 0.,  0.,  1.,  0.,  1.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,
        0.,  0.,  0.,  0.,  1.,  0.,  0.,  0.,  0.,  0.,  0.,  1.,  1.,
        0.,  0.,  0.,  0.,  0.,  0.])]
```

`print(numTrainDocs, numWord)`: 6 32 (6个文档，一共32个词汇)
`print(p0Vec, p1Vec, pAbusive)`：pAbusive是文档中是侮辱性言论的概率，为0.5。
而`p0Vec`表示类别0（非侮辱言论）中的词在词向量中出现的概率：
```
[ 0.  0.04166667  0.04166667  0.04166667  0.04166667  0.
  0.08333333  0.04166667  0.          0.04166667  0.          0.04166667
  0.          0.04166667  0.          0.          0.04166667  0.04166667
  0.04166667  0.04166667  0.04166667  0.          0.          0.04166667
  0.04166667  0.04166667  0.          0.125       0.          0.04166667
  0.04166667  0.04166667] 
```

#### 算法的改进：
1. 部分概率为0，用于上面计算独立特征概率相乘是永远为0.因此，将所有词出现的次数初始化为1，某类词项初始化为2.
2. 由于计算得到的概率太小，不断的相乘可能会导致结果溢出。因此对其取对数，单调性相同，不会影响最后对结果的比较。函数如下：

```
def trainNB1(trainMatrix, trainCategory):
    numTrainDocs = len(trainMatrix)  #文档数目
    numWord = len(trainMatrix[0])  #词汇表数目
    pAbusive = sum(trainCategory) / len(trainCategory) #p1, 出现侮辱性评论的概率
    p0Num = np.ones(numWord)  #修改为1
    p1Num = np.ones(numWord)

    p0Demon = 2 #修改为2
    p1Demon = 2

    for i in range(numTrainDocs):
        if trainCategory[i] == 0:
            p0Num += trainMatrix[i] #向量相加
            p0Demon += sum(trainMatrix[i]) #向量中1累加其和
        else:
            p1Num += trainMatrix[i]
            p1Demon += sum(trainMatrix[i])
    p0Vec = np.log(p0Num / p0Demon)  #求对数
    p1Vec = np.log(p1Num / p1Demon)

    return p0Vec, p1Vec, pAbusive
```

注意：这里得到p0Vec可能是没有规律的，但其对最后的概率比较没有影响。


#### 运用分类器函数进行文档分类
```
def classifyNB(vec2Classify, p0Vc,  p1Vc, pClass1):
    p1 = sum(vec2Classify * p1Vc) * pClass1
    p0 = sum(vec2Classify * p0Vc) * (1-pClass1)
    # p1 = sum(vec2Classify * p1Vc) + np.log(pClass1)    #取对数，防止结果溢出
    # p0 = sum(vec2Classify * p0Vc) + np.log(1 - pClass1)
    if p1 > p0:
        return 1
    else:
        return 0
```
解释一下：vec2Classify是所需分类文档的词量。根据公式 `p(ci|ω)=p(ω|ci)p(ci) / p(ω)`， 已知特征向量求分类的概率等于 `p(ω|ci)p(ci)`。忽略分母：
```
p(ci)好求，用样本集中，ci的数量/总样本数即可 
p(ω|ci)由于各个条件特征相互独立且地位相同，`p(ω|ci)=p(w0|ci)p(w1|ci)p(w2|ci)......p(wN|ci)`，可以分别求p(w0|ci),p(w1|ci),p(w2|ci),......,p(wN|ci)，从而得到p(ω|ci)。  
而求p(ωk|ci)也就变成了求在分类类别为ci的文档词汇表集合中，单个词项ωk出现的概率。
```

### 测试分类函数
使用两个不同的样本来测试分类函数：
```

# 构造样本测试
def testingNB():
    listPosts, listClasses = loadDataSet()
    myVocabList = createVocabList(listPosts)
    trainMat = []
    for postinDoc in listPosts:
        trainMat.append(setOfWords2Vec(myVocabList, postinDoc))
    p0v, p1v, pAb = trainNB0(trainMat, listClasses)
    # print(p0v, p1v, pAb)
    testEntry = ['love']
    thisDoc = setOfWords2Vec(myVocabList, testEntry)
    print(testEntry, 'classified as', classifyNB(thisDoc, p0v, p1v, pAb))

    testEntry = ['stupid', 'garbage']
    thisDoc = (setOfWords2Vec(myVocabList, testEntry))
    print(testEntry, 'classified as:', classifyNB(thisDoc, p0v, p1v, pAb))

if __name__ == '__main__':
    testingNB()
```
观察结果，可以看到将两个文档正确的分类。
完整代码请查看：

[github:naive_bayes](https://github.com/yunshuipiao/cheatsheets-ai-code/tree/master/machine_learning_algorithm/Naive_Bayes)


### 总结
* 朴素贝叶斯分类
* 条件概率
* 贝叶斯定理
* 特征条件独立性假设原则
* 根据文档构建词向量
* 词集模型和词袋模型
* 概率为0，方便计算的改进和防止溢出的取对数改进


参考文章：  
[机器学习之朴素贝叶斯(NB)分类算法与Python实现](http://blog.csdn.net/moxigandashu/article/details/71480251)





