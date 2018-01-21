以前也有写过爬虫，抓过网易云歌单和豆瓣读书的数据，当时有两个问题解决的不够好， 自动化和登录。最近花时间用scrapy去写，自认为更好的解决了上述问题。这篇文章当作一个记录，也可当作学习教程(需要BeautifulSoup， selenium基本知识)。
## 目标
用scrapy去抓取[自从有了知乎，再也不用找福利了……](https://www.zhihu.com/collection/146079773)收藏夹下每一个答案下的全部图片。

### 简易步骤
1. 账号登录知乎，抓取全部答案的链接（去除重复文章，大概39个答案）。
```
{'url': '/question/36007260', 'title': '女生坚持健身是种什么样的体验？', 'dec': ['健身']}
{'url': '/question/22132862', 'title': '女生如何选购适合自己的泳装？', 'dec': ['泳装']}
{'url': '/question/22918070', 'title': '女生如何健身锻造好身材？', 'dec': ['健身']}
{'url': '/question/24214727', 'title': '大胸妹子如何挑选合身又好看的比基尼？', 'dec': [ '比基尼']}
{'url': '/question/263451180', 'title': '你觉得健身时哪个训练动作最酷炫？', 'dec': ['健身']}
{'url': '/question/28586345', 'title': '有马甲线是种怎样的体验？', 'dec': ['马甲线']}
{'url': '/question/68734869', 'title': '2017 年，你解锁了哪些运动技能？可以用「视频」展示么？', 'dec': ['解锁']}
{'url': '/question/31983868', 'title': '有什么好看的泳装推荐？', 'dec': ['泳装']}
```
如上，对每一个问题提取url， 标题和关键字，保存到json文件方便后续工作。

2. 对每一个答案，抓取该答案下所有图片链接， 保存或者下载(此处用到selenium)。
3. 结果：半天时间抓去图片20000+张， 部分如下：
![屏幕快照 2017-12-23 23.18.04.png](http://upload-images.jianshu.io/upload_images/1794675-66a3ee4d18d86bfc.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

### 详细步骤
一. 先从2开始，目标：[如何拍好私房照？](https://www.zhihu.com/question/22856657)链接下的所有图片。
1.  新建工程 :`scrapy start zhihu`
简单介绍一下，工程目录：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-1f215b58665542bb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
`zhihu/spiders`:爬虫的主要文件。
`zhihu/items.py`:需要抓取的数据结构
`zhihu/middlewares.py`：中间键文件，selenium处理动态网页。
`zhihu/pipelines.py`:保存items中定义的数据结构或者下载图片（处理item）。

其余文件都是额外生成，不属于爬虫目录。
  `cookies.pkl`:保存登录的cookies， 下次登录。
`questions.json`: 保存所有问题的链接，方便后续使用。
上面两个文件都是在第一步用到， 后续再讲。

2. 最简单的爬虫
  相信看到这的童鞋都有用过requests库， BeautifulSoup去写过简单的爬虫。
这里不做讨论。
在`zhihu/spiders`下新建zhihu.py文件，从这里开始。
```
import scrapy
class Zhihu(scrapy.Spider):
    name = "zhihu"
    urls = ["https://www.zhihu.com/question/22856657"]
    yield request

    def start_requests(self):
        for url in self.urls:
            request = scrapy.Request(url=url, callback=self.parse)

    def parse(self, response):
        print(response.url)
        print(response.body)
```
`name`定义了爬虫的名字，`urls`定义需要爬取的链接，从`start_requests`开始，`yield`对每一个`url`执行得到生成器， `scrapy`经过网络请求返回后调用`parse`函数。
接下来在项目目录执行`scrapy crawl zhihu ` 启动爬虫，看输出结果。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-a3f938efdfa01fb6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看到输出的url和html代码，最简单的爬虫执行完毕。
**关键**：该开始运行一定要日志输出。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-9650a7123cd5a4b1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
遇到上述问题，需要打开`settings`文件做如下设置：
```
#重试设置
RETRY_ENABLE = False

# 日志输出
LOG_ENABLED = True
LOG_LEVEL = "INFO"
```
取消失败重试，设置日志级别和是否输出（对爬取无影响）。
```
# Obey robots.txt rules
ROBOTSTXT_OBEY = False

DOWNLOADER_MIDDLEWARES = {
    'scrapy.downloadermiddlewares.useragent.UserAgentMiddleware': None,
}
```
下载中间键中取消默认的UserAgent设置，以。及对robos.txt的设置。


3. 提取图片链接。（BeautifulSoup）
关于`BeautifulSoup`的使用可以看官方文档，简单明了。
获取图片的css selector，提取图片链接。
打开该url， 右击任何一张图片，检查即可看到该图片的位置。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-b4eb3c96a4959220.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

![image.png](http://upload-images.jianshu.io/upload_images/1794675-fc584709d5bd1ca5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
如上所示，即可找到该img的位置。
接下来看代码：
```
import scrapy
from bs4 import BeautifulSoup

class Zhihu(scrapy.Spider):
    name = "zhihu"
    urls = ["https://www.zhihu.com/question/22856657"]


    def start_requests(self):
        for url in self.urls:
            request = scrapy.Request(url=url, callback=self.parse)
            yield request

    def parse(self, response):
        print(response.url)
        resSoup = BeautifulSoup(response.body, 'lxml')
        items = resSoup.select("figure")
        print(len(items))
        for item in items:
            print(item)
            print(item.img)
            print(item.img['data-original'])
```
`parse`函数中，使用BeautifulSoup对网页分析。
结果如下：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-cb0fa828c44f1da6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
对比输出，共计找到30个`figure`标签。
分别对`figure`,`figure`的子标签`img` 及其`data-original`属性进行输出。
粘贴属性到浏览器打开即可看到图片。
到此为止， 对[如何拍好私房照？](https://www.zhihu.com/question/22856657)链接第一页的图片基本抓取完成。后面介绍怎么使用selenium对该链接下所有图片进行抓取。

有疑问请加weixin：youquwen1226，一起探讨。
github：https://github.com/yunshuipiao







