# Sliding Window паттерн

`Sliding Window` - это техника, а не название конкретного алгоритма.



TODO: Сделать описание техники, возможно подписать класс задач, который можно решить с ее помощью, а также подписать личные объяснения решений снизу.



# Задачи

### Подмассив длиной k с максимальной суммой элементов

Задача: дан массив и число k. Найдите подмассив длиной k с максимальной суммой элементов, и верните эту сумму.

```javascript
function maxSumSubarray(arr, k) {
  if (arr.length === 0 || k > arr.length || k < 1) 
    return null;

  let window = 0;
  for (let i = 0; i < k; i++) {
    window += arr[i];
  }
  let maxSum = window;

  for (let i = k; i < arr.length; i++) {
    window += arr[i] - arr[i-k];
    if (window > maxSum)
      maxSum = window;
  }

  return maxSum;
}


const arr = [2, 1, 5, 1, 3, 2];
const k = 3;
console.log(maxSumSubarray(arr, k)); // Вывод: 9 (подмассив [5, 1, 3])
```

Основная идея:

* Нам дано число k. Пусть будет 5.

* Мы можем вычислить сумму первых пяти элементов массива и взять ее в качестве максимальной на старте.

* Подмассив длиной k можно представить в виде "окна", которое будет скользить дальше по массиву.

* Поскольку каждый шаг окно перемещается на 1 элемент, то часть элементов у нас по сути уже сложены.

  * Поэтому, чтобы не складывать их повторно, для вычисления суммы очередного окна нам достаточно прибавить текущий элемент `arr[i]` и вычесть элемент с другого края `arr[i-k]`.

  * Т.о. мы получаем очередную сумму с минимальными затратами.

* Итоговая скорость выполнения $O(n)$.

А вот "наивная реализация" со вложенным циклом и производительностью $O(n^2)$:

```javascript
function maxSumSubarray(arr, k) {
  if (arr.length === 0 || k > arr.length || k < 1) 
    return null;

  let maxSum = 0;
  let first = true;

  for (let i = 0; i <= arr.length - k; i++) {
    let current = 0;
    for (let j = i; j < i + k; j++) {
      current += arr[j];
    }
    if (first || current > maxSum) {
      maxSum = current;
      first = false;
    }
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

