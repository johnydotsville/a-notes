# Документация

| Артефакт           | Тип     | На чем ловить | Документация                                                 |
| ------------------ | ------- | ------------- | ------------------------------------------------------------ |
| `DOMContentLoaded` | Событие | `window`      | [MDN](https://developer.mozilla.org/en-US/docs/Web/API/Document/DOMContentLoaded_event) |
| `load`             | Событие | `window`      | [MDN](https://developer.mozilla.org/en-US/docs/Web/API/Window/load_event) |
| `beforeunload`     | Событие | `window`      |                                                              |
| `unload`           | Событие | `window`      |                                                              |



# DOMContentLoaded

* Это событие возникает, когда браузер распарсил страницу и полностью построил DOM.
* При этом все остальное, вроде картинок, async-скриптов, стилей и т.д. на данный момент могло еще не загрузиться.
* Это событие срабатывает после того как выполнились defer-скрипты.

```javascript
window.addEventListener("DOMContentLoaded", (event) => {
  console.log("Обрабатываем событие DOMContentLoaded");
});
```

# load

* Это событие возникает, когда вообще все готово - и все ресурсы загрузились, и все скрипты выполнились, и все стили применились. Т.е. страница максимально полно готова.

```javascript
window.addEventListener("load", (event) => {
  console.log("Обрабатываем событие load");
});
```

# beforeunload

TODO

# unload

TODO

# document.readyState

```javascript
if (document.readyState === "loading") {
  document.addEventListener("DOMContentLoaded", (event) => {
    foobar();
  })
} else {
  foobar();
}
```

