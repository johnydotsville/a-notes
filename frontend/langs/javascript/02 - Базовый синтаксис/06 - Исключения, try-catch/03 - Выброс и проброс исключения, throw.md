

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

# Проброс исключения

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

