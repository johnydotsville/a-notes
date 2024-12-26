# Получение объекта события

Когда браузер вызывает обработчик, он отдает ему в качестве единственного аргумента `объект события`. Так что нам при написании функции-обработчика просто достаточной объявить ее с параметром и через него мы получим объект события:

```javascript
function handler(event) {
  // Получаем из объекта event всю нужную информацию.
}
```

# Свойства объекта события

## Общие для всех событий свойства

У всех объектов событий есть несколько общих свойств, а также разные специфические, характерные только для конкретного события. Общие свойства такие:

* `type` - тип произошедшего события в виде строки. Например, "click", "change".
* `target` - "цель" события. Конечный объект, до которого браузер передает событие. Наглядные примеры с объяснениями есть в конспекте про механику распространения события. Например, это кнопка, по которой щелкнул пользователь.
* `currentTarget` - объект, который в данный момент обходит браузер на пути к цели (или на пути от цели, если это уже bubbling-фаза).
* `isTrusted`
  * true, если событие сгенерировал браузер.
  * false, если событие сгенерировано программно.
* `timeStamp` - время возникновения события.
  * Это время абстрактное, т.е. не "5 декабря 2024", а просто цифра в миллисекундах, которая символизирует момент возникновения события (похоже, что от начала загрузки страницы).
  * Вычитая эти цифры друг из друга, можно например понять время, прошедшее между двумя событиями.

Пример:

```html
<body id="body">
  <div id="container">
    <button id="knopka">Нажми на кнопку, получишь результат</button>
  </div>
</body>
```

```javascript
const btn = document.querySelector("#knopka");
const container = document.querySelector("#container");

btn.addEventListener("click", handleClick);
container.addEventListener("click", handleClick);
document.body.addEventListener("click", handleClick);
document.documentElement.addEventListener("click", handleClick);

function handleClick(event) {
  console.log(`type: ${event.type}`);
  console.log(`type: ${event.timeStamp}`);
  console.log(`target: ${event.target.id}`);
  console.log(`currentTarget: ${event.currentTarget.id}`);
}
```

```javascript
// Вывод:
type: click
timeStamp: 1345
target: knopka
currentTarget: knopka

type: click
timeStamp: 1345
target: knopka
currentTarget: container

type: click
timeStamp: 1345
target: knopka
currentTarget: body

type: click
timeStamp: 1345
target: knopka
currentTarget: html
```

## event.isTrusted

По свойству `isTrusted` объекта события можно понять, было ли событие сгенерировано браузером (true), или программно (false):

```html
<body id="body">
  <div id="container">
    <p id="par">
      Нажмите сюда, чтобы браузер сгенерировал событие click на этом параграфе.
      Или нажмите на кнопку, чтобы программно сгенерировать событие click на параграфе.
    </p>
    <button id="btn">Программное событие</button>
  </div>
</body>
```

```javascript
const par = document.querySelector("#par");
const btn = document.querySelector("#btn");

par.addEventListener("click", handleParClick);
btn.addEventListener("click", handleBtnClick)

function handleParClick(event) {
  console.log(`event.isTrusted: ${event.isTrusted}`);
}

function handleBtnClick(event) {
  const clickEvent = new Event("click");
  par.dispatchEvent(clickEvent);  // <-- Породим событие на p программно
}
```

Теперь, если щелкнуть на параграфе, увидим в консоли true, а если на кнопке - то false.

# Методы объекта события

## .preventDefault()

Подробно об этом методе написано в конспекте про механику остановки распространения событий. Здесь же я оставлю только мини-пример на то, как предотвратить перезагрузку страницы при отправке формы:

```html
<body>
  <form>
    <input type="text" />
    <button type="sumbit">Отправить</button>
  </form>
</body>
```

```javascript
const f = document.querySelector("form");

f.addEventListener("submit", (event) => {
  event.preventDefault();
});
```

# this в обработчиках

Обработчик вызывается как метод объекта, на котором этот обработчик добавлен. Поэтому this внутри обработчика указывает на этот объект. Т.е. по сути, this - это то же самое, что `event.currentTarget`:

```html
<body id="body">
  <div id="container">
    <button id="knopka">Нажми на кнопку, получишь результат</button>
  </div>
</body>
```

```javascript
const btn = document.querySelector("#knopka");
const container = document.querySelector("#container");

btn.addEventListener("click", handleClick);
container.addEventListener("click", handleClick);
document.body.addEventListener("click", handleClick);
document.documentElement.addEventListener("click", handleClick);

function handleClick(event) {
  console.log(`${this.id}`);
}
```

```javascript
// Вывод:
knopka
container
body
html
```

Но с лямбдами это, очевидно, так не работает, потому что в лямбдах this вычисляется в момент их создания. Поэтому если надо получить объект элемента, то надежнее воспользоваться объектом события.

# return в обработчиках

В современном JS из обработчиков не принято ничего возвращать. Но если вернуть false, то браузер не будет выполнять дефолтное действие, связанное с событием. Например, не перезагрузит страницу после отправки формы. Однако такой способ является устаревшим и правильная реализация заключается в вызове метода `.preventDefault()` на объекте события (подробнее об этом в соответствующем конспекте).