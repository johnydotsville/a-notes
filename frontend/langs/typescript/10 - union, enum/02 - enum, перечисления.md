# Синтаксис

```typescript
export enum SelectionType {
  Repository,
  CloningRepository,
  MissingRepository,
}

const repoType: SelectionType = SelectionType.Repository;
```

# Виды перечислений

* Числовые перечисления (дефолт):

```typescript
enum Direction {
  Up,     // = 0,
  Right,  // = 1,
  Down,   // = 2,
  Left    // = 3
}
```

* Строковые перечисления:

```typescript
enum Direction {
  North = "Север",
  East = "Восток",
  South = "Юг",
  West = "Запад"
}
```

# Получение значений перечисления

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

* Числовые enum <=> number
* Строковые enum <=> string

```typescript
enum Direction { Up = "Вперед", Down = "Назад" }
enum Speed { Slow = 10, Fast = 100, UltraFast = 1000 }

function test(direction: string, speed: number) {
  console.log("Выбранное направление: " + direction);
  console.log("Скорость: " + speed);
}

test(Direction.Up, Speed.Fast);  // <-- Ok, enum вместо строки и числа.
```

## Совместимость с Object

Enum в рантайме представляет собой объект. Элементы перечисления являются полями этого объекта. Поэтому само перечисление может оказаться совместимо с объектом, у которого похожая структура:

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

# Advanced

* enum трансформируются в реальные объекты, которые в рантайме лежат в памяти на постоянной основе. Соответственно, даже если они нигде не используются в исходном коде, tree-shaking их не удаляет. Поэтому перечисления немного увеличивают итоговые размеры бандла.

