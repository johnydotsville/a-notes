# Лог

- [ ] Возврат из queryFn дополнительных данных, например не только список задач, но и общее количество задач на сервере. Паттерн поле meta.
- [ ] ??? Когда запускается функция? Когда в жизненном цикле так сказать.
  - [ ] Функция (первый раз) запускается сразу при вызове хука, т.е. в момент выполнения функции компонента, т.е. в момент создания fiber-узла компонента.



# Функция загрузки данных, queryFn

Это функция, которая реализует выборку данных. Например, запрос на сервер.

# Асинхронность

Функция загрузки *должна* быть асинхронной и возвращать *промис*. Если передать синхронную, она все равно автоматически завершится промисом, так что лучше сразу писать правильно.

```javascript
const { data, isLoading, error } = useQuery({
  queryKey: ["posts"],
  queryFn: async () => {
    const result = await fetch(`${baseUrl}/tasks`)
    return result.json();
  }
});
```

> Здесь важно не допустить распространенную ошибку "двойного await": `return await result.json()`. Метод json уже возвращает промис, что является корректным. Если же мы пишем перед ним await, получается что мы извлекаем его результат, а потом он автоматически опять оборачивается в промис. Это вносит задержку и в принципе является говнокодом.

# Отлов ошибок

Если в queryFn возникает непойманное исключение, то RQ сам его ловит и возвращает ошибку в объект error из хука.

Однако рекомендуется всегда писать try - catch, даже есть не планируем писать обработку прямо сейчас. Можно просто перевыбросить исключение в данный момент, зато будет задел на будущее. Плюс будет наглядно видно, где может возникнуть ошибка:

```javascript
const { data, isLoading, error } = useQuery({
  queryKey: ["posts"],
  queryFn: async () => {
    try {
      const result = await fetch(`${baseUrl}/tasks`)
      return result.json();
    } catch (err) {
      console.log(err);  // <-- В будущем может понадобиться более сложная обработка.
      throw err;
    }
  }
});
```

# Передача параметров в queryFn

## Проблема

Когда функция маленькая, ее можно написать непосредственно в queryFn:

```javascript
export const TaskList = () => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["posts"],
    queryFn: async () => {  // <-- Функция описана прямо на месте.
      try {
        const result = await fetch(`${baseUrl}/tasks`)
        return result.json();
      } catch (err) {
        console.log(err);
        throw err;
      }
    }
  });
  ...
}
```

И то это уже выглядит громоздко. Поэтому обычно функции выносят отдельно, а в queryFn просто указывают их:

```javascript
async function fetchTasks() {  // <-- Функцию описывают отдельно.
  try {
    const result = await fetch(`${baseUrl}/tasks`)
    return result.json();
  } catch (err) {
    console.log(err);
    throw err;
  }
}
```

```javascript
export const TaskList = () => {
  const { data, isLoading, error } = useQuery({
    queryKey: ["posts"],
    queryFn: fetchTasks  // <-- А тут просто указывают.
  });
  ...
}
```

Но что если у функции есть параметры?

```javascript
async function fetchTask(id, criteria, token) {  // <-- Что если тут нужны параметры?
  // Код выборки данных.
}
```

Для передачи параметров в функцию есть несколько подходов. Они не равнозначны и не однозначны, каждый хорош в какой-то своей ситуации.

## Способы передать параметры

Три способа:

* Через контекст react query.
* Через замыкание.
* Комбинация контекст + замыкание.

## Контекст react query

Когда RQ вызывает queryFn, он передает ей объект контекста:

```javascript
{  // <-- Структура ctx
  queryKey: any[];          // Массив из queryKey
  signal?: AbortSignal;     // Сигнал для отмены запроса (от AbortController)
  meta?: Record<string, unknown>;  // Метаданные, переданные в query
  pageParam?: unknown;      // Параметр пагинации (для useInfiniteQuery)
}
```

Параметры, которые могут понадобиться в функции загрузки данных, условно можно разделить на две группы:

* Условия отбора данных (например, id задачи, категория задачи и т.д.). По сути эти параметры идентифицируют запрос, а значит, по-хорошему, мы должны их перечислить в queryKey. Значит сможем их получить из контекста.
* Вспомогательные параметры, которые не влияют на идентификацию запроса, но нужны для его выполнения. Хороший пример - токен авторизации. Его мы не можем указать в queryKey. Такие параметры можно передавать с помощью лямбды + замыкания.

# TODO реализация передачи параметров

TODO: Примеры получились грубые и очень приблизительные. Думаю забить пока на них и скопировать удачные варианты, когда подвернется из реальных ситуаций. Не хочется тратить время на выдумывание синтетики.

TODO: Есть паттерны, например функцию пишем с параметрами без привязки к RQ, а потом пишем адаптер к этой функции. Адаптер может получить контекст например, извлечь из него параметры, и передать в функцию загрузки. Но как правило функции загрузки не используются в проекте как-то независимо, они обычно уже ориентированы на rq, так что это может быть излишним. Тем не менее, такая практика существует.

## Каноничный способ, через контекст

Таким способом хорошо передавать параметры, которые входят в состав queryKey.

```javascript
export const Task = ({ task }) => {
  const taskId = task.id;

  const { data, isLoading, error } = useQuery({
    queryKey: ["posts", taskId],
    queryFn: fetchTaskById  // <-- Указываем функцию
  });
  ...
}
```

Функция загрузки:

```javascript
async function fetchTaskById(ctx) {  // <-- RQ передает нам контекст.
  const { queryKey } = ctx;  // <-- Извлекаем массив зависимостей.
  const [_, id] = queryKey;  // <-- Извлекаем нужные параметры.
  try {
    const result = await fetch(`${baseUrl}/tasks/${id}`);  // <-- Применяем параметры.
    return result.json();
  } catch (err) {
    console.log(err);
    throw err;
  }
}
```



## Лямбда и замыкание

Этот способ хорошо подходит для передачи параметров, которые не относятся к идентификатору запроса. Стало быть, не входят в состав queryKey и через контекст мы их получить не можем. Поэтому используем замыкание для передачи:

```javascript
export const useUserData = () => {
  const { token } = useAuth(); // <-- Получаем токен каким-либо образом.

  return useQuery({
    queryKey: ["user"], // <-- Токен НЕ относится к идентификатору.
    queryFn: () => fetchUserData(token), // <-- Передаем его через замыкание.
  });
};
```

Функция загрузки:

```javascript
const fetchUserData = async (token: string) => {
  const response = await fetch("/api/user", {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.json();
};
```

## Комбинация, контекст + замыкание

```javascript
export const useUserData = () => {
  const { token } = useAuth();

  return useQuery({
    queryKey: ["user", id],
    queryFn: (ctx) => fetchUserData(token, ctx),
  });
};

const fetchUserData = async (token, ctx) => {
  const { queryKey } = ctx;
  const [_, userId] = queryKey;
  const response = await fetch(`/api/user/${userId}`, {
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });
  return response.json();
};
```



# Отмена загрузки данных

TODO: signal из контекста.







