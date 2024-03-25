# Функции через Function declaration

## Синтаксис

Такой синтаксис называется **Function declaration**:

```javascript
function hello(firstname, lastname) {
  let message = `Hello, ${firstname} ${lastname}!`;
  return message;
}
```

Мы пишем ключевое слово `function`, а после него имя функции.

## Параметры

### Передача по значению

Параметры передаются по значению. Когда мы передаем что-то в функцию, то значения копируются в локальные параметры и исходные данные уже не затрагиваются. Например:

```javascript
let username = "Alice";

function hello(user) {
  user = "Попытаемся сломать исходные данные";
  console.log("Привет, " + user);
}

console.log(username);  // Alice
hello(username);  // Привет, Попытаемся сломать исходные данные
console.log(username);  // Осталось Alice, исходные данные не затронуты
```

### Значения по умолчанию

Если не передать значение параметра, то он получит значение undefined:

```javascript
function hello(name, age) {
  console.log(`Пользователю ${name} сейчас ${age} лет.`);
}

hello("Sam", 20);  // Пользователю Sam сейчас 20 лет.
hello("Sam");      // Пользователю Sam сейчас undefined лет.
```

На такие случаи можно задать значение по умолчанию. В качестве него можно указать литерал или выражение, вызов функции, в общем, что угодно. Каждый раз, когда значение не передано, это выражение будет вычисляться заново:

```javascript
function hello(name, age = 25) {
  console.log(`Пользователю ${name} сейчас ${age} лет.`);
}

hello("Sam");  // Пользователю Sam сейчас 25 лет.
```

## Возврат результата

Возврат результата делается через `return`.

```javascript
function sum(a, b) {
  return a + b;
}
```

► Нельзя размещать return и возвращаемое значение на разных строчках. Если значение сложное и хочется его разбить, необходимо начинать его на одной строке с return, чтобы компилятор не подставил `;` после return:

```javascript
function sum(a, b) {
  return  // Ошибка! Компилятор поставит сюда ; и получится return;
    a + b;
}

function sum(a, b) {
  return (  // Вот так все будет в порядке.
    a + b;
  )
}
```

► Если в функции return указан без возвращаемого значения или вообще отсутствует, то результатом функции будет undefined:

```javascript
function justReturn() {
  return;
}

function noReturn() {
}

let r1 = justReturn();  // undefined
let r1 = noReturn();    // undefined
```