# select

```sql
select c from City c
```

City - это имя класса, который замаплен на некоторую таблицу БД.

## projection

Когда мы выбираем не все столбцы, а только некоторые, это называется projection. При проекции хибер не отслеживает изменения в возвращаемом объекте.

Результат надо возвращать либо:

* В объект, структура которого подходит под проекцию

  ```java
  TypedQuery<Name> query = em.createQuery(
  	"select new johny.dotsville.domain.other.Name(a.firstName, a.lastName) " +
      "from Actor a where id = 5", Name.class);
  Name result = query.getSingleResult();  // <-- Результат будет типа Name
  ```

  ```java
  @Getter @Setter
  public class Name {
  
      private String first;
      private String last;
  
      public Name(String first, String last) {
          this.first = first;
          this.last = last;
      }
  
  }
  ```

* В Object или Tuple:

  ```java
  TypedQuery<Tuple> query = em.createQuery(
      "select a.firstName, a.lastName from Actor a where id = 5", Tuple.class);  // <-- Projection
  var result = query.getSingleResult();
  System.out.println(String.format("first: %s, last: %s", result.get(0), result.get(1)));
  ```


## Функции в projection

В проекции можно применять функции. Например, выберем имена актеров в формате `Имя, Фамилия`:

```java
Query query = em.createQuery("select concat(concat(a.firstName, ','), a.lastName) from Actor a");
var result = (List<String>)query.getResultList();
```

### case when

```java
TypedQuery<Tuple> query = em.createQuery(
    "select a.firstName, a.lastName, " +
        "case when a.firstName = 'Nick' then 'фрик' " +
             "else 'нет прозвища' " +
        "end " +
    "from Actor a", Tuple.class);
var result = query.getResultList();
```



# where

В where мы указываем поля класса, а не поля таблицы.

## Типы данных

Типы данных имеют следующий синтаксис в запросах:

```sql
-- Числа
select c from City c where id = 100
```

```sql
-- Строки
select c from City c where name = 'Abu Dhabi'
```

```sql
-- Boolean
select e from Employee e where deactivated = true
```

```sql
-- Даты
select p from Payment p where paymentDate < {d '2007-02-16'}
```

TODO: перечисления, стр 375

## Типичные условия

Это условия, похожие на обычный sql.

### between

```sql
select c from City c where id between 100 and 150
```

### in

```sql
select c from City c where id in (1, 10, 100)
select e from Employee e where name in ('Tom', 'Huck', 'Jim')
```

### is [not] null

```sql
select e from Employee e where status is null
select e from Employee e where status is not null
```

### [not] like

```sql
select e from Employee e where name like 'To%'
select e from Employee e where name not like '_om'
```

Знаки подстановки:

* `%` - означает любые символы в любом количестве. `%om` - это **T**om, **Fr**om, **Bott**om.
* `_` - означает *один* любой символ. `_om` - это **T**om, **P**om, но не **Fr**om, **Bott**om.

### and, or

```sql
select p from Payment p where amount < 8 and paymentDate < {d '2007-02-16'}
select p from Payment p where amount < 8 or  paymentDate < {d '2007-02-16'}
```

## Условия на коллекции

Можно задавать условия на поля, которые являются коллекциями. Например, есть класс Страна, и у него поле cities замаплено так, что представляет собой коллекцию городов:

```java
@OneToMany(mappedBy = "country")
private Set<City> cities = new HashSet<>();
```

### is [not] empty

```sql
select c from Country c where c.cities is empty
select c from Country c where c.cities is not empty
```

Первый запрос выберет страны, с которыми не связано ни одного города, а второй - с которыми связан хотя бы один город. Такие запросы трансформируются в sql-exists:

```sql
select c1_0.country_id,c1_0.last_update,c1_0.country 
from country c1_0 
where not exists(
    select 1 from city c2_0 
    where c1_0.country_id=c2_0.country_id)
```

### size

```sql
select c from Country c where size(c.cities) > 15
```

Запрос выберет страны, с которыми связано больше 15 городов.

```sql
select c1_0.country_id,c1_0.last_update,c1_0.country
from country c1_0 
where (
    select count(1) from city c2_0 
    where c1_0.country_id=c2_0.country_id) > 15
```

### member of

```sql
select c from Country c where :city member of c.cities
```

Выберен страну, у которой среди городов есть город, заданный параметром city. Полный код программы:

```java
TypedQuery<City> qCity = em.createQuery("select c from City c where id = 225", City.class);
var city = qCity.getSingleResult();

TypedQuery<Country> qCountry = em.createQuery(
    "select c from Country c where :city member of c.cities", Country.class)
    .setParameter("city", city);
var country = qCountry.getResultList();
```

## Функции в условиях

К полям в условиях можно применять функции.

### lower

```sql
select c from City c where lower(c.name) = 'aden'
```

Поиск города по имени независимо от регистра.

### Доступные функции

P.S. Список функций не полный.

| Функция                           | Описание                                                     |
| --------------------------------- | ------------------------------------------------------------ |
| `upper`, `lower`                  | Привести к верхнему / нижнему регистру.                      |
| `concat(s1, s2)`                  | Возвращает объединенную строку.                              |
| `substring(s, from, length)`      | Возвращает подстроку с позиции from длиной length.           |
| `length(s)`                       | Возвращает длину строки.                                     |
| `trim([both|leading|trailing] s)` | Удаляет пробельные символы в строке s. По умолчанию работает с both. |
| `locate(what, s, from)`           | Ищет в строке `s` текст `what` и если нашло, возвращает позицию вхождения. |

Пара примеров:

```sql
select c from Country c where trim(both c.name) = 'Тилимилитрямдия'
select c from Country c where locate('tion', c.name, 0) > 0
```

# order

## order by [asc|desc]

```sql
select c from Country c order by c.name desc
select c from Country c order by c.name desc, c.foobar asc
```

# distinct

```sql
select distinct c.name from City c
```

