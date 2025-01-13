# Устройство Web Storage

Web Storage включает в себя два объекта: `localStorage` и `sessionStorage`. По принципу работы они одинаковые, но есть и некоторые отличия.

Общие черты:

* localStorage и sessionStorage являются свойствами объекта `window`.
* Хранят данные в формате "ключ-значение". Ключ и значение могут быть только строками. Если надо сохранить объект, то его сериализация \ десериализация - забота программиста.

Отличия:

* Время жизни.

  * Данные в localStorage живут до тех пор, пока приложение их не удалит. Пользователь тоже может удалить их, используя инструменты разработчика, встроенные в браузер.

  * Данные в sessionStorage живут пока открыта вкладка. Если вкладку закрыть, то данные удаляются.

    P.S. Тут стоит учитывать возможность браузеров восстанавливать ранее открытые вкладки. Если закрыть браузер целиком и в нем выставлена опция "Восстанавливать открытые ранее вкладки", то при повторном запуске браузера окно откроется и данные в sessionStorage все еще будут присутствовать.

* Скоуп (область видимости). Оба типа хранилища ограничивают скоуп ориджином. Т.е. данные, принадлежащие одному сайту, не доступны на страницах, относящихся к другому сайту. Это базовое правило. Дополнительно:

  * В localStorage область видимости - все вкладки, относящиеся к одному ориджину. Если открыть несколько вкладок и сохранить данные, то на всех вкладках эти данные будут доступны и одинаковые.
  * В sessionStorage область видимости - единственная вкладка, та в которой было выполнено сохранение. Так что если открыть несколько вкладок и сделать сохранение на одной, то эти данные не будут видны на другой вкладке. Т.е. у каждой вкладки собственное хранилище.

Все эти утверждения можно проверить на следующем примере:

```html
<body>
  <input id="srcLS" type="text" />
  <button id="btnSaveLS">Сохранить в localStorage</button>
  <button id="btnLoadLS">Загрузить из localStorage</button>
  <input id="dstLS" type="text" />
  <br>
  <input id="srcSS" type="text" />
  <button id="btnSaveSS">Сохранить в SessionStorage</button>
  <button id="btnLoadSS">Загрузить из SessionStorage</button>
  <input id="dstSS" type="text" />
  <br>
  <button id="btnDelLS">Удалить из localStorage</button>
  <button id="btnClearLS">Очистить localStorage</button>
</body>
```

```javascript
const srcLS = document.querySelector("#srcLS");
const dstLS = document.querySelector("#dstLS");
const btnSaveLS = document.querySelector("#btnSaveLS");
const btnLoadLS = document.querySelector("#btnLoadLS");

const srcSS = document.querySelector("#srcSS");
const dstSS = document.querySelector("#dstSS");
const btnSaveSS = document.querySelector("#btnSaveSS");
const btnLoadSS = document.querySelector("#btnLoadSS");

btnSaveLS.addEventListener("click", () => {
  localStorage.setItem("text", srcLS.value);
});
btnLoadLS.addEventListener("click", () => {
  dstLS.value = localStorage.getItem("text");
});

btnSaveSS.addEventListener("click", () => {
  sessionStorage.setItem("text", srcSS.value);
});
btnLoadSS.addEventListener("click", () => {
  dstSS.value = sessionStorage.getItem("text");
});

btnDelLS.addEventListener("click", () => {
  localStorage.removeItem("text");
});
btnClearLS.addEventListener("click", () => {
  localStorage.clear();
});
```

# API Web Storage

API у localStorage и sessionStorage одинаковый. Разберем на примере localStorage, а для sessionStorage все будет точно так же.

## Сохранение

* Через свойство: просто обращаемся к свойству и если его не существует, то оно создается:

  ```javascript
  localStorage.foobar = "Привет, мир!";
  ```

* Через метод `.setItem(key, value)`:

  ```javascript
  localStorage.setItem("foobar", "Привет, мир!");
  ```

## Чтение

* Через свойство: обращаемся к свойству на localStorage:

  ```javascript
  const value = localStorage.foobar;
  ```

* Через метод `.getItem(key)`:

  ```javascript
  const value = localStorage("foobar");
  ```

## Удаление

* Стандартным оператором удаления свойств из объекта `delete`:

  ```javascript
  delete localStorage.text;
  ```

* Методом `.removeItem(key)`:

  ```javascript
  localStorage.removeItem("text");
  ```

## Очистка

Очистка позволяет разом удалить из хранилища все сохраненные данные:

```javascript
localStorage.clear();
```

# События Web Storage

Существует событие `storage` (поймать его можно на window), которое возникает, когда в localStorage происходят изменения. К изменениям относится добавление, изменение, удаление и очистка данных. Браузер распространяет это событие во всех вкладках, относящихся к одному ориджину, так что это событие можно использовать для общения между вкладками или настроек. У sessionStorage такого события очевидно нет, потому что его скоуп ограничен единственной вкладкой.

В объекте события есть следующие свойства:

* `key` - ключ данных, которые изменились.
* `oldValue` - предыдущее значение ключа. Если ключ добавляется впервые и предыдущего значения нет, то будет undefined.
* `newValue` - значение, на которое изменилось старое.
* `storageArea` - сам объект localStorage.
* `url` - url страницы, которое инициировало изменение.

Есть нюанс с очисткой. Если хранилище пустое, то очистка не тригерит событие. Если не пустое, то после очистки в key, oldValue и newValue будут null, а не undefined. При удалении тоже в newValue помещается null.

```html
<body>
  <input id="srcLS" type="text" />
  <button id="btnSaveLS">Сохранить в localStorage</button>
  <input id="dstLS" type="text" />
  <br>
  <button onclick="localStorage.removeItem('text')">Удалить</button>
  <button onclick="localStorage.clear()">Очистить</button>
</body>
```

```javascript
const srcLS = document.querySelector("#srcLS");
const dstLS = document.querySelector("#dstLS");
const btnSaveLS = document.querySelector("#btnSaveLS");

btnSaveLS.addEventListener("click", () => {
  localStorage.setItem("text", srcLS.value);
  dstLS.value = localStorage.getItem("text");
});

window.addEventListener("storage", (event) => {
  dstLS.value = localStorage.getItem("text");
  console.log("key: " + event.key);
  console.log("oldValue: " + event.oldValue);
  console.log("newValue: " + event.newValue);
  console.log("storageArea: " + event.storageArea);
  console.log("url: " + event.url);
});
```

