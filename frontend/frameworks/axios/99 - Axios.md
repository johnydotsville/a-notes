# axios

Это библиотека для отправки запросов. Например, чтобы сделать запрос на какой-нибудь бесплатный апи с тестовыми данными, получить эти данные и использовать в своем приложении.

# Установка

## NodeJS и React

* Установка:

  ```
  npm i axios
  ```

* Подключение:

  ```react
  import axios, {isCancel, AxiosError} from 'axios';
  ```

Пример использования:

```react
async function fetchPosts() {
  const response = await axios.get("https://jsonplaceholder.typicode.com/posts");
  setPosts(response.data);
}
```

## Голый JS и HTML

* Подключаем минифицированную библиотеку из CDN:

  ```html
  <!DOCTYPE html>
  <html lang="en">
  <head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>dotaeomm</title>
  </head>
  <body>
    <div>
      Подключаем axios к голому JS
    </div>
    <!-- Для подключения пользуемся тегом script -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/axios/1.2.1/axios.min.js"></script>
    <!-- А в нашем скрипте будем пользоваться -->
    <script src="./scripts/foobar.js"></script>
  </body>
  </html>
  ```

* Скрипт foobar, где будем пользоваться axios'ом:

  ```javascript
  async function axe() {
    // <-- Используем объект axios
    const response = await axios.get("https://jsonplaceholder.typicode.com/posts", {
      params: {
        _limit: 10,
        _page: 1
      }
    });
    debugger;
  }
  
  axe();
  ```

# Параметры запроса

```javascript
const response = await axios.get("https://jsonplaceholder.typicode.com/posts", {
  params: {
    _limit: limit,
    _page: page
  }
});
```

