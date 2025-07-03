# Пакеты

Сам по себе vitest не содержит функцинальности для тестирования хуков. Поэтому нужна библиотека от react:

```
npm install --save-dev @testing-library/react
```

P.S. Там с версиями могут быть проблемы, т.к. разные версии реакта и разные версии либ тестирования бывают несовместимы. Разбираться надо по факту проблем.

# Конфиг

Мы тут имеем дело с реактом. Ему нужно браузерное API, а vitest работает в Node.js. Поэтому в конфиг `vite.config.ts` надо добавить настройку, имитирующую браузерное окружение:

```javascript
export default defineConfig({
  plugins: [react(), tsconfigPaths()],
  test: {
    environment: 'jsdom',  // <-- Вот так вот.
    setupFiles: ['./src/mocks/setupTests.ts'],
    silent: false
  }
})
```



# Брифинг

* Основа тестирования хуков - функция `renderHook()`.
  * 1 параметр - колбэк с вызовом хука.
  * 2 параметр - объект с полем wrapper.
    * Хуку для работы нужен компонент. Враппер - выполняет роль этого компонента.
    * Обычно создается функция, которая возвращает react-компонент, см. пример.
* renderHook() возвращает объект с полями:
  * `result`
    * Поле `current` - значение, которое вернул хук.
  * `rerender` - функция, позволяющая повторно выполнить хук.
    * Можно ей передать новые пропсы.
  * `unmount` - имитация демонтажа компонента???
* Принцип тестирования:
  * Подготавливаем все что надо.
  * Рендерим хук.
  * Получаем значение из хука через result.current и проверяем это значение.
    * Ну и все остальные интересующие вещи проверяем)))



# Враппер

* Враппер - это react-компонент, т.е. функция, которая возвращает react-элемент.
* JSX может не работать, в этом случае создаем элемент руками, через React.createElement().
* Иногда удобно оформить враппер как отдельную функцию, иногда - написать его прямо в тесте.

```typescript
const wrapper = ({ children }: { children: React.ReactNode }) => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
        },
      },
    });
    return React.createElement(QueryClientProvider, { client: queryClient }, children);
  };
```



# Пример реальный

```typescript
import { describe, it, expect, vi, beforeEach } from "vitest";
import { useFetchTaskById } from "../useFetchTaskById";
import { fetchTaskById } from "@src/api/fetchTaskById/fetchTaskById";
import { fakeTasks } from "@src/mocks/fakeTasksList";
import { renderHook, waitFor } from "@testing-library/react";
import React from "react";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";


vi.mock("@src/api/fetchTaskById/fetchTaskById");


describe('useFetchTaskById тесты', () => {
  
  beforeEach(() => {
    vi.clearAllMocks();
  });


  it('Возвращаются данные', async () => {
    vi.mocked(fetchTaskById).mockResolvedValue(fakeTasks[0]);

    const hook = renderHook(() => useFetchTaskById(1), {  // <--
      wrapper: createQueryProviderWrapper()
    })

    expect(hook.result.current.isTaskFetching).toBe(true);

    await waitFor(() => {  // <--
      expect(hook.result.current.task).toEqual(fakeTasks[0]);
      expect(hook.result.current.isTaskFetching).toBe(false);
      expect(hook.result.current.error).toBeNull();
    })

    expect(fetchTaskById).toHaveBeenCalledWith(1);
  });


  it('Возвращает ошибку при ошибке', async () => {
    const err = new Error('Ошибка');
    vi.mocked(fetchTaskById).mockRejectedValue(err);

    const hook = renderHook(() => useFetchTaskById(1), { 
      wrapper: createErrorWrapper()
    })

    await waitFor(() => {
      expect(hook.result.current.error).toEqual(err);
      expect(hook.result.current.task).toBeUndefined();
    })
  })
})


it.only('Берет данные из кэша при повторном вызове', async () => {
  vi.mocked(fetchTaskById).mockResolvedValue(fakeTasks[0]);
  
  const hook = renderHook(() => useFetchTaskById(1), {
    wrapper: createQueryProviderWrapper()
  })

  await waitFor(() => {
    expect(hook.result.current.task).toEqual(fakeTasks[0]);
  })

  vi.mocked(fetchTaskById).mockClear();

  hook.rerender();  // <--

  await waitFor(() => {
    expect(hook.result.current.task).toEqual(fakeTasks[0]);
    expect(vi.mocked(fetchTaskById)).not.toHaveBeenCalled();
  })
})


function createQueryProviderWrapper() {
  return ({ children }: { children: React.ReactNode }) => {
    const queryClient = new QueryClient();
    return React.createElement(QueryClientProvider, { client: queryClient }, children);
  };
}


function createErrorWrapper() {
  return ({ children }: { children: React.ReactNode }) => {
    const queryClient = new QueryClient({
      defaultOptions: {
        queries: {
          retry: false,
        },
      },
    });
    return React.createElement(QueryClientProvider, { client: queryClient }, children);
  };
}
```

