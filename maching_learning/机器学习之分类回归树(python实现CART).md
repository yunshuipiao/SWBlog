之前有文章介绍过决策树（ID3）。简单回顾一下：ID3每次选取最佳特征来分割数据，这个最佳特征的判断原则是通过信息增益来实现的。按照某种特征切分数据后，该特征在以后切分数据集时就不再使用，因此存在切分过于迅速的问题。ID3算法还不能处理连续性特征。
下面简单介绍一下其他算法：
![屏幕快照 2018-03-03 14.05.44.png](https://user-gold-cdn.xitu.io/2018/3/4/161efc4f7b5e4af2?w=620&h=294&f=png&s=63995)

### CART 分类回归树
CART是Classification And Regerssion Trees的缩写，既能处理分类任务也能做回归任务。  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-cbbe8cb40068a5da.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  
CART树的典型代表时二叉树，根据不同的条件将分类。  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-003324956049bdac.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  

CART树构建算法
与ID3决策树的构建方法类似，直接给出CART树的构建过程。首先与ID3类似采用字典树的数据结构，包含以下4中元素：
* 待切分的特征
* 待切分的特征值
* 右子树。当不再需要切分的时候，也可以是单个值
* 左子树，类似右子树。
----
过程如下：
1. 寻找最合适的分割特征
2. 如果不能分割数据集，该数据集作为一个叶子节点。
3. 对数据集进行二分割
4. 对分割的数据集1重复1， 2，3 步，创建右子树。
5. 对分割的数据集2重复1， 2，3 步，创建左子树。

明显的递归算法。

通过数据过滤的方式分割数据集，返回两个子集。
```
def splitDatas(rows, value, column):
    # 根据条件分离数据集(splitDatas by value, column)
    # return 2 part（list1, list2）

    list1 = []
    list2 = []

    if isinstance(value, int) or isinstance(value, float):
        for row in rows:
            if row[column] >= value:
                list1.append(row)
            else:
                list2.append(row)
    else:
        for row in rows:
            if row[column] == value:
                list1.append(row)
            else:
                list2.append(row)
    return list1, list2
```

### 划分数据点
创建二进制决策树本质上就是递归划分输入空间的过程。
![image.png](https://user-gold-cdn.xitu.io/2018/3/4/161efc4f7b490ec6?w=620&h=332&f=png&s=115995)

代码如下：
```
# gini()
def gini(rows):
    # 计算gini的值(Calculate GINI)

    length = len(rows)
    results = calculateDiffCount(rows)
    imp = 0.0
    for i in results:
        imp += results[i] / length * results[i] / length
    return 1 - imp
```

### 构建树
```
def buildDecisionTree(rows, evaluationFunction=gini):
    # 递归建立决策树， 当gain=0，时停止回归
    # build decision tree bu recursive function
    # stop recursive function when gain = 0
    # return tree
    currentGain = evaluationFunction(rows)
    column_lenght = len(rows[0])
    rows_length = len(rows)

    best_gain = 0.0
    best_value = None
    best_set = None

    # choose the best gain
    for col in range(column_lenght - 1):
        col_value_set = set([x[col] for x in rows])
        for value in col_value_set:
            list1, list2 = splitDatas(rows, value, col)
            p = len(list1) / rows_length
            gain = currentGain - p * evaluationFunction(list1) - (1 - p) * evaluationFunction(list2)
            if gain > best_gain:
                best_gain = gain
                best_value = (col, value)
                best_set = (list1, list2)
    dcY = {'impurity': '%.3f' % currentGain, 'sample': '%d' % rows_length}
    #
    # stop or not stop

    if best_gain > 0:
        trueBranch = buildDecisionTree(best_set[0], evaluationFunction)
        falseBranch = buildDecisionTree(best_set[1], evaluationFunction)
        return Tree(col=best_value[0], value = best_value[1], trueBranch = trueBranch, falseBranch=falseBranch, summary=dcY)
    else:
        return Tree(results=calculateDiffCount(rows), summary=dcY, data=rows)
```
上面代码的功能是先找到数据集切分的最佳位置和分割数据集。之后通过递归构建出上面图片的整棵树。

### 剪枝
在决策树的学习中，有时会造成决策树分支过多，这是就需要去掉一些分支，降低过度拟合。通过决策树的复杂度来避免过度拟合的过程称为剪枝。
后剪枝需要从训练集生成一棵完整的决策树，然后自底向上对非叶子节点进行考察。利用测试集判断是否将该节点对应的子树替换成叶节点。
代码如下：
```
def prune(tree, miniGain, evaluationFunction=gini):
    # 剪枝 when gain < mini Gain, 合并（merge the trueBranch and falseBranch）
    if tree.trueBranch.results == None:
        prune(tree.trueBranch, miniGain, evaluationFunction)
    if tree.falseBranch.results == None:
        prune(tree.falseBranch, miniGain, evaluationFunction)

    if tree.trueBranch.results != None and tree.falseBranch.results != None:
        len1 = len(tree.trueBranch.data)
        len2 = len(tree.falseBranch.data)
        len3 = len(tree.trueBranch.data + tree.falseBranch.data)

        p = float(len1) / (len1 + len2)

        gain = evaluationFunction(tree.trueBranch.data + tree.falseBranch.data) - p * evaluationFunction(tree.trueBranch.data) - (1 - p) * evaluationFunction(tree.falseBranch.data)

        if gain < miniGain:
            tree.data = tree.trueBranch.data + tree.falseBranch.data
            tree.results = calculateDiffCount(tree.data)
            tree.trueBranch = None
            tree.falseBranch = None
```
当节点的gain小于给定的 mini Gain时则合并这两个节点.。

最后是构建树的代码：

```
if __name__ == '__main__':
    dataSet = loadCSV()
    decisionTree = buildDecisionTree(dataSet, evaluationFunction=gini)
    prune(decisionTree, 0.4)
    test_data = [5.9,3,4.2,1.5]
    r = classify(test_data, decisionTree)
    print(r)
```
可以打印decisionTree可以构建出如如上的图片中的决策树。
后面找一组数据测试看能否得到正确的分类。

完整代码和数据集请查看：  
[github：CART](https://github.com/yunshuipiao/cheatsheets-ai-code/tree/master/machine_learning_algorithm/decision_tree)

总结：
* CART决策树
* 分割数据集
* 递归创建树

参考文章：  
[CART分类回归树分析与python实现](http://blog.csdn.net/u010665216/article/details/78410384)  
[CART决策树(Decision Tree)的Python源码实现](https://zhuanlan.zhihu.com/p/32164933)








