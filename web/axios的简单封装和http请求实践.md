对axios做一个简单的封装和实现的思路，主要实现：
* 统一捕获接口报错（拦截器）
* 基础验证 （auth）
----
```
import axios from 'axios'
// 最基本的全局配置
const instance = axios.create({
    baseURL: "https://movie.douban.com/j/",
    timeout: 2500
})
```

```
// Add a request interceptor， 发送请求之前
instance.interceptors.request.use( (config) => {
    //add auth
    return config;
},  (error) => {
    return Promise.reject(error);
});
```
```
// Add a response interceptor
instance.interceptors.response.use( (response) => {
    // 返回错误判断(服务器端定义的err code)
    //保存auth token
    return response;
},  (error) => {
    // 可以在后面的请求中catch
    return Promise.reject(error);
});
```

下面是最基本的请求：
```
//可以参考定义post， put， delete
const requests = {
    get: url =>
        instance.get(url)
            .then(res => {
                console.log(res)
                return res.data
            })
}
```

上面是基本的网络配置。

接下来就可以针对不用的模块，去发送不同的请求：
```
const Movies = {
    all: () => requests.get('/new_search_subjects?tags=%E4%B8%AD%E5%9B%BD&start=0')
}
export default {
    Movies,
}
```
如上，可以将电影模块所有相关的网络请求都放在同一个对象里面。
```
const click_button = () => {
    agent.Movies.all().then()
        .catch()
        .finally()
}
```
在组件中就可以调用如下函数，进行数据处理，异常处理。finally可用作loading的取消等等。
**注意：如果`response`有错误在interceptors被catch，那么后续的then将不会被调用。这个点可以用作对用户的错误请求提示，而不必在每个请求后手动添加catch。**

关于`finally`的使用：
`npm install axios promise.prototype.finally --save`先安装依赖。
之后如下调用，即可使用finally。
```
import promiseFinally from 'promise.prototype.finally'

promiseFinally.shim();

ReactDOM.render(<App />, document.getElementById('root'));
registerServiceWorker();
```
各业务不同，因此只是简写大概，欢迎交流。

参考资料：
[给axios做个挺靠谱的封装(报错,鉴权,跳转,拦截,提示)](https://juejin.im/post/59a22e71518825242c422604)   
[mobx组织实践和http封装组织](https://github.com/gothinkster/react-mobx-realworld-example-app)
[axios 配置finally](https://github.com/axios/axios/blob/master/COOKBOOK.md)


github：[https://github.com/yunshuipiao](https://github.com/yunshuipiao)

