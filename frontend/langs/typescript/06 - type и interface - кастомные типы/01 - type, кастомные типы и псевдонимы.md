# type ключевое слово

## Псевдоним

С помощью ключевого слова `type` можно объявить псевдоним для существующего типа:

```typescript
type имяТипа = описание;
```

```typescript
type Kg = number;  // <-- Объявляем псевдонимы
type Cm = number;

let weigth: Kg = 85;  // <-- Пользуемся псевдонимами как типами
let height: Cm = 180;
```

Это может быть удобно для придания коду большей читабельности. В примере выше становится понятно, что вес выражен не просто числом, а килограммами. Использование псевдонима - все равно что использование оригинального типа, ничем не отличается.

## Новый тип

Через `type` можно создать и полностью новый тип:

```typescript
type Kg = number;
type Cm = number;

type Person = {  // <-- Объявляем новый тип
  name: string,
  weight: Kg,  // <-- Тут можно использовать псевдонимы
  height: Cm
};

const jack: Person = {  // <-- Создаем переменную этого типа
  name: "Jack",
  weight: 85,
  height: 180
};
```

Тип не обязательно именовать, можно указывать напрямую:

```typescript
function printPoint(p: { x: number, y: number }): void {  // <-- У p объектный тип, без имени
  console.log(`Точка (x: ${p.x}, y: ${p.y})`);
}

printPoint({ x: 5, y: 7 });  // Точка (x: 5, y: 7)
printPoint({ x: 5, y: 7, z: 10 });  // Ошибка: тип не сходится, у параметра p нет поля z
```

## Опциональные свойства

Когда мы объявляем новый тип, то все поля должны присутствовать при создании объекта этого типа, иначе будет ошибка. Если хотим сделать какие-то поля опциональными, то добавляем `?` после имени поля:

```typescript
type Person = {
  name: string,
  weight?: number,  // <-- Сделали поле опциональным, используя ?
  height?: number
};

const jack: Person = {
  name: "Jack"
};
// Если не сделать пропущенные поля опциональными, будет ошибка:
// Type '{ name: string; }' is missing the following properties from type 'Person': weight, height
```

Пропущенных полей не будет в созданном объекте.

TODO: а можно сделать значения по умолчанию для полей?

## Методы

Тип может содержать не только свойства, но и методы:

```typescript
type Person = {
  firstname: string;  // <-- Свойства
  lastname: string;
  fullname(): string;  // <-- И методы
  hello(): void;
};

const tom: Person = {
  firstname: "Tom",
  lastname: "Sawyer",
  fullname() {
    return `${this.firstname} ${this.lastname}`;
  },
  hello() {
    console.log(`Hello! My name is ${this.fullname()}`);
  }
};

tom.hello();
```





