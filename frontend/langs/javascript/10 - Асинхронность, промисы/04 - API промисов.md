# static-методы класса Promise

## Метод all

Метод `Promise.all(iterable)` - принимает итерируемый объект с промисами (обычно массив), запускает их на выполнение и возвращает новый промис. Он завершается, когда все вложенные промисы завершаются успешно или в одном из них возникает ошибка. Его результат - массив с результатами исходных промисов. Если вдруг какой-то из исходных элементов массива не является промисом, то он попадает в результирующий массив как есть.

Порядок результатов в конечном массиве такой же, как порядок исходных промисов. Т.е. если промис лежит в 0 элементе массива, то его результат тоже будет в 0 элементе, даже если этот промис выполнится позже всех.

Синтетический пример:

```javascript
let prom = Promise.all([
  new Promise(resolve => setTimeout(() => resolve("Первый"), 3000)),
  new Promise(resolve => setTimeout(() => resolve("Второй"), 2000)),
  new Promise(resolve => setTimeout(() => resolve("Третий"), 1000)),
  "Я не промис"
]);

prom.then(results => results.forEach(m => console.log(m)));  // Первый, Второй, Третий, Я не промис
```

Более практический пример: вывести информацию о пользователях гитхаба с указанными логинами:

```javascript
let gitUsers = ['iliakan', 'remy', 'jeresig'];
let url = 'https://api.github.com/users/';

Promise.all(
  gitUsers
    .map(gu => url + gu)
    .map(link => fetch(link))
)
  .then(responses => Promise.all(responses.map(r => r.json())))
  .then(jsoned => jsoned.forEach(ui => console.log(`id ${ui.id}, name ${ui.name}`)));
```

Комментарии:

* fetch возвращает промис. Так что мы сначала формируем ссылки, потом каждую из них запрашиваем через fetch, получая таким образом три промиса в массиве.
* Полученные ответы нужно преобразовать в json.
  * Метод .json тоже возвращает промис. Поэтому мы из массива ответов с помощью map снова получаем массив промисов.
* Наконец, распаршенные ответы мы обходим и выводим из них информацию в консоль.

### Ошибки или reject в промисах

Промисы выполняются по принципу "все или ничего". Если хотя бы один из них закончится reject'ом, то результатом итогового промиса будет этот reject. При этом остальные промисы тоже выполнятся, т.е. нет никаких механик отмены \ остановки, просто их результаты будут проигнорированы.

```javascript
let prom = Promise.all([
  new Promise(resolve => setTimeout(() => resolve("Первый")), 3000),
  new Promise((resolve, reject) => {
    reject(new Error("Ошибка во втором промисе"));
    // или throw new Error("Ошибка во втором промисе");  // Главное, если throw то не в setTimeout
  }),
  new Promise(resolve => setTimeout(() => resolve("Третий")), 1000)
]);

prom
  .then(results => results.forEach(m => console.log(m)))  // <-- Сюда не попадаем
  .catch(err => console.log(err.message));  // Ошибка во втором промисе
```

## Метод allSettled

Метод `Promise.allSettled(iterable)` - принимает итерируемый объект с промисами (обычно массив), запускает их на выполнение и возвращает новый промис. Он завершается, когда завершаются все вложенные промисы. Его результат - массив с элементами вида:

```javascript
{status:"fulfilled", value:результат} // Результат успешного промиса
{status:"rejected", reason:ошибка}    // Результат провалившегося промиса
```

Т.е. в отличие от метода all тут все результаты идут в зачет, и успешные, и неуспешные.

Синтетический пример:

```javascript
let prom = Promise.allSettled([
  new Promise(resolve => resolve("Результат первого промиса")),
  new Promise(() => { throw new Error("Во втором промисе имитируем ошибку через throw"); }),
  new Promise(resolve => resolve("Результат третьего промиса")),
  new Promise((resolve, reject) => reject(new Error("В четвертом промисе сделали reject")))
]);

prom
  .then(results => results.forEach(r => {
      if (r.status == "fulfilled")
        console.log(r.value);
      else
        console.log(r.reason.message);
    }))
  .catch(err => console.log(err));  // <-- Сюда не попадем, т.к. ошибка тоже считается результатом
```

Метод allSettled поддерживается не всеми браузерами, так что можно добавить полифил:

```javascript
if(!Promise.allSettled) {
  Promise.allSettled = function(promises) {
    return Promise.all(promises.map(p => Promise.resolve(p).then(value => ({
      status: 'fulfilled',
      value: value
    }), error => ({
      status: 'rejected',
      reason: error
    }))));
  };
}
```

## Метод race

Метод `Promise.race(iterable)` - принимает итерируемый объект с промисами (обычно массив), запускает их на выполнение и возвращает новый промис. Он завершается тогда, когда завершается любой из переданных промисов. Результат или ошибка выполнившегося быстрее всех промиса становятся итоговым результатом, а результаты остальных промисов игнорируются.

Синтетические примеры:

```javascript
Promise.race([
  new Promise(resolve => setTimeout(() => resolve(1), 3000)),
  new Promise(resolve => setTimeout(() => resolve(2), 2000)),
  new Promise(resolve => setTimeout(() => resolve(3), 1000)),
])
  .then(result => console.log(result));  // 3
```

```javascript
Promise.race([
  new Promise(resolve => setTimeout(() => resolve(1), 3000)),
  new Promise(resolve => setTimeout(() => resolve(2), 2000)),
  new Promise((resolve, reject) => setTimeout(() => reject(new Error("Ошибка!")), 1000)),
])
  .then(result => console.log(result))
  .catch(err => console.log(err.message));  // Ошибка!
```

## Метод any

Метод `Promise.any(iterable)` - принимает итерируемый объект с промисами (обычно массив), запускает их на выполнение и возвращает новый промис. Он завершается тогда, когда появляется первый *успешно* выполненный промис. Т.е. если какой-то промис выполнился быстрее всех, но завершился ошибкой, то он игнорируется и ожидается первый успешный.

Если все промисы закончились ошибкой, то итоговый промис отклоняется, а в ошибку попадает объект `AggregateError` со свойством `errors`, в котором лежит массив со всеми ошибками выполненных промисов.

Синтетические примеры:

```javascript
Promise.any([
  new Promise((resolve, reject) => setTimeout(() => reject(new Error("Первый промис с ошибкой")), 2000)),
  new Promise(resolve => setTimeout(() => resolve(2), 3000)),
  new Promise((resolve, reject) => setTimeout(() => reject(new Error("Третий промис с ошибкой")), 1000)),
])
  .then(result => console.log(result))  // 2
  .catch(err => console.log(err.message));
```

```javascript
Promise.any([
  new Promise((resolve, reject) => setTimeout(() => reject(new Error("Первый промис с ошибкой")), 3000)),
  new Promise((resolve, reject) => setTimeout(() => reject(new Error("Второй промис с ошибкой")), 2000)),
  new Promise((resolve, reject) => setTimeout(() => reject(new Error("Третий промис с ошибкой")), 1000)),
])
  .then(result => console.log(result))
  .catch(err => err.errors.forEach(e => console.log(e.message)));
/*
  Первый промис с ошибкой
  Второй промис с ошибкой
  Третий промис с ошибкой
*/
```

## Методы resolve и reject

Метод `Promise.resolve` позволяет создать успешно завершенный промис с результатом:

```javascript
Promise.resolve(5)
  .then(result => console.log(result));  //5

// То же самое
new Promise(resolve => resolve(5))
  .then(result => console.log(result));  //5
```

Метод `Promise.reject` позволяет создать отклоненный промис с ошибкой:

```javascript
Promise.reject(new Error("Ошибка!"))
.catch(err => console.log(err.message));  // Ошибка!

new Promise((resolve, reject) => reject(new Error("Ошибка!")))
  .catch(err => console.log(err.message));  // Ошибка!
```

Обычно они используются, когда какой-то метод должен вернуть промис, но при этом как таковых вычислений делать не надо, например, результат уже есть в кэше. Тогда мы просто берем этот результат и оборачиваем в завершенный промис.