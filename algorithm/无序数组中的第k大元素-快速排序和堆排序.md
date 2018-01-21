找到无序数组中的第k大元素， kth_Largest_element_in_an_array。
常见的解决方案有两种，快速排序思想和堆排序思想，简单记录一下过程，顺便复习快速排序和堆排序过程。
## 快速排序思想
快速排序的理论基础是随机取一个数，大的放左边，小的放右边，完成一趟排序。
分别对左右两边递归做相同的分割，最后还剩一个数，自然有序，完成快速排序。
那么找第k大的元素，即完成一次排序后，随机找出的这个数的前面有 k -1个数，那么就是第k大个元素。
下标： k - 1 = index.
否则再根据下标关系判断在k在左边还是右边，继续如上操作。
快速排序完成一次排序的函数如下：
```
# 最后一个数为支点，从小到大排序
def partition_two(array, lo, hi):
    i = lo
    j = hi
    while i < j:
        while i < j and array[i] <= array[hi]:
            i += 1
        while i < j and array[j] >= array[hi]: # =保证支点不变，可以后续比较。
            j -= 1
        array[i], array[j] = array[j], array[i]
    array[i], array[hi] = array[hi], array[i]
    return i
```
下面得到分界点的下标，对左右两边递归分割，完成排序：

```
# 快速排序
def quick_sort(array, lo, hi):
    #参数合法性
    if array is None or lo >= hi:
        return
    mid = partition_two(array, lo, hi)
    quick_sort(array, lo, mid - 1)
    quick_sort(array, mid + 1, hi)
```
找第k大元素的代码类似。

#  堆排序思想
首先堆不作介绍，分为大根堆和小根堆。堆的操作有插入和删除最大(小)元素，插入的节点放在最后，然后进行堆化操作。
堆的一个经典实现是完全二叉树，这样实现的堆为二叉堆。
下面是数组进行从小到大的堆排序过程。
###堆化数组
一个随机数组，对根节点i进行堆化操作(i的左右子树也都是堆)。
选择左右节点的小节点与根节点比较，若是根节点还小于较小值，则满足堆的性质。
否则进行交换，对交换过的节点继续进行堆化操作，直至全部堆化。
```
#对数组来说，以下标index为根节点的左右子树的下标为 index * 2 + 1， index * 2 +  2
# 将length个元素，i为节点的数组堆化(i的子节点也都是堆)
def big_heap_array(array, i, length):
    # 左叶子节点
    temp_value = array[i]
    node_index = (i << 1) + 1
    while node_index < length:
        #左右叶子节点的较大值
        if node_index + 1 < length and array[node_index + 1] > array[node_index]:
            node_index = node_index + 1
        # 如果根节点大于叶子节点，说明已经是堆，退出循环。
        if array[node_index] < temp_value:
            break
        array[i] = array[node_index]
        # 调整交换过元素的子节点
        i = node_index
        node_index = (i << 1) + 1
    array[i] = temp_value
```

上面操作是堆化i下标节点的数组，从小到大的排序过程如下：
1. 从最后一个根节点开始，往上进行堆化， 此时array[0] 为最大值。
2.  交换array[0]和最后一个元素，此时再进行堆化，得到最小的元素与倒数第二个元素，逐次完成从小到大排序。
```
#n长度的数组，最后一个根节点的下标为： n / 2 - 1
#构建最大堆，从小到大排序
def heap_sort_one(array):
    # 最后一个跟节点
    last_node_index = (len(array) >> 1) - 1
    for node_index in range(last_node_index, 0 - 1, -1):  #[llast_node_index .. 0] 的列表
        big_heap_array(array, node_index, len(array))
    # 堆顶最大值和最后一个数互换， 然后堆化数组
    for index in range(len(array) - 1, 0, -1):
        array[0], array[index] = array[index], array[0]
        big_heap_array(array, 0, index)
```
下面来解决此问题。
首先利用数组的前k个元素建立一个k大小的小根堆。逐次取后面的元素与堆顶比较， 如果大于堆顶，则替换堆顶，然后进行堆化。最终结果就是最大的前k个元素都在堆中，其中堆顶元素堆小，就是第k大的元素。

寻找的代码见[github：https://github.com/yunshuipiao/sw-algorithms](https://github.com/yunshuipiao/sw-algorithms)
