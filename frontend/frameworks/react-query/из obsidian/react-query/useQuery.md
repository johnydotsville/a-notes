#useQuery

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

## Флаги
Флаги гибко отражают возможные состояния и их комбинации. Некоторые состояния:
- Данные есть в кэше \ Данных нет в кэше.
- Данные загружаются в данный момент \ Данные не загружаются в данный момент.

Первая пачка флагов:
- `isPending`
	- true - данных нет в кэше.
	- false - данные есть в кэше. Свежие \ устаревшие - не важно.
- `isFetching`
	- true - сейчас выполняется запрос выборки данных.
	- false - сейчас не выполняется запрос выборки данных
- `isLoading` - isPending && isFetching
	- true - это самый первый запрос выборки данных. Т.е. комбинация фактов "данных в кэше нет" + "идет загрузка данных"

Если задуматься, это удобно для формирования адекватного интерфейса:
- Например, данных нет + идет загрузка > отображаем вместо данных спиннер.
- Если данные есть + идет фоновое обновление > ничего не трогаем или отображаем какую-нибудь полоску сбоку. В этом случае глупо было бы отображать спиннер вместо данных, ведь данные уже есть. Может быть старые, но они есть.


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