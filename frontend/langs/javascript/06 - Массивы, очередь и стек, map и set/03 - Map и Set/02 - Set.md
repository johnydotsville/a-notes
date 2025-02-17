# Особенности Set

Set - это коллекция, в которой хранятся значения без дубликатов. Если попробовать добавить в сет одно и то же значение несколько раз, то оно добавится только первый раз, а при повторных добавлениях ничего не произойдет. Можно перебрать все элементы сета, но извлечение по одному элементу, например по индексу или ключу, как таковое не предусмотрено.

В основе сета лежит хэш-таблица.

# Основные операции

## Создание сета

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

## Добавление значения

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

## Удаление элемента и очистка коллекции

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

## Наличие элемента

Метод `has(value)`:

````javascript
let users = new Set();

let tom = new User("Tom", 14);
users.add(tom);

let exists = users.has(tom);  // true
````

## Размер коллекции

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

Здесь имеется ввиду возможность отдельно перебрать ключи, значения и пары "ключ:значение". Делается методами keys(), values(), entries() и по ним есть отдельный конспект.