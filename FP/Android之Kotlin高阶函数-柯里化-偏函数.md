## 自定义Collection的高阶函数

接上篇文章，其简单介绍了纯函数，高阶函数，写了几个简单将函数当做输入输出的例子。
现在来自定义我们的高阶函数。
首先来看最简单的forEach:
```
fun main(args: Array<String>) {
    val list = arrayListOf(1, 4, 7)
    list.forEach { println(it) }
    // 1， 4， 7
}
```
看一下源码  
```
public inline fun <T> Iterable<T>.forEach(action: (T) -> Unit): Unit {
    for (element in this) action(element)
}
```

简单解释：这里用到了泛型，简单忽略。
forEach中的action就是一个函数，类似我们上面的println()
函数执行也好理解：循环每一个元素执行action。

```
fun main(args: Array<String>) {
    val list = arrayListOf(1, 4, 7)
    list.forEach { print1(it) }
    list.forEach { print2(it) }
    list.forEach({ print1(it) })
    list.forEach(print2)
    list.forEach(print3)
}

fun print1(a: Int) {
    print(a)
}

val print2 = { a: Int ->
    print(a)
}

val print3 = ::print1
```

上述5个forEach的输出相同，在上篇文章的基础上，相信理解不难。

接下来看一下：filter：
```
    val list = arrayListOf(1, 4, 7)
    list.filter { it != 1 }.forEach { println(it) }  // 4, 7
```

```
public inline fun <T> Iterable<T>.filter(predicate: (T) -> Boolean): List<T> {
    return filterTo(ArrayList<T>(), predicate)
}

public inline fun <T, C : MutableCollection<in T>> Iterable<T>.filterTo(destination: C, predicate: (T) -> Boolean): C {
    for (element in this) if (predicate(element)) destination.add(element)
    return destination
}
```
filter函数: 根据给定的高阶函数对元素进行判断后的Boolean返回值来过滤，返回新的迭代器。
稍微花5分钟理解一下。

接下来定义一个Collection的扩展函数，对每个元素指定 +2 功能：
```
fun main(args: Array<String>) {
    val list = arrayListOf(1, 4, 7)
    list.add2().forEach { println(it) } // 3, 6, 9

}

fun Iterable<Int>.add2(): List<Int> {
    val tempList = arrayListOf<Int>()
    for (e in this) {
        tempList.add(e + 2)
    }
    return tempList
}
```
没有用到泛型， 非常容易理解，对Collection中的每个值+2。
当然，可以把2配置成参数：

```
fun main(args: Array<String>) {
    val list = arrayListOf(1, 4, 7)
    list.add2(3).forEach { println(it) }

}

fun Iterable<Int>.add2(a: Int): List<Int> {
    val tempList = arrayListOf<Int>()
    for (e in this) {
        tempList.add(e + a)
    }
    return tempList
}
```

上面看过的源码中，inline是内联的意思，针对lambda优化，表示调用这个函数时，将代码复制到调用的位置，而不经过函数的调用栈， 节省开销。

下面在此基础上改一下，加上lambda。
```
inline fun Iterable<Int>.calculate(f: (Int) -> Int): List<Int> {
    val tempList = arrayListOf<Int>()
    for (e in this) {
        tempList.add(f(e))
    }
    return tempList
}
```
定义一个扩展函数，同样也是完成加2的功能：
```
fun main(args: Array<String>) {
    val list = arrayListOf(1, 4, 7)
    list.calculate { add2(it) }.forEach { println(it) }
    list.calculate { add3(it) }.forEach { println(it) }

    list.calculate(::add2).forEach { println(it) }
    list.calculate(add3).forEach { println(it) }

    list.calculate { it + 2 }.forEach { println(it) }
}

fun add2(a: Int): Int {
    return a + 2
}

val add3 = {a: Int -> a + 2}

inline fun Iterable<Int>.calculate(f: (Int) -> Int): List<Int> {
    val tempList = arrayListOf<Int>()
    for (e in this) {
        tempList.add(f(e))
    }
    return tempList
}
```
上面有5中函数的调用演示，其结果都一样，现在应该非常容易理解。

回过头来看文章最前面定义的高阶函数：
```
fun Iterable<Int>.add2(): List<Int> {
    val tempList = arrayListOf<Int>()
    for (e in this) {
        tempList.add(e + 2)
    }
    return tempList
}
```

编译器有智能转换的功能, 转换后代码如下：
```
fun Iterable<Int>.add2(): List<Int> {
    val tempList = this.map { it + 2 }
    return tempList
}
```
此处看到了map，结合上面定义calculate函数，接收一个函数完成类似的功能，此时去看map的源码应该非常容易理解。
此时还可以继续只能转换：
```
fun Iterable<Int>.add2(): List<Int> {
    return map { it + 2 }
}
```

此时对比如下调用：
```
fun main(args: Array<String>) {
    val list = arrayListOf(1, 4, 7)

    list.add2().forEach { println(it) }
    list.map { it + 2 }.forEach { println(it) }
    list.map(::add2).forEach { println(it) }
}

fun add2(a :Int): Int {
    return a + 2
}
```

结果一样，不过过多解释。至此，相信应该不难写出自己的高阶函数。


### 柯里化

柯里化是一个数学概念， 简单的来说，就是对于一个有多个参数的函数，转换成每次只接受一个参数的函数，最后输入结果。
Currying 的重要意义在于可以把函数完全变成「接受一个参数；返回一个值」的固定形式。
看代码，从简单开始：

首先定义一个扩展方法执行打印输出：
```
fun <T> T?.println() = kotlin.io.println(this)
```

首先是一个加法函数：
```
fun main(args: Array<String>) {
    add(1, 2).println()
}

fun add(a: Int, b: Int): Int {
    return a + b
}
```
这是最简单的调用方法，柯里化后希望变成如下调用：  
`add(1)(2).println()`  
修改后的代码如下：
```
fun main(args: Array<String>) {
    add(1, 2).println()
    add1(1)(2).println()
    add2(1)(2).println()

    add3(1)(2).println()

}

fun add(a: Int, b: Int): Int {
    return a + b
}
fun add1(a: Int): (Int) -> Int {
    return {b: Int -> a + b}
}
fun add2(a: Int) = {b: Int -> a + b}

val add3 = {a: Int -> {b: Int -> a + b}}
```
全部打印的结果输出都是为3，其在函数和对象之间有略微的区别，也容易理解。
```
    val addOne = add1(1)
    addOne(2).println()
    addOne(4).println()
```
另外还可以如此调用， addOne是一个函数对象，接收一个函数完成+1的功能。

**偏函数**：对一个多参数的函数，通过指定其中的一部分参数后得到的仍然是一个函数，那么这个函数就是原函数的一个偏函数了。
  
偏函数与 Currying 有一些内在的联系，如果我们需要构造的偏函数的参数恰好处于原函数参数的最前面，那么我们是可以使用 Currying 的方法获得这一偏函数的；当然，如果我们希望得到任意位置的参数被指定后的偏函数，后面介绍方法。

加入我要完成3个参数的加法怎么办,简单写一下：
```
fun main(args: Array<String>) {

    add(1, 2, 3).println()
    add1(1)(2)(3).println()
}

fun add(a: Int, b: Int, c: Int): Int {
    return a + b + c
}

fun add1(a: Int): (Int) -> (Int) -> Int {
    return fun(b: Int): (Int) -> Int {
        return {c: Int -> a + b + c}
    }
}
```
如下，完成了3个参数相加的currying。加入随着参数的增多，会变得越来越复杂。
我们可以定义一个 **函数的扩展函数**来进行函数currying：
```
fun main(args: Array<String>) {
    ::add.curried()(1)(2)(3).println()   // 6
}

fun <P1, P2, P3, R> Function3<P1, P2, P3, R>.curried()
        = fun(p1: P1) = fun(p2: P2) = fun(p3: P3) = this(p1, p2, p3)

fun <P1, P2, R> Function2<P1, P2, R>.curried()
        = fun(p1: P1) = fun(p2: P2) = this(p1, p2)

fun add(a: Int, b: Int, c: Int): Int {
    return a + b + c
}
```

不容易理解， 看一下Function3的定义，
```
/**
 * A functional interface (callback) that computes a value based on multiple input values.
 * @param <T1> the first value type
 * @param <T2> the second value type
 * @param <T3> the third value type
 * @param <R> the result type
 */
public interface Function3<T1, T2, T3, R> {
    /**
     * Calculate a value based on the input values.
     * @param t1 the first value
     * @param t2 the second value
     * @param t3 the third value
     * @return the result value
     * @throws Exception on error
     */
    @NonNull
    R apply(@NonNull T1 t1, @NonNull T2 t2, @NonNull T3 t3) throws Exception;
}
```

这里不理解也没关系，记住currying怎么写，后面慢慢理解的。

### 偏函数

上面有简单提到, 如果我们希望得到任意位置的参数被指定后的偏函数，也需要定义 **函数的扩展方法**， 如下：

```
 fun <P1, P2, R> Function2<P1, P2, R>.partial1(p1: P1) = fun(p2: P2) = this(p1, p2) 
 fun <P1, P2, R> Function2<P1, P2, R>.partial2(p2: P2) = fun(p1: P1) = this(p1, p2)
```

目前kotlin标准库中没有实心，所以这里要定义两个方法，这两个方法分别用来生成两个参数分别被指定后的偏函数。

如下使用：
```
fun main(args: Array<String>) {
    val addOne = add.partial1(1)
    val addTwo = add.partial2("2")
    addOne("2").println()
    addTwo(3).println()
}

val add = {a:Int, b: String -> "$a$b"}
```
非常容易的能看出区别

至此，函数柯里化和偏函数基本介绍完了，本来还想写一个Android的具体例子， 限于篇幅，后面再补。

总结：

1. 函数柯里化
2. 偏函数


本篇文章和上一篇结合紧密，建议一起阅读。


* [Android之纯函数-高阶函数简单介绍](https://github.com/yunshuipiao/SWBlog/blob/master/FP/Android%E4%B9%8B%E7%BA%AF%E5%87%BD%E6%95%B0-%E9%AB%98%E9%98%B6%E5%87%BD%E6%95%B0%E7%AE%80%E5%8D%95%E4%BB%8B%E7%BB%8D.md)






