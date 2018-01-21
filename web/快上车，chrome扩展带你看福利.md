今天偶然看到 [用技术方式炸鱼](https://github.com/xlzd/zhfishhook) 很感兴趣。一则最近也在看web的东西，二则前段时间scrapy爬取的图片不是很方便去查看。因此决定决定照葫芦画瓢，尝试着从零写一个chrome 扩展。

## 参考资料
网上这方面的资料还挺多，我参考的是：[https://github.com/sxei/chrome-plugin-demo](https://github.com/sxei/chrome-plugin-demo)
从零开始怎么去写扩展，看完基本上可以完成一个简单的扩展，其余自定义的地方需要一点点时间研究。

## 扩展演示：
使用步骤：
1. 安装扩展：打开chrome扩展页， 将扩展包（[下载地址](https://github.com/yunshuipiao/zhihu-image-chrome-extension/releases/tag/1.0)）解压并拖到扩展页安装，
![image.png](http://upload-images.jianshu.io/upload_images/1794675-61fe3b11b263a656.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

右上角出现孔雀图标，安装成功。
2. 继续上次的福利，[如何拍好私房照](https://www.zhihu.com/question/22856657)。

打开上述页面，左上角出现图标开关。点击即可查看，再次点击即可关闭。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-9677546609c1fdcb.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
3. 注意：由于下拉刷新的缘故，网页不会一次返回本答案的全部图片。

一次加载的图片看完，可手动下拉刷新，点击图标继续浏览图片。

![image.png](http://upload-images.jianshu.io/upload_images/1794675-2b1ebdafad4b0576.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

## 开发步骤：
看完上面的链接，大概知道扩展的本目录结构，我的如下：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-7a9c3c63f81ba5c1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
最重要的文件：`manifest.json`。
`manifest2.json`是上述带解释的配置文件，方便对照。
```
{
  "name": "zhihuimage",
  "description": "zhihuimage",
  "version": "1.0",
  "manifest_version": 2,
  "browser_action": {
    "default_popup": "html/zhihu.html",
    "default_icon": "img/ic_launcher.png"
  },
  "icons": {
    "16": "img/ic_launcher.png",
    "48": "img/ic_launcher.png",
    "128": "img/ic_launcher.png"
  },
  // 会一直常驻的后台js或者页面
  "background": {
    // 2种指定方式，如果指定JS，那么会自动生成一个背景页
    "page": "html/zhihu.html"
    //"scripts": ["js/background.js"]
  },
  "content_scripts": [
    {
      "matches": ["https://www.zhihu.com/question/*"],
      "js":["js/content_script.js"],
      "css":["css/zhihu.css"]
    }
  ],
  "commands": {
    "_execute_browser_action": {
      "suggested_key": {
        "default": "Ctrl+Shift+E",
        "mac": "MacCtrl+Shift+E"
      },
      "description": "open html"
    }
  },
  // 普通页面能够直接访问的插件资源列表，如果不设置是无法直接访问的
  "web_accessible_resources": [
    "js/inject.js",
    "img/ic_launcher.png"
  ]
}
```

对照另一个文件即可看懂。

下面是`content_script.js`文件：
扩展比较简单，思路就是在`body`插入一个`div`元素，里面放置图片，形成列表。
```
//操作document
let div_str = '<div class="zhihu-image">\n' +
    '            <div class="zhihu-image-inner"></div>\n' +
    '        </div>'

let node = document.createElement("div")
let img = document.createElement("img")
img.src = chrome.extension.getURL("img/ic_launcher.png");
img.alt = "zhihu"
img.title = "zhihu"
img.classList = "zhihu"
img.addEventListener("click", () => {
    // 点击事件
    console.log("click")
    loadImg()
})
node.appendChild(img)
document.body.appendChild(node)


let imageNode = document.createElement("div")
imageNode.innerHTML = div_str
imageNode.getElementsByClassName("zhihu-image")[0].hidden = true
document.body.appendChild(imageNode)


const loadImg = () => {
    let node = document.getElementsByClassName("zhihu-image-inner")[0]
    let nodeHidden = document.getElementsByClassName("zhihu-image")[0]
    console.log(node.hidden)
    if (nodeHidden.hidden === true) {
        nodeHidden.hidden = false
        console.log(node)
        images = document.querySelectorAll("span > figure > span > div")
        console.log(images)
        for(i = 0; i < images.length; i++) {
            let img = document.createElement("img")
            img.src = images[i].dataset.src
            img.className = "image"
            node.appendChild(img)
        }
    } else {
        nodeHidden.hidden = true

    }
}
```
这是`content-script.js`全部代码，css见文末github。

全部完成，现在可以编写自己的扩展了。
涉及到不用页面见通信，background属性的应用，请查看官方文档。

微信：youquwen1226  
github: https://github.com/yunshuipiao/zhihu-image-chrome-extension
