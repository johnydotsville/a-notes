# mobile-first

mobile-first-подход - это когда основные стили пишутся для маленьких экранов, например мобильных телефонов. Потом с помощью медиа-запросов дописываются стили для более крупных экранов - планшетов и мониторов.

```css
/* Основные стили */
.container {
  background-color: lightgray;
}

@media (min-width: 720px) {  /* Для ширины > 720px */
  .container {
    background-color: lightpink;
  }
}

@media (min-width: 1000px) {  /* Для ширины > 1000px */
  .container {
    background-color: lightgreen;
  }
}
```

# desktop-first

desktop-first-подход - это когда основные стили пишутся для больших экранов, например мониторов компьютеров. Потом с помощью медиа-запросов дописываются стили для более маленьких экранов - планшетов и мобильников.

```css
/* Основные стили */
.container {
  background-color: lightgray;
  width: 300px;
  height: 150px;
}

@media (max-width: 1000px) {  /* Для ширины < 1000px */
  .container {
    background-color: lightgreen;
  }
}

@media (max-width: 600px) {  /* Для ширины < 600px */
  .container {
    background-color: lightpink;
  }
}
```

# Как запомнить?

Для понимания min-width и max-width есть аналогия с зарплатой:

* Зарплата *минимум* 100к - это больше 100к. Так же и min-width: 720px - стиль активируется, когда ширина 720px и выше.
  * min - это "не меньше".
* Зарплата *максимум* 100к - это значит не больше 100к. Так же и min-width: 720px - стиль активируется, когда ширина не больше 720px.
  * max - это "не больше".

