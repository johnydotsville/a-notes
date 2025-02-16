# for of

Цикл `for of` используется для обхода итерируемых объектов. Типичные примеры - строка, массив, мапа, сет:

```javascript
const arr = [5, 7, 4, 2, 8];
for (const item of arr) {
  console.log(item);
}
```

```javascript
const str = "Hello, for of!"
for (const letter of str) {  // <-- Обойдет и выведет каждую букву
  console.log(letter);
}
```

Об особенностях этого цикла с конкретными сущностями, например, с массивами - в соответствующих конспектах.

# for in

Цикл `for in` используется для обхода *свойств объекта*:

```javascript
const person = {
  name: "Tom",
  surname: "Sawyer",
  hello() {
    console.log(`Hello, I'm ${this.name} ${this.surname}!`);
  }
}

person.hello();  // Hello, I'm Tom Sawyer!

for (const prop in person) {  // <-- В prop оказывается имя свойства объекта
  console.log(`prop name: ${prop}, prop value: ${person[prop]}`);
}
/* Вывод:
   prop name: name, prop value: Tom
   prop name: surname, prop value: Sawyer
   prop name: hello, prop value: hello() {
       console.log(`Hello, I'm ${this.name} ${this.surname}!`);
     }
*/
```

Некоторые свойства не обходятся:

* Свойства, являющиеся символами (Symbol).
* Свойства, у которых флаг `enumerable`  установлен в false.

В целом, этот цикл используется редко, т.к. если нужно обойти свойства, можно сделать это так:

```javascript
for (const prop of Object.keys(person)) {
  console.log(`prop name: ${prop}, prop value: ${person[prop]}`);
}
```

Про обход свойств объекта написано в конспекте про объекты, там отдельный конспект по свойствам и что с ними можно вообще делать.

