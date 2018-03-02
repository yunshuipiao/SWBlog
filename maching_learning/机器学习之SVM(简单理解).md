`应该对现有流行并将继续流行下去的分类模型有深刻的了解。随机森林和支持向量机(svm)`   
上篇文章简单介绍了随机森林，这篇文章简单介绍一下支持向量机(svm)。
偏重应用，轻数学解释推导。
### 线性分类器
一个非常简单的分类问题。  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-66edd89d34394cc9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)   
用一条直线，将两种颜色的点分开，如图所示(可以有无数条这样的直线)。
假如将黑点记作-1， 白点+1。直线f(x) = wx+b。这里的x， w是向量，其实也可以写成这种形式。f(x) = w1x1 + w2x2 + ... + wnxn + b(w0x0)，当向量x的维度是2的时候，f(x)表示二维空间中的一条直线，当x的维度等于3的时候，f(x)表示3维空间中的一个平面；当x的维度n大于3的时候，表示的是n维空间中的n-1维超平面。
当有一个新的点需要预测分类的时候，就用sgn(f(x))。sgn表示符号函数，当f(x) > 0, 属于黑点；否则属于白点。

但是，对于无数条可能的直线，哪条效果是最好的？
 ![image.png](http://upload-images.jianshu.io/upload_images/1794675-d5b916992a50f6a0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)   
直观感受，让这条直线到给定样本中最近的点距离最远，如下图的两种分法，2效果好。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-aec865d7eefc4733.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)   
从直观上来说，分割的间隙越大越好，把两个类别的点分得越开越好。
在**SVM中，成为Maximum Marginal**， 是svm的一个理论基础之一。
选择是的空隙最大的函数是有很多道理的。比如从概率的角度讲， 就使的置信度最小的点置信度最大。
上图中被红色和蓝色的线圈出来的点就是所谓的支持向量(support vector)。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-746b804a38914487.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)     
![image.png](http://upload-images.jianshu.io/upload_images/1794675-f0fe57750654c314.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)     
上图就是对间隙的一个描述。Classifier Boundary就是fx， 红蓝线就是support vertor 所在面。红色，蓝色线之间的间隙就是要最大化的分类间的间隙。
直接给出M的公式：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-67885fcf40b87e53.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)   
另外支持向量位于wx+b = 1和wx+b=-1的直线上。在前面乘上一个该点所属的类别(1或者-1)， 就可以得到支持向量的表达式 y(wx+b) =1， 简单的将支持向量表示出来。
当确定支持向量后，分割函数也随之确定，两个问题等价。得到支持向量还有一个作用，让支持向量后面的那些点不参与计算。
省略一堆公式。  
[机器学习中的算法(2)-支持向量机(SVM)基础](http://www.cnblogs.com/LeftNotEasy/archive/2011/05/02/basic-of-svm.html)

###  线性不可分
由于线性可分的情况太少，下图是一个典型的线性不可分的分类图。  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-585249bf5d1dabf2.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)        
![image.png](http://upload-images.jianshu.io/upload_images/1794675-10fa7d08354b32e9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)   
要得到这种情况下的分类器，有两种方式。
* 用图示曲线将其完全分开
* 另一种还是直线， 不用保证可分性, 包容分错。

针对第二种情况，假如惩罚函数，使的分错的情况越合理越好。可以为分错的点加上一点惩罚，对一个分错的点的**惩罚函数**就是这个点到其正确位置的距离：  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-516bd3333b312c7f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)   
上图中，蓝色，红色的直线分别为支持向量所在的边界，绿色的线为决策函数。紫色的线表示分错的点到其相应的决策面的距离，这样可以在原函数上加上一个惩罚函数(蓝色部分)  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-640044f1fbc7b8fd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)   

### 核函数
刚刚提到，可以使用非线性的方法来完美划分。
让空间从原来的线性空间变成一个更高维的空间，在这个高维的线性空间下，在用一个超平面进行分割。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-8af495a97b3c6873.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)   
可以将上图的点映射到一个三维空间（z1, z2, z3），并对映射后的坐标旋转就可以得到一个线性可分的集。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-bd031ba8acadfb38.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)   
![image.png](http://upload-images.jianshu.io/upload_images/1794675-34209cb584a3b4d0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)   

上面就是对svm的简单理解，笔者也不太懂，只是做个记录，期待后面有能力填坑。















