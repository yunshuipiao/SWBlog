前两天在做登录注册的一个思考。无论采取jwt或者cookie验证的方法，前端一旦判断请求的验证无效，那么页面要重定向要登录页面去重新登录。
下面我的解决方法：
## 生成routerStore
在组件内部可以通过`withRouter`访问路由`history`。这里通过将history保存在routerStore，以后后续使用。
```
import {observable, action} from 'mobx'

class RouterStore {

    @observable history = null

    @action
    setHistory(history) {
        this.history = history
    }
}

export default  new RouterStore();
```
在之前App.js里面将Store引入。
```
import countStore from './mobx/CountStore'
import changeNameStore from './mobx/ChangeNameStore'
import routerStore from './mobx/RouterStore'


const stores = {
    countStore,
    changeNameStore,
    routerStore,
}
```

## 在axios里面进行全局登录判断

首先在Main组件中将history复制给store的history。
```
@withRouter
@inject("routerStore")
@observer
class Home extends React.Component {
    constructor(props) {
        super(props)
        // 没有super(props), 后面使用回报错
        // 定义state
        // bind方法
        // 其他初始化工作
        this.props.routerStore.history = this.props.history

    }
```

在axios的拦截器中做全局判断：
```
// Add a response interceptor
instance.interceptors.response.use( (response) => {
    // 返回错误判断
    console.log("filter error code")
    // 判断如果需要登录，routerStore.history.replace("/login")
    routerStore.history.replace("/login")
    return response;
},  (error) => {
    // 可以在后面的请求中catch
    console.log("interceptors response error")
    return Promise.reject(error);
});
```
这里做个测试，每一个请求都重定向到login。
测试成功，完整代码请查看:  
[github: https://github.com/yunshuipiao/react-mobx-axios](https://github.com/yunshuipiao/react-mobx-axios)

