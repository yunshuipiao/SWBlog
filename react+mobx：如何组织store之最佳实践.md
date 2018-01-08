上篇文章 [create-react-app + webpack + antd + less + mobx 的demo入门配置](https://www.jianshu.com/p/c51bbd6c5532) 简单介绍了一个笔者使用的最基本的react配置，简单引入了mobx。  
现在问题是 如果store越来越多，会导致引用混乱，非常不便于开发。因此在阅读了几篇文章之后，我尝试给出一个相对较好的使用方法。  
参考文章:  
[保有对子store的引用](https://stackoverflow.com/questions/43126615/what-is-the-best-way-to-create-a-single-mobx-store-for-an-app)  
[provider，inject引入react](http://frontendinsights.com/connect-mobx-react-router/)  
[官方文档](https://github.com/mobxjs/mobx-react)  

## 统一管理 Store
针对越来越多的`store`, 可以考虑使用 一个总 Store，保持对每个子 Store的引用。如有必要，可以在子Store中传入父Store，让子Store也可以访问到父Store。
```
import CountStore from './CountStore'
import ChangeNameStore from './ChangeNameStore'
class Store {
    constructor() {
        this.countStore = new CountStore()
        this.changeNameStore = new ChangeNameStore()
    }
}
export default new Store()
```

## 实践一
如上，简单创建的两个子Store。在需要使用数据的地方，`import store`即可， 看下面例子：
  
```
const ChangeName = (observer( ( {} ) => {
    return (
        <div className='Change'>
            <div>{store.changeNameStore.name}</div>
            <div className="buttons">
                <Button type="primary" className="btn" onClick={() =>
                    store.changeNameStore.changeName()
                }>change</Button>
            </div>
        </div>
    );
}))
export default ChangeName;
```
`ChangeNameStore`如下：
```
import {observable, action} from 'mobx'

class ChangeNameStore {

    @observable name = "sun"

    @action
    changeName() {
        if (this.name === "sun") {
            this.name = "wen"
        } else {
            this.name = "sun"
        }
    }
}

export default ChangeNameStore;
```

运行程序，点击按钮可以看到name改变。

这种方式引用方便，哪里用到store就哪里import。弊端就是如果store的层级越来越多，会导致代码难以编写。

## 实践二
官方参考redux，给出了Provider和inject组件，推荐使用DI方式去管理store，这也是我觉得最好的方式。
首先也是由一个stores保持有对所有子store的引用,接着使用Provider组件将stores传递给父组件。
```
import CountStore from './mobx/CountStore'
import ChangeNameStore from './mobx/ChangeNameStore'

const countStore = new CountStore();
const changeNameStore = new ChangeNameStore();

const stores = {
    countStore,
    changeNameStore,
}

class App extends Component {
    render() {
        return (
            <Provider {...stores}>
                <Home/>
            </Provider>
        );
    }
}

export default App;
```
如上，下面是怎么使用`inject`是自组件可以访问store。
```
const ChangeName = inject("changeNameStore")(observer( ( {changeNameStore} ) => {
    return (
        <div className='Change'>
            <div>{changeNameStore.name}</div>
            <div className="buttons">
                <Button type="primary" className="btn" onClick={() =>
                    changeNameStore.changeName()
                }>change</Button>
            </div>
        </div>
    );
}))

export default ChangeName;
```
推荐使用无状态组件，将需要的store直接传入组件，其他使用方法与之前无区别。
如此以来，无论store的层级有多少，针对单一功能组件原则， 可以将最小的store引入，方便管理。

最佳实践的代码见github：https://github.com/yunshuipiao/react-web-demo
