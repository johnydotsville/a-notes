# Разновидности типов

## string

## boolean

## number

## symbol

## any

Означает любой тип. Например, в переменную any можно положить и строку, и число, и дату, и Person и что угодно. Рекомендуется избегать использование any, потому что по сути теряется возможность проверки на ошибки типов.

## unknown

## never

`never` - это тип для функций, которые физически не могут вернуть значение. Например, в них бесконечный цикл или они всегда выбрасывают ошибку. Т.е. такие функции, по сути, никогда не могут дойти до конца:

```typescript
function foobar(): never {
  while (true) {
    console.log("TODO: вписать фразу из сияния make Jack a dull boy.");
  }
}
```

```typescript
function foobar(): never {
  throw new Error("Я никогда не верну никакого значения.");
}
```

А вот такая функция не может быть never, потому что она выполняется и возвращает undefined:

```typescript
function foobar(): never {
  // Ошибка: A function returning 'never' cannot have a reachable end point.
}
```

## void

`void` - это тип для функций, которые не должны *явно* возвращать значение. По умолчанию в JS, если функция ничего не возвращает, то возвращается undefined:

```typescript
function foobar(): void {
  
}

let zxc = foobar();  // <-- Переменная zxc станет типа undefined
```

Явно вернуть нельзя:

```typescript
function foobar(): void {
  return "Hello";  // Ошибка: Type 'string' is not assignable to type 'void'
}
```

Т.о., void - это защита от того, чтобы кто-то не решил вернуть из функции значение явно.

# Указания типа

## Синтаксис

* Тип указывается через двоеточие `:` после имени переменной или параметра функции.
* Для функции тип указывается после круглых скобок.

```typescript
let name: string;
name = "Tom Sawyer";
```

```typescript
function hello(name: string): void {
  console.log(`Hello, ${name}!`);
}
```

## Несколько типов (union type)

Можно собрать "новый" тип, используя комбинацию существующих через вертикальную черту `|`. Это используется, когда мы хотим, чтобы переменная могла принимать значения любого из указанных типов:

```typescript
let value: string | number;  // <-- Собрали новый тип, объединив string и number

value = "Hello, world!";  // <-- Теперь в переменную можно положить и строку,
value = 7;  // <-- и число
```

Реальный пример: добавить отступ к строке. Отступ можно задать либо непосредственно строкой, либо цифрой количества пробелов:

```typescript
function padLeft(value: string, padding: string | number ): string {
  if (typeof padding === "number") {
    return Array(padding + 1).join(" ") + value;
  }
  if (typeof padding === "string") {
    return padding + value;
  }
  throw new Error(`Expected string or number, got '${padding}'.`);
}
```

Хотя компилятор не даст передать в функцию ничего кроме строки или числа, однако требуется написать что-то за пределами условий, например выбросить ошибку или вернуть строку, иначе будет ошибка "Function lacks ending return statement and return type does not include 'undefined'".



## Автоматическое определение типа

Компилятор может вычислить тип автоматически при присваивании переменной значения, поэтому в таких случаях можно не указывать тип:

```typescript
let name: string = "Tom Sawyer";  // <-- Можно в таких случаях не указывать тип
let name = "Tom Sawyer";  // <-- Тип string для name определится автоматически
```

```javascript
function hello(name: string): string {
  return `Hello, ${name}!`;
}

let message: string = hello("Tom");  // <-- Здесь тоже можно не указывать тип
let message = hello("Tom");  // <-- message будет типа string
```

Если не указать тип и при этом не присвоить значение, то тип будет any:

```typescript
let name;  // <-- Тип переменной name будет any
```

## Литерал как тип

В качестве типа можно указать литерал. Тогда он автоматически станет и типом для переменной, и единственным значением, которое в нее можно положить:

```typescript
let person: "Tom Sawyer";  // <-- Литерал 'Tom Sawyer' в качестве типа

console.log(person);  // Ошибка: Variable 'person' is used before being assigned.
person = "Tom Sawyer";  // <-- Можно присвоить только одноименную строку
console.log(person);  // Tom Sawyer

person = "Huck Finn";  // Ошибка: Type '"Huck Finn"' is not assignable to type '"Tom Sawyer"'
```

TODO: дописать, зачем это вообще нужно



