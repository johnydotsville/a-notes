# Примеси

## Концепция

Концептуально примесь (или *миксин*) - это объект, который не используется сам по себе, а существует, чтобы добавить функциональность другим классам, без применения наследования.

```javascript
let sayHiMixin = {
  sayHi() {
    alert(`Привет, ${this.name}`);
  },
  sayBye() {
    alert(`Пока, ${this.name}`);
  }
};

// использование:
class User {
  constructor(name) {
    this.name = name;
  }
}

// копируем методы
Object.assign(User.prototype, sayHiMixin);

// теперь User может сказать Привет
new User("Вася").sayHi(); // Привет, Вася!
```

