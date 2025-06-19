# Суть операторов

* typeof значение => тип.
* keyof объектный тип (type, interface, class) => тип.
  * Поля исходного типа в литералы > union из этих литералов => тип.



# keyof технический пример

```typescript
type User = {  // <-- User это объектный тип (т.е. тип с полями)
  firstname: string;
  lastname: string;
  email: string;
}
```

```typescript
type foobar = keyof User;  // 'firstname' | 'lastname' | 'email'
```



# typeof технический пример

```typescript
const huck = { 
  name: "Huck Finn"
};
type Person = typeof huck;
```

Теперь Person это вот такой тип:

```
type Person = {
  name: string;
}
```

---

Еще несколько базовых технических примеров:

```typescript
let person;
type t = typeof person;  // undefined
```

```typescript
let person: string;
type t = typeof person;  // string
```

```typescript
let person = "Huck Finn";  // let
type t = typeof person;  // string
```

```typescript
const person = "Huck Finn";  // const
type t = typeof person;  // литерал, "Huck Finn"
```

