



 # SWBOLG



[![GitHub forks](https://img.shields.io/github/forks/yunshuipiao/SWBlog.svg)](https://github.com/yunshuipiao/SWBlog)
[![GitHub forks](https://img.shields.io/github/stars/yunshuipiao/SWBlog.svg)](https://github.com/yunshuipiao/SWBlog)
[![GitHub forks](https://img.shields.io/github/license/yunshuipiao/SWBlog.svg)](https://github.com/yunshuipiao/SWBlog)


有所思有所悟，个人的技术博客

## 关于用Issue写博客的说明

经过研究，打算将所有博客移动到相关仓库的Issue， 包括新增博客；不仅如此，以后所有仓库都会按照该方法作为参考来记录。

对于我来说，用Issue来写博客有以下优点：

1. label 的支持：看到相关 label 就知道该文章属于哪一类
2. markdown 语法的支持
3. 图片的插入
4. 自带的评论系统，方便交流
5. 可以配合 Project 来管理博客



除一般的 label 外，还会有以下 label 进行扩展：

1.  **AMA**： ask me anything， 提出问题
2. **TODO， DOING**： 表示一个问题，一篇文章，一个特性的进度

 


## 文章目录

----

### machine learning 资料推荐
目前学习相关知识所看过的书籍，资料，视频，下面我认为相对较好的机器学习入门路径，仅供参考。  
(说明：推荐资料全英文，也强烈建议看英文材料。好处在于 看的慢，思考的多，留下的多）  
1. [Hands-On Machine Learning with Scikit-Learn and TensorFlow](http://shop.oreilly.com/product/0636920052289.do)    
大多数人以机器学习实战，西瓜书和统计学习方法作为入门材料，前一本书重工程，后两本重理论及推导，可以作为进阶书。而 Hands-On Machine Learning with Scikit-Learn and TensorFlow 的优势在于书籍比较新，且有github配套的源代码实现，书中涉及传统的机器学习方法及其sklearn实现。  
学习建议：先看一遍书，接着对照着github代码实现一遍，最后再看一遍书。    
[官方代码实现：python2](https://github.com/ageron/handson-ml)  
[我的实现：python3, 部分中文注释，更适合, 提供pdf下载，仅供学习](https://github.com/yunshuipiao/hands-on-ml-with-sklearn-tf-python3)

2. 在阅读上述书籍的过程中，对某个传统机器学习算法感兴趣，可以参考上面的机器学习博客自己实现。  

3. [A Neural Network in 11 lines of Python (Part 1)](http://iamtrask.github.io/2015/07/12/basic-python-network/)   
上述书籍最后大半内容是关于神经网络的了解和使用，tf实现。而这篇博客对我了解反向传播算法，实现最简单的神经网络有很大的帮助。  
其后续文章介绍梯度下降，dropout防止过拟合，非常值得阅读。  

4. [Neural Networks and Deep Learning](http://neuralnetworksanddeeplearning.com/index.html)    
后续可以加深对神经网络的了解和深入。这本书的第三章使用74行代码，不借助任何深度学习框架实现神经网络识别手写数字，有了第3步的基础，相信理解不难。    
[反向传播可视化和链式求导](https://space.bilibili.com/88461692/#/channel/detail?cid=26587)      
该连接下有几个深度学习介绍的视频，在阅读书籍的过程中可以随时观看视频，对理解反向传播及链式求导有可视化的讲解。    

5. kaggle项目实战   
此时可以将学到的知识用于解决实际问题， kaggle是最合适的地方。   
我完成了三个入门题目，分别是[titanic](https://www.kaggle.com/c/titanic), [house price](https://www.kaggle.com/c/house-prices-advanced-regression-techniques), [digit recognizer](https://www.kaggle.com/c/digit-recognizer)。   
官方有合适的入门解决方案，可以参考官方或者加入自己的解决方法，完成自己的结果提交，查看排名。    
[我的实现：https://github.com/yunshuipiao/sw-kaggle](https://github.com/yunshuipiao/sw-kaggle)

上述过程也是我自己学习的过程，其中[吴恩达:机器学习](https://www.bilibili.com/video/av9912938)的视频也可利用空闲时间观看，上述资料我看过大部分，因此认为比较适合作为入门资料推荐。

其他资料博客：
1. [reddit:learnmachinelearning](https://www.reddit.com/r/learnmachinelearning/)  
reddit机器学习板块，有很多初学者面临的问题及其解答，还有高质量的文章推荐。  
2. [cs229:machine learning](http://cs229.stanford.edu/syllabus.html)    
上述吴恩达机器学习课程的讲义， 相比视频，对原理和推导更加深入。  

3. 最后我的github：https://github.com/yunshuipiao  
此部分内容会保持更新，阅读晚比较好的资料也会更新补充(强调：阅读完)

也欢迎各位同学推荐自己阅读完的资料。

### Android  
* [Android之自定义AlertDialog完成Rxjava2倒计时](https://github.com/yunshuipiao/SWBlog/issues/11)
* [关于android 悬浮窗和自启动的设置, 以及获取系统的信息](https://github.com/yunshuipiao/SWBlog/issues/18)
* [关于android architecture Component的入门资料(二)](https://github.com/yunshuipiao/SWBlog/issues/17)
* [关于android architecture Component的最简单实践](https://github.com/yunshuipiao/SWBlog/blob/master/android/%E5%85%B3%E4%BA%8Eandroid%20architecture%20Component%E7%9A%84%E6%9C%80%E7%AE%80%E5%8D%95%E5%AE%9E%E8%B7%B5.md)
* [Android之监听来电，权限管理， 多语言方案，双卡拨号](https://github.com/yunshuipiao/SWBlog/issues/12)
* [Rxjava2的listener和响应式解惑](https://github.com/yunshuipiao/SWBlog/issues/14)
* [从谷歌官方例子看constraintlayout](https://github.com/yunshuipiao/SWBlog/issues/15)


### 函数式编程(FP)  
* [Android之纯函数-高阶函数简单介绍](https://github.com/yunshuipiao/SWBlog/issues/20)
* [Android之Kotlin高阶函数-柯里化-偏函数](https://github.com/yunshuipiao/SWBlog/issues/21)

### algorithm
* [最长递增子序列--动态规划和LCS(最长公共子序列)](https://github.com/yunshuipiao/SWBlog/issues/8)
* [无序数组中的第k大元素-快速排序和堆排序](https://github.com/yunshuipiao/SWBlog/issues/7)
* [二叉树的遍历之多种后序遍历](https://github.com/yunshuipiao/SWBlog/issues/6)

### python + scrapy

* [快上车，scrapy爬虫飙车找福利(一)](https://github.com/yunshuipiao/SWBlog/issues/23)
* [快上车，scrapy爬虫飙车找福利(二)](https://github.com/yunshuipiao/SWBlog/issues/24)
* [快上车，scrapy爬虫飙车找福利(三)]( https://github.com/yunshuipiao/SWBlog/issues/25)
* [在服务器的docker中运行scrapy](https://github.com/yunshuipiao/SWBlog/issues/22)

### docker
* [在服务器的docker中运行scrapy](https://github.com/yunshuipiao/SWBlog/issues/19)







