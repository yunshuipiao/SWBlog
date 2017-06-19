

前不久Google IO 2017不仅将kotlin宣布为官方开发语言，还发布了谷歌官方 Android 应用架构库，这个新的架构库旨在帮助我们设计健壮、可测试的和可维护的应用程序。新项目也打算采用这套架构，下面一步步介绍怎么去配置和使用这套架构。（最简单介绍，详细内容看官方文档和其他参考资料）

###  总览
简单说来，该架构由数据驱动， 彻底将UI和data分离，UI层很轻，不涉及任何数据的操作的内容。ViewModel将数据的变化的反映在UI上，本身也不持有数据。官方推荐所有数据持久化。viewmodel通过Repository来管理数据，保存到数据库或者从网络获取。


### kotlin的配置
本次打算使用kotlin来进行开发

新建一个项目，完成后在project/build.gradle添加以下内容
```
//对照添加
buildscript {
    ext.kotlin_version = '1.1.2-5'
    repositories {
        jcenter()
    }
    dependencies {
        classpath 'com.android.tools.build:gradle:2.3.1'
        classpath "org.jetbrains.kotlin:kotlin-gradle-plugin:$kotlin_version"
        classpath "org.jetbrains.kotlin:kotlin-android-extensions:$kotlin_version"

        // NOTE: Do not place your application dependencies here; they belong
        // in the individual module build.gradle files
    }
}
```

在app/build.gradle的开头添加：
```
apply plugin: 'kotlin-android'
apply plugin: 'kotlin-android-extensions'
apply plugin: 'kotlin-kapt'
```
简单解释：
第二行表示kotlin扩展，可以避免使用类似findviewbyid的很多内容
第三行表示kotlin对注解的处理，感觉对databinding的支持不是很好。
到此为止， kotlin的配置已经完成。

在android studio的插件中安装kotlin后重启。
对Mainactivity.java执行code--convert java file to koltin file。
编译运行成功的话，说明kotlin配置成功

### android architecture Component的配置
该架构官方翻译参考：https://juejin.im/post/5937b1d7a22b9d005810b877

简单配置如下
在project/build.gradle中添加：
```
allprojects {
    repositories {
        jcenter()
        maven { url 'https://maven.google.com' }
    }
}
```

在app/build.gradle中添加
```
    /// Architecture Components
    compile "android.arch.lifecycle:runtime:$ac_version"
    compile "android.arch.lifecycle:extensions:$ac_version"
    kapt "android.arch.lifecycle:compiler:$ac_version"
    /// Room
    compile "android.arch.persistence.room:runtime:$ac_version"
    kapt "android.arch.persistence.room:compiler:$ac_version"
```
前面三行是关于lifecycle，livedata， viewmodel的依赖，后面是关于Room的依赖。
后面用例子进行说明。如有问题欢迎讨论。

### Room的使用
Room在sqlite之上提供了一个抽象层。
将数据持久化到本地对于应用程序处理大量结构化数据有非常大的好处。最常见的情况是缓存相关数据。这样，当设备无法访问网络时，用户仍然可以在离线状态下浏览内容。然后，在设备重新上线后，任何用户发起的内容变更都会同步到服务器。
其中有三个组件组成：
* DataBase
* Entity
* DAO

下面介绍最简单的使用方法：

如上图所示，项目结构对应架构图，另外的base，App是一些基础的ui， 项目的app单例，以后还会添加dagger2来分离模块。
新建一个Story文件，内容如下：

```
@Entity(tableName = "stories")
class Story {
    @PrimaryKey
    var id = 0
    var data = ""
    var displayData = ""
    var title = ""
    var image = ""
}
//story作为数据库的一张表，可以指定列名，主键
```

新建StoryDao文件：
```
@Dao
interface StoryDao {
    @Query("select * from stories")
    fun loadAllStories(): LiveData<List<Story>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertStories(list: List<Story>)
}
//对数据的操作，这里涉及插入和查询（查询的返回值可以是LiveData，list， Rxjava2等）
```

新建AppDatabase文件：
```
@Database(entities = arrayOf(Story::class), version = 1)
abstract class AppDatabase: RoomDatabase() {
    companion object {
        val TAG = "sw_story_db"
    }
    abstract fun storyDao(): StoryDao
}
//数据库， 通过dao操作数据。
```

完成这三个文件，表示Room组件已经可以使用。
新建DataBaseManager文件：
```
object DatabaseManager {

    lateinit var db: AppDatabase

    fun initDb(context: Context) {
        db = Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.TAG).build()
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
        return db.storyDao().loadAllStories()
    }
}
//该单例对数据库封装了一层，方便处理db的各种操作（db初始化可使用依赖注入）
数据库插入不能在主线程，涉及事务。
```
下面在MainActivity中简单介绍怎么使用

```
//未完待续
```


完整代码：[github](https://github.com/yunshuipiao/ZhiJoke)






