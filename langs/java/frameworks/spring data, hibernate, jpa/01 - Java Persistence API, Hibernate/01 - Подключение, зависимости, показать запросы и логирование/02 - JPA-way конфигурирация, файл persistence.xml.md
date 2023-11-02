# JPA-way конфигурация

Чтобы конфигурировать хибер этим способом, нужно создать файл `resources\META-INF\persistence.xml`. Его структура:

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    
    <!-- Описание persistence-юнитов -->
    <persistence-unit name="dvdrental-pu">
        ...
    </persistence-unit>
    
    <persistence-unit name="some-other-pu">
        ...
    </persistence-unit>
    
</persistence>
```

# Persistence unit

Persistence-юнит это блок конфигурации, который логически состоит из нескольких компонентов (впервые упоминается на стр.21 учебника Java persistence with Hibernate - Christian Bauer, Gavin King, Gary Gregory (2016)):

* Конфигурации подключения к БД - обязательно.
* Описание мапингов классов на элементы БД - может быть оформлено в отдельном файле и подключено, либо вообще может отсутствовать, если для мапинга используются аннотации.
* Набора некоторых настроек для хибера.

В каждом приложении, работающем с БД, должен быть как минимум один PU. Если приложение работает с несколькими БД, то и PU будет несколько.

## Структура PU

Базовая структура PU:

```xml
<persistence-unit name="dvdrental-pu">
    
    <description>PU для работы с БД dvdrental</description>
    
    <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
    
    <mapping-file>named-queries/Country.orm.xml</mapping-file>
    <mapping-file>named-queries/Actor.orm.xml</mapping-file>
    
    <properties>
        <property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver" />
        <property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/dvdrental" />
        <property name="jakarta.persistence.jdbc.user" value="postgres" />
        <property name="jakarta.persistence.jdbc.password" value="j123" />

        <property name="hibernate.show_sql" value="true"/>
        <property name="hibernate.dialect" value="org.hibernate.dialect.PostgreSQLDialect"/>
    </properties>
    
</persistence-unit>
```

Описание разделов:

* `<provider>` - поскольку JPA это просто стандарт, то реализаций существует несколько. Например, Hibernate или Eclipselink. Поэтому через `<provider>` мы как раз указываем конкретную реализацию, которую хотим использовать.
* `<mapping-file>` - с помощью этого элемента мы можем подключать в конфигурацию другие файлы. Например, с запросами или содержащие мапинги.
* `<properties>` - набор настроек. Настройки бывают двух видов:
  * JPA-стандартные - все реализации их понимают. Начинаются с префикса `jakarta.persistence`. 
  * Вендорские - настройки, специфичные для конкретной реализации. Например, хиберовские настройки начинаются с префикса `hibernate`.

## Опции соединения с БД

```xml
<property name="jakarta.persistence.jdbc.driver" value="org.postgresql.Driver" />
<property name="jakarta.persistence.jdbc.url" value="jdbc:postgresql://localhost:5432/dvdrental" />
<property name="jakarta.persistence.jdbc.user" value="postgres" />
<property name="jakarta.persistence.jdbc.password" value="j123" />
```

В строчке `jdbc:postgresql://localhost:5432/dvdrental` *dvdrental* - это имя базы данных, а не сервера, контейнера или чего-то еще.

Здесь явно написаны данные для подключения к БД, но еще их можно вынести в какой-то отдельный то ли файл, то ли что, я не знаю, при этом задействуется JNDI, а я совсем не понял, что это такое.

# Создание соединения с БД

TODO Просто для примера, как с помощью PU подключиться к БД. Впоследствии скорее всего это будет перенесено в отдельный конспект. Уж про то, что такое EntityManager точно будет отдельный конспект. Но пусть пока тут полежит:

```java
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdrental-pu");
EntityManager manager = factory.createEntityManager();  // <-- Вот он, наш контекст БД
Actor actor = manager.find(Actor.class, 225);
```







# TODO

* Погуглить про эти настройки:

  ```xml
  <jta-data-source>myDS</jta-data-source>
  <class>org.jpwh.model.helloworld.Message</class>
  <exclude-unlisted-classes>true</exclude-unlisted-classes>
  <properties>
      <property
          name="javax.persistence.schema-generation.database.action"
          value="drop-and-create"/>
      <property name="hibernate.format_sql" value="true"/>
      <property name="hibernate.use_sql_comments" value="true"/>
  </properties>
  ```

  