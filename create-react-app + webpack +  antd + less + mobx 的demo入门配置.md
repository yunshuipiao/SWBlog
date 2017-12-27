需要安装有较新版本的node,下面直接开始。
1. 脚手架工具`create-react-app`
```
npm install -g create-react-app
create-react-app react-web-demo
cd react-web-demo
yarn start
```
命令如上，然后打开 http://localhost:3000/查看app，可以看到

![image.png](http://upload-images.jianshu.io/upload_images/1794675-af550b2262f1abc1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
则说明最基本的安装已经。

2. 打开自定义配置`yarn eject`
在`create-react-app react-web-demo`命令之后，官方提供了4个命令。
分别是
`yarn start`: 启动服务并在浏览器中查看。
`yarn build`:build 应用程序，可以部署发布。
`yanr eject`: 打开自定义配置。
使用IDE打开项目目录，结构不做太多说明， 如下：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-fe25bdfa51eedbc4.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
作为最基本的配置，可以满足大部分的开发需求，但是需要加一些自定义的配置，比如less的使用等。
`yarn eject`打开自定义，不可逆。
可以看到多了`script`和`config`目录。

3. 添加`less`支持
首先安装开发所需模块
```
npm install --save-dev less
npm install --save-dev less-loader
```
接着在`config/webpack.config.dev.js`做如下修改：
```
{
                        // modify
                        test: [/\.css$/, /\.less$/],
                        use: [
                            require.resolve('style-loader'),
                            {
                                loader: require.resolve('css-loader'),
                                options: {
                                    importLoaders: 1,
                                },
                            },
                            {
                                loader: require.resolve('postcss-loader'),
                                options: {
                                    // Necessary for external CSS imports to work
                                    // https://github.com/facebookincubator/create-react-app/issues/2677
                                    ident: 'postcss',
                                    plugins: () => [
                                        require('postcss-flexbugs-fixes'),
                                        autoprefixer({
                                            browsers: [
                                                '>1%',
                                                'last 4 versions',
                                                'Firefox ESR',
                                                'not ie < 9', // React doesn't support IE8 anyway
                                            ],
                                            flexbox: 'no-2009',
                                        }),
                                    ],
                                },
                            },
                            //add
                            {
                                loader: require.resolve('less-loader'), // compiles Less to CSS
                            }
                        ],
                    },
```

在`config/webpack.config.prod.js`做如下修改：
```
{
                        // modify
                        test: /\.(css|less)$/,
                        loader: ExtractTextPlugin.extract(
                            Object.assign(
                                {
                                    fallback: {
                                        loader: require.resolve('style-loader'),
                                        options: {
                                            hmr: false,
                                        },
                                    },
                                    use: [
                                        {
                                            loader: require.resolve('css-loader'),
                                            options: {
                                                importLoaders: 1,
                                                minimize: true,
                                                sourceMap: shouldUseSourceMap,
                                            },
                                        },
                                        {
                                            loader: require.resolve('postcss-loader'),
                                            options: {
                                                // Necessary for external CSS imports to work
                                                // https://github.com/facebookincubator/create-react-app/issues/2677
                                                ident: 'postcss',
                                                plugins: () => [
                                                    require('postcss-flexbugs-fixes'),
                                                    autoprefixer({
                                                        browsers: [
                                                            '>1%',
                                                            'last 4 versions',
                                                            'Firefox ESR',
                                                            'not ie < 9', // React doesn't support IE8 anyway
                                                        ],
                                                        flexbox: 'no-2009',
                                                    }),
                                                ],
                                            },
                                        },
                                        {
                                            //add
                                            loader: require.resolve('less-loader'),
                                        }
                                    ],
                                },
                                extractTextPluginOptions
                            )
                        ),
                        // Note: this won't work without `new ExtractTextPlugin()` in `plugins`.
                    },
```

下面测试是否添加成功。
在src目录下新建component文件夹放置子组件，index.js和index.less作为根组件。
```
//index.js

import React from 'react'
import './index.less'

const Home = () => {
    return(
        <div className='Home'>
            <div>react demo</div>
            <div>react demo</div>
            <div>react demo</div>
        </div>

    );
}

export default Home
```
```
//index.less

.Home {
  background: beige;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: flex-end;
}
```

修改`App.js`:
```
import React, { Component } from 'react';
import logo from './logo.svg';
import './App.css'
import Home from './component/index'

class App extends Component {
  render() {
    return (
      <div className="App">
        <Home />
      </div>
    );
  }
}

export default App;
```

打开浏览器，可以看到如下图，说明配置成功。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-38e0807b98dcea1b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

4. 使用antd开发，并添加按需加载配置。
```
npm install --save antd
npm install babel-plugin-import --save-dev
```
修改文件如下：
```
//webpack.config.dev.js
{
                        test: /\.(js|jsx|mjs)$/,
                        include: paths.appSrc,
                        loader:  require.resolve('babel-loader'),
                        options: {

                            // This is a feature of `babel-loader` for webpack (not Babel itself).
                            // It enables caching results in ./node_modules/.cache/babel-loader/
                            // directory for faster rebuilds.
                            cacheDirectory: true,
                            //add
                            plugins: [["import", { "libraryName": "antd", "style": true }]]
                        },


                    },
```

```
//webpack.config.prod.js
{
                        test: /\.(js|jsx|mjs)$/,
                        include: paths.appSrc,
                        loader: require.resolve('babel-loader'),
                        options: {

                            compact: true,
                            plugins: [["import", { "libraryName": "antd", "style": true }]]
                        },
                    },
```

测试是否配置成功。
修改`index.js`文件如下： 
```

import React from 'react'
import './index.less'
import {Button} from 'antd'

const Home = () => {
    return(
        <div className='Home'>
            <Button type="primary">Button</Button>
            <Button type="primary">Button</Button>
            <Button type="primary">Button</Button>
        </div>
    );
}
export default Home
```
看浏览器界面如下则配置成功。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-1440926145651e9e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

最后执行`npm run-script build`生成app，按照提示 http://localhost:5000/ 查看浏览器显示结果是否一样，一样则配置成功。
**注意**：这一步浏览器可能会有缓存，build之后建议清除浏览器缓存再查看

配置结束，关于mobx的使用，可参考另一篇文章。

---

接上次所说，另外一篇文章是关于RN + mobx， 部分内容可能不合适，今天作为补充。


## redux 和 mobx 
过多的内容这里不做叙述，请看下面链接(可以知道是什么和为什么，很短)
[如何理解 Facebook 的 flux 应用架构？](https://www.zhihu.com/question/33864532/answer/57667838)
[理解 React，但不理解 Redux，该如何通俗易懂的理解 Redux？](https://www.zhihu.com/question/41312576/answer/90782136)
[MobX vs Redux: Comparing the Opposing Paradigms - React Conf 2017 纪要](https://zhuanlan.zhihu.com/p/25989654)

（对于redux，请参看[Redux 入门教程（三）：React-Redux 的用法](http://www.ruanyifeng.com/blog/2016/09/redux_tutorial_part_three_react-redux.html?hmsr=toutiao.io)）

## mobx + mobx-react
```
npm install mobx
npm install mobx-react
```
此时可以使用mobx开发，接下来配置启用`decorators`装饰器。
```
yarn add babel-plugin-transform-decorators-legacy -D
```
并在package.json文件中修改如下配置：
```
  "babel": {
    "plugins": [
      "transform-decorators-legacy"
    ],
    "presets": [
      "react-app"
    ]
  },
```
这是可以用方便易懂的装饰器进行开发。修改文件如下：
```
//component/index.js
const Home = observer( () => {
    return (
        <div className='Home'>
            <div>{startNum.startNum}</div>
            <div>{startNum.startNum}</div>
            <div className="buttons">
                <Button type="primary" className="btn" onClick={() => {
                    startNum.inc()
                }}>inc</Button>
                <Button type="primary" className="btn" onClick={() => {
                    startNum.dec()
                }}>dec</Button>
                <Button type="primary" className="btn" onClick={() => startNum.reset()}>reset</Button>
            </div>
        </div>
    );
} )

export default Home
```

```
//component/index.less
.Home {
  margin-top: 100px;
  display: flex;
  flex-direction: column;
  justify-content: flex-start;
  align-items: center;
  .buttons {
    display: flex;
    flex-direction: row;
    margin-top: 20px;
    .btn {
      margin: 0 10px;
    }
  }
}
```

在src目录新建mobx/index.js文件，作为最基本的store数据源。
```
class DemoStore {

    @observable startNum = 10

    @action
    inc() { this.startNum += 1 }

    @action
    dec() { this.startNum -= 1}

    @action
    reset() { this.startNum = 0 }
}
export default new DemoStore()
```

`yarn start`后打开浏览器，看到下图并且可以操作，配置成功。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-1bf2688d5167c77e.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

关于mobx的其他用法可以查看官方文档。
随着项目变大，如何对mobx的store进行组合，也是我目前在研究的问题。
