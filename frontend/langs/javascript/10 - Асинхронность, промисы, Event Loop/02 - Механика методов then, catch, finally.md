# Резюме по механикам

* Каждый из методов then, catch и finally возвращает новый промис. Состояние этого промиса - pending.
* Если колбэк в методах then, catch и finally не срабатывает, то метод приводит новый промис в такое же состояние как и исходный. Это касается как самого состояния промиса (fullfilled, rejected), так и результата промиса (либо данные, либо ошибка).
* Если колбэк в методах then, catch и finally срабатывает, то состояние и результат нового промиса зависит от того, как этот колбэк выполнится и что вернет.

Все эти механики описывают, в каком виде получается новый промис, выходящий из методов then, catch и finally. В подготовке конспекта хорошо помогли вот эти ссылки (разделы Return value):

* https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/then#return_value
* https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/catch#return_value
* https://developer.mozilla.org/en-US/docs/Web/JavaScript/Reference/Global_Objects/Promise/finally#return_value

# Результат методов - новый промис

Каждый из методов then, catch и finally возвращает *новый* pending-промис. Эти методы сохраняют внутри себя переданные им колбэки и исходный промис, на котором они вызваны.

Демонстрация того, что все методы then, catch и finally возвращают новый pending-промис:

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
});

const thenPromise = promise
  .then(null, null);
console.log(thenPromise === promise);  // false
  
const catchPromise = promise
  .catch(null);
console.log(catchPromise === promise);  // false

const finallyPromise = promise
  .finally(null);
console.log(finallyPromise === promise);  // false

debugger;
```

Как видим, сравнение показывает, что исходный промис и промис, возвращенный методами, являются разными объектами. В отладчике можно увидеть, что состояние у новых промисов - pending.

# Когда колбэк не срабатывает

Когда колбэк из методов then, catch и finally не срабатывает, то метод приводит новый промис в такое же состояние как и исходный. Например, если исходный промис был fullfilled и вернул какие-то данные, то и новый промис станет fullfilled и будет содержать эти же данные. А если исходный был rejected, то и новый станет rejected и будет хранить ошибку, из-за которой исходный промис был отклонен. Т.о. можно сказать, что когда колбэк не срабатывает, то метод логически как бы пробрасывает исходный промис дальше, хотя технически это уже другой промис.

## На примере then

```javascript
const promise = new Promise((resolve, reject) => {  // 1
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .then(null, null)  // 2
  .then(result => {  // 3
    console.log(result);
  });
```

В этом примере у нас есть fullfilled-промис [1], т.е. промис, завершившийся успешно. Мы вызвали на нем then [2] и получили т.о. новый pending-промис. Поскольку для then [2] мы не указали колбэки, то then [2] приводит новый промис в такое же состояние как и исходный, т.е. fullfilled с данными в виде строки "Hello, world!". Далее на этом новом промисе мы вызываем then [3] и колбэк из then [3] получает данные из этого нового промиса.

Вот аналогичный пример для rejected-промиса:

```javascript
const promise = new Promise((resolve, reject) => {  // 1
  console.log("Работает исходный промис.");
  reject(new Error("Ошибки случаются."));
})
  .then(null, null)  // 2
  .then(  // 3
    result => console.log(result),
    error => console.log(error.message)  // Ошибки случаются
  );
```

then [2] привел свой новый промис к такому же виду, как и исходный, т.е. rejected с объектом ошибки внутри. Поэтому на then [3] сработал колбэк, отвечающий за обработку rejected-промиса.

## На примере catch

```javascript
const promise = new Promise((resolve, reject) => {  // 1
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .catch(error => console.log(error.message))  // 2
  .then(result => {  // 3
    console.log(result));  // Hello, world!
  });
```

Колбэк в catch не срабатывает, потому что в исходном промисе нет ошибки. Поэтому catch свой pending-промис переводит в состояние fullfilled и кладет в него данные в виде строки "Hello, world!". Соответственно, уже на этом новом колбэке вызывается then [3] и выводит в консоль данные.

## На примере finally

Пример с finally привести невозможно, потому что колбэк из finally срабатывает всегда. Поэтому о finally в следующем разделе.

# Когда колбэк срабатывает

Когда колбэк срабатывает, то состояние и данные нового промиса зависят от того, как колбэк выполнится и что вернет. В целом есть четыре варианта, что может вернуть колбэк:

* Колбэк не возвращает ничего. Т.е. отсутствие return. Этот случай расценивается как return undefined, что является стандартным поведением для функций в JS. Промис становится fullfilled и в качестве данных у него undefined.
* Колбэк возвращает примитив или объект. Например, `"Hello, world"` или `{ name: "Tom", surname: "Sawyer" }`. В этом случае промис становится fullfilled и в качестве данных у него этот примитив или объект.
* Колбэк возвращает промис, назовем его P. Если P создан уже как успешно завершенный, то новый промис тоже будет fullfilled и с такими же данными. Если P создан как отклоненный, то новый промис тоже будет rejected и содержать ошибку, из-за которой произошло отклонение. Если P создан как pending, тогда новый промис сменит свое состояние только когда P завершится. Ну и данные \ ошибку тоже получит после завершения P.
* В колбэке происходит ошибка. Тогда новый промис будет rejected и содержит ошибку.

## На примере then

### Колбэк не возвращает ничего

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .then(result => {  // 1
    console.log(result);  // Hello, world!
    // Когда нет return, это все равно что return undefined
  })
  .then(result => {  // 2
    console.log(result);  // undefined
  })
```

Колбэк в then [1] не возвращает ничего, поэтому результатом является undefined. Ошибок не было, поэтому промис, который then [1] вернул, становится fullfilled и получает в качестве данных undefined. Колбэк в then [2] получает этот undefined и выводит его в консоль.

### Колбэк возвращает примитив или объект

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .then(result => {  // 1
    console.log(result);  // Hello, world!
    return { name: "Tom", surname: "Sawyer" };
  })
  .then(result => {  // 2
    console.log(`${result.name} ${result.surname}`);  // Tom Sawyer
  })
```

Колбэк в then [1] возвращает объект. Ошибок нет, поэтому промис, который then [1] вернул, становится fullfilled и получает в качестве данных объект ` { name: "Tom", surname: "Sawyer" }`. Колбэк в then [2] получает этот объект и выводит его в консоль.

### Колбэк возвращает промис

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .then(result => {  // 1
    console.log(result);  // Hello, world!
    return new Promise((resolve, reject) => resolve(1337));
  })
  .then(result => {  // 2
    console.log(result);  // 1337
    return Promise.resolve(322);
  })
  .then(result => {  // 3
    console.log(result);  // 322
    return Promise.reject(new Error("Ошибочка вышла."));
  })
  .then(null, error => console.log(error.message));  // Ошибочка вышла.
```

Если колбэк возвращает промис X, то возвращенный из then промис принимает такой же статус как у X, когда X завершается и данные \ ошибка из X попадают в него.

### В колбэке происходит ошибка

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .then(result => {  // 1
    throw new Error("Ошибочка вышла.");
  })
  .then(null, error => console.log(error.message));  // 2
```

В колбэке в then [1] происходит не пойманная ошибка, поэтому промис, возвращенный из then [1], меняет статус на rejected и получает эту ошибку. А коллбэк из then [2] эту ошибку выводит в консоль.

## На примере catch

Если мы что-то возвращаем из колбэка в catch, то все работает по тем же принципам, что и в then. Также если в catch происходит не пойманная ошибка, то все тоже работает как и в then.

## На примере finally

Особенность метода finally в следующем:

* Его колбэк срабатывает всегда.
* finally всегда приводит свой промис к такому же состоянию и данным как промис, на котором finally вызван.
* Любой return в колбэке finally игнорируется.

Вызовем finally на fullfilled-промисе, тогда он и свой промис переведет в fullfilled статус и положит в него данные из исходного промиса:

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .finally(() => {
    console.log("Работает колбэк finally.");
    return 1337;  // Не оказывает никакого эффекта.
    // return Promise.resolve(1337);  // Аналогично, любые return'ы не имеют эффекта.
  })
  .then(result => {
    console.log(result);  // Hello, world!
  });
```

Следующий finally вызван на rejected-промисе, поэтому он и свой промис переведет в статус rejected и положит в него ошибку из исходного промиса:

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  reject(new Error("Ошибочка вышла."));
})
  .finally(() => {
    console.log("Работает колбэк finally.");
  })
  .then(null, error => console.log(error.message));  // Ошибочка вышла.
```

Здесь в самом колбэке finally произойдет ошибка, поэтому finally переведет свой промис в статус rejected и положит в него ошибку:

```javascript
const promise = new Promise((resolve, reject) => {
  console.log("Работает исходный промис.");
  resolve("Hello, world!");
})
  .finally(() => {
    console.log("Работает колбэк finally.");
    throw new Error("Ошибочка вышла.");
  })
  .then(null, error => console.log(error.message));  // Ошибочка вышла.
```

