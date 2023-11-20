# Схема

## Создание схемы

Схема это способ логически сгруппировать элементы (таблицы и прочее). Например, вспомогательные таблицы вроде авторизации - в одной схеме, а таблицы бизнес-логики - в другой схеме.

```sql
drop schema if exists bl cascade;
create schema bl;
--- Создаем схему с именем bl (business logic)
```

## Использование схемы

Теперь можно указать конкретную схему при создании всех остальных компонентов БД, например:

```sql
drop sequence if exists bl.feature_id_seq cascade;  --- схема.компонент
```

```sql
--- Создаем последовательность с именем feature_id_seq внутри схемы bl
create sequence bl.feature_id_seq
    start with 1 
    increment by 1 
    no minvalue 
    no maxvalue 
    cache 1;
```

