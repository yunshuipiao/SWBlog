# 从谷歌官方例子看constraintlayout

标签（空格分隔）： googlesamples 知乎

---
研究官方文档，是通往业界大拿的途径-----不知道是谁。
这是一个新的系列，打算看一些 github 上谷歌官方的小例子，好处不必多说。
最近的项目中，所有的布局已经换成了 `constraintlayout`，这是谷歌推荐的布局。
因此打算从 `constraintlayoutexamples` 入手。

一点小技巧：视图编辑器用来辅助，布局还是得手写代码。其中 `tools` 开头的属性是负责在视图编辑器辅助展示我们的布局，手写布局可以忽略。

基本知识不做过多介绍，说一些我的收获。

1. 关于 view 的居中：
用的最多的是约定上下左右即可，通过 margin 设置偏移量。
若是要求图中的 `button2` 相对 `button1` 的底部居中， 相对 `centeredbutton` 的右边居中：
则设置 `button2` 的 `top`, `bottom` 与 `button1` `top` 对齐，右边类之。
实现下图的居中对齐：
中间按钮先居中对齐，然后是左右。

2. `layout_constraintDimensionRatio`属性：
设置空间的宽高比，默认宽：高。官方中可以通过指定：`“h, 16:9”` 设成高：宽（有待确认）。注意：不固定的一方的值设为`0dp`。

3. 关于`guideline`，特别好用，其中：
`app:layout_constraintGuide_percent`：用百分比设置偏移量；
`app:layout_constraintGuide_begin`: 具体数值设置偏移量。
与之不同的属性：`app:layout_constraintHorizontal_bias="0.5"`
通过百分比设置 `view` 的偏移量

4. 链条（`chains`）：相互引用, 可以设置偏移量,方便统一管理。
其中 `layout_constraintVertical_chainStyle` 属性在链条的头部设置。
三个值：
`packed`：链条控件紧凑
`spread`：链条控件均匀分布。
`spread_inside`：链条内部控件均匀分布

5. 通过 `constraintset` 设置动画
参考资料[constraintlayout动画](https://github.com/xitu/gold-miner/blob/master/TODO/constraint-layout-animations-dynamic-constraints-ui-java-hell.md)

我的总结：不要盲目使用，根据需求综合使用 `constraintlayout`，搭配 `linelayout`。
使用视图编辑器坐辅助，手写布局。

[官方例子原址](https://github.com/googlesamples/android-ConstraintLayoutExamples)

欢迎加微信**youquwen1226**与我讨论。







