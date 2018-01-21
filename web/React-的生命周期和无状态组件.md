本篇文章打算简单介绍`react`的生命周期，并尝试与无状态组件做个对比。
在我之前的文章中，我几乎都使用无状态组件。在使用`redux`或者`mobx`之后，理论上可以全部使用无状态组件。

##  React.Compont  生命周期
```
class Hello extends React.Component {
    constructor(props) {
        super(props)
        console.log("constructor")
        this.state = {
            age: 0
        }
    }

    componentWillMount() {
        console.log("componentWillMount")
    }

    render() {
        console.log("render")
        return (
            <div className='Hello'>
                <div>{this.props.name}</div>
                <div>{this.state.age}</div>
                <Button type="primary" onClick={() =>
                    this.setState({
                        age: this.state.age === 0 ? 111 : 0
                    })}
                >Click</Button>
            </div>
        );
    }

    componentDidMount() {
        console.log("componentDidMount")
    }

    componentWillReceiveProps() {
        console.log("componentWillReceiveProps")
    }

    shouldComponentUpdate() {
        console.log("shouldComponentUpdate")
        return true
    }

    componentWillUpdate() {
        console.log("componentWillUpdate")
    }

    componentDidUpdate() {
        console.log("componentDidUpdate")
    }

    componentWillUnmount() {
        console.log("componentWillUnmount")
    }

    componentDidCatch() {
        console.log("componentDidCatch")
    }
}

const Home = observer(() => {
    return (
        <div className='Home'>
            <Count />
            <ChangeName />
            <Hello name="swen"/>
        </div>
    );
})
```
如上，`Component`可以是UI模块化， 可以单独设计，重用，是一个抽象类，基本使用如上。
至少需要一个render方法，父组件可以通过props传递数据到子组件，组件内部通过state去改变数据。
-------
如上，生命周期大概分为三部分
#### Mounting

![image.png](http://upload-images.jianshu.io/upload_images/1794675-5027216c81e0c369.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

这个时期函数调用如下，几个点需要注意：
`constructor()`函数用于初始化state和props，绑定方法，必须调用`super(props)`。
`componentDidMount`在`render()`之后执行，调用`setState()`将会重新渲染组件。
建议在此方法发布订阅

#### Updating()
点击按钮改变state，根据`shouldConponment`的返回值调用以下方法。
![image.png](http://upload-images.jianshu.io/upload_images/1794675-079733bb3f0bc83b.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)

`componentWillUnmount()`:组件Unmount或者销毁时调用，可以做一些清理工作，取消网络连接等。

具体看[官方文档](https://reactjs.org/docs/react-component.html)，讲的比较清楚。

## 无状态组件
`纯函数就是相同的输入，永远都会有相同的输出，没有任何可观察的副作用`
故名思议，内部无state(有props)的组件，纯的组件。输出取决于props。
例子可见前面的文章和github。

相比于 class 创建组件
* 语法更简洁
* 占内存更小（class 有 props context _context 等诸多属性），首次 render 的性能更好
* 可以写成无副作用的纯函数
* 可拓展性更强（函数的 compose，currying 等组合方式，比 class 的 extend/inherit 更灵活）

推荐使用无状态组件。
后续待补充。
