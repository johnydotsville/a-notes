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

