# Sliding Window паттерн

`Sliding Window` - это техника, а не название конкретного алгоритма.



TODO: Сделать описание техники, возможно подписать класс задач, который можно решить с ее помощью, а также подписать личные объяснения решений снизу.

TODO: У меня есть проблема с ощущением границы массива из-за 0 и j < arr.length. Как пофиксить?

# Задачи

### Подмассив длиной k с максимальной суммой элементов

Задача: дан массив и число k. Найдите подмассив длиной k с максимальной суммой элементов, и верните эту сумму.

```javascript
function maxSumSubarray(arr, len) {
  let windowSum = arr.slice(0, len).reduce((acc, cur) => acc += cur, 0);
  let maxSum = windowSum;

  for (let i = len; i < arr.length; i++) {
    windowSum += arr[i] - arr[i-len];
    if (windowSum > maxSum)
      maxSum = windowSum;
  }

  return maxSum;
}

const arr = [2, 1, 5, 1, 3, 2];
const k = 3;
console.log(maxSumSubarray(arr, k)); // Вывод: 9 (подмассив [5, 1, 3])
```







### asdf

Задача: дан массив и число k. Найти минимальную длину подмассива, в котором сумма элементов не меньше k. Если такого подмассива нет, вернуть -1.

```javascript
function minSubarrLen(arr, k) {
  let minLen = arr.length + 1;
  let sum = 0;
  let left = 0;

  for (let right = 0; right < arr.length; right++) {
    sum += arr[right];
    while (sum >= k) {
      minLen = Math.min(minLen, right - left + 1);
      sum -= arr[left];
      left++;
    }
  }

  return minLen > arr.length ? -1 : minLen;
}

const arr = [2, 1, 3, 4, 5];
const k = 7;
console.log(minSubarrLenWindow(arr, k));  // 2 [3, 4]
```

