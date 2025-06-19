# Глобальная настройка для вообще всех тестов

* Задаем колбэки:

```javascript
import { server } from './server'
import { beforeAll, afterEach, afterAll } from 'vitest'

beforeAll(() => {
  server.listen()
})
afterEach(() => {
  server.resetHandlers()
})
afterAll(()  => {
  server.close()
})
```

* Настраиваем конфиг `vite.config.ts`:

```typescript
import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';
import tsconfigPaths from 'vite-tsconfig-paths';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  test: {
    setupFiles: ['./src/mocks/setupTests.ts'],  // <-- Файл с колбэками
    silent: false 
  }
})

```

# Настройка для блока

* before \ after можно применять внутри describe и тогда эти колбэки будут срабатывать только для тестов этого блока.
* Принцип "снаружи > вовнутрь > наружу", т.е. сначала будут срабатывать, например, глобальные колбэки, а потом локальные.
  * Итого, последовательность такая:
    1. beforeAll (глобальный)
    2. beforeAll (локальный в describe)
    3. beforeEach (глобальный)
    4. beforeEach (локальный в describe)
    5. Тест (it / test)
    6. afterEach (локальный в describe)
    7. afterEach (глобальный)
    8. afterAll (локальный в describe)
    9. afterAll (глобальный)

# Гайды когда использовать

```javascript
beforeAll(() => {  // <-- Принимает колбэк
  ({ taskId, url, task } = prepareMockData());
  vi.mocked(builder.getTaskByIdUrlBuilder).mockReturnValue(url);
});
```

Названия говорят сами за себя:

* `beforeAll(() => { })`
  * Колбэк выполняется перед *первым* тестом.
  * Тут настраиваем общие для всех тестов моки, данные.
* `beforeEach(() => { })`
  * Колбэк выполняется перед каждым тестом.
* `afterEach(() => { })` 
  * Колбэк выполняется после каждого теста.
  * Здесь можно 
* `afterAll(() => { })` 
  * Колбэк выполняется после *последнего* теста.
