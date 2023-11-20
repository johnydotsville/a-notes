# Нативные запросы

Если запрос не удается написать на jpql, можно отправить в БД обычный sql-код. Это называется *нативные запросы*. Их тоже можно оформлять разными способами, но мне понравилось через xml. В них уже используем полноценные названия таблиц и полей, как если бы писали запрос просто в sql-редакторе. Т.е. если у нас есть сущность ClientCurrentStatus и в БД ей соответствует таблица client_current_status, то в нативном запросе мы должны писать именно client_current_status.

На выполнение они вызываются методом manager.create**Named**Query, а не .createNativeQuery (да, именно так, это не опечатка, все так как написано)

## "Сырые" запросы

Это когда не указывается возвращаемый тип. Можно выбрать какие угодно поля, а потом руками сделать с ними что нужно:

```xml
<!-- Фильм, который не брали в аренду ни разу -->
<named-native-query name="get_no_rental_films_raw">
    <query>
        select f.film_id, f.title
        from Film f
            inner join inventory i
                on f.film_id = i.film_id
            left join rental r
                on i.inventory_id = r.inventory_id
        where r.rental_id is null
    </query>
</named-native-query>
```

```java
public void noRentalFilmsNativeQueryRaw() {
    Query qry = manager
        .createNamedQuery("get_no_rental_films_raw");
    List<Object[]> result = qry.getResultList();
    for (Object[] info : result) {
        System.out.println(info[0] + " " + info[1]);
    }
}
```

## Типизированные

Указываем тип и из результирующих полей  нужный объект формируется автоматически:

```xml
<!-- Фильм, который не брали в аренду ни разу -->
<named-native-query name="get_no_rental_films_typed" 
                    result-class="johny.dotsville.domain.entities.Film">  <!-- Указываем тип результата (пакет обязателен) -->
    <query>
        select f.*
        from Film f
            inner join inventory i
                on f.film_id = i.film_id
            left join rental r
                on i.inventory_id = r.inventory_id
        where r.rental_id is null
    </query>
</named-native-query>
```

```java
public void noRentalFilmsNativeQueryTyped() {
    Query qry = manager
            .createNamedQuery("get_no_rental_films_typed");
    List<Film> films = qry.getResultList();
    for (Film film : films) {
        System.out.println(film.getTitle());
    }
}
```

# Параметры для нативного запроса

Можно выставлять параметры. JPA поддерживает вроде только позиционные, но хибер умеет в именованные:

UPD. Нет, работают оба стиля, и именованные, и позиционные.

```xml
<!-- Все жанры, в которых актер НЕ снимался -->
<named-native-query name="get_actor_skipped_genres"
                    result-class="johny.dotsville.domain.entities.Category">
    <query>
        select cat.*
        from category cat
        where cat.category_id not in
        (
            select distinct fc.category_id
            from actor ac
                inner join film_actor fa
                    on ac.actor_id = fa.actor_id
                inner join film_category fc
                    on fa.film_id = fc.film_id
            where ac.actor_id = :actorId  <!-- Стильно, модно, молодежно -->
        )
    </query>
</named-native-query>
```

```java
public void actorSkippedGenres(long actorId) {
    Query qry = manager
            .createNamedQuery("get_actor_skipped_genres")
            .setParameter("actorId", actorId);
    List<Category> categories = qry.getResultList();
    for (Category cat : categories) {
        System.out.println(cat.getName());
    }
}
```

# Когда несколько schema

Если у нас таблицы лежат не в дефолтной схеме, тогда мы можем либо явно писать схему в запросах перед именем таблицы (например, ... from bl.client_current_status), либо переназначить дефолтную схему.

Для этого понадобится две вещи:

* В persistence.xml в разделе properties указать имя нужной схемы:

  ```xml
  <properties>
      ...
      <property name="hibernate.default_schema" value="bl"/>
      ...
  </properties>
  ```

* В запросах писать {h-schema} перед именами таблиц:

  ```xml
  select ccs.*
  from {h-schema}client_current_status ccs
  where ccs.client_id = :clientId
  ```

# Сложный мэппинг

Результат нативных запросов можно мэпить как угодно, для этого есть специальные средства. Сейчас оно мне пока не надо, но вот тут можно почитать, когда понадобится https://thorben-janssen.com/jpa-native-queries/ в разделе "Use JPA’s *@SqlResultSetMapping*"