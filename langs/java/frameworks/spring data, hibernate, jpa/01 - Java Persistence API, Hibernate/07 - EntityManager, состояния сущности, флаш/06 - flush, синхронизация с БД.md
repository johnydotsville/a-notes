# Флаш

## Что такое флаш

Под синхронизацией с БД подразумевается слив данных из памяти в БД. Я буду дальше называть это *флаш*, по названию метода `.flush()`.

P.S. При проверке примеров нужно учитывать, какая используется СУБД. В постгре нет режима грязного чтения, поэтому если смотреть через pgAdmin на содержимое таблиц, то там записи появляются только после полного завершения транзакции. Но при этом запросы в примерах возвращают все записи, даже те, которые pgAdmin еще не видит.

TODO: написать про ручную настройку квери спейса для нативных запросов?

TODO: написать про ситуацию, когда одна таблица ссылается на другую. Будет ли флаш, если кс пересекается только с одной из них?

TODO: прочитать эту статью https://thorben-janssen.com/hibernate-query-spaces/

## Режимы флаша

Есть несколько режимов флаша. Некоторые являются JPA-стандартизированными, а некоторые есть только в хибере:

* JPA, Hibernate
  * AUTO
  * COMMIT
* Hibernate
  * ALWAYS
  * MANUAL

Они отличаются моментами, в которых хибер выполняет флаш. Для подавляющего большинства рабочих ситуаций походит дефолтный режим AUTO. Если по коду идут какие-то махинации с переустановкой режимов, это говорит о том, что скорее всего эти люди используют хибер неправильно.

## Синтаксис установки режима

Есть два перечисления с режимами `jakarta.persistence.FlushModeType` и `org.hibernate.FlushMode`.

* Установка на весь контекст:

  * JPA-синтаксис:

    ```java
    em.setFlushMode(FlushModeType.COMMIT);
    ```

  * Хибер-синтаксис:

    ```java
    Session ses = em.unwrap(Session.class);
    ses.setHibernateFlushMode(FlushMode.ALWAYS);
    // Далее по коду можно продолжать пользоваться em, не обязательно делать все через ses.
    ```

* Локальная установка для конкретного запроса:

  * JPA-синтаксис:

    ```java
    List<Country> allCountries = em.createQuery("select c from Country c", Country.class)
        .setFlushMode(FlushModeType.COMMIT)  // <-- Задаем режим флаша.
        .getResultList();
    ```

  * Хибер-синтаксис:

    ```java
    List<Country> allCountries = em.createQuery("select c from Country c", Country.class)
        .unwrap(org.hibernate.query.Query.class)  // <-- Достаем хиберовский Query.
        .setHibernateFlushMode(FlushMode.AUTO)  // <-- Задаем режим флаша.
        .getResultList();
    ```

* Глобальная установка через persistence.xml:

  ```xml
  <persistence-unit name="dvdrental-pu">
      ...
      <properties>
          ...
          <property name="org.hibernate.flushMode" value="ALWAYS" />
      </properties>
  
  </persistence-unit>
  ```

# Механика работы режимов

Во всех режимах есть общий момент выполнения флаша - при коммите транзакции. Остальные моменты отличаются.

## AUTO

`AUTO` является режимом по умолчанию. При нем флаш происходит в двух ситуациях:

* При коммите транзакции.

* Перед выполнением запроса через интерфейс `Query`, если квери спейс запроса пересекается с грязными сущностями контекста.

  При поиске через метод .find() флаш не происходит. Хибер сначала ищет в памяти, но даже если в памяти сущности нет и хибер полезет искать в БД, то флаш он перед этим все равно не сделает.

### Query space и грязный контекст

Что означает фраза "если квери спейс запроса пересекается с грязными сущностями контекста"?

*Квери спейс* запроса - это, грубо говоря, таблицы, которые используются в запросе. Подробнее можно почитать в [этой статье](https://thorben-janssen.com/hibernate-query-spaces/). *Грязные сущности контекста* - это сущности в памяти, которые хибер отслеживает и которые либо были изменены, либо являются новыми, но еще не сохраненными (т.е. persist() на них выполнен, но флаша еще не было).

Соответственно, фраза означает, что в памяти есть грязная сущность и в запросе используется таблица, с которой эта сущность связана.

### Примеры

* Квери спейс пересекается с грязной сущностью контекста:

```java
var country = new Country();  // <-- Создали новую сущность.
country.setName("Белоруссия");

em.setFlushMode(FlushModeType.AUTO);

EntityTransaction tx = em.getTransaction();
tx.begin();

em.persist(country);  // <-- Теперь country - "грязная сущность".

// <-- Запрос использует таблицу country, и в контексте есть грязная сущность country,
//     значит квери спейс запроса пересекается с грязными сущностями контекста.
// <-- Поэтому перед выполнением этого запроса будет флаш: [1], [2]
List<Country> allCountries = em.createQuery("select c from Country c", Country.class).getResultList();

tx.commit();
```

```sql
[1] insert into otmb.country (name, cnt_id) values (?, ?)
[2] select c1_0.cnt_id,c1_0.name from otmb.country c1_0
```

* Квери спейс не пересекается с грязными сущностями контекста:

```java
var lang = new Lang();  // <-- Создали новую сущность.
lang.setName("Китайский");

em.setFlushMode(FlushModeType.AUTO);

EntityTransaction tx = em.getTransaction();
tx.begin();

em.persist(lang);  // <-- Теперь lang - "грязная сущность".

// <-- Запрос использует таблицу country, а в контексте у нас грязная сущность lang.
//     Значит кс запроса не пересекается с грязными сущностями контекста.
// <-- Поэтому здесь будет просто select [1]
List<Country> allCountries = em.createQuery("select c from Country c", Country.class).getResultList();

// <-- А флаш выполнится только здесь [2]
tx.commit();
```

```sql
[1] select c1_0.cnt_id,c1_0.name from otmb.country c1_0
[2] insert into otmb.language (name, lang_id) values (?, ?)
```

* Грязь может быть разная - не только новые сущности, но и измененные:

```java
var country = em.find(Country.class, 46);  // <-- select [1]
country.setName("Беларусь");  // <-- "Загрязняем" сущность.

em.setFlushMode(FlushModeType.AUTO);

EntityTransaction tx = em.getTransaction();
tx.begin();

em.persist(country);

// <-- Флаш, поэтому сначала update [2], а потом select [3]
var c = em.createQuery("select c from Country c where c.id = :id", Country.class)
    .setParameter("id", country.getId())
    .getSingleResult();

tx.commit();
```

```sql
[1] select c1_0.cnt_id,c1_0.name from otmb.country c1_0 where c1_0.cnt_id=?
[2] update otmb.country set name=? where cnt_id=?
[3] select c1_0.cnt_id,c1_0.name from otmb.country c1_0 where c1_0.cnt_id=?
```

* Флашу подвергается вся грязь, а не только та, которая пересекается с квери спейсом:

```java
var country = new Country();  // <-- Создаем две новые сущности. Первая
country.setName("Белоруссия");

var lang = new Lang();  // <-- и вторая.
lang.setName("Китайский");

em.setFlushMode(FlushModeType.AUTO);

EntityTransaction tx = em.getTransaction();
tx.begin();

em.persist(country);
em.persist(lang);

// <-- Перед выполенением запросов хибер сделает флаш, insert [1] и [2].
//     Т.е. в БД разом отправится и страна, и язык.
// <-- Поэтому в этом списке будет Белоруссия,
List<Country> allCountries = em.createQuery("select c from Country c", Country.class).getResultList();  // [3]
// <-- а в этом - китайский язык.
List<Lang> allLangs = em.createQuery("select l from Lang l", Lang.class).getResultList();  // [4]

tx.commit();
```

```sqlite
[1] insert into otmb.country (name, cnt_id) values (?, ?)
[2] insert into otmb.language (name, lang_id) values (?, ?)
[3] select c1_0.cnt_id,c1_0.name from otmb.country c1_0
[4] select l1_0.lang_id,l1_0.name from otmb.language l1_0
```



## COMMIT

В этом режиме флаш происходит только при коммите транзакции.

```java
var country = new Country();
country.setName("Белоруссия");

var lang = new Lang();
lang.setName("Китайский");

em.setFlushMode(FlushModeType.COMMIT);  // <-- Задаем режим флаша

EntityTransaction tx = em.getTransaction();
tx.begin();

em.persist(country);
em.persist(lang);

// <-- В этом списке не будет Белоруссии,
List<Country> allCountries = em.createQuery("select c from Country c", Country.class)
    .getResultList();
// <-- а здесь не будет китайского языка,
List<Lang> allLangs = em.createQuery("select l from Lang l", Lang.class)
    .getResultList();

tx.commit();  // <-- потому что флаш произойдет только сейчас.
```

```sql
select c1_0.cnt_id,c1_0.name from otmb.country c1_0
select l1_0.lang_id,l1_0.name from otmb.language l1_0
insert into otmb.country (name, cnt_id) values (?, ?)
insert into otmb.language (name, lang_id) values (?, ?)
```

## ALWAYS

В этом режиме флаш происходит перед любым запросом через интерфейс Query. Не важно, пересекается его кс с контекстом или нет:

```java
var lang = new Lang();
lang.setName("Вьетнамский");

Session ses = em.unwrap(Session.class);

ses.setHibernateFlushMode(FlushMode.ALWAYS);

EntityTransaction tx = ses.getTransaction();
tx.begin();

ses.persist(lang);
List<Country> allCountries = ses.createQuery("select c from Country c", Country.class)
    .getResultList();  // <-- Флаш [1] + [2], хотя кс с контекстом не пересекается.
allCountries.forEach(c -> System.out.println(c.getName()));

tx.commit();
```

```sql
[1] insert into otmb.language (name, lang_id) values (?, ?)
[2] select c1_0.cnt_id,c1_0.name from otmb.country c1_0
```

## MANUAL

В этом режиме флаш происходит только когда мы явно вызываем метод `em.flush()`. Автофлаша нет вообще, даже при завершении транзакции.

# Нативные запросы и флаш

В интернетах пишут, что хибер не может автоматически определить квери спейс для нативных запросов. Фактически это означает, что если мы сохраним новый язык, а потом выполним запрос `em.createNativeQuery("select l.lang_id, l.name from otmb.language l", Lang.class);`, то хибер не сделает перед ним флаш, потому что не поймет, что квери спейс этого запроса пересекается с контекстом.

На практике же у меня нативные запросы работали точно так же как обычные, хибер делал флаш. Не знаю почему. Так что на всякий случай привожу пример, как вручную указать хиберу, с какими сущностями связан нативный запрос, чтобы он мог понять, делать флаш или нет. Сам синтаксис нативных запросов - в отдельном конспекте:

```java
import org.hibernate.query.SynchronizeableQuery;  // <-- Важно импортировать правильный тип

var query = em.createNativeQuery("select l.lang_id, l.name from otmb.language l", Lang.class);

// <-- Извлекаем из JPA-шного запроса хиберовскую реализацию и задаем ей связанные классы.
SynchronizeableQuery sq = query.unwrap(SynchronizeableQuery.class);
sq.addSynchronizedEntityClass(Lang.class);

// <-- Теперь хибер должен суметь понять, нужен ли флаш перед этим запросом.
var allLangs = (List<Lang>)query.getResultList();
```



