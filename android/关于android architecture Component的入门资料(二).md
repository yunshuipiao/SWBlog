上篇回顾：kotlin和android architecture Component，Room的完成与测试。

接上篇，如果看到模拟的数据插入，那么可以继续往下看

### lifecycle
该架构提供了一种管理activity和fragmeng生命周期的方法，并且让接下来介绍的livedata可以在有效生命周期内收到观察，更新UI。首先来看：
因为还是测试版的原因，目前可以继承lifecycleActivity和lifecyclefragment来实现。
```
class StoryListFragment : LifecycleFragment() {}
```
说一下类的继承结构：
lifecycleActivity（LifecycleFragment）实现LifecycleRegistryOwner接口。只有一个方法：
```
//源码
public interface LifecycleRegistryOwner extends LifecycleOwner {
    @Override
    LifecycleRegistry getLifecycle();
}
```

在activity或fragmeng中可以调用getLifecycle(),可以得到LifecycleRegistry，获取当前ui的状态。
可以做个测试：
自定义一个MainObserve：
```
class MainObserve : LifecycleObserver {
    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    internal fun start() {
        Log.d(TAG, "start: " + "init")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    internal fun create() {
        Log.d(TAG, "create: " + "init")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_STOP)
    internal fun stop() {
        Log.d(TAG, "stop: " + "init")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    internal fun destory() {
        Log.d(TAG, "destory: " + "init")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    internal fun resume() {
        Log.d(TAG, "resume: " + "init")
    }
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    internal fun pause() {
        Log.d(TAG, "pause: " + "init")
    }
    companion object {
        private val TAG = "MyObserve"
    }
}
```
然后在ui中使用`lifeRegister.addObserver(MainObserve())`可以得到结果。
推荐看一下这几个接口和类的源代码，都非常简单。

上面都不是重点。

#### Lifecycle最佳实践
* 尽可能保持UI控制器（Activity和Fragment）的简洁。它们不应该去获取数据，而是使用ViewModel 来做这个工作，然后观察LiveData 把变化反应给view。
* 尝试写数据驱动的UI，UI controller的职责是在数据改变的时候更新view，或者把用户的操作通知给ViewModel。
* 把数据逻辑放在ViewModel 类中。ViewModel的角色是UI控制器与app其余部分的桥梁。不过要注意，ViewModel的职责并不是获取数据（比如，从网络）。相反 ViewModel应该调用合适的控件去做这件事情，然后把结果提供给UI控制器。
* 使用 Data Binding来让view和UI控制器之间的接口保持干净。这可以让你的view更加声明式同时最小化Activity和Fragment中的代码。如果你更喜欢用Java代码做这件事情，使用 Butter Knife来避免繁琐的代码。
* 如果你的UI非常复杂，考虑创建一个Presenter类来处理UI的变动。通常这会有点多余，但可能会让UI的测试更加简单。
* 绝对不要在 ViewModel中引用View 或者 Activity 的context。如果ViewModel活的比Activity更长，Activity可能会泄漏，无法正常回收。

### ViewModel
该架构与其他架构类似，提供viewmodel来分离UI和数据的处理。之前有部分的数据在UI中处理，UI层会变得臃肿，并且难以修改和重构。
以我的例子说明：
自定义的viewmodel需要继承ViewModel。如果需要用到application做系统相关的事情，可以继承AndroidViewModel（也可以用全局单例提供）。
```
class StoryListViewModel(val app: Application): AndroidViewModel(app) {}
```

这时，可以在activity使用`var viewModel = ViewModelProviders.of(activity).get(StoryListViewModel::class.java)`得到viewmodel的实例，处理数据，联系UI。
注意：经笔者测试，如果使用`var viewModel = StoryListViewModel()`new 出实例也是可以的，此时还可以带参数，方便很多。按我的理解，区别在于：
**当然：viewModel不推荐带参数**
前者可以保持一个单例，使用情况多个fragmeng在同一个activity中，利用viewmodel可以保存数据，多个fragmeng共享数据。但是，参数app必须是Application，在viewModel中做强制转换。
`(app as ZhiJokeApplication)`.如果是`ZhiJokeApplication`,编译会错误。

### Livedata
livedata和viewmodel和结合在一起的。livedata时一个可以感受UI生命周期的组件，不会造成内存泄漏。根据最佳实践，viewmodel处理数据逻辑，但并不涉及数据获取（db， net）。该架构推荐repository来获取保存数据。
与上一篇文章结合，自定义一个repository：
```
//使用了Dagger2， 不懂可以跳过。后面会写文章介绍。
//简单理解；等同于new， 创建实例
class StoryRepository @Inject
constructor(var db: AppDatabase) {

    companion object {
        var TAG = "StoryRepository"
    }

    fun insertStories(storys: List<Story>) {
        Flowable.just(storys)
                .observeOn(Schedulers.io())
                .subscribe {
                    db.beginTransaction()
                    try {
                        db.storyDao().insertStories(storys)
                        db.setTransactionSuccessful()
                    } finally {
                        db.endTransaction()
                    }
                }
    }
    fun loadAllStories(): LiveData<List<Story>> {
        var result = db.storyDao().loadAllStoriesLiveData()
        return  result
    }

    fun simlutateInsertData() {
        var list = ArrayList<Story>()

        for (i in 1..20) {
            var s = Story()
            s.id = (i)
            s.data = ((i * i).toString() + "--" + i.toString())
            s.displayData = ((i * i).toString() + "--" + i.toString())
            s.title = ((i * i).toString() + "--" + i.toString())
            s.image = ((i * i).toString() + "--" + i.toString())
            list.add(s)
        }
        insertStories(list)
    }
}
```
该类有三个方法，模拟插入数据和读取数据。

在viewmodel定义以下数据：
```
var storyList = MediatorLiveData<List<Story>>()

@Inject
//实例化StoryRepository
lateinit var storyRes: StoryRepository
```
关于livedata：有三个类，
* livedata抽象类：可以被viewmodel观察，在数据变化时响应。
* MutableLiveData：继承livedata， 提供setvalue和postvalue，设置值。
* MediatorLiveData： 可以对livedata进行观察，作出响应（有例子介绍）

我的理解：
之所以声明为MediatorLiveData，是因为story的来源可以有多种，网络，数据库。如果livedata的话，则网络获取的数据就不能赋值给livedata。如果MutableLiveData，普通list可以赋值给MutableLiveData。此时有一个问题，若数据库有变（插入数据）， 则loadAllStories的返回结果时livedata，无法通过
MutableLiveData.postvalue赋值， 因为livedata.getvalue的值永远为null。
因此，viewModel的整体代码如下：
```
class StoryListViewModel(val app: Application): AndroidViewModel(app) {
    companion object {
        val TAG = "StoryListViewModel"
    }
    var storyList = MediatorLiveData<List<Story>>()

    @Inject
    lateinit var storyRes: StoryRepository
    init {
        (app as ZhiJokeApplication).component.inject(this)
    }
    fun getStoryList():LiveData<List<Story>> {
        storyList.addSource(storyRes.loadAllStories(), {
            if (it == null || it.size == 0) {
                storyRes.simlutateInsertData()
                Thread.sleep(1000)
            }
            storyList.postValue(it)
        })
        return storyList
    }
}
```
getStoryList提供给UI，对storyList进行观察，根据变化去更新UI。
```
var viewModel = ViewModelProviders.of(activity).get(StoryListViewModel::class.java)
        viewModel.getStoryList().observe(this, Observer {
            if (it != null) {
            // update UI
                mAdapter.setStories(it)
            }
        })
```

到此为止，改架构大部分都介绍完了，感觉也没介绍的多详细可以自己实现一个demo感受一下。

[我的kotlin版demo](https://github.com/yunshuipiao/ZhiJoke)

几点建议：
使用dagger2来解耦。
如果rxjava2使用的非常熟练的话，也可以不用livedata。

这两篇文章也是对自己学习的一个回顾，其中穿插有自己对某些点的感想。
希望得到各位的建议。（wx：youquwen1226，欢迎交流）

后面打算写篇文章介绍dagger2.
若是不习惯用kotlin，我会再上传一个java版本的demo。
