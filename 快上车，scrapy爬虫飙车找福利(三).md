前面文章讲到怎么提取动态网页的全部内容。接下来返回文章一，怎么登录并且保存登录状态，以便带上cookies下次访问。
## 步骤
1. 利用selenium登录知乎， 登录成功后保存cookies 到本地。
2. 请求之前读取cookies， 加载cookies访问，看是否成功登录。

### 详细步骤：
1. 利用selenium登录知乎
回到文章一， 从[自从有了知乎，再也不用找福利了……](https://www.zhihu.com/collection/146079773)链接开始。
从提取标题开始：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-4ea604036fa81b0f.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

 ```
if __name__ == '__main__':
    url = 'https://www.zhihu.com/collection/146079773'
    res = requests.get(url, verify=False)
    resSoup = BeautifulSoup(res.content, 'lxml')
    items = resSoup.select("div > h2 > a")
    print(len(items))
```
`verify=False`:取消ssl的验证。
运行这段代码， 输出结果未0， 粘贴该网页到一个没有登录知乎的浏览器打开，重定向到登录页， 说明需要登录。

验证：
```
if __name__ == '__main__':
    url = 'https://www.zhihu.com/collection/146079773'
    # res = requests.get(url, verify=False)
    driver = webdriver.Chrome()
    driver.get(url)
    driver.implicitly_wait(2)
    res = driver.page_source
    resSoup = BeautifulSoup(res, 'lxml')
    items = resSoup.select("div > h2 > a")
    print(len(items))
```
执行代码，打开浏览器，显示知乎登录页，说明访问收藏夹需要登录。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-f04add34342acd70.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

登录技巧：
使用selenium打开登录页，设定延时时间（比如60s），手动输入账号密码登录知乎，60秒之后保存cookies到本地，完成登录。后续请求携带保存的cookie进行的登录。如果cookies过期，则简单重复这一步骤。
下面是详细步骤：
```

if __name__ == '__main__':

    ssl._create_default_https_context = ssl._create_unverified_context
    # url = 'https://www.zhihu.com/collection/146079773'
    url = "https://www.zhihu.com/signin"
    # res = requests.get(url, verify=False)
    driver = webdriver.Chrome()
    driver.implicitly_wait(5)
    driver.get(url)
    time.sleep(40)
    cookies = driver.get_cookies()
    pickle.dump(cookies, open("cookies.pkl", "wb"))
    print("save suc")
```
执行这段代码，看是否有cookies.pkl文件生成， 成功保存了cookies。

接下来用第二段代码去验证。
```
if __name__ == '__main__':
    cookies = pickle.load(open("cookies.pkl", "rb"))
    url = 'https://www.zhihu.com/collection/146079773'
    driver = webdriver.Chrome()
    driver.get("https://www.zhihu.com/signin")
    for cookie in cookies:
        print(cookie)
        driver.add_cookie(cookie)
    driver.get(url)
    driver.implicitly_wait(2)
    res = driver.page_source
    resSoup = BeautifulSoup(res, 'lxml')
    items = resSoup.select("div > h2 > a")
    print(len(items))
```
打开浏览器， 加载任意网页，接着加载cookies， 打开给定的url。运行代码， 
![image.png](http://upload-images.jianshu.io/upload_images/1794675-8b50984a6faa9568.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
如上，看到打印的cookies和提取的10个标题, 打开浏览器，页面不是登录页，说明登录成功。看cookies的有效时间。即可知道下次cookies的替换时间。

至此，最难定义的动态网页和登录问题已经解决。
下面就是怎么保存抓到的数据。
我的想法是先将需要登录的10页中所有问题和问题链接提取出来，保存为json文件以后后续处理。接着对每一个问题下的所有图片链接提取，保存或者直接下载就看个人选择了。

3. 提取该收藏夹下的全部链接保存到为json文件或者txt文件。
回到爬虫，现在我们已经有了cookies，可以不用selenium很快的保存问题列表。
将上一步保存的cookies.pkl复制一份到根目录，或者配置打开属性。
首先取消`settings.py `文件中的中间键， 
```
DOWNLOADER_MIDDLEWARES = {
    'scrapy.downloadermiddlewares.useragent.UserAgentMiddleware': None,
    # 'zhihu.middlewares.PhantomJSMiddleware': 100,
}
```
反爬虫策略：
对于访问过快，网页一般会静止访问或者直接封ip。因此对于需要登录的爬虫来说，限制访问速度，比如5秒/次， 或者每个ip每分钟最大访问次数。对于不需要登录的页面来说，使用代理ip是最好的选择，或者降低访问次数都是可行的办法。
  `settings.py `文件的设置， 
```
# Configure maximum concurrent requests performed by Scrapy (default: 16)
# CONCURRENT_REQUESTS = 32

# Configure a delay for requests for the same website (default: 0)
# See http://scrapy.readthedocs.org/en/latest/topics/settings.html#download-delay
# See also autothrottle settings and docs
DOWNLOAD_DELAY = 2
# The download delay setting will honor only one of:
# CONCURRENT_REQUESTS_PER_DOMAIN = 16
# CONCURRENT_REQUESTS_PER_IP = 16
```
这几个选项都是控制访问速度的，一般我设置`DOWNLOAD_DELAY`即可，即每两秒访问一次。

执行代码如下：
```
class Zhihu(scrapy.Spider):
    name = "zhihu"
    cookeis = pickle.load(open("cookies.pkl", "rb"))
    urls = []
    questions_url = set()
    for i in range(1, 11):
        temp_url = "https://www.zhihu.com/collection/146079773?page=" + str(i)
        urls.append(temp_url)

    def start_requests(self):
        for url in self.urls:
            request = scrapy.Request(url=url, callback=self.parse, cookies=self.cookeis)
            yield request

    def parse(self, response):
        print(response.url)
        resSoup = BeautifulSoup(response.body, 'lxml')
        items = resSoup.select("div > h2 > a")
        print(len(items))
        for item in items:
            print(item['href'])
            self.questions_url.add(item['href'] + "\n")

    @classmethod
    # 信号的使用
    def from_crawler(cls, crawler, *args, **kwargs):
        print("from_crawler")
        # This method is used by Scrapy to create your spiders.
        s = cls()
        crawler.signals.connect(s.spider_opened, signal=signals.spider_closed)
        return s

    def spider_opened(self, spider):
        print("spider close, save urls")
        with open("urls.txt", "w") as f:
            for url in self.questions_url:
                f.write(url)
```
命令行运行爬虫，查看`url.txt`文件。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-b579a8610e5c09a6.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

可以看到，成功抓取了44个链接，去除people， zhuanlan等几个无效链接，
后面即可从该文件读取内容，拼接链接，利用selenium做中间键提取所有的图片链接。

总结：这本文章讲了如何利用selenium去手动登录网站，保存cookies，以后后续登录（几乎可以登录所有的网站，**限制访问速度避免被封**）。

这三篇文章讲解了怎么使用scrapy去抓取想要的东西。现在无需使用框架，也可以涉及实现自己的爬虫。对于怎么保存图片，使用代理，后面会做简单介绍。
后面会写一篇怎么将爬虫部署在服务器上，利用docker搭建python环境去执行爬虫。

weixin：youquwen1226
github：https://github.com/yunshuipiao
欢迎来信探讨。



