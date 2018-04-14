

说明：本文章在上篇的基础上，代码在Android的环境中运行，当然可以单独写。
后面的文章就不跟Android代码联系，最后再回到Android， 写一个柯里化和函数式编程的例子。


## 纯函数

上篇文章提到一下纯函数， 类似两个数的加法这样：
```
fun add1(a: Int, b: Int): Int {
    return a + b
}
```

`纯函数可以理解为一种 相同的输入必定有相同的输出的函数，没有任何可以观察到副作用`
纯函数的输出只取决于输入，输入定责输出定。在笔者写Android的过程中，尽量使用纯函数，较少副作用。

关于上面的函数，可以写成如下的形式：
`fun add2(a: Int, b: Int) = a + b`

## 高阶函数
在函数式编程中，首先要介绍一下 高阶函数

高阶函数是将函数用作参数或者返回值的函数。Kotlin的Collection类型中有大量的这种高阶函数，例如：Iterable的filter, foreach函数。本篇文章后面也可写相应的高阶函数。

### 将函数当做参数
现在开始，定义一个函数，将上面的`add1`当做参数执行计算。
代码如下：
```
fun calculate(a: Int, b: Int, f: (Int, Int) -> Int) : Int {
    return f(a, b)
}
```
如上， 定义了一个`calculate`函数, 该函数接收三个参数， 整形的a，b和作为函数的f。这比较特别，看参数定义可以知道，函数f将两个Int型值作为输入，输出一个Int值。可以进行如下调用：
```
codeBtn.setOnClickListener {
                when (position) {
                    0 -> {
                        codeBtn.countDown()
                    }
                    1 -> {
                        val result = calculate(2, 3, ::add1)
                        Timber.d("计算结果为: $result")
                    }
                }
            }
```
上述是RecyclerView的部分代码，这里只关注1的情况，将（2，3，::add1）传入，add1前有双冒号表示当做函数参数传入。输入结果如下：
```
 D/CodeExampleActivity$ItemViewHolder$bindData: 计算结果为: 5
```

当然也可以去除双冒号：既然函数可以当做参数或者返回值，那么相应的可以将函数理解为对象，
定义一个函数对象，执行如上相同的操作：
```
val add3 = {a: Int, b: Int -> a + b}
Timber.d(add3(3, 5).toString())
val result = calculate(2, 3, add3)
Timber.d("计算结果为: $result")
```
log打印如下：
```
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数对象计算结果：8
 D/CodeExampleActivity$ItemViewHolder$bindData: 计算结果为: 5
```

### 将函数当做返回值
相比起来，我觉得当做返回值要难一些，慢慢来：
```
fun add4(): (Int, Int) -> Int {
    return ::add2
}

fun add5() = {
    add3
}

val add6 = add3
```

首先做如上函数声明，函数add2 和函数对象 add3 用作返回值。
因为add2是函数，所以有双冒号；add3是对象，特殊在其是一个函数。

那么怎么去调用呢：
```
                        val addF = add4()

                        val addFF = add5()
                        Timber.d("函数add4(): ${add4()}")
                        Timber.d("函数对象addF: $addF")

                        Timber.d("函数add5(): ${add5()}")

                        Timber.d("函数对象addF计算结果：${addF(1, 2)}")
                        Timber.d("函数add4()计算结果: ${add4()(3, 4)}")


                        Timber.d("函数addFF计算结果: ${addFF()(4, 5)}")
                        Timber.d("函数add5()计算结果: ${add5()()(6, 7)}")
```

log日志打印如下：
```
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add4(): fun add2(kotlin.Int, kotlin.Int): kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数对象addF: fun add2(kotlin.Int, kotlin.Int): kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add5(): () -> (kotlin.Int, kotlin.Int) -> kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数对象addF计算结果：3
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add4()计算结果: 7
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数addFF计算结果: 9
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add5()计算结果: 13
```

在以上kotlin代码中有一个小插曲：  
log打印中出现提示： `Kotlin reflection is not available`  
解决方案：[kotlin-reflection-is-not-available](https://stackoverflow.com/questions/44348557/kotlin-reflection-is-not-available)  
在`build.gradle` 中添加 `implementation "org.jetbrains.kotlin:kotlin-reflect:$kotlin_version"`  

对log输出稍做解释，不过我相信没有介绍也能看懂。  
函数add4() 对象addF都是 函数add2(), log中有参数和返回值信息。  
而add5()是对象add3， 是一个对象，接收0个参数返回一个函数，这个函数接收两个参数返回一个值。  
根据log可以简单的了解其用法，后面会介绍。


进一步改写：
```
fun add7(): (Int, Int) -> Int {
    return fun(a: Int, b: Int): Int {
        return a + b
    }
}

fun add8(): (Int, Int) -> Int {
    return {a: Int, b: Int -> a + b}
}

val add9 = {a: Int, b: Int -> a + b}
```
这里我们直接定义匿名函数当做函数值。同样看一下log打印输出：
```
                        val addF = add7()

                        val addFF = add8()
                        Timber.d("函数add7(): ${add7()}")
                        Timber.d("函数对象addF: $addF")

                        Timber.d("函数add8(): ${add8()}")
                        Timber.d("函数addFF: $addFF")

                        Timber.d("函数add6(): $add9")

                        Timber.d("函数对象addF计算结果：${addF(1, 2)}")
                        Timber.d("函数add7()计算结果: ${add7()(3, 4)}")


                        Timber.d("函数addFF计算结果: ${addFF(4, 5)}")
                        Timber.d("函数add8()计算结果: ${add8()(6, 7)}")
```

log输出：
```
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add7(): (kotlin.Int, kotlin.Int) -> kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数对象addF: (kotlin.Int, kotlin.Int) -> kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add8(): (kotlin.Int, kotlin.Int) -> kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数addFF: (kotlin.Int, kotlin.Int) -> kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add9: (kotlin.Int, kotlin.Int) -> kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数对象addF计算结果：3
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add7()计算结果: 7
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数addFF计算结果: 9
 D/CodeExampleActivity$ItemViewHolder$bindData: 函数add8()计算结果: 13
```

这里不做解释，相信你也能看懂了。

其中有个地方需要注意,即add5()函数的定义为什么是：
`函数add5(): () -> (kotlin.Int, kotlin.Int) -> kotlin.Int`:

这里我再写两个函数说明一下：
```
val add5_1 = { 10 }

val add5_2 = 10

fun add5_1() = {
    10
}

fun add5_2(): Int {
    return 10
}
```


![](https://user-gold-cdn.xitu.io/2018/4/14/162c36914218b82b?w=1320&h=598&f=png&s=99467)

这里要搞清楚对象和函数的区别就简单。
当返回或者等于`{}`时，它就是一个函数, 调用后返回最后一个对象。

```
                        val addF = add5_1
                        val addFF = add5_2
                        val addFFF = add5_1()
                        val addFFFF = add5_2()
                        Timber.d("$addF")
                        Timber.d("${addF()}")
                        Timber.d("$addFF")
                        Timber.d("$addFFF")
                        Timber.d("$addFFFF")
```

输出如下：
```
 D/CodeExampleActivity$ItemViewHolder$bindData: () -> kotlin.Int
 D/CodeExampleActivity$ItemViewHolder$bindData: 17
 D/CodeExampleActivity$ItemViewHolder$bindData: 10 D/CodeExampleActivity$ItemViewHolder$bindData: () -> kotlin.Int D/CodeExampleActivity$ItemViewHolder$bindData: 10
```

那么假如返回的最后一个不是数怎么办，当然，它可以是一个函数。
如下演示:

```
val add5_3_1 = {
    val temp = 10
    {a: Int -> a + temp}
}


fun add5_3_2() = {
    val temp = 10
    {a: Int -> a + temp}
}
```

首先是add5_3_2()是一个函数，其返回值也是一个函数，该函数完成`+10`的功能。
如下调用：
```
                        Timber.d("${add5_3_1()(15)}")
                        Timber.d("${add5_3_2()()(15)}")
```
现在应该能简单理解并知道结果为 25 了吧。

以上就是介绍将函数作为输入和输出的简单介绍。当然，我上面的例子由于过于浅显和简单，容易混淆，实际使用过程中也不会如上定义函数， 不过慢慢会理解的。  
接下来就可以继续上述的一个事情，针对collection写自己的高阶函数。

下篇文章见

完整代码查看：  
[github: 纯函数  高阶函数 函数当做输入输出](https://github.com/yunshuipiao/SwDesign/blob/8d3c18b5734524ee682ddc3c2ecb676e3f883f3f/app/src/main/java/com/swensun/swdesign/base/ViewExtension.kt)


