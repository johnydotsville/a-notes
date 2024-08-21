TODO: вернуться сюда позже, когда лучше пойму, зачем оно надо.

https://webpack.js.org/configuration/resolve/

https://webpack.js.org/concepts/module-resolution/

```javascript
module.exports = {
  ...
  resolve: {
    extensions: ['.tsx', '.ts', '.js'],  // <-- Расширения файлов, которые нужно обрабатывать. ??? Мб это и есть цепочка?
  }
};
```

