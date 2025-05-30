# Установка и настройка

## Webpack

### Пакеты

Пакеты тестирования устанавливаются как dev-зависимость.

* Пакет джеста:

    ```
    npm i -D jest
    ```

* Типы для поддержки ts'а:

    ```
    npm i -D ts-jest @types/jest
    ```

* Проверить, что jest установился:

    ```
    npm list jest
    ```

### Настройка

Создание файла конфига:

```
npx ts-jest config:init
```

В самом конфиге джеста пока что ничего менять не пришлось, выглядит он так:

```yaml
/** @type {import('ts-jest').JestConfigWithTsJest} **/
module.exports = {
  testEnvironment: "node",
  transform: {
    "^.+.tsx?$": ["ts-jest",{}],
  },
};
```

Далее в конфиг нода package.json добавляем скрипт запуска тестов:

```yaml
{
  # ...
  "scripts": {
    # ...
    "test": "jest"
  },
}
```



## Vite

### Пакеты

```
npm install --save-dev jest ts-jest @types/jest @jest/globals jest-environment-jsdom
```

### Настройка

По умолчанию jest не умеет работать с ESM модулями, поэтому придется это настроить отдельно.

* Создать в корне проекта файл `jest.config.js`:

```javascript
export default {
  preset: 'ts-jest/presets/default-esm', // Используем ESM-пресет
  testEnvironment: 'jsdom', // или 'node', если не нужен браузерный DOM
  moduleNameMapper: {
    '^(\\.{1,2}/.*)\\.js$': '$1', // Для поддержки ESM-импортов
    '^@/(.*)$': '<rootDir>/src/$1', // Алиасы, если используются (как в Vite)
  },
  transform: {
    '^.+\\.tsx?$': [
      'ts-jest',
      {
        useESM: true, // Включаем ESM-поддержку
        // Доп. настройки, если нужно:
        tsconfig: './tsconfig.json',
      },
    ],
  },
  extensionsToTreatAsEsm: ['.ts', '.tsx'], // Обрабатывать .ts как ESM
};
```

* В `tsconfig.app.json` добавить одну настройку:

```json
"compilerOptions": {
  "esModuleInterop": true,
```

## Общие настройки

В конфиг нода `package.json` добавить скрипты запуска тестов:

```json
"scripts": {
  "test": "jest",
  "test:watch": "jest --watchAll"
},
```





# Запуск тестов

* Запускать тесты из консоли командой командой `npm run test`

# Папка для тестов

Два варианта:

* В папке `src`, где лежит проект, надо создать папку `__tests__`. Все тесты кладем в нее и даем им названия `имяТеста.test.ts`
* Можно класть тесты рядом с вещами, которые они тестируют. Главное .test.ts добавлять в имя.

Для проверки базовой работоспособности создаем где-нибудь простую функцию:

```javascript
export function hi() {
  return "hi";
}
```

И пишем для нее тест:

```javascript
import { hi } from "../utils/time-utils";

test("Testing hi function", () => {
  expect(hi()).toBe("hi");
});
```


