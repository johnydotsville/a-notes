

# Синтаксис try-catch

```javascript
try {
  noSuchVariable;
} catch (err) {
  console.log("Ошибка обработана");
}
```

Есть новый вариант синтаксис, без объекта ошибки в catch:

```javascript
try {
  noSuchVariable;
} catch {
  console.log("Ошибка обработана");
}
```

# Объект ошибки

## Стандартные ошибки

Объект ошибки содержит два обязательных свойства:

* `name` - название ошибки.
* `message` - описание ошибки.

В разных окружениях кроме них могут быть и другие свойства, например:

* `stack` - сообщение со всем стеком произошедших ошибок.

```javascript
try {
  noSuchVariable;
} catch (err) {
  console.log("Вид ошибки: " + err.name);  // Вид ошибки: ReferenceError
  console.log("Текст ошибки: " + err.message);  // Текст ошибки: noSuchVariable is not defined
}
```

Вообще, в качестве ошибки можно использовать даже примитивы, однако лучше так не делать.

## Собственные ошибки

Для создания собственных категорий ошибок нужно расширить класс Error. Он выглядит примерно так:

```javascript
// "Псевдокод" встроенного класса Error, определённого самим JavaScript
class Error {
  constructor(message) {
    this.message = message;
    this.name = "Error"; // (разные имена для разных встроенных классов ошибок)
    this.stack = <стек вызовов>; // нестандартное свойство, но обычно поддерживается
  }
}
```

Пример создания собственного класса ошибки:

```javascript
class ValidationError extends Error {
  constructor(message) {
    super(message);
    this.name = "ValidationError";
  }
}
```

# Выброс исключения

## throw

Делается оператором `throw`:

```javascript
throw new Error("Учимся выбрасывать исключения самостоятельно.");
```

Есть несколько встроенных типов ошибок, вот некоторые:

* Error
* SyntaxError
* ReferenceError
* TypeError

```javascript
try {
  throw new Error("Учимся выбрасывать исключения самостоятельно.");
} catch (err) {
  console.log("Вид ошибки: " + err.name);  // Вид ошибки: Error
  console.log("Текст ошибки: " + err.message);  // Текст ошибки: Учимся выбрасывать исключения самостоятельно.
}
```

## Проброс исключения

В catch мы должны обрабатывать только предусмотренные нами ошибки. Все остальные мы пробрасываем дальше. Но поскольку catch ловит все исключения, нам придется проверить ошибку, чтобы понять ее вид. Это можно сделать несколькими способами:

► Проверить свойство name:

```javascript
try {
  noSuchVariable;
} catch (err) {
  if (err.name == "Error") {
    console.log("Ошибку вида Error мы обработаем.");
  } else {
    throw err;
  }
}
```

► Воспользоваться оператором instanceof:

```javascript
try {
  let user = readUser('{ "age": 25 }');
} catch (err) {
  if (err instanceof ValidationError) {
    alert("Некорректные данные: " + err.message);
  } else if (err instanceof SyntaxError) {
    alert("JSON Ошибка Синтаксиса: " + err.message);
  } else {
    throw err;
  }
}
```

Вариант с instanceof предпочтительнее, потому что более гибкий. Если мы сделаем много наследников VelidationError, то этот обработчик будет срабатывать на них всех. В случае с именем же такого не будет.

# finally

Характеристики блока finally в JS:

* Блок finally выполняется всегда, не зависимо от того, была ошибка или нет.
* У каждого блока - try, catch и finally - собственная область видимости.
* Если в try или catch был return, то finally получит управление до этого return и все равно выполнится.
* Чтобы пользоваться finally, блок catch не обязателен. В этом случае даже если в try возникает ошибка, она не обрабатывается, а finally все равно срабатывает.

# Ошибки в отложенном коде

try-catch работает синхронно. Это значит, что он не поймает ошибку в коде, который выполнится в будущем. Например:

```java
try {
  setTimeout(() => noSuchVariable, 1000);
} catch (err) {
  console.log("Ошибка обработана");
}
```

В таких случаях надо ловить ошибку в самом отложенном коде.

# Глобальный catch

В разных окружениях есть разные способы настроить "глобальный" отлов ошибок. Обычно это нужно не для того, чтобы предотвратить падение скрипта, а чтобы залогировать ошибку и просто сказать пользователю "Извините, наши полномочия тут все".

В браузере это делается путем установки функции-обработчика в `window.onerror`:

```javascript
window.onerror = function(message, url, line, col, error) {
  alert(`${message}\n В ${line}:${col} на ${url}`);
};
```

