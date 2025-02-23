P.S. Изначально этот конспект был в разделе "Браузерный JS". Но потом я решил перенести его в "Общие темы", потому что обнаружил здесь заготовку под этот конспект. Здесь упоминаются события вроде DOMContentLoaded, load - о них можно почитать в разделе "Браузерный JS". Возможно потом этот конспект переедет обратно.

# Скрипты и HTML

Браузеры не выполняют js-код сам по себе. Для этого его нужно подключить в виде скрипта к html-странице. Тогда при загрузке этой страницы браузер выполнит и код.

Для подключения кода к странице используется парный тег `<script></script>`. Есть два способа оформить код с помощью него:

* Написать код непосредственно в теге.
* Указать путь к скрипту через атрибут `src` (такой скрипт называется "внешний"), например:
  * ```html
    <script src='./js-scripts/simplescript.js'></script>
    ```
  
  * ```html
    <script src="https://cdn.jsdelivr.net/npm/js-cookie@3.0.5/dist/js.cookie.min.js"></script>
    ```

Вариант с указанием через src предподчтительнее, потому что:

* Позволяет использовать один и тот же скрипт на нескольких страницах.
* Позволяет браузеру закешировать скрипт и не загружать его повторно при использовании на других страницах.
* Позволяет использовать скрипты, экспортированные веб-серверами.

# Момент выполнения скриптов

## Историческая справка

Когда браузер загрузил html-страницу, он начинает ее парсить и на ходу формировать DOM-дерево. Момент, когда скрипт начнет выполняться, зависит от следующих вещей:

* Место, где скрипт расположен на странице.
* Наличие атрибутов `async` и `defer` у скрипта.

Технически, скрипт может располагаться в любом месте страницы - хоть перед тегом head, хоть внутри него, хоть в body, хоть после закрывающего html. И браузер, как только встретит тег script, тут же начинает его загружать и выполнять (если ничего не настраивать дополнительно). На время загрузки и выполнения скрипта, соответственно, дальнейший парсинг html-страницы приостанавливается.

Такое поведение вызвано историческими причинами. Когда JS только появился, еще не было развитого API по модификации страницы. Т.е. если браузер уже распарсил страницу и нарисовал, то ничего изменить в ней уже было невозможно. Единственным способом кастомизации было прямо в процессе парсинга что-то вписать в текст страницы с помощью скрипта.

Это делалось методом `document.write();` примерно так:

```html
<body>
  <h1>Момент выполнения скриптов.</h1>
  <script>
    document.write("<p>Внедрились в страницу и нарисовали в ней еще один параграф</p>");
  </script>
  <div>Заключение: ну нифига себе!</div>
</body>
```

В результирующей странице сначала будет идти заголовок `<h1>`, потом параграф `<p>` и в конце `<div>`. Т.о. браузер обязан начать загрузку и выполнение скрипта сразу, как только встретит его в тексте страницы, на случай если там используется такой метод. Потому что если такой скрипт пропустить и выполнить его, например, в конце, тогда получим неправильную последовательность элементов - `<h1>`, `<div>` и только потом `<p>`.

## Отложенное выполнение скрипта, атрибуты defer и async

Если браузер каждый раз будет останавливать парсинг страницы, чтобы загрузить и выполнить скрипт, то страницу мы можем увидеть с очень большой задержкой, поскольку скрипты могут быть большими и их может быть подключено много.

Поскольку единственной причиной такого поведения было использование `document.write()`, а теперь он уже не используется, то во время парсинга страницы и формирования DOM логичнее не прерываться на загрузку и выполнение скриптов, а загружать их параллельно и выполнять уже потом, когда страница готова.

Для этого у тега `<script>` существует два атрибута - `defer` и `async`:

```html
<script defer src="./js/foobar.js"></script>
```

```html
<script async src="./js/foobar.js"></script>
```

Общие особенности этих атрибутов:

* И тот, и другой не блокируют парсинг страницы и построения DOM-дерева. Они загружаются параллельно этому процессу.
* Эти атрибуты работают только для внешних скриптов, т.е. для тех, путь до которых указан в src. Если код написан непосредственно в теле тега `<script>`, то атрибуты defer и async игнорируются и скрипт выполняется мгновенно, блокируя парсинг страницы.
* Если у скрипта стоят оба этих атрибута, то async имеет приоритет.
* Вызовы метода `document.write()` в таких скриптах игнорируется.

Отличия атрибутов:

|       | Порядок выполнения                                           | Событие DOMContentLoaded                                     |
| ----- | ------------------------------------------------------------ | ------------------------------------------------------------ |
| defer | defer-скрипты выполняются в том порядке, в котором идут в тексте страницы. Если скрипт A загрузился быстрее скрипта B, но подключен после него, то он будет ждать, пока загрузится и выполнится B. Это полезно при подключении зависимых скриптов. Например, если один скрипт является библиотекой, а второй скрипт пользуется функциями этой библиотеки, тогда очевидно что он должен дождаться, пока библиотека загрузится и выполнится, и только после этого сможет выполниться сам. Deferred-скрипты дожидаются загрузки стилей. | Это событие генерируется только после того как все defer-скрипты загрузятся и выполнятся. Т.е. оно их ждет и только потом возникает. |
| async | async-скрипты выполняются в том порядке, в каком успели загрузиться. Они никого не ждут и их никто не ждет. Поэтому атрибут async подходит для маркировки самостоятельных скриптов, которые ни от кого не зависят и от них тоже никто не зависит. Например, какие-нибудь счетчики посещения. TODO: когда-нибудь вписать побольше примеров. | Это событие и async-скрипты друг от друга не зависят.        |

> Событие `DOMContentLoaded` возникает, когда DOM-дерево полностью построено. Оно хорошо подходит для того, чтобы начать навешивать обработчики на элементы. Однако такие вещи как картинки могут быть еще не загружены.

Приблизительной альтернативой использованию async и defer является размещение тегов `<script>` после закрывающего `</html>`. Тогда когда парсер дойдет до них, DOM-дерево и так уже будет построено.

## Динамическое подключение скрипта

Если не использовать модули, то динамическое подключение скрипта реализуется через программное создание элемента script и добавление его в документ. Под "динамическим" подключением подразумевается, что изначально мы этот скрипт не упоминаем в разметке, а добавляем его уже после того, как разметка отрисовалась. Например, когда пользователь нажал кнопку. Пример реализации:

Пишем какой-нибудь скрипт, который будем подключать динамически, и сохраняем его в отдельный файл, например `helloscript.js`:

```javascript
function message(text) {
  console.log(text);
}
```

Теперь мы хотим подключить этот скрипт к странице, чтобы иметь возможность пользоваться функцией message.

```html
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8">
    <title>HTML CSS Project</title>
    <link rel="stylesheet" href="css/styles.css">
    <script defer src="js/prog.js"></script>
  </head>
  <body>
    <button onclick="start()">Загрузить скрипт</button>
    <button onclick="message('Hello, script')">Выполнить функцию</button>
  </body>
</html>
```

Здесь у нас к странице подключается базовый скрипт prog.js, который выглядит так:

```javascript
function include(url) {  // <-- Функция загрузки и подключения скрипта к странице.
  return new Promise((resolve, reject) => {
    const script = document.createElement("script");
    script.onload = () => resolve();
    script.onerror = (err) => reject(err);
    script.src = url;
    document.head.append(script);
  });
}

async function start() {  // <-- Функция запуска загрузки.
  try {
    // Путь надо указывать от страницы, на которой выполняется текущий скрипт (prog.js)
    await include("js/helloscript.js");
    console.log("Скрипт успешно добавился на страницу.");
  } catch(err) {
    console.log(err.message);
  }
}
```

Теперь, если нажать на первую кнопку, то скрипт загрузится и функция message станет доступна для использования. Если же мы сразу нажмем вторую кнопку, то будет ошибка, что функция message неизвестна.

# Фазы выполнения, свойство Document.readyState

"Программой" на клиентской части можно назвать всю совокупность скриптов, подключенных к странице. Условно можно разделить выполнение этой программы на две фазы:

* "Фаза выполнения скриптов". Первая фаза - это момент загрузки страницы, во время которой выполняются подключенные скрипты. Эти скрипты могут как ничего не делать (например, просто объявлять функции и переменные для дальнейшего использования), так и выполнять довольно объемную работу (например, искать все заголовки на странице и добавлять в начало блок с оглавлением).
* "Событийная". Вторая фаза - асинхронная, управляемая событиями. Например, щелчки мышью, ввод с клавиатуры, сетевые события и т.д. Если скрипты во время первой фазы навесили обработчики на интересующие их события, то браузер будет вызывать эти обработчики при наступлении соответствующих событий.

Первая фаза обычно довольно быстрая, мб около секунды, а вторая - по сути не ограничена временем. Пока страница открыта, можно сказать, что она находится во второй фазе. Может ничего не происходить, а могут быть всплески активности.

Если детализировать этапы загрузки страницы, то все выглядит примерно так:

* Браузер загружает страницу и создает объект документа (Document).
* Браузер начинает парсить страницу. У документа есть свойство, `Document.readyState`. На этом этапе оно имеет значение `loading`.
* По мере парсинга тегов браузер создает экземпляры соответствующих классов и добавляет эти экземпляры в Document.
* Когда браузер встречает скрипт:
  * Браузер создает объект скрипта и добавляет его в документ.
  * Если скрипт без атрибутов async, defer и не является модулем, то:
    * Начинается синхронное выполнение скрипта, а парсинг страницы приостанавливается. Если нужно загрузить скрипт, тогда он сначала загружается, а потом выполняется.
    * Такой скрипт может использовать метод document.write() и записанные вещи будут видны парсеру при возобновлении парсинга. Также скрипту виден документ в том виде, в каком он существует на данный момент, т.е. например если расположить скрипт посередине страницы, то он будет видеть все теги, которые были выше него. Он видит свой собственный тег скрипта.
  * Если скрипт с атрибутом async, то:
    * Браузер стартует загрузку текста скрипта, а сам в это время продолжает парсить страницу. Т.е. не ждет, пока скрипт загрузится до конца.
    * Асинхронный скрипт начинает выполнение после загрузки, не дожидаясь пока документ полностью распарсится. async-скрипт видит свой тег скрипта и элементы, которые стоят перед ним. Остальные может видеть, а может и нет. Он не может использовать метод document.write(). Попытки вызвать этот метод игнорируются.
  * Если скрипт с атрибутом defer, то:
    * Скрипт начинает загружаться, но выполнение откладывается до момента, пока документ полностью не распарсится.
    * defer-скрипты выполняются строго в том порядке, в каком они следуют в документе.
    * defer-скрипты тоже не могут использовать метод document.write(). Попытки вызвать этот метод игнорируются.
* Когда браузер полностью распарсил документ, то `Document.readyState` получает значение `interactive`.
* После окончания парсинга браузер начинает выполнять все defer-скрипты. Т.о. defer-скриптам доступна полностью готовая структура документа. Когда браузер выполнил все defer-скрипты, то он вызывает событие `window.DOMContentLoaded`.
* В этот момент может продолжаться догрузка async-скриптов, а также картинок (а еще мб стилей, шрифтов, но это не точно, TODO). Когда все эти вещи тоже загружены, и async-скрипты выполнены, то `Document.readyState` получает значение `complete`, а браузер вызывает событие `window.load`.
* С этого момента начинается вторая фаза, когда браузер запускает обработчики событий по мере возникновения этих событий.

Резюмируя состояние документа:

| document.readyState | Что означает                                                 |
| ------------------- | ------------------------------------------------------------ |
| `loading`           | Браузер парсит страницу и еще не закончил формирование DOM.  |
| `interactive`       | Браузер полностью распарсил страницу и DOM полностью готов. Но ресурсы, вроде картинок, async-скриптов и т.д. не факт что уже загрузились. |
| `complete`          | Браузер загрузил и выполнил вообще все - и async-скрипты, и картинки, и стили и т.д. |

TODO: надо бы дополнительно погуглить про комбинации async + module, defer + module. Потому что до конца не понятно, влияет ли модульность как-то на загрузку или нет.