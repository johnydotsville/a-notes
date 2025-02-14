# Зачем нужен

readonly - это модификатор для полей объектов, чтобы их нельзя было изменить после инициализации. Т.е. один раз присвоить значение можно, а изменить потом - уже нельзя.

# Где можно применить

* Применяется  к полям при объявлении типов через ключевые слова type, class, interface.
* Может использоваться для создания неизменяемых массивов, но это уже другая история.

# Применение

## type

```typescript
type Person = {
  readonly firstname: string;
  readonly lastname: string;
}

const tom: Person = {
  firstname: "Tom",
  lastname: "Sawyer"
};

tom.firstname = "Huck";  // <-- Нельзя!
```

## interface

```typescript
interface Person {
  readonly firstname: string;
  readonly lastname: string;
}

const tom: Person = {
  firstname: "Tom",
  lastname: "Sawyer"
};

tom.firstname = "Huck";  // <-- Нельзя!
```

## class

```typescript
class Person {
  readonly firstname: string;
  readonly lastname: string;

  constructor(firstname: string, lastname: string) {
    this.firstname = firstname;
    this.lastname = lastname;
  }
}

const tom: Person = new Person("Tom", "Sawyer");

tom.firstname = "Huck";  // <-- Нельзя!
```

