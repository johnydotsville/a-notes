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
