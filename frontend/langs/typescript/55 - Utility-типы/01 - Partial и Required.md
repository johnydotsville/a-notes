# Partial

```typescript
Partial<User>
```

* `Partial` создает новый тип, где все поля такие же как в User, но только являются опциональными (т.е. необязательными).

## Область использования

### "PATCH"-логика

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

### Дефолтные значения + переопределение

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



# Required

```typescript
Required<Foobar>
```

* `Required` создает новый тип, в котором все поля такие же как оригинальном, но только являются обязательными.

## Область использования

### Обязательность для чужих типов

Обычно Required используется, когда мы работаем с чужими типами, которые недоступны нам для редактирования (или редактировать их стремно, дремучее легаси например), и в этих типах есть опциональные поля, а нам эти поля нужны как обязательные. Тогда мы можем применить к чужому типу Required и получить нужную нам обязательность без модификации исходного типа.

```typescript
// Чужой тип (условно импортирован из библиотеки)
type ExternalConfig = {
  apiUrl?: string;
  timeout?: number;
  retry?: boolean;
};

// Наша функция, требующая ВСЕ поля
function initializeApp(config: Required<ExternalConfig>) {
  // do some
}

// Корректный вызов, т.к. все поля заполнены
initializeApp({
  apiUrl: "https://api.example.com",
  timeout: 5000,
  retry: true
});

// Ошибка при компиляции, часть полей не передана:
initializeApp({
  apiUrl: "https://api.example.com",
  timeout: 5000
});
```

