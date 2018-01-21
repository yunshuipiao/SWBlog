## 问题描述：
前端react-app使用nginx部署到服务器，浏览器访问80端口，页面请求同一服务器的后台api，由于端口不同，产生跨域。（域名不同也会跨域）

## 解决
nginx修改，在监听80端口的同时，对前端的请求转发的实际的后台服务。

```
//nginx 部分修改
server {
        listen 80;
        server_name  localhost;
        
        location / {
            root   /webserver;
            index  index.html index.htm;
            autoindex on;
            autoindex_exact_size on;
            autoindex_localtime on;
        }
        location /api/ {
            proxy_pass  http://120.78.202.210:7001/;
        }
    }
```
如上，将前端`/api` 下的请求转发到配置地址。

```
//前端代码修改
const getData = () => {
    axios.get("http://120.78.202.210/api/v1/zhihu/topimage/")
        .then(res => {
            console.log(res)
        }).catch(res => {
        console.log(res)
    })
}
```
如上，由于直接请求`"http://120.78.202.210:7001/v1/zhihu/topimage/`端口不同导致跨域，因此修改全部请求代码，将`/api/`下的请求转发到实际的服务器。


若是本地调试，可以添加chrome扩展。

![image.png](http://upload-images.jianshu.io/upload_images/1794675-52fe301f9fd91704.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
