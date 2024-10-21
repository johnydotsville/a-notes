# Переполнение, overflow

Не наследуется.

```css
overflow: visible;  /* Дефолт, для обоих осей */
overflow: visible visible;  /* Ось x, ось y */
overflow-x: visible;
overflow-y: visible;
```

## Зачем нужно

Определяет поведение блочного элемента, когда содержимое превышает его размеры.

```html
<body>
  <div class="foobar overflow-demo">
    Раз прислал мне барин чаю и велел его сварить,
    а я отроду не знаю как хороший чай варить.
  </div>
  <br />
  <button>Прокрутить невидимое</button>
  <script>
    const box = document.querySelector(".foobar");
    const but = document.querySelector("button");
    but.addEventListener("click", () => box.scrollTop += 10);
  </script>
</body>
```

```css
.foobar {
  background-color: bisque;
  border: 1px solid black;
  width: 150px;
  height: 47px;
}

.overflow-demo {
    
}
```

## Значения

<img src="img/overflow-demo.png" alt="overflow-demo" style="zoom:80%;" />

Как работают эти значения:

* `visible` - значение по умолчанию. Не влезающее содержимое просто отображается за пределами блока.
* `scroll` и `auto`
  * `scroll` - браузер добавляет элементу полосы прокрутки, которыми можно пролистать содержимое, если оно не влезает в блок. Полосы отображаются в любом случае, даже если все влезает и они по сути не нужны.
  * `auto` - браузер добавляет элементу полосы прокрутки только при реальной необходимости.
* `hidden` и `сlip`:
  * `hidden` - не влезающее содержимое визуально обрезается, но его можно пролистать программно.
  * `clip` - не влезающее содержимое визуально обрезается и его *нельзя* пролистать программно.

