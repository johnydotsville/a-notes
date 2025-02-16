# Всплывание объявления (hoisting)

Всплывание объявления - это эффект, из-за которого объявленная через var переменная видна сразу в начале скрипта \ функции. Для сравнения, вот let:

```javascript
function hoisting() {
  name = "Tom";  // <-- Эта строка вызовет ошибку, т.к. переменная объявлена через let
  console.log(name);
  let name;
}

hoisting();  // Ошибка: Cannot access 'name' before initialization
```

А вот var:

```javascript
function hoisting() {
  name = "Tom";  // <-- Когда переменная объявлена через var, такое обращение не дает ошибку 
  console.log(name);
  var name;
}

hoisting();  // Tom
```

Все потому, что var-объявление "всплывает" это равнозначно тому, что переменная объявлена в самом начале.

Однако это относится только к объявлению, но не к присвоению:

```javascript
function hoisting() {
  console.log(name);
  var name = "Tom";
}

hoisting();  // undefined
```

Объявление всплыло, поэтому log видит переменную. А вот инициализация не всплывает, поэтому переменная undefined.
