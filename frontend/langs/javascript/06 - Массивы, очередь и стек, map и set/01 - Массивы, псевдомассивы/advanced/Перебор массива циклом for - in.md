# for ... in

Этот цикл не рекомендуется использовать для перебора массивов, потому что:

* Он оптимизирован для работы с произвольными объектами, но не массивами.
* Он перебирает все свойства объекта, а в случае массива нас интересуют только числовые. В случае т.н. "псевдомассивов" можем получить лишнее.

```javascript
let arr = ["Яблоко", "Апельсин", "Груша"];

for (let item in arr) {
  console.log(arr[item]);  // С виду все нормально, но есть нюанс.
}
```

