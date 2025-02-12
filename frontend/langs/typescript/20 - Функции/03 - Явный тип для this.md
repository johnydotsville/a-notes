# this

Если в обычной функции используется this, то он может быть разных типов в зависимости от того, как функция вызовется и это может привести к ошибкам. Поэтому можно явно указать, какого типа должен быть this. Для этого пишем `this: тип` первым параметром функции. Это не влияет на остальные параметры:

```typescript
type Person = {
  name: string,
  age: number
};

function hello(this: Person, zxc: string) {  // <-- Теперь this в этой функции может быть только типа Person
  console.log(`Hello, my name is ${this.name} and I am ${this.age} years old!`);
}

const tom: Person = { name: "Tom Sawyer", age: 14 };
hello.call(tom, "no matter");

const smt = new Date();
hello.call(smt, "no matter");  // Ошибка: Argument of type 'Date' is not assignable to parameter of type 'Person'

hello("no matter");  // Ошибка: The 'this' context of type 'void' is not assignable to method's 'this' of type 'Person'.
```

Т.о. мы защитились от того, что функция будет вызвана с this некорректного типа.