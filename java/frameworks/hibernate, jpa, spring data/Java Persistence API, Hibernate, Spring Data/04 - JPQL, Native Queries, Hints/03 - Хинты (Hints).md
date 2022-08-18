# Hints

Хинты - это особые параметры, которые можно добавить к запросу, чтобы провайдер выполнил запрос как-то по-особенному. Выглядит как тема, раскрывающаяся исключительно на практическом примере, поэтому просто напишу как добавить хинт, а зачем каждый конкретный нужен - потом разберусь:

```java
import org.hibernate.jpa.QueryHints;
import org.hibernate.CacheMode;

public void implicitHintSetting(String searchName) {
    TypedQuery<Country> tquery = manager
        .createNamedQuery("GET_COUNTRY_BY_EXACT_NAME", Country.class)
        .setHint("org.hibernate.readOnly", true)
        // .setHint("org.hibernate.cacheable", true)
        .setHint(QueryHints.HINT_CACHEABLE, true)
        // .setHint(QueryHints.HINT_CACHE_MODE, CacheMode.GET)
        .setHint("org.hibernate.cacheMode", CacheMode.GET)
        .setParameter("name", searchName);
    Country country = tquery.getSingleResult();
    System.out.println(country.getName());
}
```

Это не единственный способ задать их, но именно так делали в броадлифе, а я на них ориентируюсь.

Из интересного, в этом примере видно, что можно использовать непосредственно имя хинта, а можно пользоваться enum'ом. Но енумы объявлены как депрекейтед.

Некоторых хинтов, которые есть у хибера, может не быть в jpa.