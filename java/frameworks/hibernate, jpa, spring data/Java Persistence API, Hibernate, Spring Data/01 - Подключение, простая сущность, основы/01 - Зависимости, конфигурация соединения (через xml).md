# Зависимости

Нужна зависимость jpa, hibernate и провайдера драйвера:

```xml
<dependency>
    <groupId>jakarta.persistence</groupId>
    <artifactId>jakarta.persistence-api</artifactId>
    <version>3.1.0</version>
</dependency>
```

```xml
<dependency>
    <groupId>org.hibernate.orm</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>6.1.0.Final</version>
</dependency>
```

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.3.6</version>
</dependency>
```

# Два стиля конфигурирования

Хибер можно использовать как самостоятельную технологию, а можно как реализацию JPA. При этом как минимум отличается способ конфигурирования.

## Hibernate-стиль

Конфигурация кладется в папку resources/META-INF/hibernate.cfg.xml

Конкретно этот стиль я пока не использовал, не буду про него ничего писать.

## JPA-стиль

Конфигурация кладется в папку resources/META-INF/persistence.xml

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="dvdrental-pu">
        <description>JPA s ru4koi</description>
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
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

Собственно все понятно и так, это минимальная конфигурация.

Здесь явно написаны данные для подключения к БД, но еще их можно вынести в какой-то отдельный то ли файл, то ли что, я не знаю, при этом задействуется JNDI, а я совсем не понял, что это такое.

Кроме того, в некоторых примерах тут же написаны классы, которые участвуют в хранении, и была настройка, чтобы убрать автоматическое сканирование классов. Но у меня явное перечисление классов вызвало только проблемы. Я удалил весь этот треш и начало хоть как-то работать.

*Persistense Unit* - трудно сказать, *что* это такое. С виду это раздел в файле persistence.xml. Имеет ли он еще какие-то инкарнации, я не знаю. Но понятно, *для чего* он нужен - информация из него используется для создания EntityManager. Самое смешное, что, блять, что такое EntityManager, ты узнаешь только сильно впереди.