Предзагрузка данных

```javascript
export function usePrefetchTask() {
  const client = useQueryClient();
  return (taskId: number) => {
    client.prefetchQuery({
      queryKey: ['tasks', taskId],
      queryFn: () => fetchTaskById(taskId),
      staleTime: Infinity
    })
  }
}
```

* Синтаксис такой же как у useQuery.
* Не вызывает ререндер.