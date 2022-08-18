# JPQL и HQL

И то, и другое - sql'еподобный язык для оформления запросов в сущностям. HQL - это хиберовская версия, а JPQL - стандартизированная. Отношения между ними такие же как между хибером и JPA - в HQL побольше функционала.

Полезные ссылки:

```
https://docs.jboss.org/hibernate/orm/5.2/userguide/html_single/chapters/query/hql/HQL.html
```

Главное помнить, что jpql-запросы создаются к *сущностям*, а не к таблицам БД, поэтому и думать надо при составлении запросов, отталкиваясь именно от сущностей.

Для создания запросов используется EntityManager.

# Неименованные запросы

Текст таких запросов пишется **непосредственно** в методе *EF.createQuery*:

```java
import jakarta.persistence.TypedQuery;

TypedQuery<Country> tquery = manager.createQuery(  // <--
    "select c from Country c " +
    "where name like :name", Country.class
);
tquery.setParameter("name", name + "%");  // <-- Задаем параметры для запроса
List<Country> countries = tquery.getResultList();  // Когда ожидаем несколько строк
```

# Именованные запросы

Такие запросы оформляются отдельно, а для вызова используется их имя.

Можно объявлять их через аннотацию *@NamedQuery*, но мне такой способ не нравится, т.к. еще больше захламляет классы. Так что я опишу только способ через xml, который посмотрел у броадлифов.

Создаем файл в resources/named-queries/Country.orm.xml (непосредственно папка с запросами может иметь любое имя, главное класть ее в ресурсы. orm в названии тоже не обязательно, но так понятнее, что файл связан с orm темой):

```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://java.sun.com/xml/ns/persistence/orm"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_2_0.xsd" version="2.0">

    <named-query name="GET_COUNTRY_BY_EXACT_NAME" >
        <query>select c from johny.dotsville.domain.entities.Country c
            where c.name = :name</query>
    </named-query>
    
    <named-query name="UPDATE_COUNTRY" >
        <query>update johny.dotsville.domain.entities.Country c
            set c.name = :newName
            where c.name = :name</query>
    </named-query>

    <named-query name="GET_COUNTRY_BY_LIKE_NAME" >
        <query>select c from johny.dotsville.domain.entities.Country c
            where c.name like :name</query>
    </named-query>

</entity-mappings>
```

И указываем этот файл в resources/META-INF/persistence.xml

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="dvdrental-pu">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>

        <mapping-file>named-queries/Country.orm.xml</mapping-file>  <!-- Туть -->

        <properties>
            <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver" />
            <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/dvdrental" />
            <property name="jakarta.persistence.jdbc.user" value="postgres" />
            <property name="jakarta.persistence.jdbc.password" value="j123" />

            <property name="hibernate.show_sql" value="true"/>
            <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
        </properties>
    </persistence-unit>
</persistence>
```

Расположение mapping-file важно - если поместить его до провайдера или после свойств, будет ошибка.

Чтобы воспользоваться таким запросом, указываем его имя в методе EF.createNamedQuery:

```java
import jakarta.persistence.TypedQuery;

TypedQuery<Country> tquery = manager
    .createNamedQuery("GET_COUNTRY_BY_EXACT_NAME", Country.class)  // <-- Имя запроса вместо текста
    .setParameter("name", searchName);
Country country = tquery.getSingleResult();  // Когда ожидаем только одну строку (иначе исключение)

// Еще есть .getResultStream();
```

# TypedQuery и Query

Когда запрос не возвращает никакого типа (например, при update), вместо *TypedQuery* можно использовать *Query*, например:

```java
public void updateNamedQuery(String name, String newName) {
    manager.getTransaction().begin();
    Query tq = manager
        .createNamedQuery("UPDATE_COUNTRY")
        .setParameter("name", name)
        .setParameter("newName", newName);
    int updated = tq.executeUpdate();
    manager.getTransaction().commit();
    System.out.println("Обновлено записей: " + updated);
}
```

```xml
...
<named-query name="UPDATE_COUNTRY" >
    <query>update johny.dotsville.domain.entities.Country c
        set c.name = :newName
        where c.name = :name</query>
</named-query>
...
```



# Выбор одного столбца

Если вдруг нужно выбрать не весь объект, а только один столбец:

```xml
select fa.id.filmId 
from johny.dotsville.domain.entities.FilmActor fa
where fa.id.actorId = :actorId
```

```java
public void getActorsFilms(long actorId) {
    TypedQuery<Long> tqry = manager
        .createNamedQuery("get_actor_genres", Long.class)
        .setParameter("actorId", actorId);
    List<Long> fa = tqry.getResultList();
    int filmsCount = 1;
    for (Long item : fa) {
        System.out.println(String.format("Фильм #%d: %d", filmsCount, item));
        filmsCount++;
    }
}
```

# inner join

Делается немного нетипично - при соединении указывается не вторая сущность, а поле первой сущности, которое на нее указывает, а on вообще не нужен (команда on есть, но используется, с виду, как where, поэтому не пишу ее сюда):

```xml
<named-query name="get_country_cities">
    <query>
        select ct City
        from johny.dotsville.domain.entities.City ct
            inner join ct.country cr  <!-- Здесь имя поля, а не класса -->
        where cr.name = :name
    </query>
</named-query>
```

```java
public class City {
    ...
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.PERSIST)
    @JoinColumn(name = "country_id", nullable = false)
    private Country country;
    ...
}
```

## Соединение нескольких сущностей

Задача "Выбрать все жанры, в которых снимался заданный актер":

```xml
<named-query name="get_actor_genres" >
    <query>
        select fc.category from johny.dotsville.domain.entities.FilmCategory fc
        inner join fc.film f
        inner join f.filmActor fa
        where fa.id.actorId = :actorId
        order by fc.category.name
    </query>
</named-query>
```

```java
public void printActorsGenres(long actorId) {
    TypedQuery<Category> tqry = manager
        .createNamedQuery("get_actor_genres", Category.class)
        .setParameter("actorId", actorId);
    List<Category> categories = tqry.getResultList();
    for (Category cat : categories) {
        System.out.println(cat.getName());
    }
}
```

Cуть такова, что мэпинг у нас уже есть - в сущностях, и там уже указано, по каким полям БД они друг с другом соединяются. Поэтому в inner join мы просто указываем *поле* одной сущности, которое содержит объект (или коллекцию объектов) второй сущности, с которой мы хотим соединиться. Фрагменты классов для наглядности:

```java
public class FilmCategory {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", insertable = false, updatable = false)
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "film_id", insertable = false, updatable = false)
    private Film film;
...
public class Film {
    @OneToMany(fetch = FetchType.LAZY, mappedBy = "film")
    private Set<FilmActor> filmActor = new HashSet<>();
...
public class FilmActor {
    @EmbeddedId
    private Id id = new Id();
    ...
    @Setter @Getter
    public static class Id implements Serializable {
        @Column(name = "actor_id")
        private long actorId;
```

Использовать JPQL для составления сложных запросов с соединениями на данных момент мне кажется извращением. Для этого куда удобнее использовать нативные запросы.