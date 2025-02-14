# Что такое enum

[Официальная документация](https://www.typescriptlang.org/docs/handbook/enums.html)

Концептуально перечисление - это набор *именованных констант*. Т.е. когда какая-то переменная может принимать только одно из нескольких заранее известных значений, то эти значения можно объединить в перечисление и TypeScript не позволит использовать для этой переменной никакие другие значения.

Например:

```typescript
export enum SelectionType {  // <-- Набор предопределенных значений.
  Repository,
  CloningRepository,
  MissingRepository,
}

const repoType: SelectionType = SelectionType.Repository;
```

# Виды перечислений

В TypeScript существует два вида перечислений:

* Числовые
* Строковые

## Числовые перечисления

Это перечисления, у которых значениями являются числа:

```typescript
enum Direction {
  Up = 0,
  Right = 1,
  Down = 2,
  Left = 3
}
```

Если не указать числа явно, то нумерация происходит автоматически, начиная с 0:

```typescript
enum Direction {
  Up,     // 0
  Right,  // 1
  Down,   // 2
  Left    // 3
}
```

Можно задать число явно только одной из констант, тогда остальные пронумеруются автоматически:

```typescript
enum Direction {
  Up = 5,
  Right,  // 6
  Down,   // 7
  Left    // 8
}
```

```typescript
enum Direction {
  Up,     // 0
  Right,  // 1  
  Down = 5,
  Left    // 6
}
```

## Строковые перечисления

Строковые - это перечисления, у которых значения являются строками:

```typescript
enum Direction {
  North = "Север",
  East = "Восток",
  South = "Юг",
  West = "Запад"
}
```

Значения таких перечислений удобно читать в рантайме при отладке.

## Смешанные перечисления (гетерогенные)

Технически есть возможность использовать и числовые, и строковые значения в одном перечислении:

```typescript
enum Mixed {
  Foo = 1,
  Bar = 2,
  Zxc = "Nevermind",
  Qwe = "Come as you are"
}
```

Ситуации, когда это было бы реально полезно, науке не известны.

##  Вычисляемые перечисления

Значения для элементов перечислений могут вычисляться:

```typescript
enum FileAccess {
  None,  // <-- Константное значение
  G = "123".length,  // <-- Вычисленное
}
```

TODO: вписать сюда хороший пример, если когда-нибудь встретится.

# Получение значений перечисления

Из перечисления можно удобно получить значения всех констант:

```typescript
enum Direction {
  North = "Север",
  East = "Восток",
  South = "Юг",
  West = "Запад"
}

const directions: Direction[] = Object.values(Direction);
console.log(directions);  // ["Север", "Восток", "Юг", "Запад"]
```

# Совместимость enum 

## Совместимость со string и number

Строковые и числовые enum совместимы, соответственно, со string и number. Там, где требуется строка или число, можно передать соответствующий enum:

```typescript
enum Direction {
  Up = "Вверх",
  Down = "Вниз",
  Left = "Влево",
  Right = "Вправо"
}

enum Speed {
  Slow = 10,
  Fast = 100,
  UltraFast = 1000
}

function printDirection(direction: string, speed: number) {
  console.log("Выбранное направление: " + direction);
  console.log("Скорость: " + speed);
}

printDirection(Direction.Up, Speed.Fast);  // <-- Ok, можем передать enum вместо строки и числа.
```

## Совместимость с Object

Enum в рантайме представляет собой объект. Элементы перечисления являются полями этого объекта (чуть подробнее об этом в разделе enum под капотом). Поэтому само перечисление может оказаться совместимо с объектом, у которого похожая структура:

```typescript
enum Direction {
  Up = "Вверх",
  Down = "Вниз",
  Left = "Влево",
  Right = "Вправо"
}

function foobar(arg: { Up: string }) {
  console.log(arg.Up);
}

foobar(Direction);  // <-- "Вверх"
```

# Enum под капотом

В основном все типы, которые мы пишем в TS, существуют только в момент транспиляции, а в рантайме от них не остается и следа. Однако с перечислениями это не так. Перечисления трансформируются в реальные объекты, которые в рантайме лежат в памяти на постоянной основе и не могут быть удалены, даже если не используются (tree-shaking не работает на перечисления). Поэтому перечисления немного увеличивают итоговые размеры бандла.

Допустим, у нас есть такой enum:

```typescript
enum Direction {
  Up = "Вверх",
  Down = "Вниз",
  Left = "Влево",
  Right = "Вправо"
}

const names = Object.keys(Direction);
const values = Object.values(Direction);

console.log(names);   // ["Up", "Down", "Left", "Right"]  // имена констант
console.log(values);  // ["Вверх", "Вниз", "Влево", "Вправо"]  // значения констант
```

После транспиляции он превращается вот в такой код:

```typescript
"use strict";
var Direction;
(function (Direction) {
    Direction["Up"] = "UP";
    Direction["Down"] = "DOWN";
    Direction["Left"] = "LEFT";
    Direction["Right"] = "RIGHT";
})(Direction || (Direction = {}));
```

Т.е. по сути перечисление становится объектом, а элементы перечисления становятся полями этого объекта. Значения полей - значения элементов перечисления. Поэтому в примере выше работают методы Object keys и values.

# Альтернатива enum'у

## union

```typescript
type SideValues = "Юг" | "Север" | "Восток" | "Запад";

function printSide(side: SideValues): void {
  console.log(side);
}

printSide("Юг");
```

Чтобы не писать от руки значения при передаче, есть еще подход через константный объект.

## Константный объект

Ахтунг! Сложна! P.S. См конспект по typeof и keyof, если вдруг не понятно, как они работают в своей основе:

```typescript
const SideValues = {  // <-- Альтернатива enum'у
  South: "Юг",
  North: "Север",
  East: "Восток",
  West: "Запад"
} as const;

type SideValuesType = typeof SideValues;  // <-- Получаем тип константного объекта.
/*
  type sideType = {
      readonly South: "Юг";  // <-- Надо создать union-тип из этих литералов (Юг, Север и т.д.)
      readonly North: "Север";  // Он заменит нам перечисление на местах.
      readonly East: "Восток";  // <-- Восток - это не значение, это тип-литерал!
      readonly West: "Запад";
  }
*/
// <-- Сначала получаем union из имен полей типа:
type unionTypeFromKeyNames = keyof SideValuesType;  // "South" | "North" | "East" | "West"
// <-- А теперь с его помощью добираемся до типов-литералов:
type Side = SideValuesType[unionTypeFromKeyNames];  // "Юг" | "Север" | "Восток" | "Запад"
// Все, используем Side на местах вместо перечисления.

type SV = typeof SideValues[keyof typeof SideValues];  // То же самое, только в одну строчку.

// А так бы это выглядело, если написать это вручную
type SV2 = typeof SideValues["North" | "South" | "East" | "West"];
// Синтаксис выше - то же самое что
type SV3 = SideValuesType["North"] | SideValuesType["South"]  |
           SideValuesType["East"]  | SideValuesType["West"];

function printSide(side1: Side, side2: SV, side3: SV2, side4: SV3): void {
  console.log(side1);
  console.log(side2);
  console.log(side3);
  console.log(side4);
}

printSide(SideValues.East, SideValues.North, SideValues.West, SideValues.South);
```

Работает это примерно так:

* В основе лежит тот факт, что если объект константный, то значения его полей не могут изменяться. Значит типы этих полей представляют собой типы-литералы, т.е. не string, а "Север", "Юг" и т.д..

  * Стоит однако помнить, что этот литерал не является ЗНАЧЕНИЕМ, это именно тип с единственно возможным значением, поэтому и работать с ним можно только как с типом.

* Так что если мы делаем `typeof SideValues["North"]` , то получаем тип-литерал `Север`

  * Выражение

    ```typescript
    typeof SideValues["North" | "South" | "East" | "West"];
    ```

    это то же самое, что написать вот так:

    ```typescript
    SideValuesType["North"] | SideValuesType["South"] | SideValuesType["East"]  | SideValuesType["West"];
    ```

  * Таким образом мы достаем каждый литерал и формируем новый union-тип, собранный из отдельных литералов "Север", "Юг" и т.д.

* Сохранив его под самостоятельным псевдонимом, можем использовать его в местах, где нам потребовалось бы перечисление. А для удобного выбора значений используем константный объект.







