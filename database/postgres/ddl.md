# Database

```sql
create database ubay with template = template0 
    encoding = 'UTF8' 
    lc_collate = 'English_United States.1252' 
    lc_ctype = 'English_United States.1252';

alter database ubay owner to postgres;

set statement_timeout = 0;
set lock_timeout = 0;
set idle_in_transaction_session_timeout = 0;
set client_encoding = 'UTF8';
set standard_conforming_strings = on;
select pg_catalog.set_config('search_path', '', false);
set check_function_bodies = false;
set client_min_messages = warning;
set row_security = off;
```



# Схема

Схема это способ логически сгруппировать элементы (таблицы и прочее). Например, вспомогательные таблицы вроде авторизации - в одной схеме, а таблицы бизнес-логики - в другой схеме.

```sql
drop schema if exists bl cascade;
create schema bl;
```

Теперь можно указать конкретную схему при создании всех остальных компонентов БД, например:

```sql
drop sequence if exists bl.feature_id_seq cascade;  --- bl.имяКомпонента

create sequence bl.feature_id_seq
    start with 1 
    increment by 1 
    no minvalue 
    no maxvalue 
    cache 1;
```



# Sequence

```sql
drop sequence if exists feature_id_seq cascade;

create sequence feature_id_seq
    start with 1 
    increment by 1 
    no minvalue 
    no maxvalue 
    cache 1;
```

# Table

```sql
drop table if exists feature cascade;

create table feature (
    id integer default nextval('bl.feature_id_seq'::regclass),
	name character varying(100) not null unique,
	last_update timestamp without time zone default now() not null,
	mark_deleted boolean not null default 'false',
	---
	primary key(id)
    --- primary key(f1, f2) в случае составного pk
    --- unique(f1, f2) когда именно комбинация столбцов дб уникальной, при этом если они не pk
);

alter table feature owner to postgres;

-- Отдельное создание PK
alter table only feature
    add constraint feature_pk primary key (id);
```

## Foreign Key

Связующая таблица с двумя внешними ключами:

```sql
alter table only goods_feature
    add constraint goods_feature_to_goods_fk foreign key (goods_id) references bl.goods(id) 
    on delete restrict;
alter table only goods_feature
    add constraint goods_feature_to_feature_fk foreign key (feature_id) references bl.feature(id) 
    on delete restrict;
```

FK сам по себе не применяет на поле not null, надо делать это самому

# Trigger

```sql
drop trigger if exists last_updated on feature cascade;

create trigger last_updated
    before update on feature --Таблица, на которую вешаем триггер
	for each row execute procedure last_updated();
```



# Function

```sql
drop function if exists last_updated cascade;

create function last_updated() returns trigger
    language plpgsql
    as $$
begin
    new.last_update = current_timestamp;
    return new;
end $$;
```

