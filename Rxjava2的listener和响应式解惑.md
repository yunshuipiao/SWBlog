
# Rxjava2的listener和响应式解惑

标签（空格分隔）： 知乎

---


最近两天一直在被 Rxjava2 的一些用法困扰，比如怎么去写一个 observable 的 listener， 怎么去体现响应式（观察者模式）。网上找了大部分资料，都只涉及怎么简单用订阅，链式发送字符串。
最终在 **stackoverflow** 找到了灵感，特此记录：

--------

## 实现一个Obervable的listener

[灵感来源：http://stackoverflow.com/questions/25457737/how-to-create-an-observable-from-onclick-event-android](http://stackoverflow.com/questions/25457737/how-to-create-an-observable-from-onclick-event-android)

这是一个例子，对大部分监听都适用。使用场景如下：
比如想在 button 点击后异步去获得数据，成功后再主线程更新 UI。实现如下：
```
//注意listener的实现
Observable.create(new ObservableOnSubscribe<View>() {
            @Override
            public void subscribe(final ObservableEmitter<View> e) throws Exception {
                listener = new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    //do something
                        if (!e.isDisposed()) {
                            e.onNext(v);
                        }
                    }
                };
            }
        }).subscribe(new Consumer<View>() {
            @Override
            public void accept(@NonNull View view) throws Exception {
                ((Button)view).setText(i++ + "---");
            }
        });
//button监听没有区别
button.setOnClickListener(listener);
```
这只是一个简单的实现例子，达到同样的效果不难。作为例子，可以实现这样的接口：
```
    private interface OnGetDataListener {
        void success(String data);
        void errorResult(Error error);
    }
/配合Rxjava2的observer的onNext， onComplete, onError, onSubscribe等去接收，错误时取消订阅等功能。
```

-------

## 对大部分事件的响应：比如list的add。

[灵感来源：http://stackoverflow.com/questions/28816691/how-can-i-create-an-observer-over-a-dynamic-list-in-rxjava](http://stackoverflow.com/questions/28816691/how-can-i-create-an-observer-over-a-dynamic-list-in-rxjava)

想在list.add()操作是获得响应，并打印列表。(这只是一个例子)
```
//首先申明list和一个subject观察者并初始化
private List<Integer> list = new ArrayList<>(0);
private Subject<List<Integer>> subject;

//oncreate()订阅
subject = PublishSubject.create();
subject.subscribe(new Consumer<List<Integer>>() {
        @Override
        public void accept(@NonNull List<Integer> integers) throws Exception {
            Log.d(TAG, "accept: " + integers);
        }
    });
    
//需要的时候发送：list加数据
    private void addInteger(int i) {
        list.add(i++);
        subject.onNext(list);
    }
```
这是大概流程，可扩展其他接收结果，取消订阅等。

-------

## 踩坑笔记：

今天上午在使用属性动画时，一直在研究重复restart结束的闪动效果。
解决办法：添加线性动画插值器LinearInterpolator即可
默认AccelerateDecelerateInterpolator(先加后减)





