* значение `as const` - влияет на то, какой тип вычислится для значения.

```typescript
let status = 'pending';  // у status тип string  
```

```typescript
let status = 'pending' as const;  // у status тип-литерал pending
```

* Для объектов делает то же самое - тип каждого поля становится литералом. Работает на всю глубину объекта.