之前简单介绍过决策树，这篇文章简单介绍一下GBDT(Gradient Boosting Decision Tree).

### Gradient Boosting

**决策树**是一种基本的分类与回归方法。决策树模型具有分类速度快，模型容易可视化的解释，但是同时是也有容易发生过拟合，虽然有剪枝，但也是差强人意。

**提升方法（boosting）**在分类问题中，它通过改变训练样本的权重（增加分错样本的权重，减小分队样本的的权重），学习多个分类器，并将这些分类器线性组合，提高分类器性能。
于是决策树与boosting结合产生许多算法，主要有提升树、GBDT等。

Gradient Boosting是一种Boosting方法，主要思想是：每一次建立模型是在之前建立模型损失函数的梯度下降方向。损失函数是评价模型性能(一般为拟合程度+正则项)，认为损失函数越小，性能越好。让损失函数持续下降，就能使模型不断改进提升性能，最好的方法就是使损失函数沿着梯度方向下降。
Gradient Boosting是一个框架，里面可以套入不同很多的算法

### Gradient Boosting Decision Tree
每一次建立树模型是在之前建立模型损失函数的梯度下降方向。即利用了损失函数的负梯度在当前模型的值作为回归问题提升树算法的残差近似值，去拟合一个回归树。

#### 决策树的子类
```
class DecisionTree(object):
    """Super class of RegressionTree and ClassificationTree.
 
    def __init__(self, min_samples_split=2, min_impurity=1e-7,
                 max_depth=float("inf"), loss=None):
        self.root = None  # Root node in dec. tree
        # Minimum n of samples to justify split
        self.min_samples_split = min_samples_split
        # The minimum impurity to justify split
        self.min_impurity = min_impurity
        # The maximum depth to grow the tree to
        self.max_depth = max_depth
        # Function to calculate impurity (classif.=>info gain, regr=>variance reduct.)
        # 切割树的方法，gini，方差等
        self._impurity_calculation = None
        # Function to determine prediction of y at leaf
        # 树节点取值的方法，分类树：选取出现最多次数的值，回归树：取所有值的平均值
        self._leaf_value_calculation = None
        # If y is one-hot encoded (multi-dim) or not (one-dim)
        self.one_dim = None
        # If Gradient Boost
        self.loss = loss
```
上面代码中有两个重要变量。`impurity_calculation`和`leaf_value_calculation`。
前者代表切割树的分类标准是什么。分类树就是基尼系数，回归树就是最小平方残差。
后者代表计算节点值的方法。分类树则切割数据集中数量最多的种类，回归树则计算切割数据集中所有的平均值。

classification Tree:
```
class ClassificationTree(DecisionTree):
    def _calculate_information_gain(self, y, y1, y2):
        # 交叉墒
        # Calculate information gain
        p = len(y1) / len(y)
        entropy = calculate_entropy(y)
        info_gain = entropy - p * \
                              calculate_entropy(y1) - (1 - p) * \
                                                      calculate_entropy(y2)
        # print("info_gain",info_gain)
        return info_gain
 
    def _majority_vote(self, y):
      # 计算节点，出现最多
        most_common = None
        max_count = 0
        for label in np.unique(y):
            # Count number of occurences of samples with label
            count = len(y[y == label])
            if count > max_count:
                most_common = label
                max_count = count
        # print("most_common :",most_common)
        return most_common
 
    def fit(self, X, y):
        self._impurity_calculation = self._calculate_information_gain
        self._leaf_value_calculation = self._majority_vote
        super(ClassificationTree, self).fit(X, y)
```

RegressionTree:
```
class RegressionTree(DecisionTree):
    def _calculate_variance_reduction(self, y, y1, y2):
        # 平方残差
        var_tot = calculate_variance(y)
        var_1 = calculate_variance(y1)
        var_2 = calculate_variance(y2)
        frac_1 = len(y1) / len(y)
        frac_2 = len(y2) / len(y)
 
        # Calculate the variance reduction
        variance_reduction = var_tot - (frac_1 * var_1 + frac_2 * var_2)
 
        return sum(variance_reduction)
 
    def _mean_of_y(self, y):
        # 平均值
        value = np.mean(y, axis=0)
        return value if len(value) > 1 else value[0]
 
    def fit(self, X, y):
        self._impurity_calculation = self._calculate_variance_reduction
        self._leaf_value_calculation = self._mean_of_y
        super(RegressionTree, self).fit(X, y)
```

----

### GDBT应用--回归和分类
分类：每棵树拟合当前整个模型的损失函数的负梯度，构建新的树加到当前模型中形成新模型，下一棵树拟合新模型的损失函数的负梯度。
回归：每一棵树拟合当前整个模型的残差，构建新的树加到当前模型中形成新模型，下一棵树拟合新模型的损失函数的负梯度。

### GBDT的原理
#### 如何在不改变原有模型的结构上提升模型的拟合能力
假设存在样本集(x1, y1), (x2, y2)....， 然后用一个模型f(x)去拟合数据，使的平方损失函数:  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-f8afa5af3ec9370e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/240)  
最小。
但是发现虽然拟合效果很好，但是仍有差距。
既然不能更改原来模型的参数，意味着要在原来模型的基础上做改善，直观就是建议一个新的模型fx来拟合Fx为完全拟合真是样本的残差， 即y-F(x)。
对于每个样本得到的拟合样本集变为：
(x1, y1 - F(x1)), (x2, y2 - F(x2)), (x3, y3 - F(x3)),...,  (xn, yn - F(xn))

#### GBDT构建新的特征
特征决定模型上届。如果能够将数据表达成为线性可分的数据，那么使用简单的线性模型可以得到更好的效果。GBDT构建新的特征也是使特征更好的表达数据。
`在预测Facebook广告点击中，使用一种将决策树与逻辑回归结合在一起的模型，其优于其他方法，超过3％。`  
主要思想：GBDT每棵树的路径直接作为LR输入特征使用。  
`用已有特征训练GBDT模型，然后利用GBDT模型学习到的树来构造新特征，最后把这些新特征加入原有特征一起训练模型。构造的新特征向量是取值0/1的，向量的每个元素对应于GBDT模型中树的叶子结点。当一个样本点通过某棵树最终落在这棵树的一个叶子结点上，那么在新特征向量中这个叶子结点对应的元素值为1，而这棵树的其他叶子结点对应的元素值为0。新特征向量的长度等于GBDT模型里所有树包含的叶子结点数之和。`
![image.png](http://upload-images.jianshu.io/upload_images/1794675-5c849e2537b05ef6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/640)  
例子1：上图有两棵树，左树有三个叶子节点，右树有两个叶子节点，最终的特征即为五维的向量。对于输入x，假设他落在左树第一个节点，编码[1,0,0]，落在右树第二个节点则编码[0,1]，所以整体的编码为[1,0,0,0,1]，这类编码作为特征，输入到线性分类模型（LR or FM）中进行分类。


上面简单介绍了原理，笔者也是不太懂。后续学到再补充。
代码这里就不做实现，有需要请查看sklearn的工具包。

参考资料：  
[GBDT原理及利用GBDT构造新的特征-Python实现](http://blog.csdn.net/shine19930820/article/details/71713680)  
[机器学习-一文理解GBDT的原理-20171001](https://zhuanlan.zhihu.com/p/29765582)  
[GBDT的python源码实现](https://zhuanlan.zhihu.com/p/32181306)






