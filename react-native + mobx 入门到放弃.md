# react-native + mobx 入门到放弃

标签（空格分隔）： 未分类

---

作为一个刚开始看react-native的小白，找到的源码我都看不太懂，还有涉及redux的知识。后面同事介绍mobx，因此记录一下学习过程。

## redux 和 mobx 
过多的内容这里不做叙述，请看下面链接(可以知道是什么和为什么，很短)

[如何理解 Facebook 的 flux 应用架构？](https://www.zhihu.com/question/33864532/answer/57667838)

[理解 React，但不理解 Redux，该如何通俗易懂的理解 Redux？](https://www.zhihu.com/question/41312576/answer/90782136)

[MobX vs Redux: Comparing the Opposing Paradigms - React Conf 2017 纪要](https://zhuanlan.zhihu.com/p/25989654)

（对于redux，请参看[Redux 入门教程（三）：React-Redux 的用法](http://www.ruanyifeng.com/blog/2016/09/redux_tutorial_part_three_react-redux.html?hmsr=toutiao.io)）

务必多看几遍，下面开始。

## react-native

安装好所需的环境。
选择一个目录，执行
```
react-native init FirstReact
cd FisrtReact
npm install 
react-native run-adnroid
```
至此RN的demo可以正常启动。

### mobx

安装mobx：
`npm i mobx mobx-react --save`
安装mobx相关的包


`npm i babel-plugin-transform-decorators-legacy babel-preset-react-native-stage-0 --save-dev`
安装一些 babel 插件，以支持 ES7 的 decorator 特性(后面有不用的方法， ES6)

然后打开 .babelrc 文件配置 babel 插件：
```
{
  "presets": ["react-native"],
  "plugins": [
    "syntax-decorators",
    "transform-decorators-legacy"  ]
}
```
依赖安装完成。


在根目录下创建`mobxDemo`文件夹。
新建`AppState.js`文件:
```
import {action, observable, useStrict} from "mobx";
import {extendObservable} from "mobx";

class AppState {
    @observable
    timer = 101;
    
    addTimers() {
        this.timer += 1
    }
    resetTimer() {
        this.timer = 0;
    }
}
export default new AppState()
```
`@observable` 指明需要观察的对象(值，列表，数组，类等。)
其他的 `action`， `computed`可以后面去了解。

同目录下新建文件：MobxDemo.js
```
@observer
class App extends React.Component {
    render() {
        return (
            <View>
                <Text>当前的数是：{AppState.timer}</Text>
                <Button
                    onPress={() =>
                        AppState.addTimers()}
                    title='add'
                />
                <Button
                    onPress={() =>
                        AppState.resetTimer()
                    }
                    title='reset'
                />
            </View>
        );
    }
}
export default App;
```

在需要观察的地方加`@observer`。

### end

修改index.js文件：
```
import { AppRegistry } from 'react-native';
import App from './mobx/MobxDemo';
AppRegistry.registerComponent('FirstReact', () => App);
```

刷新运行程序，完成对timer的加和重置。

### ES6
在找资料的过程中，基本没有es6的相关实现。
中文文档：http://cn.mobx.js.org/

下面是ES6不带装饰器的写法：
AppState.js
```
import {action, observable, useStrict} from "mobx";
import {extendObservable} from "mobx";
class AppState {
    constructor() {
        let that = this;
        extendObservable(this, {
            timer: 11,
            get tenTimer() {
              return that.timer * 2
            },
            addTimers: action(function () {
                this.timer += 1
            }),
            resetTimer: action( () => {
                that.timer = 0
            })
        })
    }
}
export default new AppState()
```
MobxDemo.js
```
import React from "react";
import {observer} from "mobx-react";
import {View, Text, Button} from "react-native";
import AppState from './AppState'
const App = observer( class MobxDemo extends React.Component {
    render() {
        return (
            <View>
                <Text>当前的数是：{AppState.tenTimer}</Text>
                <Button
                    onPress={() =>
                        AppState.addTimers()}
                    title='add'
                />
                <Button
                    onPress={() =>
                        AppState.resetTimer()
                    }
                    title='reset'
                />
            </View>
        );
    }
})
export default App;
```

### result: 统一数据处理，观察。








