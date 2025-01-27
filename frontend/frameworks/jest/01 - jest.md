# Пакеты и настройка

P.S. Написанное далее - сырой черновик (пока что).

Пишу про подключение к уже созданному webpack + typescript проекту.

Пакеты тестирования устанавливаются как dev-зависимость.

Пакет джеста:

```
npm i -D jest
```

Типы для поддержки ts'а:

```
npm i -D ts-jest @types/jest
```

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

Запускать тесты из консоли командой командой `npm run test`

# Папка для тестов

В папке `src`, где лежит проект, надо создать папку `__tests__`. Все тесты кладем в нее и даем им названия `имяТеста.test.ts`

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

