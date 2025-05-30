# Зачем нужны

* Применяются в основном к union-типам.
  * Принцип похож на Pick и Omit, только по отношению не к полям, а к типам. Если уже есть большой тип, в котором всего понамешано, а нам нужна только какая-то часть этого типа, то извлекаем \ исключаем нужную часть.
* Получается новый тип, в котором
  * Из указанного типа остается только нужное (Extract, "взять себе").
  * Из указанного типа выкидывается ненужное (Exclude, "выбросить нахой").



# Простая демострация

На простых примерах:

```typescript
type SomeBasic = string | number | boolean | undefined | null;

type Defined = Exclude<SomeBasic, undefined>;  // выбросить undefined
type NonEmpty = Exclude<SomeBasic, undefined | null>;  // выбросить undefined и null

type Empty = Extract<SomeBasic, undefined | null>;  // взять undefined и null
```

Когда надо несколько типов взять \ выбросить, пользуемся объединением `|`



# Сложные типы в объединении, принцип совместимости

Когда работаем со сложными типами, вроде примера выше, то Extract \ Exclude ориентируются на совместимость типов:

```typescript
type WsEvent = 
  // События, связанные с пользователями
  | { event: 'message'; content: string; userId: string }
  | { event: 'join'; userId: string; room: string }
  | { event: 'leave'; userId: string }
  // События, НЕ связанные с пользователями
  | { event: 'server_restart'; timestamp: number }
  | { event: 'maintenance'; start: string; end: string }
  // Ошибки (могут быть как с userId, так и без)
  | { event: 'error'; reason: string; userId?: string };

// message, join, leave
type UserEvents = Extract<WsEvent, { userId: string }>;

// message, join, leave, server_restart, maintenance
type NonErrorEvents = Exclude<WsEvent, { event: 'error' }>;
```

Мы не указывали тип полностью, а писали только часть, например `{ userId: string }` или `{ event: 'error' }`. 

Такое возможно, потому что TS ориентируется по *совместимости* типов. На простом примере:

```typescript
type A = { id: number; name: string };
type B = { id: number };

let a: A = { id: 1, name: "Alice" };
let b: B = a;
```

A совместим с B, потому что у A есть все, что нужно для B. Но B не совместим с A, потому что у B нет поля name, т.е. как бы "не хватает полей".

Если сделать поле name опциональным:

```typescript
type A = { id: number; name?: string };  // Добавили опциональность для name
type B = { id: number };

let a: A = { id: 1, name: "Alice" };
let b: B = a;
```

тогда B станет совместим с A, потому что A допускает отсутствие поля name.

Поэтому в примере про события, UserEvents не захватывает error, т.к. у error поля userId может не быть, а мы его гарантированно требуем.

# Опциональные поля

TODO: Exclude с опциональными полями может быть хитрым. Сходу так и не придумаешь хороший пример. Внести, когда встречу.