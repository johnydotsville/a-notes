# Особенности мапы

Map хранит данные в виде пары `ключ:значение`, но в отличие от обычных объектов, не преобразует ключ в строку, а сохраняет его исходный тип. Алгоритм, по которому мапа сравнивает ключи, переопределить невозможно.

В основе мапы лежит хэш-таблица.

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

# Основные операции

## Создание мапы

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

## Установка и получение значения

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

## Удаление элемента и очистка коллекции

Элемент удаляется методом `delete(key)`:

```javascript
guys.set("2", new Person("Huck", 14));  // Добавили
guys.delete("2");  // Удалили
```

Вся коллекция очищается методом `clear()`:

```javascript
guys.clear();
```

## Наличие ключа

Метод `has(key)`:

```javascript
guys.set(1, new Person("Tom", 14));
guys.set("2", new Person("Huck", 14));

let has1 = guys.has(1);  // true
let hasOlolo = guys.has("Ololo");  // false
```

## Размер коллекции

Свойство `size`:

```javascript
guys.set(1, new Person("Tom", 14));
guys.set("2", new Person("Huck", 14));

console.log(guys.size);  // 2
```

## Перебор мапы

Здесь имеется ввиду возможность отдельно перебрать ключи, значения и пары `ключ:значение`. Делается методами keys(), values(), entries() и по ним есть отдельный конспект.

# Конвертация мапы

## Мапа из объекта

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

## Объект из мапы

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


