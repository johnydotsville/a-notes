# CDN

Многие библиотеки можно подключить напрямую к своей странице, используя CDN. Строка подключения выглядит так:

```html
<script defer src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js"></script>
```

Разместить ее можно, например, в конце тега `<head>`:

```html
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>HTML CSS Project</title>
    <link rel="stylesheet" href="./css/styles.css">
    <!-- Библиотека для работы с куки, подключаем из CDN -->
    <script defer src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js"></script>
    <script defer src="/js/prog.js"></script>  <!-- Какой-то свой локальный скрипт -->
  </head>
  <body>
    <p>Hello, html!</p>
  </body>
</html>
```

Больше ничего не требуется. Теперь в своем скрипте мы можем использовать функции подключенной библиотеки:

```javascript
let cookieName = "user name";
let cookieValue = "Tom Sawyer";

Cookies.set(cookieName, cookieValue);
console.log(Cookies.get("user name"));
```

Собственные скрипты должны идти на странице после библиотечных, чтобы сначала загрузились библиотечные и мы могли ими пользоваться уже в своих скриптах.

P.S. Подробнее о способах подключения скриптов к странице можно почитать в разделе про браузерный JS.