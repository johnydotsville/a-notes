# Принадлежность к классу

## a instanceof Klass

Оператор `instanceOf` определяет принадлежность экземпляра `a` к классу `Klass`.

* В качестве Klass должен быть указан класс или функция-конструктор (что по сути одно и то же).
* Проверяется принадлежность по цепочке, а не только непосредственная.
  * Прототип экземпляра `a` сравнивается с объектом из `Klass.prototype`. Если равны, значит a является экземпляром Klass.

Пример:

```javascript
function Person(firstname, lastname) {  // <-- Первый конструктор
  this.firstname = firstname;
  this.lastname = lastname;
}

function Human(fullname) {  // <-- Второй конструктор
  const name = fullname.split(" ");
  this.firstname = name[0];
  this.lastname = name[1];
}

const foobar = {  // <-- Общий для обоих конструкторов прототип
  fullname() {
    return `${this.firstname} ${this.lastname}`;
  }
};

Person.prototype = foobar;  // <-- Выставим одинаковые прототипы
Human.prototype = foobar;

const huck = new Person("Huck", "Finn");  // <-- Создали Гека через конструктор Person
const huckIsPerson = huck instanceof Person;  // <-- Поэтому очевидно, что Гек это Person
console.log(huckIsPerson);  // true

const huckIsHuman = huck instanceof Human;  // <-- Но он так же и Human
console.log(huckIsHuman);  // true
```

Хотя huck был создан с помощью конструктора `Person`, но `instanceOf Human` тоже выдает true. Все потому, что у Person и Human выставлены одинаковые прототипы, а оператор instanceof ориентируется по прототипам. Если прототипом объекта `a` является объект из `Klass.prototype`, то считается что a является экземпляром класса Klass.

Использовать объект справа от instanceof нельзя:

```javascript
const tom = new Human("Tom Sawyer");
console.log(huck instanceof tom);  // Ошибка: Right-hand side of 'instanceof' is not callable
```



## a.isPrototypeOf(b)

С помощью instanceof нельзя проверить прототипность двух объектов непосредственно. Например:

```javascript
const a = { };
const b = Object.create(a);

const bIsA = b instanceof a;  // <-- Ошибка!
console.log(bIsA);
```

Для этих случаев есть метод `a.isPrototypeOf(b)`:

```javascript
const a = { };
const b = Object.create(a);

const bIsA = a.isPrototypeOf(b);  // true
console.log(bIsA);
```

Прототипность проверяется по цепочке:

```javascript
class Parent { }
class Child extends Parent { }

const p = new Parent();
const c = new Child();

const ParentPrototype = Object.getPrototypeOf(p);
const ChildPrototype = Object.getPrototypeOf(c);

// <-- Для экземпляра Child прототипом считается не только Child.prototype,
console.log(ChildPrototype.isPrototypeOf(c));  // true
// <-- но и Parent.prototype
console.log(ParentPrototype.isPrototypeOf(c));  // true
```

