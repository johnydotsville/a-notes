# Терминология

## Окружение

Окружение - это среда, в которой выполняется js-код. Например, браузер, Node JS - это примеры окружений. Каждое окружение предоставляет объекты с функциональностью, которую мы можем использовать. Например, браузер предоставляет объект `document`, через который мы можем влиять на страницу - добавлять в нее новые блоки, удалять существующие, заменять им стили и т.д.

## DOM

Document Object Model (объектная модель документа) - это объекты, которые нам предоставляет браузер для манипуляций с элементами страницы (добавление, удаление, замена стилей и т.д.). Пример такого объекта - `document`. Кроме браузеров, теоретически, DOM нам могут предоставлять и другие окружения. Там может быть урезанная функциональность, но тем не менее.



## BOM

Browser Object Model (объектная модель браузера) - это объекты, которые нам предоставляет браузер для работы с вещами, не относящимися непосредственно к странице. Например, `navigator`, `locator`. С помощью навигатора можно получить, например, информацию о том, в каком именно браузере открыта страница, в какой операционной системе.

## CSSOM

Это спецификация, которая определяет, как css-стили должны представляться в виде объектов. На практике используется редко, потому что css обычно статичный, мы не меняем его в ходе работы программы.

# Спецификации

Несколько ссылок на полезные спецификации:

* HTML https://html.spec.whatwg.org/
* DOM https://dom.spec.whatwg.org
* Мозила база знаний https://developer.mozilla.org/ru/

Совет по гуглению: чтобы найти что-то непосредственно на этих ресурсах, гуглим `WHATWG что-ищем` или `MDN что-ищем`. Например `whatwg localstorage`