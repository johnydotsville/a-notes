

# Внешний вид, темы

* Typora > Preferences > Appearence > Themes > Open theme folder
* Менять надо в файле, который в корне, а подпапке.
* Для проверки надо переоткрыть файл (на маке не обязательно закрывать программу, достаточно закрыть файл).

## Чекбосы, модификация вида

 ```css
.md-task-list-item > input:before {  /*  Выключенный  */
    content: "";
    display: inline-block;
    vertical-align: middle;
    text-align: center;
    background-color: #363B40;
    /* custom: закругление, размер, положение */
    border: 1.25px solid #b8bfc6;
    margin-top: -0.3rem;
    width: 1rem;
    height: 1rem;
    border-radius: 50%;
}
 ```

  ```css
.md-task-list-item > input:checked:before,  /*  Включенный  */
.md-task-list-item > input[checked]:before {
    content: '\221A';
    /*◘*/
    font-size: 0.625rem;
    line-height: 0.625rem;
    color: #DEDEDE;
    background-color: #3abe25;  /* Зеленый фон */
}
  ```


- [ ] Проверка: выключенный
- [x] Провека: включенный

## Inlide-code

Код, `встроенный ` в строку.

```css
code,  /*  Ищем это  */
tt,
var {
    background: rgba(0, 0, 0, 0.05);
}

code {  /*  И снизу переопределяем  */
	border: 1px solid #00AF14;
	border-radius: 3px;
	background: rgba(40, 60, 40, 0.8);
}
```

## Подчеркнутые заголовки h1

```css
h1 {
  position: relative;
  display: inline-block;
  margin-bottom: 25px; /* Расстояние до текста под линией */
}

h1::after {
  content: '';
  position: absolute;
  left: 0;
  bottom: -5px; /* Отступ линии от заголовка */
  width: 65vw; /* Ширина на всю страницу */
  height: 1px; /* Толщина линии */
  background-color: #E5E5E5; /* Цвет линии */
}
```

