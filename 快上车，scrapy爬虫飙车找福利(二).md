上一篇文章实现的最简单的爬虫，抓取了某个链接下第一次加载的所有图片链接。因为存在下拉刷新， 因此怎么获得该页面的全部答案是这篇文章需要去处理的事情。

## 方案：
1. 抓包，看下拉刷新向服务器发送什么请求，模拟去发送请求（结构化数据适用）
2. selenium执行js的滑动到底部，判断是否滑动到底部，以此循环。

## 具体实施；
这里选择使用方案2，方案1后面遇到再讨论。

一：selenium的简单使用。
这里涉及selenium的安装，[Selenium with Python](http://selenium-python.readthedocs.io/)官方文档讲解的特别简单。我使用的的chrome（可以配置无头属性）。
**注意**：需要将下载的driver配置环境变量，以便可以访问。
```
if __name__ == '__main__':
    options = webdriver.ChromeOptions()
    options.add_argument('headless')
    # driver = webdriver.Chrome(options=options)
    driver = webdriver.Chrome()
    driver.implicitly_wait(2)
    driver.get("https://www.zhihu.com/question/22856657")
    time.sleep(2)
    resSoup = BeautifulSoup(driver.page_source, 'lxml')
    items = resSoup.select("figure > span > div")
    print(len(items))
    for item in items:
        print(item)
    #driver.close()
```

在项目执行代码可以看到输出：`python zhihu/spiders/zhihu.py`
![image.png](http://upload-images.jianshu.io/upload_images/1794675-d4f605572789f5b5.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
可以看到共抓到198张图片，对去`data-src`属性即可得图片链接。
代码解释：
前三行用于启动一个无头的driver。如果需要查看加载的情况，只用第四行代码即可，执行完毕可以查看浏览器打开的url， 如下。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-cdb62ed14fdd9ecd.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

接下来三行：
第一行启动driver的隐式等待，简单意思就是：2秒内网页加载完毕就往下执行，否则就加载完2秒，继续往下执行。
第二行用于打开链接，相当于手动在地址栏输入链接。
第三行延时，等待网页加载。
后面的内容前面有接触。
如果输出结果和我的相差不大，那么继续下一步。

二：selenium执行js代码，加载全部内容。
```
if __name__ == '__main__':
    options = webdriver.ChromeOptions()
    options.add_argument('headless')
    # driver = webdriver.Chrome(options=options)
    driver = webdriver.Chrome()
    driver.implicitly_wait(2)
    driver.get("https://www.zhihu.com/question/22856657")
    time.sleep(2)

    count = 1
    css_selector = "#root > div > main > div > div.Question-main > div.Question-mainColumn > div > div.Card > button"
    css_selector2 = "#root > div > main > div > div.Question-main > div.Question-mainColumn > div > div.CollapsedAnswers-bar"
    while len(driver.find_elements_by_css_selector(css_selector)) == 0 and \
            len(driver.find_elements_by_css_selector(css_selector2)) == 0:
        print("count:" + str(count))
        js = "var q=document.documentElement.scrollTop=" + str(count * 200000)
        count += 1
        driver.execute_script(js)
        time.sleep(0.5)

    resSoup = BeautifulSoup(driver.page_source, 'lxml')
    items = resSoup.select("figure > span > div")
    print(len(items))
    for item in items:
        print(item)
```
![image.png](http://upload-images.jianshu.io/upload_images/1794675-6b5f7e52e4efd4e3.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
结果输出：共计抓取翻页13次，抓取662个图片链接。
中间部分新增的代码， count用于记录翻页次数。
css_selector和css_selector2用于判断某个元素是否存在，决定是否滑动到底部，如下。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-4562c343c9730e7f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

用一个循环去执行js代码，简单意思是滑动到距离页面顶部x的距离。经过测试，200000/页是比较好的选择。

至此，可以抓取某个链接下的所有图片。

三: selenium与spider middlewares的结合。
上面一切顺利之后， 接下来去使双方结合。
关于scrapy 的下载中间键(DOWNLOADER_MIDDLEWARES)：
简单来说，该中间键就是调用process_request， 将获取url的request经过处理，返回request，response，None三值之一。
返回 request：继续执行后面的process_request方法（包括中间键）
response：不知行后面的process_request方法，以此response结果直接返回，执行zhihu/spiders/zhihu.py  的回调方法。
具体请看官方文档： https://docs.scrapy.org/en/latest/topics/downloader-middleware.html
（还有spider middlewares， 本次未用到）
![image.png](http://upload-images.jianshu.io/upload_images/1794675-603185a32f35a80b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

话不多说，开始写代码：
在middlewares.py中定义自己的中间键：
```

class PhantomJSMiddleware(object):

    def __init__(self):
        options = webdriver.ChromeOptions()
        options.add_argument('headless')
        self.driver = webdriver.Chrome()
        self.driver.implicitly_wait(1)

    def process_request(self, request, spider):
        print(request.url)
        driver = self.nextPage(request)
        return HtmlResponse(url=request.url, body=driver.page_source, encoding="utf-8")
        # 翻页操作

    def nextPage(self, request):
        self.driver.get(request.url)
        time.sleep(2)
        count = 1
        css_selector = "#root > div > main > div > div.Question-main > div.Question-mainColumn > div > div.Card > button"
        css_selector2 = "#root > div > main > div > div.Question-main > div.Question-mainColumn > div > div.CollapsedAnswers-bar"
        # css_selector = "div > a > img"
        # print(len(self.driver.find_elements_by_css_selector(css_selector)))
        while len(self.driver.find_elements_by_css_selector(css_selector)) == 0 and len(
                self.driver.find_elements_by_css_selector(css_selector2)) == 0:
            print("count:" + str(count))
            js = "var q=document.documentElement.scrollTop=" + str(count * 200000)
            count += 1
            self.driver.execute_script(js)
            time.sleep(0.5)
        print(count)
        time.sleep(2)
        return self.driver

    @classmethod
    # 信号的使用
    def from_crawler(cls, crawler):
        print("from_crawler")
        # This method is used by Scrapy to create your spiders.
        s = cls()
        crawler.signals.connect(s.spider_opened, signal=signals.spider_closed)
        return s

    def spider_opened(self, spider):
        print("spider close")
        self.driver.close()
```
稍作解释：
`__init__`函数做初始化工作， 
`nextPage`函数根据得到的request做翻页操作。
**`process_request `**函数是中间键的必要函数， 启动中间键之后，yield生成器中的request都会经过该函数，然后返回结果（一定要在此函数执行return）。

后面是spiders信号的使用实例， 用于在spiders执行结束的时候做处理工作，比如关闭driver等操作。

**setttings.py**：配置对下载中间键的使用。
```
DOWNLOADER_MIDDLEWARES = {
    'scrapy.downloadermiddlewares.useragent.UserAgentMiddleware': None,
    'zhihu.middlewares.PhantomJSMiddleware': 100,
}
```
以上配置完毕，即可执行爬虫。
命令行执行 `scrapy crawl zhihu`启动爬虫，注意看日志，有如下输出。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-79212167ab055021.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

下拉刷新根据网速决定，所以count值会有不同。
可以看到这里抓取到了660张图片链接（允许个别误差）。

至此，对于使用scrapy结合selnium抓取动态网页已经不是问题。

对于某些需要登录的链接，打开url之后会直接去到登录页。下一篇文章介绍怎么使用selenium 去登录，保存cookies， 带着cookies去请求（可能是万能的登录方法，对于图片验证， 手机验证码也可能适用）。

微信：youquwen1226
github：https://github.com/yunshuipiao
欢迎来信一起探讨。






