# Остаточные параметры

Остаточные параметры `...` - это синтаксис, позволяющий передать в функцию произвольное количество аргументов, независимо от того, сколько параметров в ней объявлено. Все аргументы, которым не нашлось места в параметрах, собираются в массив (настоящий массив, а не псевдомассив). Остаточные параметры обязаны идти в конце списка параметров. Например:

```javascript
function hello(firstname, lastname, ...rest) {
  console.log("Имя: " + firstname + " " + lastname);
  console.log("Псевдонимы:");
  rest.forEach(v => console.log(v));
}
// firstname, lastname |      попадут в массив
hello("Джеки", "Чан",    "Громобой", "Азиатский ястреб", "Ковбой");

// Имя: Джеки Чан
// Псевдонимы:
//   Громобой
//   Азиатский ястреб
//   Ковбой
```

Можно вообще единственным параметром сделать остаточный:

```javascript
function sum(...nums) {
  return nums.reduce((prev, curr) => prev += curr, 0);
}

console.log(sum(5, 6, 4));  // 15
```

# arguments

`arguments` - это псевдомассив, в который собираются все аргументы, переданные в функцию:

```javascript
function foo(name, surname) {  // <-- Попадут в arguments[0] и arguments[1] соответственно
  console.log(arguments[0]);  // Huck
  console.log(arguments[1]);  // Finn
}

foo("Huck", "Finn");
```

Самое интересное,  что аргументы попадают в этот псевдомассив, даже если в функции не описано ни одного параметра:

```javascript
function bar() {  // <-- Параметров нет, но аргументы все равно принимаются
  console.log(arguments[0]);  // Huck
  console.log(arguments[1]);  // Finn
}

bar("Huck", "Finn");
```

Поскольку arguments это псевдомассив, а не настоящий массив, то для него не доступны методы массивов вроде map, reduce и т.д.

## arguments и лямбды

Лямбды не имеют этой переменной и используют arguments внешней функции:

```javascript
function foo() {
  let showArg = () => console.log(arguments[0]);
  showArg(10);  // 5, а не 10
}

foo(5);
```

