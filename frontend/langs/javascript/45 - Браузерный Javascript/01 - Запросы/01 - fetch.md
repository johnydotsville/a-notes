

# Механики

* fetch возвращает промис.
* fetch относится к web api.
  * Браузер сразу начинает выполнять запрос в параллельном потоке.
  * Когда получен ответ от сервера, браузер планирует м**А**крозадачу, в которой обрабатывается результат.

# Базовый синтаксис

```javascript
const response = await fetch(url);
```

Мини-пример:

```javascript
async function testFetch() {
  const url = 'https://jsonplaceholderZ.typicode.com/users';

  try {
    const response = await fetch(url);
    if (response.ok) {
      const data = await response.json();
      data.map(d => d.name).forEach(d => console.log(d)); 
    }
  } catch (err) {
    console.log(`Ошибка: ${err.message}`);
  }
}

testFetch();
```



# Задать HTTP-метод

По умолчанию используется метод GET.

```javascript
fetch(url, {
  method: "POST"
})
```



# Обработка ответов, ошибок

* Т.е. fetch возвращает промис, то можно работать через then \ catch \ finally или через async \ await.
* Что считается ошибками, при которых мы попадаем в catch:
  * Некорректный url, указывающий на несуществующий сервер.
  * Отсутствие подключения к сети.
  * CORS-ошибка (если fetch используется в браузере).
* Успешные ответы попадают в then.
  * Ответ с кодом 404 (Not Found), 500 (Internal Server Error) и прочие не считаются ошибкой и в catch не попадают, потому что технически это успешный ответ.
    * Так что надо проверять ответ на ok, прежде чем парсить, иначе можно получить ошибку в неожиданном месте, например при переборе данных.

## response и что из него можно получить

```javascript
const response = await fetch(url);
```

response - это объект, у которого есть поля:

* `ok` - true | false соответственно для ответов с кодами 2xx | 4xx, 5xx.
* `status` - код статуса (202, 404 и т.д.)
* `headers` - объект, похожий на Map, содержащий заголовки ответа.
* `url` - конечный url, с которого вернулся ответ. Актуально, когда сервер делает редиректы.

 и методы:

* `json()` - возвращает промис. Если сервер вернул json, то метод парсит этот json из body и возвращает js-объект.

## Извлечение заголовков

`response.headers`

* `.get('Foo-Header-Bar`')
* `.has('FooHeader-Bar')`

## Парсинг json-ответа

`response.json()`

* Если в body лежит не json, промис отклоняется с ошибкой.
* Парсится только один раз. Второй раз не даст, будет ошибка.

# TODO

* Отмена запроса через AbortController.

