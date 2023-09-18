# Table

## Удаление таблицы

```sql
drop table if exists feature cascade;
```

## Создание таблицы

TODO: вписать модификаторы, чтобы создание столбца было описано более обособленно и было видно, что при этом доступно.

```sql
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

