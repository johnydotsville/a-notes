# CDN

Многие библиотеки можно подключить напрямую к своей странице, используя CDN. Строка подключения выглядит так:

```html
<script src=" https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js "></script>
```

А к своей странице подключается так:

```html
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>HTML CSS Project</title>
    <link rel="stylesheet" href="./css/styles.css">
    <!-- Библиотека для работы с куки -->
    <script src=" https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js "></script>
  </head>
  <body>
    <p>Hello, html!</p>
  </body>
  <script src="/js/prog.js"></script>  <!-- Какой-то свой локальный скрипт -->
</html>
```

Больше ничего не требуется. Теперь в своем скрипте мы можем использовать функции подключенной библиотеки:

```javascript
let cookieName = "user name";
let cookieValue = "Tom Sawyer";

Cookies.set(cookieName, cookieValue);
console.log(Cookies.get("user name"));
```

