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





