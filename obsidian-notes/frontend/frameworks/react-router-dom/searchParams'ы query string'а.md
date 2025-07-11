Параметры строки запроса извлекаются через хук `useSearchParams()`:
```jsx
import { useSearchParams } from 'react-router-dom';


export function TeamPagination() {
  const [searchParams] = useSearchParams();
  const page = Number(searchParams.get('page')) || 1;
  const limit = Number(searchParams.get('limit')) || 10;

  const { team, teamLoading, teamError } = useTeam(page, limit);

  // ... остальное
}
```
