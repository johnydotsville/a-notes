# Триггер

```sql
drop trigger if exists last_updated on feature cascade;

create trigger last_updated
    before update on feature --Таблица, на которую вешаем триггер
	for each row execute procedure last_updated();
```

