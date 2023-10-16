# Жадная и ленивая выборка

Через параметр *fetch* в аннотациях @ManyToOne и @OneToMany (ну и наверное @OneToOne, но я его не использовал) можно задать тип выборки связанных данных - жадную или ленивую.

```java
fetch = FetchType.LAZY | EAGER
```

Вероятно, в разных случаях по умолчанию используются разные типы, поэтому лучше указывать явно, чтобы было очевидно. При жадной во время выбора города сразу же будет запрос в БД и на его страну. При ленивой, запрос на страну пойдет только когда будет реальное обращение к полю country объекта.

```java
class City {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "country_id")
    private Country country;
}
...
С жадной:
select c1_0.city_id, c2_0.country_id, c2_0.last_update, c2_0.country, c1_0.last_update, c1_0.city 
from city c1_0
    left join country c2_0
        on c2_0.country_id=c1_0.country_id
where c1_0.city_id=?

С ленивой:
select c1_0.city_id, c1_0.country_id, c1_0.last_update, c1_0.city 
from city c1_0
where c1_0.city_id=?
```

А вот в случае со страной, список городов кажется по умолчанию ленивый:

```java
Если не указывать тип выборки:
select c1_0.country_id, c1_0.last_update, c1_0.country 
from country c1_0
where c1_0.country_id=?

Если указать EAGER:
select c1_0.country_id, c2_0.country_id, c2_0.city_id, c2_0.last_update, c2_0.city, c1_0.last_update, c1_0.country 
from country c1_0 
    left join city c2_0 
        on c1_0.country_id=c2_0.country_id 
where c1_0.country_id=?

class Country {
    @OneToMany(mappedBy = "country", fetch = FetchType.EAGER)
    private Set<City> cities = new HashSet<>();
}
```

А если указать жадную в обоих классах, получается два запроса, но я их писать сюда не буду, потому что это уже какой-то треш.

# Пара примеров

## LAZY

```java
private void getCityWithCountry(long cityId) {
    System.out.println("Пытаемся выбрать город");
    City city = manager.find(City.class, cityId);
    System.out.println(String.format("Город %s", city.getName()));
}
```

```
Пытаемся выбрать город
Hibernate: select c1_0.city_id,c1_0.country_id,c1_0.city from city c1_0 where c1_0.city_id=?
Город Ziguinchor
```

Поскольку мы не обращались к стране, она и не выбралась - это видно по единственному запросу хибера.

А теперь обратимся к стране:

```java
private void getCityWithCountry(long cityId) {
    System.out.println("Пытаемся выбрать город");
    City city = manager.find(City.class, cityId);
    System.out.println(String.format("Город %s", city.getName()));
    System.out.println("А теперь обратимся к стране");
    System.out.println(String.format("Страна %s", city.getCountry().getName()));  // <-- Туть
}
```

```
Пытаемся выбрать город
Hibernate: select c1_0.city_id,c1_0.country_id,c1_0.city from city c1_0 where c1_0.city_id=?
Город Ziguinchor
А теперь обратимся к стране
Hibernate: select c1_0.country_id,c1_0.country from country c1_0 where c1_0.country_id=?
Страна Senegal
```

Видно, что хибер сделал второй дополнительный запрос, чтобы довыбрать страну, только когда мы уже обратились к ней.

## EAGER

```java
private void getCityWithCountry(long cityId) {
    System.out.println("Пытаемся выбрать город");
    City city = manager.find(City.class, cityId);
    System.out.println(String.format("Город %s", city.getName()));
}
```

```
Пытаемся выбрать город
Hibernate: select c1_0.city_id,c2_0.country_id,c2_0.country,c1_0.city from city c1_0 join country c2_0 on c2_0.country_id=c1_0.country_id where c1_0.city_id=?
Город Ziguinchor
```

Видно, что хибер сгенерировал запрос с соединением сразу же, независимо от того, будем ли мы реально обращаться к полю со страной или нет.