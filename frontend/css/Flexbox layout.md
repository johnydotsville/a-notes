# Flexbox layout

Это техника позволяет делать макеты со сложной структурой. Раньше такое делали на таблицах.

# Анатомия

Терминология:

* Флекс-контейнер - элемент, для которого установлено свойство `display: flex`.
* Флекс-элемент - элемент, который *непосредственно* вложен во флекс-контейнер. На эти элементы действуют правила, заданные в контейнере.

```html
<div class="flex-container">
  <div class="flex-element">
    Раз прислал мне барин чаю и велел его сварить.
  </div>
  <div class="flex-element">
    А я отроду не знаю, как хороший чай варить.
  </div>
</div>
```

```css
.flex-container {
  display: flex;  /* Превращает элемент во flex-элемент */
  background-color: aquamarine;
  column-gap: 10px;
  padding: 10px;
}

.flex-element {
  background-color: bisque;
  padding: 10px;
  border: 1px solid black;
}
```



Оси: основная и побочная (поперечная).