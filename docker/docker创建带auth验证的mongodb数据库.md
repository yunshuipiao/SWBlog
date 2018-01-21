#### 记录本身，即已是反抗

首先下载`mongo`镜像，简单命令不做过多叙述，前面文章有介绍怎么基本使用docker。
之后编写`docker-compose.yml`文件，运行 `docker-compose  up -d`生成容器并后台启动。

## root创建
```
// docker-compose.yml
// command：启动是的命令行参数，添加认证auth
version: '2'
services:
  mongodb:
    image: mongo
    ports:
        - 27017:27017
    volumes:
        - "./data/configdb:/data/configdb"
        - "./data/db:/data/db"
    command: mongod --auth
    tty: true
```
运行`docker ps`查看容器是否运行。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-136eb8fae9827cf0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
进入docker容器并进入mongo命令行。
```
docker exec -it 4 /bin/bash
mongo
```
此时`show dbs`无法执行，需要认证。
切换到`admin`并创建root用户：
```
use admin
db.createUser({ user: 'root', pwd: 'root', roles: [ { role: "userAdminAnyDatabase", db: "admin" } ] })
```
![image.png](http://upload-images.jianshu.io/upload_images/1794675-44910583fff6364c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
如上，可以看到root用户创建成功。`exit`退出mongo命令行，带验证的mongodb已经创建成功。

## 普通用户创建
接下来创建普通用户，并演示验证。
再次执行`mongo` 进入mongodb命令行。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-a483f58b493b019c.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到root用户验证成功,并且可以查看数据库。

下面创建普通用户，和创建root用户基本一致，只是角色不同 。
```
//拥有对数据库app的读写权限。
use app
db.createUser(
  {
    user: "swen",
    pwd: "swen",
    roles: [ { role: "readWrite", db: "app" }
             ]
  }
)
```
![image.png](http://upload-images.jianshu.io/upload_images/1794675-31fc5dcab4483f62.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
创建成功并`exit`退出，swen用户可以对(只能对)app进行操作。
下面做基本演示。

基本验证：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-e5ac3d087e612444.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
演示往 `test`集合插入简单数据，并查看数据库状态。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-eb42bd1ec355e275.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

介绍到此完毕。
参考资料：
[MongoDB 用户名密码登录](https://www.jianshu.com/p/79caa1cc49a5)  
[MongoDB 常用基本命令](https://www.jianshu.com/p/140cd046c748)

github: [https://github.com/yunshuipiao](https://github.com/yunshuipiao)


