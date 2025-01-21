# Состав URL

URL - Uniform Resource Locator. Полный URL со всеми возможными частями выглядит так:

```
https://example.com:8000/info/name?q=term&time=2000#some-anchor
```

Концептуально в URL можно выделить такие части:

* protocol - `https`, протокол, который используется для обращения к ресурсу.
* hostname - `example.com`, имя домена, на котором расположен ресурс.
* port - `8000`, порт на сервере.
* host - `example.com:8000`, понятие хост включает в себя имя хоста + порт.
* origin - `https://example.com:8000`, ориджин - это протокол + имя хоста + порт.
* pathname - `info/name`, путь на сервере до ресурса. Когда-то это мог быть реальный путь в файловой системе, но теперь это обычно абстрактная строка, выражающая логический путь, а не физический.
* search - `q=term&time=2000`, параметры запроса.
* hash - `some-anchor`, конкретная часть ресурса, к которой надо перейти. Например, для веб-страницы, состоящей из множества разделов, это может быть конкретный раздел. Для видео это может быть конкретный момент времени.

# Текущее положение, объект Location

[Документация](https://developer.mozilla.org/en-US/docs/Web/API/Location)

У объектов window и document есть свойство `location`, в котором лежит объект `Location`. Он олицетворяет собой url документа, который в данный момент отображен в окне, а также содержит API для загрузки в окно других документов.

Кроме того, есть свойство `document.URL`, в нем url хранится в виде обычной строки.

## Свойства объекта Location

Свойства объекта Location позволяют удобно получать разные части URL. В этом плане у них с URL свойства совпадают.

```
https://example.com:8000/info/name?q=term&time=2000#some-anchor
```

```javascript
const url = new URL("https://example.com:8000/info/name?q=term&time=2000#some-anchor");
console.log(url.protocol);  // https:
console.log(url.host);      // example.com:8000
console.log(url.hostname);  // example.com
console.log(url.port);      // 8000
console.log(url.origin);    // https://example.com:8000
console.log(url.pathname);  // /info/name
console.log(url.search);    // ?q=term&time=2000
console.log(url.hash);      // #some-anchor
console.log(url.href);  // https://example.com:8000/info/name?q=term&time=2000#some-anchor
```

## Получение значений параметров из url

У Location нет возможности разбить набор параметров на отдельные элементы и вернуть их значения. Набор параметров представлен единой строкой. Однако у URL такая возможность есть и мы можем воспользоваться тем, что можно создать URL на основе Location, а через URL уже получить отдельные значения параметров. 

У URL есть свойство `searchParams`, а у него метод `get()`. Пример:

```javascript
// const url = new URL(window.location);  // <-- Не было под рукой реального URL
const url = new URL("https://example.com:8000/info/name?q=term&time=2000#some-anchor");
console.log(url.searchParams.get("q"));     // term
console.log(url.searchParams.get("time"));  // 2000
```

# Работа с location

## Загрузка новой страницы

Запись в свойство `.location` браузер воспринимает как команду перехода на новую страницу. Правила такие:

* Если указать протокол + домен, тогда браузер воспринимает url как абсолютный и непосредственно загружает указанную страницу.
* Если протокол не указывать, тогда url воспринимается как относительный. Браузер прилепляет эту строку к текущему url и пытается загрузить.
* Вместо прямой записи в location можно использовать метод `.assign()` вот так:  `document.location.assign("https://microsoft.com")`. Что метод, что прямая запись работают идентично.

Перезапись отдельных свойств location, вроде path, search и остальных, браузер тоже воспринимает как команду загрузки новой страницы. Единственное исключение - перезапись hash. Она не ведет к загрузке страницы, а просто осуществляется прокрутка до этого якоря. Если нет явного якоря с именем `#top`, то запись `document.location.hash = "#top"` приведет к прокрутке страницы до самого верха.

Пример: здесь если щелкнуть по первой кнопке, то попадем на сайт microsoft, а если по второй - то не попадем, потому что url будет воспринят как относительный от текущей локальной демо-страницы:

```html
<body>
  <button id="b1">Кнопка 1</button>
  <button id="b2">Кнопка 2</button>
</body>
```

```javascript
const b1 = document.querySelector("#b1");
b1.addEventListener("click", (e) => {
  document.location = "https://microsoft.com";
});

const b2 = document.querySelector("#b2");
b2.addEventListener("click", (e) => {
  document.location = "microsoft.com";
});
```

## Замена страницы

Есть метод `document.location.replace(url)`. Он работает по тем же принципам, что и присвоение в location, но в этом случае новая страница заменяет в истории браузера текущую. Например, если мы находимся на странице А, потом присваиваем в location страницу B, то при нажатии кнопки "Назад" снова окажемся на странице А. Но если мы переходим на В с помощью метода replace, то на А мы уже не попадем, потому что в истории ее как бы и не было, т.к. В ее заменила, а не "вытеснила".

## Перезагрузка страницы

Методом `document.location.reload()` можно перезагрузить текущую страницу.

