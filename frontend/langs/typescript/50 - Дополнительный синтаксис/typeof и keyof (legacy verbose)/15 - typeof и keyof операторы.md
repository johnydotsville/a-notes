# typeof





#  keyof

## Резюме

* keyof применяется к типам (type, interface, class).
  * К объектам не применяется.
* keyof берет названия свойств из типа и создает из них новый литеральный union-тип.
* Область использования:
  * Предотвращение доступа к отсутствующим свойствам. Например, при экспериментах с дженериками.

## База

keyof - это оператор из typescript, в javascript такого нет. Он принимает тип, берет из него *названия свойств* и делает из них литеральный тип. Например:

```typescript
type User = {
  firstname: string;
  lastname: string;
  email: string;
}
```

```typescript
type foobar = keyof User;  // 'firstname' | 'lastname' | 'email'
```

## Типичные ошибки

keyof работает с *типом*, а не с конкретным объектом. Например:

```typescript
type User = {
  firstname: string;
  lastname: string;
  email: string;
}
type foobar = keyof User;  // Ok

const huck: User = {
  firstname: 'Huck',
  lastname: 'Finn',
  email: 'huckfinn@gmail.com'
}
type baz = keyof huck;  // Ошибка!
```

huck - это конкретный объект, а не тип, поэтому keyof к нему применить нельзя.

Но вот если сначала получить тип объекта huck, а потом применить keyof, тогда нормально:

```typescript
type baz = keyof typeof huck;  // Теперь Ok
```

