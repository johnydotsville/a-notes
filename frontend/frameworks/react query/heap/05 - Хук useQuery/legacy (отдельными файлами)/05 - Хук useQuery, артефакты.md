

# Хук useQuery

```javascript
const { data, isLoading, error } = useQuery({
  queryKey: ["posts"],    // <-- Идентификатор запроса.
  queryFn: async () => {  // <-- Функция загрузки данных.
    const result = await fetch(`${baseUrl}/tasks`)
    return result.json();
  }
});
```

В одном компоненте можно использовать хук useQuery много раз, если компоненту нужно много разных данных.

# Артефакты

* data - когда загрузка завершена, в объект data попадают полученные данные. Пока не загружены, data = undefined.
* isLoading - становится true, когда RQ запускает функцию загрузки данных.
* error - в случае ошибки при загрузке данных в error попадает объект ошибки.