# Ключевое слово class

## statement-стиль

Введение ключевого слова `class` является синтаксическим сахаром и не меняет природы устройства классов:

```javascript
class Person {  // <-- "Класс" это, по сути, всё та же функция-конструктор
  constructor(firstname, lastname) {  // <-- Вот тело этой ФК
    this.firstname = firstname;
    this.lastname = lastname;
  }

  fullname() {  // <-- Эта функция будет у всех объектов, созданных через Person
    return `${this.firstname} ${this.lastname}`;
  }  // <-- Запятая между методами не нужна
    
  foobar() {  // <-- Для объявления функции используется shorthand-синтаксис, без слова function
    return "Hello, classes!";
  }
}

const huck = new Person("Huck", "Finn");  // <-- Для создания объекта используем имя класса
console.log(huck.fullname());  // Huck Finn
```

Что нужно знать:

* "Класс" Person по сути - это старая-добрая функция-конструктор, а не "класс".
  * Слово class создает переменную Person и присваивает ей функцию, описанную через `constructor`.
* Внутри класса автоматически используется strict mode.
* Класс не "всплывает". Т.е. нельзя пользоваться классом до его объявления, например, не получится описать его в конце файла, а вызвать в начале - будет ошибка.
* Если объектам не нужна инициализация, можно не писать функцию-конструктор. Тогда автоматически создастся пустая ФК.
* Невозможно объявить несколько конструкторов с разными параметрами, как это делается в типичных ООП-языках.
* Показанный в этом примере код является statement-стилем объявления класса. Это то же самое как есть Function Declaration, а есть Function Expression.

## expresstion-стиль

Используется редко, но тем не менее, для справки, что такое существует:

```javascript
const Person = class {  // <-- Тут можно было бы дать имя классу, например, class Foobar
  constructor(firstname, lastname) {
    this.firstname = firstname;
    this.lastname = lastname;
  }

  fullname() {
    return `${this.firstname} ${this.lastname}`;
  }
}

const huck = new Person("Huck", "Finn");  // <-- Используем имя переменной, в которую положили класс
console.log(huck.fullname());
```

# Передача классов

Поскольку класс это функция, то с ним можно делать все то же самое, что с функцией - например, передавать куда-то и там использовать.

```javascript
class User {
  constructor(name) {
    this.name = name;
  }
}

function create(klass, username) {
  return new klass(username);
}

let user = create(User, "Huck");
console.log(user.name);  // Huck
```
