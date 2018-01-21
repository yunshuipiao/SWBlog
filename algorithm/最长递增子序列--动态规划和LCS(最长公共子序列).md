最长递增子序列: 动态规划和LCS(最长公共子序列)
子序列和子串的区别：子序列不连续，字串连续。
这个题两种解法
1. 动态规划
2. 复制数组并排序，求两数组的最长公共子序列。

下面分别做简单介绍：
## 动态规划
 O(n^2)时间复杂度。想求的array[0, i]的最大递增子序列。则计算array[0, i- 1]中以**各元素为最后元素的最长递增序列**。与array[i]比较, 因为不连续。
```
def longest_increasing_subsequence_one(array):
    temp_array = [1] * len(array)
    for index, _ in enumerate(array):
        for i in range(0, index):
            if array[i] < array[index] and temp_array[i] + 1 >= temp_array[index]:
                temp_array[index] = temp_array[i] + 1
    return max(temp_array)
```

### LCS ：最长公共子序列
也是动态规划。
若两序列A[i]的元素和B[i]的元素相等，那么最长公共子序列为A[0, i -1], B[0, j - 1]的最长公共子序列的值加1。否则分别是A[0, i- 1], B[0 ,j] 或者A [0, i - 1], B[0, j ]的最长公共子序列的较大值。
其中选择一个二维数组来标记记住A[i], B[j]的最长公共子序列，见代码。

```
#复制列表并排序，求两列表的最长公共子序列( LCS ): 动态规划
def longest_increasing_subsequence_two(array):
    copy_array = array[:]
    copy_array.sort()
    #中间结果
    temp_array = [[0 for i in range(len(array))] for j in range(len(copy_array))]
    for i in range(1, len(array)):
        for j in range(1, len(copy_array)):
            if array[i] == copy_array[j]:
                temp_array[i][j] = temp_array[i - 1][j - 1] + 1
            else:
                temp_array[i][j] = max(temp_array[i][j - 1],  temp_array[i - 1][j])
    #倒推求出最长公共子序列
    result = []
    i = len(array) - 1
    j = len(copy_array) - 1
    while i > 0 and j > 0:
        if array[i] == copy_array[j]:
            result.append(array[i])
            i -= 1
            j -= 1
        else:
            if temp_array[i][j - 1] >= temp_array[i - 1][j]:
                j -= 1
            else:
                i -= 1
    result.reverse()
    print(result)
    print(len(result))
```
