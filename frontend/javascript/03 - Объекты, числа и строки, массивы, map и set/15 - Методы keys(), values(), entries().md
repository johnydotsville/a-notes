# Методы keys(), values(), entries()

## Для кого существуют

Эти методы есть для всех видов объектов: для массивов, Map, Set, а также для обычных объектов.

Где-то они упрощают взаимозаменяемость структур, например Map и Set. Где-то служат для удобства, например в обычных объектах. А где-то выглядят как пятое колесо (имхо), например в массивах.

Характеристики:

* В массивах, Map и Set эти методы можно вызывать прямо на целевом объекте.
  * Важно! Результатом этих методов является *итерируемый объект* (а не массив).
* Для обычных объектов эти методы существуют в виде методов-хелперов в объекте `Object`.
  * Важно! Здесь результатом является массив (а не итерируемый объект).
  * Такой формат вызова (Object.method) мотивирован тем, что для произвольных объектов мы можем захотеть самостоятельно написать методы с именами keys(), values(), entries(). Поэтому, чтобы не было пересечения, стандартные методы вынесены в Object.
  * Эти методы для обычных объектов игнорируют свойства, в качестве имени которых используется Symbol. Если нужны только символьные ключи, есть метод `Object.getOwnPropertySymbols`. Если нужны все вообще свойства, то `Reflect.ownKeys(obj)`.

## Понятие записи

Entry ("запись") это массив из двух элементов:

* `0` - ключ.
* `1` - значение.

Встречая понятие записи, надо помнить эту нехитрую структуру, тогда проще будет понять, что именно попадает в эти индексы.

# Массивы

## .keys()

Возвращает индексы элементов массива в виде итерируемого объекта:

```javascript
let names = ["Tom", "Huck", "Jim"];

let keys = names.keys();  // Итерируемый объект, Array Iterator

for (let k of keys) {  // <-- Ok
  console.log(k);   // 0, 1, 2
}

let k = keys[0];  // undefined, т.к. keys это не массив, а итерируемый объект
```

## .values()

Возвращает значения элементов массива в виде итерируемого объекта:

```javascript
let names = ["Tom", "Huck", "Jim"];

let values = names.values();  // Итерируемый объект, Array Iterator

for (let v of values) {  // <-- Ok
  console.log(v);  // "Tom", "Huck", "Jim"
}

let v = values[0];  // undefined, т.к. values это не массив, а итерируемый объект
```

## .entries()

Возвращает элементы массива в виде итерируемого объекта, содержащего записи формата [0] - индекс элемента в массиве, [1] - значение элемента массива:

```javascript
let names = ["Tom", "Huck", "Jim"];

let entries = names.entries();  // Итерируемый объект, Array Iterator

for (let e of entries) {  // <-- Ok
  //         индекс      значение
  console.log(e[0] + " " + e[1]);  // "0 Tom", "1 Huck", "2 Jim"
}

let e = entries[0];  // undefined, т.к. entries это не массив, а итерируемый объект
```

# Map

## .keys()

Возвращает ключи мапы в виде итерируемого объекта:

```javascript
let users = new Map([
  ["k-tom",  { name: "Tom",  age: 14 }],
  ["k-huck", { name: "Huck", age: 15 }]
]);

let keys = users.keys();  // MapIterator, не массив!

for (let k of keys) {
  console.log(k);  // k-tom, k-huck
}

let k = keys[0];  // undefined, т.к. keys это не массив, а итерируемый объект
```

## .values()

Возвращает значения ключей мапы в виде итерируемого объекта:

```javascript
let users = new Map([
  ["k-tom",  { name: "Tom",  age: 14 }],
  ["k-huck", { name: "Huck", age: 15 }]
]);

let values = users.values();  // MapIterator, не массив!

for (let v of values) {
  console.log(v.name + " " + v.age);  // Tom 14, Huck 15
}

let v = values[0];  // undefined, т.к. values это не массив, а итерируемый объект
```

## .entries()

Возвращает элементы мапы в виде итерируемого объекта, содержащего записи формата [0] - ключ элемента, [1] - значение элемента:

```javascript
let users = new Map([
  ["k-tom",  { name: "Tom",  age: 14 }],
  ["k-huck", { name: "Huck", age: 15 }]
]);

let entries = users.entries();  // MapIterator, не массив!

for (let e of entries) {
  //          ключ       значение
  console.log(e[0] + " " + e[1]);  // k-tom [object Object], k-huck [object Object]
  console.log(e[1].name + " " + e[1].age);  // Tom 14, Huck 15
}

let e = entries[0];  // undefined, т.к. entries это не массив, а итерируемый объект
```

## Перебор без метода

Если просто перебирать мапу в цикле, то перебираются записи. Получается тот же самый эффект, что при методе `.entries()`:

```javascript
let users = new Map([
  ["k-tom",  { name: "Tom",  age: 14 }],
  ["k-huck", { name: "Huck", age: 15 }]
]);

for (let e of users) {  // <-- Указываем просто саму переменную с мапой
  //          ключ       значение
  console.log(e[0] + " " + e[1]);  // k-tom [object Object], k-huck [object Object]
  console.log(e[1].name + " " + e[1].age);  // Tom 14, Huck 15
}
```

# Set

Хотя у сета нет ключа, но тем не менее у него тоже есть полный набор методов keys(), values(), entries().

## .values()

Возвращает элементы сета в виде итерируемого объекта:

```javascript
let users = new Set([
  { name: "Tom",  age: 14 },
  { name: "Huck", age: 15 }
]);

let values = users.values();  // SetIterator, не массив!

for (let v of values) {
  console.log(v.name + " " + v.age);  // Tom 14, Huck 15
}

let v = values[0];  // undefined, т.к. values это не массив, а итерируемый объект
```

## .keys()

Тоже возвращает элементы сета в виде итерируемого объекта. По сути то же самое, что values():

```javascript
let users = new Set([
  { name: "Tom",  age: 14 },
  { name: "Huck", age: 15 }
]);

let keys = users.keys();  // SetIterator, не массив!

for (let k of keys) {
  console.log(k.name + " " + k.age);  // Tom 14, Huck 15
}

let k = keys[0];  // undefined, т.к. keys это не массив, а итерируемый объект
```

## .entries()

Возвращает элементы сета в виде итерируемого объекта, содержащего записи формата [0] - значение элемента, [1] - значение элемента. Т.е. в обоих индексах записи лежит одно и то же:

```javascript
let users = new Set([
  { name: "Tom",  age: 14 },
  { name: "Huck", age: 15 }
]);

let entries = users.entries();  // SetIterator, не массив!

for (let e of entries) {
  console.log(e[0].name + " " + e[0].age);  // Tom 14, Huck 15
  console.log(e[1].name + " " + e[1].age);  // Tom 14, Huck 15
  //
}

let e = entries[0];  // undefined, т.к.
```

# Обычные объекты

## Object.keys(o)

Возвращает имена свойств объекта в виде массива:

```javascript
let user = {
  name: "Tom",
  age: 14,
  state: "Missouri"
};

let keys = Object.keys(user);  // Array, ['name', 'age', 'state']

for (let k of keys) {
  console.log(k);  // name, age, state
}

let k = keys[0];  // name
```

## Object.values(o)

Возвращает значения свойств объекта в виде массива:

```javascript
let user = {
  name: "Tom",
  age: 14,
  state: "Missouri"
};

let values = Object.values(user);  // Array, ['Tom', 14, 'Missouri']

for (let v of values) {
  console.log(v);  // Tom, 14, Missouri
}

let v = values[0];  // Tom
```

## Object.entries(e)

Возвращает массив записей ("массив массивов") формата [0] - имя свойства, [1] - значение свойства:

```javascript
let user = {
  name: "Tom",
  age: 14,
  state: "Missouri"
};

let entries = Object.entries(user);  // Array[
//   ['name' ,  Tom' ],
//   ['age'  ,  14   ],
//   ['state', 'Missouri']
// ]

for (let e of entries) {
  console.log(e[0] + " " + e[1]);
}

let e = entries[0];  // ['name', 'Tom']
console.log(e[0]);   // name
console.log(e[1]);   // Tom
```
