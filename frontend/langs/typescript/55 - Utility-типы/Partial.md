# Описание

```typescript
Partial<User>
```

* `Partial` создает на лету новый тип, где есть все поля исходного типа, но только они необязательные.
* Оригинальный тип не изменяется, т.е. если там все поля были обязательными, они обязательными и остаются. Просто Partial на лету создает в месте использования новый тип, который допускает передачу как бы "частично заполненного" объекта.

# Область использования

## "PATCH"-логика

Partial часто используется в функциях обновления объектов, и подобных местах, где надо получить какую-то часть полей и заменить ими поля исходного объекта. Поскольку исходный тип как правило требует всех полей, то чтобы компилятор разрешил передать "частичный" объект, используется Partial. Например:

```typescript
type UserProfile = {
  id: string;
  name: string;
  email: string;
  age: number;
  isVerified: boolean;
};

function updateProfile(profile: UserProfile, updates: Partial<UserProfile>): UserProfile {
  return { ...profile, ...updates };
}

const alice: UserProfile = {
  id: "user-1",
  name: "Alice",
  email: "alice@example.com",
  age: 25,
  isVerified: false,
};

console.log(alice);

const upedAlice = updateProfile(alice, {
  email: "mynameisalice@umbrella.com",  // <-- Передаем "обгрызанный" объект
  isVerified: true
});

console.log(upedAlice);
```

## Дефолтные значения + переопределение

По сути то же самое, просто немного в другой формулировке. Когда есть какой-то объект с дефолтными значениями и надо заменить в нем значения на другие:

```typescript
type Config = {
  timeout: number;
  retries: number;
  apiUrl: string;
  loggingEnabled: boolean;
};

// Дефолтная конфигурация
const defaultConfig: Config = {
  timeout: 1000,
  retries: 3,
  apiUrl: 'https://api.example.com',
  loggingEnabled: true
};

// Пользовательские переопределения (частичные)
const userConfig: Partial<Config> = {
  timeout: 2000,
  loggingEnabled: false
};

// Финальный конфиг (дефолты + пользовательские настройки)
const finalConfig: Config = {
  ...defaultConfig,
  ...userConfig
};

console.log(finalConfig);
```

