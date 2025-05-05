# Темы

Список тем по синтаксису языка, который желательно иметь в активной памяти, плюс механики работы браузера и темы на понимание, которые важны, чтобы не путаться в реальных задачах.

- [ ] JavaScript
  - [ ] Продвинутый синтаксис
    - [ ] Деструктурирующее присваивание
    - [ ] Операторы
      - [ ] Остаточные параметры
      - [ ] spread-оператор (оператор разбиения)
  - [ ] Механики, базированные концепции
    - [ ] фыва





# Базовый синтаксис

## Типы данных

В JS динамическая нестрогая типизация:

* Динамическая:
  * Типы определяются во время выполнения, а не во время компиляции.
  * Переменная может менять тип во время выполнения.
* Нестрогая:
  * Автоматическое неявное приведение типов.

Примитивные:

```
number: особые значения Infinity, -Infinity, NaN
BigInt
string
boolean
symbol
null
undefined
```

Не примитивные:

```
object
Обертки: String, Number, Boolean, Symbol, (BigInt - не явл оберткой)
```

```javascript
const a = new Number(7)
const str = new String("Hello, world!")
const flag = new Boolean(true)
const sym = new Symbol("foobar")
const big = BigInt(10)  // без new!
```

### Определение типа

```
if (typeof(foo) === "number")
```

```javascript
if (typeof foo === "number")
```

```javascript
typeof null  // "object"
typeof alert // "function"
```

```javascript
function foobar() { }
typeof(foobar)  // function
```

### Преобразование типов

В строку:

```javascript
let s;
s = String(255);        // "255"
s = String(false);      // "false"
s = String(true);       // "true"
s = String(null);       // "null"
s = String(undefined);  // "undefined"
s = String(NaN);        // "NaN"
s = String(255 / 0);    // "Infinity"
```

В число:

* Пробельные символы *по краям* из строк удаляются, а все что после этого осталось, уже преобразуется в число.
* Пустая строка трактуется как 0.

```javascript
let n;
n = Number("   255   ");  // 255
n = Number(true);         // 1
n = Number(false);        // 0
n = Number("   ");        // 0
n = Number("");           // 0
n = Number(null);         // 0
n = Number("2 5 5");  // NaN
n = Number("   ололо ");  // NaN
n = Number(undefined);    // NaN
n = Number(NaN);          // NaN
n = Number(Infinity);     // Infinity
```

В boolean:

* Все, что как бы "интуитивно" пустое, нулевое, непонятное, неопределенное, неизвестное вроде пустых строк, null, NaN, undefined, является false.
* Числа 1 и 0 являются, соответственное, true и false. Однако строка "0" является true, потому что она рассматривается именно как строка, а не как число. Она не пустая, поэтому true. Впрочем, в других языках может быть по-другому.
* При логическом преобразовании пробельные символы из строки не удаляются, поэтому строка из пробелов считается не пустой, а значит является true.

```javascript
let b;
b = Boolean("   255   ");    // true
b = Boolean("   2 5 5   ");  // true
b = Boolean("   ололо   ");  // true
b = Boolean("   ");          // true
b = Boolean("0");            // true, расц. как непустая строка
b = Boolean(Infinity);       // true
b = Boolean(1);              // true
b = Boolean(0);              // false
b = Boolean("");             // false
b = Boolean(null);           // false
b = Boolean(undefined);      // false
b = Boolean(NaN);            // false
```

### Упражнения

```javascript
"" + 1 + 0
"" - 1 + 0
true + false
6 / "3"
"2" * "3"
4 + 5 + "px"
"$" + 4 + 5
"4" - 2
"4px" - 2
"  -9  " + 5
"  -9  " - 5
null + 1
undefined + 1
" \t \n" - 2
```

Ответы:

```javascript
"10"
-1
1
2
6
"9px"
"$45"
2
NaN
"  -9  5"
-14
1
NaN
-2
```



# Продвинутый синтаксис

## Деструктурирующее присваивание

Как запомнить, какие скобки для чего нужны:

* Массив объявляется с помощью квадратных скобок [ ]. Поэтому и деструктурируются они через [ ]. А данные, для которых не хватило переменных, собираются в *массив* (при использовании оператора ...)
* Объект объявляется с помощью фигурных скобок { }. Поэтому и деструктурируются они с помощью фигурных скобок { }. Свойства, для которых не хватило переменных, собираются в *объект* (при использовании оператора ...)

Массивы, мапы, сеты

```javascript
const [a, b] = ["Hello", "world"];
```

```javascript
// В сущ-ие переменные
let a, b;
[a, b] = ["Hello", "world"];
```

```javascript
// Пропуск элементов
const someDate = "2025-04-28";
const [, month, day] = someDate.split("-");
```

```javascript
// Свап
let x = 50;
let y = 100;
[x, y] = [y, x];
```

```javascript
// Дефолтные значения
let fruits = [];
let [apple = "Яблоко", pear = "Груша"] = fruits;
```

Объекты:

```javascript
// авто-сопоставление свойств и переменных
const obj = { name: "Huck Finn", age: 14}
const { name, age } = obj;
```

```javascript
// сопоставление вручную
const obj = { name: "Huck Finn", age: 14}
const { name: n, age: a } = obj;  // откуда: куда
```

```javascript
// В сущ-ие переменные
const obj = { name: "Huck Finn", age: 14}
let name, age;
({ name, age } = obj);  // ( )
```

```javascript
// Дефолтные значения
const obj = { }
const { name: n = "Huck", age: a = 14 } = obj;

const obj = { }
const { name = "Huck", age = 14 } = obj;
```

```javascript
// Свап значений свойств
({ firstname: user.lastname, lastname: user.firstname } = user);
```

```javascript
// Вложенная деструктуризация
let settings = {
  size: [1920, 1080],
  title: "Мост в Терабитию",
  info: {
    year: 2007,
    duration: 93,
    country: "США"
  }
};

let {
  size: [x, y],
  title,
  info: {
    year: g,
    duration: d,
    country
  }
} = settings;
```

При деструктурирующем присваивании мы должны добраться до "минимальных" частичек контейнера (в массиве это элементы, а в объекте - свойства). Пока до них добираемся, должны указывать путь. В примере выше, свойство title - уже минимальная частичка, поэтому она разобьется. А вот info - не минимальная, это составной объект. Поэтому для него снова применяем синтаксис деструктуризации. size - тоже не минимальная, поэтому и для нее снова применяем деструктуризацию.

```javascript
// Обход свойств и значений
const obj = { a: 1, b: 2 };

for (const [key, value] of Object.entries(obj)) {
  console.log(`key: ${key}, value: ${value}`);
}
```

## rest-оператор

```javascript
let fruits = ["Яблоко", "Груша", "Апельсин", "Мандарин"];
let [apple, pear, ...other] = fruits;
```

```javascript
let user = {
  name: "Huck",
  age: 15,
  state: "Illinois"
};

let {state, ...other} = user;  // name и age стали свойствами объекта other
```

```javascript
function hello(firstname, lastname, ...rest) {
}
// firstname, lastname |       попадут в массив rest
hello("Джеки", "Чан",    "Громобой", "Азиатский ястреб", "Ковбой");
```

```javascript
const sum = (...nums) => nums.reduce((prev, curr) => prev += curr, 0);
```

```react
const MyButton = ({children, ...rest}) => {
  console.log(rest);
  return (
    <button {...rest}>{children}</button>
  );
};
```

## spread-оператор

