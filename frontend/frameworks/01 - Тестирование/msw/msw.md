# Зачем

`MSW` - Mock Service-Worker. Перехватывает HTTP-запросы и возвращает запрограммированный ответ. Поэтому отлично подходит для тестов, чтобы мокать API.

# Установка

```
npm install -D msw @types/node
```

# Настройка

## Брифинг

* В обработчиках и fetch надо указывать полный url. Как минимум, это касается тестов.
* Расположение всех этих файлов и правильные импорты - по вкусу.

## Обработчики

```javascript
import { HttpResponse, http } from 'msw';
import { fakeTasks } from './fakeTasksList';

export const handlers = [
  http.get('http://localhost:3007/tasks', () => {
    console.log('MSW поймал запрос /tasks');
    return HttpResponse.json(fakeTasks);
  }),
]
```

Фейковые данные:

```javascript
export const fakeTasks: Task[] = [
  {
    id: 1,
    title: "Закончить проект",
    completed: false,
    description: "Доделать финальные правки и отправить на проверку",
    createdAt: "2023-05-15T10:30:00",
    priority: "high",
    tags: ["work", "important"]
  },
  {
    id: 2,
    title: "Купить продукты",
    completed: true,
    description: "Молоко, хлеб, яйца, фрукты",
    createdAt: "2023-05-10T18:45:00",
    priority: "medium",
    tags: ["shopping", "home"]
  },
];
```

## Сервер

```javascript
import { setupServer } from 'msw/node'
import { handlers } from './handlers'  // <-- Тут путь не забудь поправить на свой.

export const server = setupServer(...handlers)
```

## Настройка запуска сервера

Создаем файл `setupTests.ts` чтобы сервер стартовал перед тестами:

```javascript
import { server } from './server'  // <-- Тут путь не забудь поправить на свой.
import { beforeAll, afterEach, afterAll } from 'vitest'

beforeAll(() => {
  console.log('СЕРВЕР MSW ЗАПУЩЕН!');
  server.listen()
  console.log('СПИСОК ОБРАБОТЧИКОВ:', server.listHandlers());
})
afterEach(() => {
  console.log('ОБРАБОТЧИКИ MSW СБРОШЕНЫ!');
  server.resetHandlers()
})
afterAll(()  => {
  console.log('СЕРВЕР MSW ВЫКЛЮЧЕН!');
  server.close()
})
```

* При запуске тестов сервер стартует, а после выполнения всех - дропается.

Чтобы это запускалось автоматически, добавляем в конфиг vite (`vite.config.js` в корне проекта) путь до файла сетапа:

```javascript
export default defineConfig({
  plugins: [react()],
  test: {
    setupFiles: ['./src/mocks/setupTests.ts'],  // <-- Свой путь до сетапа.
  }
})
```

## Проверка тестом

Создаем где-нибудь файл `foobar.test.ts` и пытаемся отправить запрос на обработчик:

```javascript
import { describe, it, expect } from 'vitest'
import { fakeTasks } from '../../../mocks/fakeTasksList'

describe('ПРОВЕРКА MSW', () => {
  it('Проверка /tasks', async () => {
    const response = await fetch('http://localhost:3007/tasks');  // <-- url из обработчика.
    const json = await response.json()
    expect(json).toEqual(fakeTasks)
  })
})
```

```
npm run test --no-cache  // Или как там у тебя запускаются тесты
```

* В консоли мы должны увидеть:
  * Что сервер стартует \ гасится благодаря тому что писали `console.log('СЕРВЕР MSW ЗАПУЩЕН!');`
  * Что msw перехватывает запросы (`console.log('MSW поймал запрос /tasks');`)
  * PROFIT. Ну или в гугл.