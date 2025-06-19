# Суть

* `тип['поле'] => тип поля`
  * По аналогии как для `объект['поле'] => значениеПоля`, только для типов.

```typescript
type User = {
  firstname: string;
  lastname: string;
  age: number;
}

type ageType = User['age'];  // number
```



# Где применяется

Часто используется с дженериками, в комбинации с keyof, чтобы получать типы полей. Например, реализация типобезопасного геттера для свойств из произвольного объекта:

```typescript
type User = {
  firstname: string;
  lastname: string;
  age: number;
}

function getProp<T, K extends keyof T>(obj: T, prop: K): T[K] {
  return obj[prop];
}

const huck = {
  firstname: 'Huck',
  lastname: 'Finn',
  age: 14
}

console.log(getProp(huck, "lastname"));    // Ok
console.log(getProp(huck, "secondname"));  // Ошибка!
```

* С помощью keyof получили литеральный тип из имен полей T.
* Указали, что K является подтипом этого типа, т.е. его значением может быть только строка из названия поля.
* С помощью T[K] получили тип этого поля.

P.S. Если не понятно, можно повторить конспект про литеральные типы, а потом про оператор keyof.