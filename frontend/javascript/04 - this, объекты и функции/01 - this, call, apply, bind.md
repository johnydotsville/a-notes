# this

## Момент вычисления this

`this` - это ключевое слово, а не переменная или поле. this в js изменяется каждый раз при [вызове функции](https://stackoverflow.com/questions/28016664/when-you-pass-this-as-an-argument/28016676#28016676). Правило, по которому определяется значение для this, зависит от способа вызова функции.

Есть шесть основных правил вычисления this. Четыре из них можно отнести к автоматическому, и в двух случаях мы сами явно можем указать значение для this.

# Автоматическое определение this

## Вызов обычной функции

При вызове обычной функции, не принадлежащей объекту, в this кладется глобальный объект (в обычном режиме) или undefined (в строгом режиме).

```javascript
function foobar() {
  console.log(this.innerWidth);
}

foobar();  // 990 (текущая ширина вьюпорта)
```

В браузере этот код вернет текущую ширину вьюпорта (окна браузера), потому что this установится на объект window.

## Вызов метода объекта

При вызове функции как метода объекта, в this кладется этот объект.

```javascript
let user = {
  name: "Tom",
  hello() {
    console.log(`Hello, my name is ${this.name}.`);
  }
}

user.hello();  // Hello, my name is Tom
```

## Вызов функции через new

Когда функция вызывается с использованием ключевого слова new (т.н. "функция-конструктор"), то в начале этой функции неявно создается новый объект и кладется в this, а в конце функции он возвращается:

```javascript
function User(name) {
  // this = {};  // Неявно
  this.name = name;
  // return this;  // Неявно
}

let user = new User("Tom");
console.log(user.name);
```

## Лямбды

TODO



# Явное задание this

## Методы call и apply

Это методы объекта функции. Оба они позволяют вызвать функцию, явно указав объект, который надо положить в this. Отличия в том, что call принимает параметры для функции отдельными значениями, а apply - псевдомассивом:

```javascript
call(context, arg1, arg2, ..., argN);  // Параметры - отдельными значениями
```

```javascript
apply(context, pArr);  // Параметры - псевдомассивом
```

Примеры:

```javascript
let tom = { name: "Tom" };
let huck = { name: "Huck" };

let hello = function(age, state) {
  console.log(`Hello! My name is ${this.name}. I'm ${age} years old. I live in ${state}.`);
}

hello.call(tom, 13, "Missouri");  // Каждый параметр вызываемой функции передается отдельно
hello.call(huck, 14, "Illinois");

hello.apply(tom, [13, "Missouri"]);  // Параметры вызываемой функции передаются псевдомассивом
hello.apply(huck, [14, "Illinois"]);
```

Первым параметром передаем контекст, а второй и последующие параметры - собственные параметры вызываемой функции.

## Метод bind

Бывают ситуации, когда нужно передать метод объекта куда-то в качестве колбэка. Но метод, выдернутый из объекта, не будет работать как надо, потому что в момент его вызова, в this будет не этот объект, а что-то другое:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

user.intro();  // I am Tom

setTimeout(user.intro, 1000);  // I am ""
```

На этот случай существует метод `bind`.  Это метод объекта функции, он принимает объект, который надо использовать в качестве this, и возвращает новую функцию с "правильным" this. Подробнее о реализации механики bind - дальше в отдельном разделе. А вот просто пример использования, который чинит ошибку из предыдущего примера:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

user.intro();  // I am Tom

let binded = user.intro.bind(user);  // <-- Прибиваем к методу intro объекта user правильный this
setTimeout(introBinded, 1000);  // I am Tom
```



















# Подробнее о методе bind

## Механика метода bind

Метод bind *не изменяет исходный объект функции*, он возвращает новую функцию-обертку, которая вызывает исходную, передавая ей объект, который нужно использовать в качестве this. Для понимания как это примерно устроено, есть такой пример "самодельной" реализации bind, названной bin**t**:

```javascript
function bint(pthis) {
  let origin = this; // в this лежит объект функции, на которой вызван метод link
  
  return function() {
    origin.call(pthis);
  };
}

let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

user.intro.bint = bint;
let f = user.intro.bint(user);

setTimeout(f, 1000);  // I am Tom
```

Комментарии:

* Если вызвать на функции метод, то внутри него this будет указывать на эту функцию. Все потому, что функция по своей природе является объектом, а при вызове метода на объекте, как известно, this указывает на этот объект.
* Поэтому мы сначала превращаем функцию bint в метод функции intro таким образом `user.intro.bint = bint;`
* Следовательно, когда мы вызываем `user.intro.bint(user);`, то this внутри bint указывает на функцию intro.
* Т.о., выражением `origin = this;` мы сохраняем в переменную origin исходную функцию intro.
* Затем возвращаем новую функцию-обертку, которая вызывает исходную функцию с помощью call, передавая ей правильный объект под this.
  * Промежуточная переменная origin нужна, чтобы сохранить текущее значение this. Нельзя было бы написать в обертке `this.call(pthis)`, потому что this - динамическое и в момент вызова обертки, this указывало бы не на intro, а на что-то другое.

## Примеры

Просто несколько примеров на закрепление.

► Прибьем контекст к обычной функции, использующей this:

```javascript
function intro() {  // <-- Обычная функция, но использует this
  console.log("I am " + this.name);
}

let user = {
  name: "Tom"
};

intro();  // I am "" // Нет контекста
let f = intro.bind(user);  // Прибили контекст
f();  // I am Tom  // Контекст появился, поэтому name теперь имеет значение
```

► Сохраним контекст для метода, выдернутого из объекта:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = user.intro.bind(user);
f();  // I am Tom
```

► Демонстрация того, что bind не изменяет исходную функцию:

```javascript
function intro() {
  console.log("I am " + this.name);
}

let user = {
  name: "Tom",
}

intro();  // I am ""
intro.bind(user);
intro();  // I am ""  // <-- Исходная функция intro не изменилась, поэтому и результат такой же

let introBinded = intro.bind(user);  // <-- Нужно сохранить новую функцию
// <-- и тогда при ее использовании она вызовет исходную функцию, передав ей контекст
introBinded();  // I am Tom
```

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = user.intro;  // <-- Выдернули функцию из объекта, чтобы потерять контекст
f();  // I am ""  // <-- Конекст потерян, поэтому this.name дает пустую строку
let binded = f.bind(user);  // <-- Прибили к функции контекст
binded();  // I am Tom

let fb = user.intro.bind(user);
fb();  // I am Tom
```

► Стирание или *полная* замена исходного объекта другим объектом не повлияет на bind, потому что он запомнил ссылку на исходный объект:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = user.intro.bind(user);  // <-- 1. user будет запомнен в его текущей форме

// <-- 2. Делаем вызов с задержкой, чтобы успеть запороть user'а
setTimeout(f, 1000);  // <-- 4. I am Tom  // Все равно все правильно

user = "Стерли!";  // <-- 3. Уничтожаем объект контекста до момента вызова прибитой функции
```

По той же причине, если подменить объект user на другой с аналогичной структурой, на вызов это не повлияет:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = user.intro.bind(user);

setTimeout(f, 1000);  // I am Tom

user = {
  name: "Huck",
  intro() {
    console.log("Вообще другая функция.");
  }
}
```

Но вот если *изменить* исходный объект (а не *заменить*), то эти изменения повлияют на вызов прибитой функции:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = user.intro.bind(user);

setTimeout(f, 1000);  // I am Huck

user.name = "Huck";  // <-- Новое имя учтется
user.intro = function() {  // <-- А новая реализация метода - нет
  console.log("Вообще другая функция.");
}
```

Опять же, нюанс, вроде бы очевидно из описания bind, но все же напишу: фиксируется только реализация прибиваемой функции. Все остальное - будто данные, или функции, могут изменяться. Пример:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
    this.demo();
  },
  demo() {
    console.log("foobar");
  }
}

let f = user.intro.bind(user);

setTimeout(f, 1000);  /*
  I am Huck
  HELLO, WORLD!  // а не foobar
*/

user.name = "Huck";
user.intro = function() {
  console.log("Вообще другая функция.");
}
user.demo = function() {
  console.log("HELLO, WORLD!");
}
```

## bindAll

Импровизированный метод замены всех методов объекта на аналогичные, но с прибитым контекстом:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

for (let key in user) {  // <-- "bindAll"
  if (typeof user[key] == 'function') {
    user[key] = user[key].bind(user);
  }
}
```

## Альтернатива bind'у

Пишу просто для справки. Альтернатива с уязвимостью. Можно сохранить контекст, если создать функцию-обертку:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = function() {
  user.intro();  // <-- Используем замыкание, чтобы не потерять контекст
};

f();  // I am Tom
```

Уязвимость заключается в том, что к моменту вызова функции f, в объекте user может измениться реализация метода intro:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = function() { 
  user.intro() 
};

// <-- Отложим вызов, чтобы успеть изменить объект
setTimeout(f, 1000);  // "Вообще другая функция. name: "Huck""

user = {
  name: "Huck",
  intro() {  // <-- Изменили реализацию метода
    console.log("Вообще другая функция. name: " + this.name);
  }
}
```

Как будто такой сценарий маловероятен, какой смысл заменять метод таким образом? Но факт есть факт. Bind бы такого не допустил, т.к. он возвращает новый объект функции на основе существующего и стало быть замена функции в объекте ему нипочем. Однако конечно от замены данных мы не застрахованы:

```javascript
let user = {
  name: "Tom",
  intro() {
    console.log("I am " + this.name);
  }
}

let f = user.intro.bind(user);

setTimeout(f, 1000);  // I am Tom

user = {
  name: "Huck",
  intro() {
    console.log("Вообще другая функция.");
  }
}
```


