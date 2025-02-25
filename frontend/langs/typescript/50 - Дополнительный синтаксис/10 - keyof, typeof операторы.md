# typeof

Оператор `typeof` в typescript работает иначе, чем в javascript. И там, и там, операндом является *значение*. Например, примитив или объект. А вот результат - отличается.

## javascript

В js typeof возвращает тип операнда-значения *в виде строки*:

```javascript
// typeof в javascript
const person = { name: "Huck Finn" };  // <-- Переменная со значением.
const t = typeof person;  // <-- Вернет тип значения в виде СТРОКИ
console.log(t);  // object
```

## typescript

В ts typeof возвращает тип операнда-значения *в виде типа*, т.е. в прямом смысле слова возвращает сам тип:

```typescript
const person = { name: "Huck Finn" };  // <-- Переменная со значением.
type t = typeof person;  // <-- Вернет тип значения в виде ТИПА, поэтому исп. type
```

Теперь t у нас представляет собой вот такой тип:

```
type t = {
  name: string;
}
```

### Больше базовых примеров

```typescript
let person;
type t = typeof person;  // undefined
```

```typescript
let person: string;
type t = typeof person;  // string
```

```typescript
let person = "Huck Finn";
type t = typeof person;  // string
```

```typescript
const person = "Huck Finn";
type t = typeof person;  // "Huck Finn"  литерал
```

### Константный объект

Особо интересен пример, когда извлекается тип константного объекта. Интерес в том, что в этом типе в качестве типа полей будет тип-литерал, который *выглядит* как значение:

```typescript
let person = {
  name: "Huck Finn"
} as const;
type t = typeof person;
```

```
type t = {
  readonly name: "Huck Finn";  // <-- Тут тип не string, а "Huck Finn", литеральный тип.
}
```

Однако не надо путать его со значением - это именно тип-литерал, а не значение. Поэтому и работать с ним можно только как с типом. Например, извлечь и объявить с помощью него переменную:

```typescript
let person = {
  name: "Huck Finn"
} as const;
type t = typeof person;

type HuckFinnType = typeof person["name"];
let huckfinn: HuckFinnType = "Huck Finn";
huckfinn = "Tom Sawyer";  // <-- Ошибка!
```

# keyof

`keyof` - это оператор из typescript, в js такого нет. keyof применяется к *типу*. Результатом тоже является тип, представляющий собой union из *имен* всех полей операнда. Вообще это конечно не имена, а строковые литералы, которые сами по себе являются отдельными типами (см конспект по базовым типам про литералы).

Наглядный пример:

```typescript
const person = {  // <-- Есть какое-то значение, объект в данном случае
  name: "Huck Finn",
  age: 13
}

type t = typeof person;  // <-- Получаем ТИП этого значения.
/* 
  type t = {
      name: string;
      age: number;
  }
*/
type k = keyof t;  // "name" | "age"   - keyof выдал ТИП - union из имен полей типа-операнда
```

## Менее очевидный пример

```typescript
let person = "Huck Finn";
type t = typeof person;  // string
type k = keyof t;        // "toString" | "charAt" | "charCodeAt" | "concat" и т.д.
```

```typescript
const person = "Huck Finn";
type t = typeof person;  // "Huck Finn"
type k = keyof t;        // "toString" | "charAt" | "charCodeAt" | "concat" и т.д.
```

В данном случае видно, что keyof взял все свойства, которые есть у string и составил из их имен новый union-тип. Для каждого примитива у ts есть интерфейс, описывающий доступные свойства и методы, так что все эта информация берется из интерфейса, когда keyof генерирует новый тип.

## Константный объект

```typescript
const person = {  // <-- Есть какое-то значение, объект в данном случае
  name: "Huck Finn",
  age: 13
} as const;

type t = typeof person;  // <-- Получаем ТИП этого значения.
/* 
  type t = {
    readonly name: "Huck Finn";
    readonly age: 13;
  }
*/
type k = keyof t;  // "name" | "age"   - keyof так же выдал union из имен полей типа-операнда
```

