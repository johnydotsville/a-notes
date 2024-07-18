# Указания типа

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

# Автоматическое определение типа

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

# Комбинация типов (union type)

Можно собрать "новый" тип, используя комбинацию существующих типов через вертикальную черту `|` или амперсанд `&`. 

## Примитивные типы

Когда мы хотим, чтобы переменная могла принимать значения любого из указанных типов, используем `|`:

```typescript
let value: string | number;  // <-- Собрали новый тип, скомбинировав string и number

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

## Объекты

Для объектов:

* Комбинация `|` предполагает, что итоговый тип должен включать как минимум полный набор свойств одного из исходных типов. Свойства из другого типа - по желанию в любом количестве (либо полностью, либо частично).
* Комбинация `&` предполагает, что итоговый тип должен включать все свойства исходных типов.

Разберем на примерах. Тип "Кошка":

```typescript
type Cat = {  // <-- У кошки
  name: string,  // <-- есть имя,
  meow(): void,  // <-- она умеет мяукать
  scratch(): void  // <-- и царапаться.
}

const barsik: Cat = {  // <-- Это конкретно кошка
  name: "Барсик",
  meow(): void {
    console.log(`${this.name} мяукает.`);
  },
  scratch() {
    console.log(`${this.name} царапается!`);
  }
}
```

Тип "Собака":

```typescript
type Dog = {  // <-- У собаки
  name: string,  // <-- есть имя,
  woof(): void,  // <-- она умеет лаять
  bite(): void  // <-- и кусаться.
}

const tuzik: Dog = {  // <-- Это конкретно собака
  name: "Тузик",
  woof(): void {
    console.log(`${this.name} лает.`);
  },
  bite() {
    console.log(`${this.name} кусается!`);
  }
}
```

### Комбинация |

Комбинация `|` "То ли кот, то ли пес" предполагает, что:

* Животное должно быть или *полноценным* котом, или полноценной собакой.
* Оно может быть и полноценным котом, и полноценной собакой одновременно.
* Когда оно является, например, полноценным котом, то может быть еще и *частично* собакой.
  * И наоборот - если оно полноценная собака, то может быть частично котом.

```typescript
let toLiKotToLiPes: Cat | Dog;  // <-- Гибридный тип "То ли кот, то ли пес"

// <-- Животное является полноценным котом и ни капельки не собакой
toLiKotToLiPes = {
  name: "То ли кот, то ли пес",
  meow(): void {
    console.log(`${this.name} мяукает.`);
  },
  scratch() {
    console.log(`${this.name} царапается!`);
  }
}

// <-- А это одновременно полноценная кошка и полноценная собака
toLiKotToLiPes = {
  name: "То ли кот, то ли пес",
  meow(): void {
    console.log(`${this.name} мяукает.`);
  },
  scratch() {
    console.log(`${this.name} царапается!`);
  },
  woof() {
    console.log(`${this.name} лает.`);
  },
  bite() {
    console.log(`${this.name} кусается!`);
  }
}

// <-- Это полноценный кот, который научился у собаки лаять
toLiKotToLiPes = {
  name: "То ли кот, то ли пес",
  meow(): void {
    console.log(`${this.name} мяукает.`);
  },
  scratch() {
    console.log(`${this.name} царапается!`);
  },
  woof() {
    console.log(`${this.name} лает.`);
  }
}
```

### Комбинация &

Комбинация `&` "Котопес" предполагает, что:

* Животное должно быть одновременно *полноценным* котом и полноценной собакой.

```typescript
let kotopes: Cat & Dog;

// <-- Котопес одновременно обязан быть полноценным котом и полноценной собакой
kotopes = {
  name: "То ли кот, то ли пес",
  meow(): void {
    console.log(`${this.name} мяукает.`);
  },
  scratch() {
    console.log(`${this.name} царапается!`);
  },
  woof() {
    console.log(`${this.name} лает.`);
  },
  bite() {
    console.log(`${this.name} кусается!`);
  }
}

// <-- Ошибка! Котопес не умеет кусаться, значит от не котопес.
kotopes = {
  name: "То ли кот, то ли пес",
  meow(): void {
    console.log(`${this.name} мяукает.`);
  },
  scratch() {
    console.log(`${this.name} царапается!`);
  },
  woof() {
    console.log(`${this.name} лает.`);
  }
}
```

