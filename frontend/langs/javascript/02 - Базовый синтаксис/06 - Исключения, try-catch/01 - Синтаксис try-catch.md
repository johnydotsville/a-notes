# Синтаксис try-catch

```javascript
try {
  noSuchVariable;
} catch (err) {
  console.log("Ошибка обработана");
}
```

Есть новый вариант синтаксис, без объекта ошибки в catch:

```javascript
try {
  noSuchVariable;
} catch {
  console.log("Ошибка обработана");
}
```

