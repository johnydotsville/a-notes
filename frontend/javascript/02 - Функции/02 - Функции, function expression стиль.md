# Функции через function expression

## Синтаксис

Такой синтаксис называется **Function expression**:

```javascript
let hello = function(name, age = 25) {
  console.log(`Пользователю ${name} сейчас ${age} лет.`);
};  // <-- Нужна точка с запятой.

hello();  // Выполняем функцию.
```

Мы описываем функцию и сохраняем ее в переменную. Пара моментов:

* В конце нужна точка с запятой, как при присваивании обычных значений.
* Имя функции можно не указывать. Такая функция называется *анонимной*.
* Вызов этой функции происходит через переменную, в которой она лежит.

## NFE, named function expression

Для FE можно все-таки указать имя. Это может пригодиться для того, чтобы функция могла вызвать саму себя. Кроме как внутри самой функции такое имя нигде не доступно. Например:

```javascript
let hello = function foobar(name) {  // <-- Даем функции имя foobar
  if (name) console.log("Hello, " + name)
  else foobar("stranger");  // <-- Обращаемся к самой себе по заданному имени
};

hello("Tom");  // Hello, Tom
hello();  // Hello, stranger
```

В общем-то, если не давать FE имя, то она могла бы вызвать саму себя вот так:

```javascript
let hello = function(name) {
  if (name) console.log("Hello, " + name)
  else hello("stranger");
};

hello("Tom");  // Hello, Tom
hello();  // Hello, stranger
```

Но есть нюанс - если перезаписать переменную, в которую изначально была положена функция, то будет ошибка:

```javascript
let hello = function(name) {
  if (name) console.log("Hello, " + name)
  else hello("stranger");
};

let privet = hello;
privet();  // Hello, stranger // Пока еще нормально.
hello = null;
console.log(privet.name);  // hello // Имена это тема отдельного конспекта, а тут просто чтоб было
privet();  // Ошибка: hello is not a function
```

