# Hints

Хинты - это дополнительные "системные" настройки для запроса, например, таймаут, кэширование и т.д. Если провайдер не понимает какой-то хинт, он его просто игнорирует. Есть стандартные JPA-хинты, а есть уникальные хинты хибера, которых может не быть в JPA.

# Синтаксис установки хинта

## Обычный запрос

Хинт устанавливается на объекте запроса методом `.setHint`. Можно устанавливать много хинтов:

```java
TypedQuery<City> query = em.createQuery("select c from City c where c.id = :id", City.class)
    .setHint("javax.persistence.query.timeout", 60_000)  // <-- Устанавливаем первый хинт
    .setHint("some.other.hint", hint_value)  // <-- Устанавливаем второй хинт и т.д.
    .setParameter("id", 1);  
```

Если для запросов есть какие-то глобальные настройки, например, таймаут, то установка хинта эту настройку перебивает.

## Именованный запрос

В **XML** используется элемент `<hint>`, который размещается внутри элемента запроса:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<entity-mappings xmlns="http://java.sun.com/xml/ns/persistence/orm"
                 xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                 xsi:schemaLocation="http://java.sun.com/xml/ns/persistence/orm http://java.sun.com/xml/ns/persistence/orm_2_0.xsd" version="2.0">
   
    <named-query name="hinted_getCityById" >
        <query><![CDATA[
            select c from City c where c.id = ?1
        ]]></query>
        <hint name="javax.persistence.query.timeout" value="60000"/>     <!-- Устанавливаем хинты -->
        <hint name="org.hibernate.comment" value="Custom SQL comment"/>  <!-- Устанавливаем хинты -->
    </named-query>

    <!-- Аналогично работает и для <named-native-query> -->
    
</entity-mappings>
```

В **аннотации** запроса используется параметр `hints`, внутри которого хинты задаются с помощью `@QueryHint`:

```java
@Entity
@Table(name = "city")
@Getter @Setter
@NamedQueries({
        @NamedQuery(
                name = "hinted_annotation_jpql_getCityById",
                query = "select c from City c where id = :id",
                hints = {  // <-- Устанавливаем хинты
                        @QueryHint(name="javax.persistence.query.timeout", value="60000"),
                        @QueryHint(name="org.hibernate.comment", value="Custom SQL comment")
                }
        )
})
public class City extends AbstractEntity {
    ...
}
```

# JPA-хинты

| Хинт                                 | Значение                   | Описание                                              |
| ------------------------------------ | -------------------------- | ----------------------------------------------------- |
| javax.persistence.query.timeout      | число в мс                 | Максимальное время, отведенное запросу на выполнение. |
| javax.persistence.cache.retrieveMode | `"USE | BYBASS"`           |                                                       |
| javax.persistence.cache.storeMode    | `"USE | BYPASS | REFRESH"` |                                                       |



# Hibernate-хинты

| Хинт                    | Значение | Описание |
| ----------------------- | -------- | -------- |
| org.hibernate.flushMode |          |          |
| org.hibernate.readOnly  |          |          |
| org.hibernate.fetchSize |          |          |
| org.hibernate.comment   |          |          |



TODO: хинты связаны с настройками, которые надо рассматривать отдельно, потому что это самостоятельные темы. Так что пока здесь ограничусь синтаксисом. В книге это стр. 365, там есть краткие описания некоторых хинтов. Можно будет продолжить с этого места.


