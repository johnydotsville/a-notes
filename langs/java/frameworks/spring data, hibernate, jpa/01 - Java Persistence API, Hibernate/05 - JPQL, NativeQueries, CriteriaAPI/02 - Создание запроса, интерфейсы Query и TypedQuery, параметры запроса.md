# Создание запроса

Создание запроса выполняется с помощью метода  `.createQuery` EntityManager:

Простой запрос на JPQL выглядит примерно так:

```
select c from City c where c.id = :id
```

Здесь `City` - это имя класса сущности, а не таблицы. А id - это поле класса, а не столбец в таблице.

# Интерфейсы Query и TypedQuery

Метод создания запроса возвращает объект, который мы должны поместить в переменную одного из этих типов.

## Query

Это интерфейс, который не требует указания типа результата запроса. `Query` удобно использовать для запросов, которые в принципе не возвращают значений. Например, update, delete.

Если запрос возвращает результат, его придется явно привести к нужному типу:

```java
Query query = em.createQuery("select c from City c where c.id = :id")
    .setParameter("id", 1);
var city = (City) query.getSingleResult();  // <-- Приводим к типу City явно
```

## TypedQuery

Когда запрос возвращает значение, удобнее использовать интерфейс `TypedQuery`, который позволяет указать тип результата, чтобы хибер автоматически сделал приведение:

```java
TypedQuery<City> query = em.createQuery("select c from City c where c.id = :id", City.class)  // <--
    .setParameter("id", 1);
var city = query.getSingleResult();  // <-- Больше не нужно приводить результат вручную
```

# Параметры запроса

## Простой параметр

Для предотвращения sql-инъекций нужно пользоваться синтаксисом параметров. Имя параметра указывается в запросе через двоеточие `:`,  а значение параметру задается методом `.setParameter`

```java
TypedQuery<City> query = em.createQuery("select c from City c where c.id = :id", City.class)  // <-- :id
    .setParameter("id", 1);  // <-- Устанавливаем значение параметра
var city = query.getSingleResult();
```

## Параметр-дата

Если параметр представляет собой дату, нужно указывать это явно:

```java
TypedQuery<Payment> query = em.createQuery("select p from Payment p where p.paymentDate < :paydate", Payment.class)
    .setParameter("paydate", new Date(2007, 2, 20), TemporalType.TIMESTAMP);  // <-- TemporalType.TIMESTAMP
var payment = query.getResultList();
```

Для работы с датами используется тип `java.util.Date`. Типы LocalDateTime и похожие использоваться не могут, поэтому и поле в сущности нужно объявлять именно типа Date:

```java
@Column(name = "payment_date")
private Date paymentDate;
```

## Параметр-объект

В качестве параметра можно ставить сложный объект. Например, найдем все города, находящиеся в какой-нибудь стране:

```java
// Сначала получим комплексный объект - Страну
TypedQuery<Country> qCountry = em.createQuery("select c from Country c where c.id = :id", Country.class)
    .setParameter("id", 10);
var country = qCountry.getSingleResult();

// Теперь установим этот объект в качестве параметра
TypedQuery<City> qCity = em.createQuery("select c from City c where c.country = :country", City.class)
    .setParameter("country", country);  // <-- Параметр является сложным объектом
var cities = qCity.getResultList();
```

Под капотом все работает через id, т.е. условие преобразуется в нечто вроде `c.country.id = :id`.

## Позиционные параметры

Позиционные параметры имеют синтаксис `?n`, где n - это любое число, начиная с 1:

```java
TypedQuery<City> query = em.createQuery("select c from City c where c.id = ?1", City.class)
    .setParameter(1, 15);
var city = query.getSingleResult();
```

Причем номера параметров должны быть по порядку. Например, если параметр единственный, нельзя написать `?5` - будет ошибка. Или если параметров два, то написать `?1` и `?4`, должно быть `?1` и `?2`.