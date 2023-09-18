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

