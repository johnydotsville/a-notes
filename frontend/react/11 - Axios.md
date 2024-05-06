# axios

Это библиотека для отправки запросов. Например, чтобы сделать запрос на какой-нибудь бесплатный апи с тестовыми данными, получить эти данные и использовать в своем приложении.

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



# Параметры запроса

```javascript
const response = await axios.get("https://jsonplaceholder.typicode.com/posts", {
  params: {
    _limit: limit,
    _page: page
  }
});
```

