# Описание

Просто отобразить данные. Никаких требований особых нет. Нет пагинации, нет никаких настроек по автоматическому перезапросу и т.д.

# Пример

React-компонент:

```react
export const TaskListSimple = () => {
  const {
    data,
    isPending,
    isFetching,
    error
  } = useQuery({
    queryKey: ["tasks"],
    queryFn: () => fetchTasks({ limit: 4 })
  });

  if (isPending || isFetching) return <div>ЗАГРУЗКА ДАННЫХ...</div>
  if (error) return <div>ОШИБКА: { error.message }</div>

  return (
    <TaskList tasks={data.tasks} />
  )
}
```

Функция загрузки данных:

```javascript
const baseUrl = "http://localhost:3001";

// TODO: Потом мб на дженерики переделать
export async function fetchTasks(
  { fid, currentPage, gotoPage, limit }: Partial<PaginationParams> = { }
): Promise<FetchTasksResult> {
  console.log("Запрос на сервер с пагинацией.");
    console.log(`${fid}, ${currentPage}, ${gotoPage}, ${limit}`);

  let queryString = '';
  const params = new URLSearchParams();
  if (fid) params.append("_fid", fid.toString());
  if (currentPage) params.append("_cp", currentPage.toString());
  if (gotoPage) params.append("_gp", gotoPage.toString());
  if (limit) params.append("_limit", limit.toString());
  if (params.size > 0) {
    queryString = `?${params}`
  }

  try {
    const response = await fetch(`${baseUrl}/tasks${queryString}`);
    // const tasksTotalCount = response.headers.get('X-Total-Count');
    const tasksTotalCount = 345;  // TODO: Какой-то баг с получением заголовка, потом разобраться и поправить.
    const tasks = await response.json();
    return { 
      tasks, 
      meta: { tasksTotalCount } 
    };
  } catch (err) {
    console.log(err);
    throw err;
  }
}
```


