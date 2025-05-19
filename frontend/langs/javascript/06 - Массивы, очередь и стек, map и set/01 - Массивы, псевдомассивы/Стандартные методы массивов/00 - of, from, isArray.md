





Часто используют для генерации задач:

```javascript
Array.from({ length: 20 }, (_, i) => ({
  id: i + 1,
  test: `Task ${i + 1}`
}));
```

