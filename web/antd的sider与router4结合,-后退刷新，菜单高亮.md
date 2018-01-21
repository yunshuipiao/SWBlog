在前面的文章中，简单介绍了react + less + axios + mobx的使用，UI库可以选择antd或者material UI。
目前使用的antd。其中在最简单的布局中，使用sider遇到了问题。在页面的强制刷新或者后退，左边menu的高亮和页面元素并不会发生变化。下面简单记录使用react-router4和antd搭配，完美的解决这个问题，后续还有一个全局的登录问题可以考虑。

## 基本的router4 使用
`yarn add react-router-dom`: 安装需要用到的路由模块。
修改 `App`组件： 后面可以借助withrouter在组件内访问路由，后面有例子。
```
class App extends Component {
    render() {
        return (
            <Provider {...stores}>
                <BrowserRouter>
                    <div className="App">
                        <Home/>
                    </div>
                </BrowserRouter>
            </Provider>
        );
    }
}
```

修改`Main`组件，也是最简单的router4使用：
```
class Home extends React.Component {
    constructor(props) {
        super(props)
        // 没有super(props), 后面使用回报错
        // 定义state
        // bind方法
        // 其他初始化工作
    }

    componentWillMount() {
        // 服务器渲染的唯一hook
    }

    componentDidMount() {
        // 可以调用setState， render Component
    }

    render() {
        return (
            <div className="Home">
                <Switch>
                    <Route path={"/login"} component={Login}/>
                    <Route path={"/"} component={Main}/>
                    <Redirect to={"/"}/>
                </Switch>
            </div>
        );
    }
}

const Login = () => {
    return(
        <div>login</div>
    );
}

class Main extends React.Component {
    render() {
        return(
            <div>
                Main
            </div>
        );
    }
}
export default Home
```
具体细节不做解释，可以与我沟通或者上网查阅资料。
`yarn start` 打开浏览器，分别输入不同的地址，可以匹配到不同的组件。

## 基本的antd布局
`Login`组件不做改动。下面修改Main。
```
import React from "react";
import {Layout, Menu} from 'antd'
const { Header, Content, Sider, Footer } = Layout;

class Main extends React.Component {
    render() {
        return(
            <div>
                <Layout>
                    <Header>header</Header>
                    <Layout>
                        <Sider>left sidebar</Sider>
                        <Content>main content</Content>
                        <Sider>right sidebar</Sider>
                    </Layout>
                    <Footer>footer</Footer>
                </Layout>
            </div>
        );
    }
}

export  default Main;
```
基本布局，上中下，具体可以参考antd官网。下面分别修改sider组件，content组件。

代码如下：
```
import React from "react";
import {Layout, Menu} from 'antd'
import {Link, Route, Switch, Redirect} from "react-router-dom"
import './index.less'

const {Header, Content, Sider, Footer} = Layout;

const MyHeader = () => {
    return (
        <Header className='main-header'>
            header
        </Header>
    );
}

const MyFooter = () => {
    return (
        <Footer className='main-footer'>
            footer
        </Footer>
    );
}

const Demo1 = () => {
    return (
        <div>
            demo1
        </div>
    );
}

const Demo2 = () => {
    return (
        <div>
            demo2
        </div>
    );
}

const Demo3 = () => {
    return (
        <div>
            demo3
        </div>
    );
}

const RightContent = () => {
    return (
        <div>
            <Content>
                <Switch>
                    <Route path="/1" component={Demo1}/>
                    <Route path="/2" component={Demo2}/>
                    <Route path="/3" component={Demo3}/>
                    <Redirect to="/1"/>
                </Switch>
            </Content>
        </div>
    );
}

const LeftSider = () => {
    return (
        <Sider>
            <Menu
                mode="inline"
                defaultSelectedKeys={['/1']}
            >
                <Menu.Item key="/1">
                    <Link to="/1"/>
                    option1
                </Menu.Item>
                <Menu.Item key="/2">
                    <Link to="/2"/>
                    option2
                </Menu.Item>
                <Menu.Item key="/3">
                    <Link to="/3"/>
                    option3
                </Menu.Item>
            </Menu>
        </Sider>

    );
}

class Main extends React.Component {
    render() {
        return (
            <div className="Main">
                <Layout className='main-layout'>
                    <MyHeader/>
                    <Layout>
                        <LeftSider/>
                        <RightContent/>
                    </Layout>
                    <MyFooter/>
                </Layout>
            </div>
        );
    }
}


export default Main;
```
打开浏览器可以看到如下页面：
![image.png](http://upload-images.jianshu.io/upload_images/1794675-b75291733da90aa1.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
点击不同的菜单，右边内容做相应的变化。
不过，对于后退操作和刷新页面操作无效，左边菜单无法保持选中项高亮。
解决办法如下：
menu用seletedkeys来决定哪项被选中。需要判断当前选前的路由是什么，可以借助withrouter。修改如下：
`withrouter`的使用请查看官网
```
const LeftSider = withRouter(({history}) => {
    return (
        <Sider>
            <Menu
                mode="inline"
                defaultSelectedKeys={['/1']}
                selectedKeys={[history.location.pathname]}
            >
                <Menu.Item key="/1">
                    <Link to="/1"/>
                    option1
                </Menu.Item>
                <Menu.Item key="/2">
                    <Link to="/2"/>
                    option2
                </Menu.Item>
                <Menu.Item key="/3">
                    <Link to="/3"/>
                    option3
                </Menu.Item>
            </Menu>
        </Sider>

    );
} )
```
