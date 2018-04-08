最近在学习机器学习有关神经网络的部分，其中有两份很好的参考资料，链接如下：  
[Neural Networks and Deep Learning](http://neuralnetworksanddeeplearning.com/index.html)  
[http://colah.github.io](http://colah.github.io/)  
前者是关于神经网络和深度学习的一份简单的入门资料，后者是关于神经网络的一系列文章。
前者涉及到神经网络基本概念及其公式的介绍， 搭配手写数字识别的算法，对初学者非常友好。
不过我建议在阅读之前，先看一下更适合入门的视频，链接如下：  
[深度学习入门](https://space.bilibili.com/88461692/#/channel/detail?cid=26587)

这篇文章我打算在作者已有python2算法的基础上，对其进行python3的转换，同时做出自己的理解，算是加深印象。

### 神经网络基本结构
```
class Network(object):
    def __init__(self, sizes):
        self.num_layers = len(sizes)
        self.sizes = sizes
        self.biases = [np.random.randn(y, 1) for y in sizes[1:]]
        self.weights = [np.random.randn(y, x)
                        for x, y in zip(sizes[:-1], sizes[1:])]
```
如上，sizes举例如下，[2, 3, 1]。表示该神经网络有3层，分别为输入层，隐藏层和输出层。
对于该数据集来说，每张图片为28 * 28像素，共计784个像素，输出为10个神经元，取其中最大值作为预测数字。
因此 `net = Network([784, 30, 10])`表示该神经网络有3层，输入层有784个神经元，输出层有10个神经元。

### 数据加载
```
def load_data():
    f = gzip.open('./data/mnist.pkl.gz', 'rb')
    training_data, validation_data, test_data = pickle.load(f, encoding='latin1')
    f.close()
    return (training_data, validation_data, test_data)
```
从文件中读取该数据集进行处理。
```
def load_data_wrapper():
    tr_d, va_d, te_d = load_data()
    training_inputs = [np.reshape(x, (784, 1)) for x in tr_d[0]]
    training_results = [vectorized_result(y) for y in tr_d[1]]
    training_data = zip(training_inputs, training_results)
    validation_inputs = [np.reshape(x, (784, 1)) for x in va_d[0]]
    validation_data = zip(validation_inputs, va_d[1])
    test_inputs = [np.reshape(x, (784, 1)) for x in te_d[0]]
    test_data = zip(test_inputs, te_d[1])
    return (training_data, validation_data, test_data)
```
经过以上两步处理，training_data包含数据50000组数据，每组数据包含向量分别如下：
前者是784个像素点的灰度值，后者包含10个值，对应该图片的正确分类值。
```python
//表示分类为5.
       [ 0.],
       [ 0.],
       [ 0.],
       [ 0.],
       [ 0.],
       [ 1.],
       [ 0.],
       [ 0.],
       [ 0.],
       [ 0.]
```

利用如下方法可以将矩阵转为图片查看：
```
    c = tr_d[0][0]
    s = np.reshape(c, (784, 1))
    img = s.reshape((28, 28))
    new_im = Image.fromarray(img)
    # print(s)
    new_im.show()
```

以上，数据准备完毕。

基本的概念，比如sigmoid激活函数，随机梯度下降，前向传播算法和反向传播算法，代码中会有实现，有疑问可以参考上述资料。

### 随机梯度下降
```
    def SGD(self, training_data, epochs, mini_batch_size, eta, test_data=None):
        """
        desc: 随机梯度下降
        :param training_data: list of tuples (x,y)
        :param epochs: 训练次数
        :param mini_batch_size: 随机的最小集合
        :param eta: learning rate： 学习速率
        :param test_data: 测试数据，有的话会评估算法，但会降低运行速度
        :return:
        """
        if test_data:
            test_data = list(test_data)
            n_test = len(test_data)
        training_data = list(training_data)
        n = len(training_data)
        for j in range(epochs):
            random.shuffle(training_data)
            mini_batches = [
                training_data[k: k + mini_batch_size]
                for k in range(0, n, mini_batch_size)
            ]
            for mini_batch in mini_batches:
                self.update_mini_batch(mini_batch, eta)
            if test_data:
                print("Epoch {}: {} / {}".format(
                    j, self.evaluate(test_data), n_test))
            else:
                print("Epoch {} complete".format(j))
```
参数的解释如上。
对于每一次迭代，首先打乱数据集，根据随机梯度下降给定的最小batch数据集，将训练数据分开进行。对于每一个batch数据集，用学习速率进行更新。如果有测试集，则评估算法的准确性。评估算法如下：

```
    def evaluate(self, test_data):
        """
        评估测试集的准确性
        :param test_data:
        :return:
        """
        test_results = [(np.argmax(self.feedforward(x)), y)
                        for (x, y) in test_data]
        return sum(int(x == y) for (x, y) in test_results)
        
    def feedforward(self, a):
        """return the output of the network if "a" is input"""
        for b, w in zip(self.biases, self.weights):
            a = sigmoid(np.dot(w, a) + b)
        return a
```
对于测试数据集，feedforward函数根据随机梯度下降中更新得到的biases， weights计算10个值的预测输出。np.argmax()函数得到10个值中的最大值，即使预测的输出值。
判断是否相等并统计，看共计多少个预测准确。
如上，让我们忽略`update_mini_batch()`函数

```
if __name__ == '__main__':
    training_data, validation_data, test_data = \
        mnist_loader.load_data_wrapper()
    net = Network([784, 30, 10])
    net.SGD(training_data, 30, 10, 3.0, test_data)
```
初始化神经网络，建立3层，输入层784个神经元，隐藏层30个神经元，输出层10个神经元。
利用随机梯度下降，迭代30次，随机下降的数据集为10个，学习速率为3.0。针对测试集的结果如下：
```
Epoch 0: 9080 / 10000
Epoch 1: 9185 / 10000
Epoch 2: 9327 / 10000
Epoch 3: 9348 / 10000
Epoch 4: 9386 / 10000
Epoch 5: 9399 / 10000
Epoch 6: 9391 / 10000
Epoch 7: 9446 / 10000
Epoch 8: 9427 / 10000
Epoch 9: 9478 / 10000
Epoch 10: 9467 / 10000
Epoch 11: 9457 / 10000
Epoch 12: 9453 / 10000
Epoch 13: 9440 / 10000
Epoch 14: 9452 / 10000
Epoch 15: 9482 / 10000
Epoch 16: 9470 / 10000
Epoch 17: 9483 / 10000
Epoch 18: 9488 / 10000
Epoch 19: 9484 / 10000
Epoch 20: 9476 / 10000
Epoch 21: 9496 / 10000
Epoch 22: 9469 / 10000
Epoch 23: 9503 / 10000
Epoch 24: 9495 / 10000
Epoch 25: 9499 / 10000
Epoch 26: 9510 / 10000
Epoch 27: 9495 / 10000
Epoch 28: 9487 / 10000
Epoch 29: 9478 / 10000
```
可以看到随着训练次数的增加，模型的准确率在不断提高。


### 随机梯度下降更新biases和weights
```
    def update_mini_batch(self, mini_batch, eta):
        """
        梯度下降更新weights和biases， 用到backpropagation反向传播。
        :param mini_batch:
        :param eta:
        :return:
        """
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]

        for x, y in mini_batch:
            delta_nabla_b, delta_nabla_w = self.backprop(x, y)
            nabla_b = [nb + dnb for nb, dnb in zip(nabla_b, delta_nabla_b)]
            nabla_w = [nw + dnw for nw, dnw in zip(nabla_w, delta_nabla_w)]

        self.weights = [w - (eta / len(mini_batch)) * nw
                        for w, nw in zip(self.weights, nabla_w)]
        self.biases = [b - (eta / len(mini_batch)) * nb
                       for b, nb in zip(self.biases, nabla_b)]
```
首先初始化与biases和weights相同大小的矩阵。
对于每一个nimi_batch的x(像素矩阵， 784)， y(预测矩阵， 10)，利用反向传播算法计算  
`delta_nabla_b, delta_nabla_w = self.backprop(x, y)`得到每一次的梯度，相加得到mini_batch的梯度，进行权重和偏置项更新。

### 反向传播算法
```
    def backprop(self, x, y):
        """
        :param x:
        :param y:
        :return: (nabla_b, nabla_w): gradient for 损失函数，类似于biaes， weight。
        """
        nabla_b = [np.zeros(b.shape) for b in self.biases]
        nabla_w = [np.zeros(w.shape) for w in self.weights]
        # feedforward
        activation = x
        activations = [x]  # 存储所有激活值
        zs = []  # 存储所有的z向量
        for b, w in zip(self.biases, self.weights):
            z = np.dot(w, activation) + b
            zs.append(z)
            activation = sigmoid(z)
            activations.append(activation)

        # backward pass
        delta = self.cost_derivative(activations[-1], y) * \
                sigmoid_prime(zs[-1])
        nabla_b[-1] = delta
        nabla_w[-1] = np.dot(delta, activations[-2].transpose())

        for l in range(2, self.num_layers):
            z = zs[-l]
            sp = sigmoid_prime(z)
            delta = np.dot(self.weights[-l + 1].transpose(), delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, activations[-l - 1].transpose())
        return (nabla_b, nabla_w)

    def cost_derivative(self, output_activations, y):
        """
        :param output_activations:
        :param y:
        :return: 给定输出激发。
        """
        return (output_activations - y)
```
上面就是最重要也就是最复杂的反向传播算法。该算法mini_bitch中的向量为参数，
首先也是初始化与biases和weights相同大小的矩阵，并存储所有的激活值。
```
 for b, w in zip(self.biases, self.weights):
            z = np.dot(w, activation) + b
            zs.append(z)
            activation = sigmoid(z)
            activations.append(activation)
```
上述函数对biases和weights进行运算，通过sigmoid函数得到激活值。
接着求出最后一层的参数。
```
        for l in range(2, self.num_layers):
            z = zs[-l]
            sp = sigmoid_prime(z)
            delta = np.dot(self.weights[-l + 1].transpose(), delta) * sp
            nabla_b[-l] = delta
            nabla_w[-l] = np.dot(delta, activations[-l - 1].transpose())
```
该函数从倒数第二层开始，迭代分别求出每一层梯度值，返回更新梯度。


至此算法的全部代码完成。
完整代码请查看：  
[github: code](https://github.com/yunshuipiao/sw_machine_learning/tree/master/neural-networks-and-deep-learning-python3)

总结：
* python3
* 神经网络入门
* 随机梯度下降
* 反向传播算法

todo：  
利用这个思想去看kaggle上的手写数字识别的题目。



