# Хук useMutation, базовый пример

Пример кастом-хука, в котором инкапсулировано использование хука useMutation:

```javascript
export const useDeleteTask = () => {
  const client = useQueryClient();

  const deleteTaskMutation = useMutation({
    mutationFn: deleteTask,
    onSuccess: () => client.invalidateQueries({ queryKey: ['tasks'] })
  });

  return {
    fnDeleteTask: (id: number) => deleteTaskMutation.mutate(id)
  }
}
```

Функция, непосредственно выполняющая удаление:

```typescript
export async function deleteTask(id: number) {
  const url = `http://localhost:3001/tasks/${id}`;
  const response = await fetch(url, { method: 'DELETE' });

  if (!response.ok) {
    throw new Error(`Не удалось удалить задачу ${id}`);
  }
  return true;
}
```

# Артефакты useMutation

* useMutation возвращает объект с артефактами вроде isSuccess, mutate.
  * Этот объект обычно не принято деструктурировать.
  * Переменную под этот объект принято именовать по шаблону `действиеMutation`. Например, если назначение мутации - это удалить задачу из списка задач, то имя переменной будет `deleteTaskMutation`.

## Доступные артефакты

* `mutate()` - функция, которая вызывает mutationFn.
* TODO



# Выполнение мутации

* Для выполнения мутации нужно вызвать артефакт-функцию `.mutate(x)`

  * Переданный ей параметр React Query передает в mutationFn.
    * Если параметров много, лучше оформить их в виде объекта.

  

## Как отобразить изменения, сделанные мутацией

* Когда мутация выполнена, для отображения изменений в интерфейсе есть несколько путей:
  * Инвалидировать кэш, чтобы RQ запросил данные повторно.

# Функция mutationFn

## Правила оформления

Функция mutationFn:

* Должна быть асинхронная.
* Должна возвращать что-то, что может быть расценено как true. Т.е. нельзя не вернуть ничего, потому что тогда не сработает колбэк onSuccess.
  * Это значение можно получить через параметр onSuccess.

## Ошибки в mutationFn

* Если в mutationFn возникает ошибка, RQ ловит ее и передает в коллбэк onError.





# Общие советы

* Каждое действие оформлять в виде отдельной мутации.