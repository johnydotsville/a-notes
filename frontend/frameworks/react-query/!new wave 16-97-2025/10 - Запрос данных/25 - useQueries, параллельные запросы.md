# useQueries

По сути то же самое, что useQuery, только передается не один объект запроса, а несколько:

```jsx
export function useEmployeeBioAndFeedbacks(id) {
  const results = useQueries({
    queries: [  // <-- Передаем массив запросов
      {
        queryKey: ['employee', id],
        queryFn: async () => {
          return await fetchEmployee(id);
        }
      },
      {
        queryKey: ['feedbacks', id],
        queryFn: async () => {
          return await fetchFeedbacksByEmployeeId(id);
        }
      },
    ],
  });

  const [employeeQuery, reviewsQuery] = results;

  return {
    employeeBio: employeeQuery.data,
    employeeBioLoading: employeeQuery.isLoading,
    employeeBioError: employeeQuery.error,
    feedbacks: reviewsQuery.data,
    feedbacksLoading: reviewsQuery.isLoading,
    feedbacksError: reviewsQuery.error
  }
}
```

