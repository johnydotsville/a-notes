* Все Utility-типы работают по одному принципу:
  * На основе указанного типа создают новый тип в месте использования, а оригинальный тип не затрагивается.

Например:

```typescript
interface User {
  id: number;
  firstname: string;
  lastname: string;
  email: string;
}
```

```typescript
function updateUser(user: User, updUser: Partial<User>): User {
  return { ...user, ...updUser };
}

const huck = {
  id: 1,
  firstname: "Huck",
  lastname: "Finn",
  email: "huckfinn@yahoo.com"
}

const upd = updateUser(huck, { email: "huckleberryfinn@gmail.com" });
```

У нас интерфейс User, где все поля обязательные и функция, которая принимает юзера, у которого надо заменить некоторые поля на переданные. Для второго параметра мы указываем тип `Partial<User>`, чтобы разрешить передавать лишь частично заполненный объект.

Partial - это один из готовых Utility-типов. Из `Partial<User>` получится новый тип, в котором будут такие же поля как в User, но только все они будут необязательные. А оригинальный User как был так и останется. В этом и есть концепция и удобство utility-типов.