前面的文章介绍了怎么用scrapy去完成一个爬虫，涉及动态抓取和登录等操作。这篇文章简单介绍怎么让爬虫运行在服务器的docker里。

## 步骤
1. 利用sshfs将服务器磁盘挂载到本地，实现本地开发，省去同步代码等步骤。
2. docker通过pull或者Dockerfile拉取镜像。
3. 通过docker-compose配置镜像，在启动的镜像里启动爬虫。

### 详细步骤：
首先安装sshfs工具，可以参考网上教程。
接着切换到个人目录，新建 `aliyun`文件夹。
```
localhost:~ swensun$ cd ~
localhost:~ swensun$ mkdir aliyun
localhost:~ swensun$ sshfs 120.78.202.210:/  aliyun/
```
之后输入密码，打开aliyun目录，即可看到的服务器上的目录挂载到了本地，可以本地编辑，保存。

2. 切换到个人目录，新建docker文件夹，再新建爬虫的spider文件夹。
路径如下：
`/Users/swensun/aliyun/home/swensun/App/docker/spider`
* 下载Dockerfile（也可以使用pull）下载docker镜像。

在该目录下新建Dockerfile文件，最简单内容如下，
```
FROM python:latest

RUN apt-get update \
	&& apt-get install -y vim \
	&& rm -rf /var/lib/apt/lists/*
```
这里执行了最简单的操作，下载python镜像，并执行RUN后面的命令。

ssh连接到服务器， 切到相应目录，
执行`sudo docker build -t pythonspider .` 命令, 下载生成镜像, 输出如下：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-00bd5a9553f793dc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
执行`sudo docker images`命令，可以看到已经生成了pythonspider镜像。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-a1dd50af65a2c547.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

3. 将本地写的爬虫复制到Dockerfile目录，并新建`docker-compose.yml`文件。
文件目录如下：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-f9e2d7579c023e62.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

`docker-compose.yml`文件内容：
```
version: '2'
services:
  spider:
    image: pythonspider      
    volumes:
      - ./zhihu:/spider
    tty: true
```
简单解释：services可启动多个服务，比如数据库，nignx和执行程序配合生成一个容器。该服务叫spider，利用前面下载的pythonspider镜像，将`./zhihu`数据卷同步到docker中。tty保证创建容器后保持后台运行，以免创建后关闭。
执行如下命令：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-e138708e56935f43.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
![image.png](http://upload-images.jianshu.io/upload_images/1794675-61f270814ff66bc9.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
如上，可以看到创建的容器正在运行。
下面进入容器运行爬虫
![image.png](http://upload-images.jianshu.io/upload_images/1794675-30cca43cac08bebe.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到数据卷spider已经在根目录。进入根目录，执行爬虫(需要安装python需要的包，也可以在前面的dockerfile中安装)。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-833dadce764da7d0.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看到和本地输出了一样的结果，说明docker中运行scrapy成功。

关于Dockerfile和docker-compose的其他命令及其高级用法，我也不是很懂，后面慢慢研究。

总结：
本文介绍了sshfs工具的使用，以及docker的安装与部署。
（Dockerfile和docker-compose）。


weixin：youquwen1226
github：https://github.com/yunshuipiao

欢迎来信一起探讨。



