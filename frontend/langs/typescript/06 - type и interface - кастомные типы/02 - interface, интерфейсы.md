# Объявление интерфейса

* Делается с помощью ключевого слова `interface`.
* Интерфейс может содержать:
  * Свойства.
  * Методы.

```typescript
interface Person {
  readonly firstname: string;
  readonly lastname: string;
  hello(): void;
}
```

# Реализация интерфейса

## Объектом

Объект может реализовать интерфейс. Для этого:

* Объект должен содержать строго все поля и методы, которые есть в интерфейсе, *не больше и не меньше*.
* Добавить в объект дополнительные поля или методы - нельзя, будет ошибка.

```typescript
interface Person {
  firstname: string;
  lastname: string;
  fullname(): string;
  hello(): void;
}

const tom: Person = {  // <-- Для реализации у объекта дб все поля и методы интерфейса.
  firstname: "Tom",
  lastname: "Sawyer",
  fullname() {
    return `${this.firstname} ${this.lastname}`;
  },
  hello() {
    console.log(`Hello! My name is ${this.fullname()}.`);
  }
};

tom.hello();
```

## Классом

Класс может реализовать интерфейс. Для этого:

* Используем ключевое слово `implements`.
* Класс должен иметь все поля и методы, которые есть в интерфейсе.
* Можно добавить в класс дополнительные поля и методы.

```typescript
interface Person {
  firstname: string;
  lastname: string;
  hello(): void;
};

class Character implements Person {  // <-- implements, реализуем интерфейс.
  firstname: string;  // <-- Дб все поля и методы из интерфейса.
  lastname: string;

  constructor(firstname: string, lastname: string) {
    this.firstname = firstname;
    this.lastname = lastname;
  }

  private fullname() {  // <-- и можно добавить дополнительные поля и методы, это не ошибка.
    return `${this.firstname} ${this.lastname}`;
  }

  hello() {
    console.log(`Hello! My name is ${this.fullname()}.`);
  }
}

const huck = new Character("Huck", "Finn");

huck.hello();
```

# Модификаторы для членов интерфейса

## Модификаторы видимости

* К членам интерфейса нельзя применять никакие модификаторы видимости.
* Все члены по умолчанию считаются public и писать это явно не надо (и нельзя).

```typescript
interface Person {
  public firstname: string;  // <-- Ошибка: 'public' modifier cannot appear on a type member.
  private lastname: string;  // <-- Ошибка: 'private' modifier cannot appear on a type member.
  hello(): void;  // <-- Метод public по умолчанию и писать это специально не надо и нельзя.
}
```

## readonly

* К полям интерфейса можно применять модификатор `readonly`.
* В случае объектов:
  * Соответствующие поля объекта станут readonly и модификатор не надо (и нельзя) указывать явно.
* В случае классов:
  * Если не написать readonly для полей, то они перестанут быть readonly. Т.о., класс может понизить требование интерфейса, сделав поля перезаписываемыми.

Объекты:

```typescript
interface Person {
  readonly firstname: string;  // <-- У поля интерфейса мб модификатор readonly.
  readonly lastname: string;
  fullname(): string;
  hello(): void;
}

const tom: Person = {
  firstname: "Tom",  // <-- В реализующем объекте не надо и нельзя писать readonly явно.
  lastname: "Sawyer",
  fullname() {
    return `${this.firstname} ${this.lastname}`;
  },
  hello() {
    console.log(`Hello! My name is ${this.fullname()}.`);
  }
};

tom.firstname = "Huck";  // <-- Ошибка! Нельзя изменять readonly-поля.
tom.lastname  = "Finn";  // <-- Ошибка! Нельзя изменять readonly-поля.
```

Классы:

```typescript
interface Person {
  readonly firstname: string;  // <-- У поля интерфейса мб модификатор readonly.
  readonly lastname: string;
  fullname(): string;
  hello(): void;
}

class Character implements Person {
  firstname: string;  // <-- Класс может снять ограничение readonly.
  readonly lastname: string;

  constructor(firstname: string, lastname: string) {
    this.firstname = firstname;
    this.lastname = lastname;
  }

  fullname() {
    return `${this.firstname} ${this.lastname}`;
  }

  hello() {
    console.log(`Hello! My name is ${this.fullname()}.`);
  }
}

const huck = new Character("Huck", "Finn");

huck.hello();  // Hello! My name is Huck Finn.

huck.firstname = "Tom";  // <-- Изменяется! Т.к. в классе поле уже не readonly.
huck.lastname  = "Sawyer";  // <-- Ошибка! Нельзя изменять readonly-поля.

huck.hello();  // Hello! My name is Tom Finn.
```

# Наследование интерфейсов

Интерфейсы могут наследоваться:

* Используем ключевое слово `extends`.
* Если у интерфейсов `A` и `B` какие-то поля или методы пересекаются, и объект имеет тип `B`, то мы реализуем именно ту версию метода, которая описана в интерфейсе `B`.

```typescript
interface Person {
  firstname: string;
  lastname: string;
  fullname(): string;
  hello(): void;
}

interface Student extends Person {  // <-- extends, Student расширяет интерфейс Person
  specialization: string;
  hello(): string;  // <-- Метод hello() пересекается с методом из Person и возвращает др. тип
}

const danila: Student = {  // <-- Т.к. объект имеет тип Student,
  firstname: "Danila",
  lastname: "Bagrov",
  specialization: "Doctor",
  fullname() {
    return `${this.firstname} ${this.lastname}`;
  },
  hello(): string {  // <-- то реализуем версию hello() именно из интерфейса Student
    return `Hello! My name is ${this.fullname()}. I'm a ${this.specialization}.`;
  }
};

console.warn(danila.hello());  // Hello! My name is Danila Bagrov. I'm a Doctor.
```

# Дополнение интерфейса

* Если объявить интерфейс с одним и тем же именем несколько раз, то оба описания сольются, и в итоговом интерфейсе окажутся члены из обоих описаний.
  * Это называется *declaration merging*.

```typescript
interface Person {
  firstname: string;
  lastname: string;
}

interface Person {  // <-- Повторное объявление интерфейса дополняет исходную версию
  fullname(): string;
  hello(): void;
}

const tom: Person = {
  firstname: "Tom",
  lastname: "Sawyer",
  fullname() {
    return `${this.firstname} ${this.lastname}`;
  },
  hello() {
    console.log(`Hello! My name is ${this.fullname()}.`);
  }
};

tom.hello();  // Hello! My name is Tom Sawyer.
```

