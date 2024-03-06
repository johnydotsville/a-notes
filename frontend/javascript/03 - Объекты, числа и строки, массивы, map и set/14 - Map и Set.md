# Map

## Особенности мапы

Map хранит данные в виде пары "ключ:значение", но в отличие от обычных объектов, не преобразует ключ в строку, а сохраняет его исходный тип. Алгоритм, по которому мапа сравнивает ключи, переопределить невозможно.

Наглядная разница - посчитать, сколько раз некоторый пользователь "посетил сайт":

```javascript
let tom = { name: "Tom" };
let huck = { name: "Huck" };

let visitors = new Map();
visitors.set(tom, 100);
visitors.set(huck, 50);

console.log("Tom: " + visitors.get(tom));    // 100
console.log("Huck: " + visitors.get(huck));  // 50
```

```
visitors: Map(2)
  [[Entries]]
    0: {Object => 100}
    1: {Object => 50}
    size: 2
  [[Prototype]]: Map
```

Аналогично попробуем сделать через обычный объект, задав ему объект в качестве свойства. Любой объект превратится в строку "object Object", стало быть получится одинаковое строковое свойство и в нем окажется только последнее значение:

```javascript
let vis = {};
vis[tom] = 100;
vis[huck] = 50;  // Перекроет предыдущее значение

console.log("Tom: " + vis[tom]);    // 50
console.log("Huck: " + vis[huck]);  // 50
```

```
vis: 
  [object Object]: 50
  [[Prototype]]: Object
```

Для Map движок применяет оптимизации, поэтому важно пользоваться мэпом именно как мэпом, чтобы эти оптимизации не отключились. Для этого надо выполнять работу с ней через специальные методы.

## Основные операции

### Создание мапы

► Пустая:

```javascript
let users = new Map();
```

► С начальными значениями, для этого нужно передать массив, в котором каждый элемент - тоже массив из двух элементов (ключ и значение):

```javascript
let users = new Map(
  [
    ["tom", { name: "Tom", age: 14 }],
    ["huck", { name: "Huck", age: 15 }]
  ]
);

console.log(users.get("tom").name);  // Tom
```

### Установка и получение значения

Методы `set(key, value)` и `get(key)`:

```javascript
let guys = new Map();

guys.set(1, new Person("Tom", 14));
guys.set("2", new Person("Huck", 14));
guys.set(true, new Person("Jim", 35));

let tom  = guys.get(1);     // Person {name: 'Tom', age: 14}
let huck = guys.get("2");   // Person {name: 'Huck', age: 14}
let jim  = guys.get(true);  // Person {name: 'Jim', age: 35}

function Person(name, age) {
  this.name = name;
  this.age = age;
}
```

P.S. метод set возвращает мапу, поэтому можно переписать установку значений вот так:

```javascript
guys.set(1, new Person("Tom", 14))
  .set("2", new Person("Huck", 14))
  .set(true, new Person("Jim", 35));
```

### Удаление элемента и очистка коллекции

Элемент удаляется методом `delete(key)`:

```javascript
guys.set("2", new Person("Huck", 14));  // Добавили
guys.delete("2");  // Удалили
```

Вся коллекция очищается методом `clear()`:

```javascript
guys.clear();
```

### Наличие ключа

Метод `has(key)`:

```javascript
guys.set(1, new Person("Tom", 14));
guys.set("2", new Person("Huck", 14));

let has1 = guys.has(1);  // true
let hasOlolo = guys.has("Ololo");  // false
```

### Размер коллекции

Свойство `size`:

```javascript
guys.set(1, new Person("Tom", 14));
guys.set("2", new Person("Huck", 14));

console.log(guys.size);  // 2
```

## Перебор мапы

Перебрать можно три вещи:

* `.keys()` - только ключи.
* `.values()` - только значения.
* `.entries()` - пару ключ-значение. Каждый элемент будет представлен массивом, где в 0 индексе лежит ключ, а в 1 значение.

За основу возьмем такую мапу:

```javascript
let tom = { name: "Tom" };
let huck = { name: "Huck" };

let users = new Map();
users.set(tom, 100);
users.set(huck, 50);

// <-- Только ключи
for (let k of users.keys()) {
  console.log(k);  // {name: 'Tom'}, {name: 'Huck'}
}

// <-- Только значения
for (let v of users.values()) {
  console.log(v);  // 100, 50
}

// <-- Ключи и значения
for (let e of users.entries()) {  // <-- Каждый элемент представлен массивом из двух ячеек
  console.log(e[0].name + " : " + e[1]);
}
```

## Конвертация мапы

### Мапа из объекта

Можно создать мапу из любого итерируемого объекта:

```javascript
let tom = {
  name: "Tom",
  age: 14,
  state: "Missouri"
};

let ents = Object.entries(tom);  // [Array(2), Array(2), Array(2)]
let tomAsMap = new Map(ents);

console.log(tomAsMap.get("name"));   // Tom
console.log(tomAsMap.get("age"));    // 14
console.log(tomAsMap.get("state"));  // Missouri
```

Метод `Object.entries(obj)` разбивает полученный объект на "массив массивов", где каждый внутренний массив состоит из двух элементов. Первый используется как ключ, а второй - как значение. Передав этот "массив массивов" в конструктор мапы, получаем мапу с этими элементами.

### Объект из мапы

Методом `Object.fromEntries(entries)` можно собрать объект из массива, элементами которого являются двухэлементные массивы:

```javascript
let entries = [
  ["name", "Tom"],
  ["age", 14],
  ["state", "Missouri"]
];

let user = Object.fromEntries(entries);
// {name: 'Tom', age: 14, state: 'Missouri'}
```

За счет того, что у мапы есть метод `entries()`, который возвращает такой же "массив массивов", мы можем собрать объект вот так:

```javascript
let data = new Map();
data.set("name", "Tom");
data.set("age", 14);
data.set("state", "Missouri");

let obj = Object.fromEntries(data.entries());
// {name: 'Tom', age: 14, state: 'Missouri'}

console.log(obj);
```

# Set

## Особенности сета

Set - это коллекция, в которой хранятся значения без дубликатов. Если попробовать добавить в сет одно и то же значение несколько раз, то оно добавится только первый раз, а при повторных добавлениях ничего не произойдет. Можно перебрать все элементы сета, но извлечение по одному элементу, например по индексу или ключу, как таковое не предусмотрено.

## Основные операции

### Создание сета

► Пустой:

```javascript
let users = new Set();
```

► С начальными значениями:

```javascript
let users = new Set(
  [
    new User("Tom", 14),
    new User("Huck", 15)
  ]
);

console.log(users);

function User(name, age) {
  this.name = name;
  this.age = age;
}
```

### Добавление значения

Метод `add(значение)` добавляет элемент в сет и возвращает этот сет, так что возможно добавление по цепочке:

```javascript
let users = new Set();

let tom = new User("Tom", 14);
let huck = new User("Huck", 14);

users.add(tom);
users.add(tom);  // <-- Это добавление не состоится, т.к. Тома уже добавили.
users.add(huck);

console.log(users);  // 2 пользователя

function User(name, age) {
  this.name = name;
  this.age = age;
}
```

P.S. Если добавлять несколько раз непосредственно созданием нового объекта `.add(new User("Tom", 14))`, тогда конечно добавится.

### Удаление элемента и очистка коллекции

Элемент удаляется методом `delete(значение)`. Если элемент был и удалился, то возвращает true. Если элемента не было, возвращает false:

```javascript
let users = new Set();

let tom = new User("Tom", 14);
let huck = new User("Huck", 14);

users.add(tom);
users.add(huck);

users.delete(huck);  // <-- Удалили Гека

console.log(users);
```

Вся коллекция очищается методом `clear()`:

```javascript
users.clear();
```

### Наличие элемента

Метод `has(value)`:

````javascript
let users = new Set();

let tom = new User("Tom", 14);
users.add(tom);

let exists = users.has(tom);  // true
````

### Размер коллекции

Свойство `size`:

```javascript
let users = new Set();

let tom = new User("Tom", 14);
let huck = new User("Huck", 14);

users.add(tom);
users.add(huck);

console.log(users.size);  // 2
```

## Перебор сета

Хотя у сета нет ключа, но тем не менее у него есть такие же методы как у мапы:

* `values()` - возвращает итерируемый объект со значениями. 
* `keys()` -  тоже итерируемый объект со значениями.
* `entries()` - возвращает итерируемый объект

Это, теоретически, упрощает взаимозаменяемость этих двух видов коллекций.

```javascript
let users = new Set();

let tom = new User("Tom", 14);
let huck = new User("Huck", 14);

users.add(tom);
users.add(huck);

// <-- Каноничный перебор
for (let user of users) {
  console.log(user);  // {name: 'Tom', age: 14}, {name: 'Huck', age: 14}
}

// <-- Остальные переборы, для совместимости с Map
for (let k of users.keys()) {
  console.log(k);  // {name: 'Tom', age: 14}, {name: 'Huck', age: 14}
}

for (let v of users.values()) {
  console.log(v);  // {name: 'Tom', age: 14}, {name: 'Huck', age: 14}
}

for (let e of users.entries()) {
  console.log(e[0] + " " + e[1]);  /*
  Array(2)
    0: User {name: 'Tom', age: 14}
    1: User {name: 'Tom', age: 14}
*/}

function User(name, age) {
  this.name = name;
  this.age = age;
}
```

