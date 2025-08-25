# Как оформить
Основные шаги:
- Получить queryClient через хук `useQueryClient`.
- Вызвать на клиенте метод `prefetchQuery`. Синтаксис такой же как у useQuery.
- Задача префетча - просто запустить запрос, а не извлекать данные. Извлекать их будет useQuery.

Вот пример, где  префетч оформлен отдельной функцией:
```typescript
import { useQueryClient } from "@tanstack/react-query";


export function usePrefetchEmployee() {
  const client = useQueryClient();  // <-- Получаем клиент
  
  return (id) => {  // <-- Возвращаем функцию
    client.prefetchQuery({  // <-- Запускаем запрос
      queryKey: ['team', id],
      queryFn: async () => {
        return await fetchEmployee(id);
      }
    });
  }
}
```
Вот компонент, где используется префетч:
```jsx
import { usePrefetchEmployee } from '../hooks/usePrefetchEmployee';


export function EmployeeCard({ employee }) {
  const prefetchEmployee = usePrefetchEmployee();

  const gotoEmployeePage = (id) => {
    // Можно и тут было бы префетчить
    navigate(`/team/${id}`);
  }

  return (
    // ... остальное
  <Button 
	onClick={() => gotoEmployeePage(employee.id)} 
	onMouseEnter={() => prefetchEmployee(employee.id)}  // <-- Префетчим
  >Подробнее</Button>
  )
}
```
На что обратить внимание:
- Правило всех хуков - вызывать только на верхнем уровне компонента.
	- Поэтому мы не запускали префетч в функции usePrefetchEmployee - мы из нее лишь вернули функцию, которая его запускает. Т.о. useQueryClient у нас оказался на верхнем уровне компонента EmployeeCard.

# Где запускать
В зависимости от нужд, префетч можно запускать в разных местах:
- При наведении на элемент.
- При щелчке на элемент.