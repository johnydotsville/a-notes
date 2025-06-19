# Теория по куки

Теория про куки, что они из себя представляют, как образуются, хранятся и т.д., см в общем конспекте по web, в разделе HTTP.

# Способы работы с куки

Работать с куки можно несколькими способами:

* Прямая запись в `document.cookie` - это свойство-сеттер, которое содержит все куки текущей страницы в виде монолитной строки. Поэтому работать с ней напрямую неудобно и легко допустить ошибку.
* Сторонние библиотеки, например, `js-cookie`. Библиотеки предоставляют удобные методы для установки, чтения и удаления кук.
* Cookie Store API - это находящийся в разработке стандартный интерфейс для работы с куки. Пока что плохо поддерживается браузерами, поэтому просто оставлю здесь его упоминание.

# Как выглядят куки

Строка из нескольких кук без атрибутов выглядит так:

```
username=johny; lang=ru; browser=firefox
```

С атрибутами - так:

```
// Атрибуты при выводе document.cookies не отображаются
```

TODO: Было бы неплохо найти текстовый файл, в котором лежат куки и посмотреть как они там хранятся.

# Стандартные операции

Будут рассмотрены через библиотеку `js-cookie` ([CDN](https://www.jsdelivr.com/package/npm/js-cookie), [git](https://github.com/js-cookie/js-cookie)), чтобы не возиться с низкоуровневым API.

> Важно! Хром не позволяет управлять куками, если запустить страницу просто через открытие ее в браузере. Ни установка, ни чтение кук не будет работаоть. Это не проблема библиотеки. Так что надо либо использовать сервер (например, локальный сервер с вебпаком), либо другой браузер (в firefox все вроде нормально).

```html
<!doctype html>
<html id="html">
  <head>
    <meta charset="UTF-8">
    <title>HTML CSS Project</title>
    <link rel="stylesheet" href="css/styles.css">
    <script defer src="https://cdnjs.cloudflare.com/ajax/libs/js-cookie/3.0.5/js.cookie.min.js"></script>
    <script defer src="js/prog.js"></script>
  </head>
  <body>
    Hello, cookie!
  </body>
</html>
```

## Записать куку

### Обычная кука

```javascript
Cookies.set("user name", "Tom Sawyer");
```

В именах и значениях можно использовать пробелы и другие символы, потому что библиотечный метод позаботится об их корректном кодировании.

### Кука с атрибутами

У кук могут быть атрибуты, вроде времени истечения срока действия и т.д.:

```javascript
Cookies.set("access", "denied", { expires: 7 });  // Истекает через 7 дней
```

## Прочитать куку

### Прочитать конкретную куку

```javascript
Cookies.get("username");
```

```javascript
Cookies.set("username", "Tom Sawyer");
Cookies.set("email", "tomsawyer@mail.sp");

const username = Cookies.get("username");  // <-- Читаем конкретную куку
console.log(username);
```

### Прочитать все куки

```javascript
Cookies.get();
```

```javascript
Cookies.set("username", "Tom Sawyer");
Cookies.set("email", "tomsawyer@mail.sp");

const all = Cookies.get();  // <-- Читаем все куки
console.log(all.username);
console.log(all.email);
```

Метод `get()` без параметров возвращает объект, в котором все имеющиеся куки представлены полями.

## Удалить куку

```javascript
Cookies.remove("username");
```

TODO: удаление кук с определенными атрибутами? А будет ли удалена кука, если у нее заданы атрибуты, а при удалении мы указываем только имя?

TODO: дописать про добавление кук со сроком годности.

# TODO

* Почитать фак по js-cookie https://github.com/js-cookie/js-cookie/wiki/Frequently-Asked-Questions#how-to-remove-all-cookies