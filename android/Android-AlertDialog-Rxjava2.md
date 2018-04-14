## 自定义 AlertDialog 对话框
目前AlertDialog已经足够全面，包括简单，列表，多选列表对话框，可以满足大部分日常开发。
这里自定义一个对话框， 了解一下material design关于对话框的相关标准。

首先定义一个`TextView`的扩展方法(非常建议， 可以获取context)， 如下：
```
fun TextView.countDown() {
    val frameLayout = FrameLayout(context)
    val rootView = LayoutInflater.from(context).inflate(R.layout.view_count_down, null)
    val mobileView = rootView.find<EditText>(R.id.vcd_et_mobile)
    val codeView = rootView.find<EditText>(R.id.vcd_et_code)
    val sendCode = rootView.find<TextView>(R.id.vcd_tv_send_code)
    sendCode.setOnClickListener {
        Timber.d("发送验证码")
    }

    val lp = FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.WRAP_CONTENT)
    lp.topMargin = dp2px(20f)
    lp.bottomMargin = dp2px(20f)
    lp.leftMargin = dp2px(24f)
    lp.rightMargin = dp2px(24f)
    rootView.layoutParams = lp
    frameLayout.addView(rootView)

    AlertDialog.Builder(context).setTitle("手机号码")
            .setNegativeButton("取消", { _, _ ->
                showToast("取消")
            })
            .setPositiveButton("确认", { _, _ ->
                showToast("确认")
            })
            .setView(frameLayout)
            .show()
}
```

布局UI如下：
```
<?xml version="1.0" encoding="utf-8"?>
<android.support.constraint.ConstraintLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="wrap_content"
    xmlns:app="http://schemas.android.com/apk/res-auto"
    android:background="@color/white">

    <EditText
        android:id="@+id/vcd_et_mobile"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toTopOf="parent"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@color/white"
        android:padding="10dp"
        android:hint="请输入手机"
        android:text=""
        android:textSize="16sp"
        />

    <EditText
        android:id="@+id/vcd_et_code"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:hint="请输入验证码"
        app:layout_constraintTop_toBottomOf="@id/vcd_et_mobile"
        app:layout_constraintRight_toLeftOf="@id/vcd_tv_send_code"
        android:background="@color/white"
        app:layout_constraintLeft_toLeftOf="parent"
        android:layout_marginTop="10dp"
        android:layout_marginRight="10dp"
        android:padding="10dp"
        android:textSize="16sp"
        />
    <TextView
        android:id="@+id/vcd_tv_send_code"
        android:layout_width="wrap_content"
        android:layout_height="0dp"
        app:layout_constraintTop_toTopOf="@id/vcd_et_code"
        app:layout_constraintBottom_toBottomOf="@id/vcd_et_code"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintLeft_toRightOf="@id/vcd_et_code"
        android:text="获取验证码"
        android:background="@color/white"
        android:textColor="@color/colorTextMain"
        android:textSize="16sp"
        android:gravity="center"
        />

</android.support.constraint.ConstraintLayout>
```

上面对应的几个点如下：
1. 对话框的margin值
2. 自定义对话框内容的字体大小:16sp
3. 控件命名：文件名首写字母_控件名称缩写_所执行功能
4. 相关控件的监听设置

完成的UI如下：

![](https://user-gold-cdn.xitu.io/2018/4/14/162c2da594f5495b?w=768&h=878&f=png&s=55264)

### Rxjava2完成倒计时功能

假设对Rxjava2的使用有一定的了解，知道Observable， disposable， subscribe的简单使用。

首先定义一个`Observable`和`disposable`对象：
```
    var disposable: Disposable? = null
    val countTimesObservable = Observable.interval(1, TimeUnit.SECONDS)
            .take(10)
            .doOnDispose {
                Logger.d(TAG, "取消订阅")
            }
            .doOnSubscribe {
                Logger.d(TAG, "开始计时")
                disposable = it
            }.doOnComplete {
                Logger.d(TAG, "结束计时")
                sendCode.text = context.getString(R.string.login_get_code)
                disposable?.let {
                    if (it.isDisposed) {
                        it.dispose()
                    }
                }
            }
            .observeOn(AndroidSchedulers.mainThread())
```
这里简单解释一下几个方法的意思：
`interval` 表示每间隔1秒发送一个 `Long` 型数据；  
`take` 表示取10个；  
doOnDispose：表示当取消订阅（调用`disposable.dispose()`）是产生的回调；  
`doOnSubscribe`：表示开始接受数据(调用`countTimesObservable.subscribe()`)是的回调，这里将disposable赋值，以便后续调用。  
`doOnComplete`：表示结束时（倒计10s完成）的回调；  
**这里有个点需要注意**：`doOnDispose` 一定要在 `doOnSubscribe` 之前声明  

这几个方法主要是做现场的保留与恢复，体现链式的好处。

接下面是完成按钮的监听，即点击 发送验证码 时的功能：
```
    sendCode.setOnClickListener {
        countTimesObservable.subscribe {
            val second = 9 - it
            sendCode.text = "已发送($second)"
            Timber.d(second.toString())
        }
    }
```
点击按钮开始倒计时，并修改显示文本，打印log如下:
```
04-14 14:50:57.915 28606-28606/com.swensun.swdesign D/ViewExtensionKt$countDown$countTimesObservable: 开始计时
04-14 14:50:58.922  D/ViewExtensionKt$countDown: 9
04-14 14:50:59.921  D/ViewExtensionKt$countDown: 8
04-14 14:51:00.921  D/ViewExtensionKt$countDown: 7
04-14 14:51:01.921  D/ViewExtensionKt$countDown: 6
04-14 14:51:02.919  D/ViewExtensionKt$countDown: 5
04-14 14:51:03.923  D/ViewExtensionKt$countDown: 4
04-14 14:51:04.922  D/ViewExtensionKt$countDown: 3
04-14 14:51:05.921  D/ViewExtensionKt$countDown: 2
04-14 14:51:06.922  D/ViewExtensionKt$countDown: 1
04-14 14:51:07.922  D/ViewExtensionKt$countDown$countTimesObservable: 结束计时
04-14 14:51:07.923  D/ViewExtensionKt$countDown: 0
04-14 14:51:07.924  D/ViewExtensionKt$countDown$countTimesObservable: 取消订阅
```
可以看到当点击按钮时开始倒计时，完成时调用onComplete完成现场恢复。

假如需要在倒计时中间停止怎么办：
设置dialog的监听， 取消订阅，修改代码如下：
```
    AlertDialog.Builder(context).setTitle("手机号码")
            .setNegativeButton("取消", { _, _ ->
                showToast("取消")
                disposable?.dispose()
            })
            .setPositiveButton("确认", { _, _ ->
                showToast("确认")
                disposable?.dispose()
            })
            .setView(frameLayout)
            .show()
```

开始计时后点击取消，log如下：
```
04-14 14:57:04.190 28980-28980/com.swensun.swdesign D/ViewExtensionKt$countDown$countTimesObservable: 开始计时
04-14 14:57:05.198 28980-28980/com.swensun.swdesign D/ViewExtensionKt$countDown: 9
04-14 14:57:06.198 28980-28980/com.swensun.swdesign D/ViewExtensionKt$countDown: 8
04-14 14:57:07.197 28980-28980/com.swensun.swdesign D/ViewExtensionKt$countDown: 7
04-14 14:57:08.197 28980-28980/com.swensun.swdesign D/ViewExtensionKt$countDown: 6
04-14 14:57:09.116 28980-28980/com.swensun.swdesign D/ViewExtensionKt$countDown$countTimesObservable: 取消订阅
```

在倒计时到5时取消订阅，调用相关方法恢复现场。

这里作为一个Rxjava2的使用场景，相比使用Runnable来说，结构清晰明了，也是整个函数变的更纯，以后会陆续介绍使用Rxjava2的使用场景，方便开发。2

完成示例代码：  
[github：dialog countdown Rxjava2](https://github.com/yunshuipiao/SwDesign/blob/0d662f85361b3e0fe7361015360270863cfa9a4d/app/src/main/java/com/swensun/swdesign/ui/codeexample/CodeExampleActivity.kt)

关于纯函数，下面会介绍函数式编程的基本概念和方法， 以及柯里化的相关知识。

总结：
1. 自定义AlertDialog 对话框
2. Rxjava2 基本函数的使用


