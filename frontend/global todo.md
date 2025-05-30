

# API Браузера

## fetch

- [ ] fetch
  - [ ] Задать эндпоинт.
  - [ ] Указать метод.
    - [ ] Может ли get иметь body?
  - [ ] Задать данные.
    - [ ] Через параметры, через body.
    - [ ] Как потом это извлечь на Express сервере.

- [ ] new URL

  - [ ] ```
    const url = new URL(`http://localhost:3001/tasks/${id}`);
      url.search = qstring.toString();
    ```

- [ ] URLSearchParams автопроверка на наличие, не надо .size руками делать.

  ```
  const qstring = new URLSearchParams();
    if (fail) qstring.append('_fail', 'true');
  
    console.log(`ВЫЗВАНА ФУНКЦИЯ УДАЛЕНИЯ ЗАДАЧИ ${id}`);
    const url = new URL(`http://localhost:3001/tasks/${id}`);
    url.search = qstring.toString();
  ```

- [ ] 



# HTTP

- [ ] Записанные на сервере заголовки не всегда можно прочитать на клиенте, почему?

  - [ ]  CORS

  - [ ] ```javascript
    res.set({
        'Access-Control-Allow-Origin': '*',
        'Access-Control-Expose-Headers': 'X-Total-Count',
        'X-Total-Count': filteredTasks.length.toString(),
      });
    ```

  - [ ] 





# React

- [ ] Жизненный цикл компонента, монтирование и т.д.
- [ ] Лучше разобраться в том что вообще происходит когда в компоненте много хуков, в них используется async и прочая лабуда.
- [ ] useLayoutEffect.
- [ ] Лучше разобраться в написании собственных хуков.





# Javascript

- [ ] Конструкция `...(data && { tasks: data.tasks }),` как работает, что дб в ()
- [ ] Работа с датами
- [ ] JSON



# Typescript

- [ ] ```
  { [K in keyof T]: NoNulls<T[K]> }
  ```

- [ ] as const оператор

- [ ] ```
  type StrictKeepOptional<T> = {
    [K in keyof T]: NonNullable<T[K]>;
  } & {
    [K in keyof T as undefined extends T[K] ? K : never]?: NonNullable<T[K]>;
  };
  ```

- [ ] ```
  type DeepNonNullable<T> = T extends object
    ? {
        [K in keyof T]: DeepNonNullable<NonNullable<T[K]>>;
      }
    : NonNullable<T>;
  
  interface NestedUser {
    id: number | null;
    info: {
      name: string | undefined;
      age?: number | null;
    };
  }
  
  type StrictNestedUser = DeepNonNullable<NestedUser>;
  ```





# Технологии

- [ ] **Jest, MSW и React Query Devtools** для тестирования react query. **Sentry/LogRetch** — логирование ошибок в проде
- [ ] Open api, Swagger