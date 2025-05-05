`InstanceType<T>` можно применить только к конструктору. На типе `T` стоит вот такое ограничение:

```typescript
abstract new (...args: any) => any
```

