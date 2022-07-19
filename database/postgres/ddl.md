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





# Sequence

```sql
drop sequence if exists bl.feature_id_seq cascade;

create sequence bl.feature_id_seq
    start with 1 
    increment by 1 
    no minvalue 
    no maxvalue 
    cache 1;
```

# Table

```sql
drop table if exists bl.feature cascade;

create table bl.feature (
    id integer default nextval('bl.feature_id_seq'::regclass),
	name character varying(100) not null unique,
	last_update timestamp without time zone default now() not null,
	mark_deleted boolean not null default 'false',
	---
	primary key(id)
);

alter table bl.feature owner to postgres;

-- Отдельное создание PK
alter table only bl.feature
    add constraint feature_pk primary key (id);
```

## Foreign Key

Связующая таблица с двумя внешними ключами:

```sql
alter table only bl.goods_feature
    add constraint goods_feature_to_goods_fk foreign key (goods_id) references bl.goods(id) 
    on delete restrict;
alter table only bl.goods_feature
    add constraint goods_feature_to_feature_fk foreign key (feature_id) references bl.feature(id) 
    on delete restrict;
```



# Trigger

```sql
drop trigger if exists last_updated on bl.feature cascade;

create trigger last_updated
    before update on bl.feature 
	for each row execute procedure bl.last_updated();
```



# Function

```sql
drop function if exists bl.last_updated cascade;
create function bl.last_updated() returns trigger
    language plpgsql
    as $$
begin
    new.last_update = current_timestamp;
    return new;
end $$;
```

