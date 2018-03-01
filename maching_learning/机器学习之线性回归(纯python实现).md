线性回归是机器学习中最基本的一个算法，大部分算法都是由基本的算法演变而来。本文着重用很简单的语言说一下线性回归。

### 线性回归
包括一元线性回归和多元线性回归，一元指的是只有一个x和一个y。通过一元对于线性回归有个基本的理解。

一元线性回归就是在数据中找到一条直线，以最小的误差来(Loss)来拟和数据。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-172c4070b436ed96.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)

上面提到的误差可以这样表示，假设那条直线如下图：  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-e43cb8ebc64a9164.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/320)


理想情况是所有点都落在直线上。退一步，希望所有点离直线的距离最近。简单起见，将距离求平方，误差可以表示为：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-8d9b185e85a22c5a.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

上面的i表示第i个数据。一般情况下对Loss求平均，来当作最终的损失。

### 最小化误差
找到最能拟合数据的直线，也就是最小化误差。

#### 最小二乘法
上述公式只有m, b未知，因此可以看最一个m， b的二次方程，求Loss的问题就转变成了求极值问题。
这里不做详细说明。

另每个变量的偏导数为0， 求方程组的解。  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-a60727d0dc3ad827.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620) 
![image.png](http://upload-images.jianshu.io/upload_images/1794675-02b7f07fdd600702.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/620)  
求出m，b即可得到所要的直线。

### 梯度下降法
没有梯度下降就没有现在的深度学习。
最小二乘法可以一步到位，直接求出m，b。在大部分公式中是无法简单的直接计算的。而梯度下降通过一步一步的迭代，慢慢的去靠近那条最优的直线，因此需要不断的优化。
Loss的函数图像可以类比成一个碗。  
![image.png](http://upload-images.jianshu.io/upload_images/1794675-9b2c05037f1a5bf1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)  
要求的最小值就在碗底，随意给出一点往下走，即沿着下降最快的方向（梯度）往下走，定义每一步移动的步长，移动的次数来逼近最优值。
下面用算法来实现：

初始化：
```
def init_data():
    data = np.loadtxt('data.csv', delimiter=',')
    return data

def linear_regression():
    learning_rate = 0.01 #步长
    initial_b = 0
    initial_m = 0
    num_iter = 1000 #迭代次数

    data = init_data()
    [b, m] = optimizer(data, initial_b, initial_m, learning_rate, num_iter)
    plot_data(data,b,m)
    print(b, m)
    return b, m
```

优化器去做梯度下降：
```
def optimizer(data, initial_b, initial_m, learning_rate, num_iter):
    b = initial_b
    m = initial_m

    for i in range(num_iter):
        b, m = compute_gradient(b, m, data, learning_rate)
        # after = computer_error(b, m, data)
        if i % 100 == 0:
            print(i, computer_error(b, m, data)) # 损失函数，即误差
    return [b, m]
```

每次迭代计算梯度做参数更新：
```
def compute_gradient(b_cur, m_cur, data, learning_rate):
    b_gradient = 0
    m_gradient = 0

    N = float(len(data))
    #
    # 偏导数， 梯度
    for i in range(0, len(data)):
        x = data[i, 0]
        y = data[i, 1]

        b_gradient += -(2 / N) * (y - ((m_cur * x) + b_cur))
        m_gradient += -(2 / N) * x * (y - ((m_cur * x) + b_cur)) #偏导数

    new_b = b_cur - (learning_rate * b_gradient)
    new_m = m_cur - (learning_rate * m_gradient)
    return [new_b, new_m]
```

Loss值的计算：
```
def computer_error(b, m, data):
    totalError = 0
    x = data[:, 0]
    y = data[:, 1]
    totalError = (y - m * x - b) ** 2
    totalError = np.sum(totalError, axis=0)
    return totalError / len(data)
```
执行函数计算结果：
```
if __name__ == '__main__':
    linear_regression()
```
运算结果如下：
```
0 3.26543633854
100 1.41872132865
200 1.36529867423
300 1.34376973304
400 1.33509372632
500 1.33159735872
600 1.330188348
700 1.32962052693
800 1.32939169917
900 1.32929948325
1.23930380135 1.86724196887
```
可以看到，随着迭代次数的增加，Loss函数越来越逼近最小值，而m，b也越来越逼近最优解。

### 注意：
在上面的方法中，还是通过计算Loss的偏导数来最小化误差。上述方法在梯度已知的情况下，即肯定按照下降最快的方法到达碗底。那么在公式非常难以计算的情况下怎么去求最优解。此时求偏导数可以使用导数的定义，看另一个函数。
```
def optimizer_two(data, initial_b, initial_m, learning_rate, num_iter):
    b = initial_b
    m = initial_m

    while True:
        before = computer_error(b, m, data)
        b, m = compute_gradient(b, m, data, learning_rate)
        after = computer_error(b, m, data)
        if abs(after - before) < 0.0000001:  #不断减小精度
            break
    return [b, m]

def compute_gradient_two(b_cur, m_cur, data, learning_rate):
    b_gradient = 0
    m_gradient = 0

    N = float(len(data))

    delta = 0.0000001

    for i in range(len(data)):
        x = data[i, 0]
        y = data[i, 1]
        # 利用导数的定义来计算梯度
        b_gradient = (error(x, y, b_cur + delta, m_cur) - error(x, y, b_cur - delta, m_cur)) / (2*delta)
        m_gradient = (error(x, y, b_cur, m_cur + delta) - error(x, y, b_cur, m_cur - delta)) / (2*delta)

    b_gradient = b_gradient / N
    m_gradient = m_gradient / N
    #
    new_b = b_cur - (learning_rate * b_gradient)
    new_m = m_cur - (learning_rate * m_gradient)
    return [new_b, new_m]


def error(x, y, b, m):
    return (y - (m * x) - b) ** 2
```

上述两种中，迭代次数足够多都可以逼近最优解。  
分别求得的最优解为：
1： 1.23930380135 1.86724196887
2： 1.24291450769 1.86676417482

### 简单比较
sklearn中有相应的方法求线性回归，其直接使用最小二乘法求最优解。简单实现以做个比较。
```
def scikit_learn():
    data = init_data()
    y = data[:, 1]
    x = data[:, 0]
    x = (x.reshape(-1, 1))
    linreg = LinearRegression()
    linreg.fit(x, y)
    print(linreg.coef_)
    print(linreg.intercept_)

if __name__ == '__main__':
    # linear_regression()
    scikit_learn()
```
此时求的解为：
1.24977978176  1.86585571  
可以说明上述计算结果比较满意，通过后期调整参数，可以达到比较好的效果。

源码和数据参考：  
[https://github.com/yunshuipiao/cheatsheets-ai-code](https://github.com/yunshuipiao/cheatsheets-ai-code/tree/master/machine_learning_algorithm/linear%20regression)

感谢并参考博文：  
[线性回归理解（附纯python实现）](http://blog.csdn.net/sxf1061926959/article/details/66976356)  
[Gradient Descent 梯度下降法](https://ctmakro.github.io/site/on_learning/gd.html)  
[梯度下降（Gradient Descent）小结](http://www.cnblogs.com/pinard/p/5970503.html)





