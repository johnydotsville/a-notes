[Официальная документация](https://tanstack.com/query/v5/docs/framework/react/reference/useQuery)

# Синтаксис хука

`useQuery` - базовый хук для выбора данных.

```javascript
const { data, isLoading, error } = useQuery({
  queryKey: ["products", { page, limit }],
  queryFn: async () => {
    return await fetchProducts(page, limit);
  }
});
```

Параметры:

- `queryKey` - ключ запроса.
- `queryFn` - функция выборки данных.

Хук возвращает объект, в полях которого все что нам надо (названия полей могут меняться, см. документацию):

- `data` - Загруженные данные.
- `error` - Ошибка загрузки.
- `refetch` - Функция для вызова повторной загрузки.
- Флаги:
  - `isFetching`, `isPending`, `isLoading` и прочие.

# Примеры

```jsx
import { useQuery } from "@tanstack/react-query";

export function useProductsPagination(page = 1, limit = 10) {
  const { data, isLoading, error } = useQuery({
    queryKey: ["products", { page, limit }],
    queryFn: async () => {
      return await fetchProducts(page, limit);
    }
  });

  return {
    products: data || [],
    isProductsLoading: isLoading,
    productsError: error
  }
}
```