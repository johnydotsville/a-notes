Думаю можно разделить все материалы на две категории: архитектура и практика. В архитектуре можно перечислить основные компоненты, как они связаны и разные околотеоретические вещи. А в практике - что с помощью этих компонентов можно непосредственно сделать. Попробуем-с.

# Hibernate и JPA

Сначала появился Hibernate (в 2001 году) и использованные в нем идеи всем так понравились, что на его основе создали спецификацию JPA (в 2006 году). Изначально между ними были некоторые различия - в хибере было больше функциональности в целом, да и по ходу приготовления спецификации вероятно придумали какие-то вещи (но это не точно), которых еще не было в хибере. Одним словом, они отличались. Последние версии хибера, насколько я понимаю, полностью JPA-совместимы, т.е. полностью реализуют JPA. Но в хибере все еще есть и дополнительный функционал, который в JPA не описан.

Сам JPA является одной из множества других спецификаций, вместе образующих JavaEE. Но JPA это не просто "бумажка с описанием" что и как должно быть на словах, но еще и библиотека.

Библиотека JPA определяет интерфейсы для выполнения объекто-реляционного мэпинга и *управления хранимыми объектами* (операции по сохранению, извлечению, обновлению и т.д.). Но кроме абстрактных интерфейсов, реализацию которых должны уже предоставлять конкретные библиотеки, вроде хибера, в ней все же есть и конкретный функционал. В основном это код для поиска этих самых конкретных библиотек в системе и загрузки их в память. Одним словом, чтобы использовать конкретную реализацию, ее нужно сначала найти и запустить. Этим и занимаются те немногие *конкретные* классы из JPA-библиотеки.

Библиотека JPA представлена вот этой зависимостью:

```xml
<dependency>
    <groupId>javax.persistence</groupId>
    <artifactId>javax.persistence-api</artifactId>
    <version>2.2</version>
</dependency>
```

Она автоматически подтягивается в проект, когда мы подключаем конкретную реализацию, например, хибер:

```xml
<dependency>
    <groupId>org.hibernate</groupId>
    <artifactId>hibernate-core</artifactId>
    <version>5.1.11</version>
</dependency>
```

# Persistence, EntityManagerFactory и EntityManager

С этих трех ребят все начинается. О каждом расскажу вкратце, просто чтобы понять как они все связаны, а более детально - в отдельных темах.

*EntityManager* - это "контекст", т.е. главный объект, который позволяет нам выполнять сохранение, обновление, извлечение, удаление объектов из БД, а также умеет запускать транзакции, принимать и откатывать их. В общем, это основной объект, открывающий нам доступ к работе с БД.

Для создания EntityManager используется фабрика *EntityManagerFactory*. У нее для этого есть метод *.createEntityManager*

А EntityManagerFactory, в свою очередь, создается с помощью класса *Persistence* и его метода *.createEntityManagerFactory*

Итого:

```
Persistence -> EntityManagerFactory -> EntityManager -> ??? (работа с БД) -> PROFIT
```

Теперь поподробнее о том, как это работает. За основу возьмем такой фрагмент кода и разберемся, что происходит:

```java
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.EntityManager;
...
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdrentalPU");
EntityManager manager = factory.createEntityManager();
Actor actor = manager.find(Actor.class, 225);
```

В библиотеке JPA есть класс Persistence и интерфейсы EntityManagerFactory и EntityManager. В библиотеке конкретной реализации (например, в Hibernate, EclipseLink и т.д.) есть классы, которые эти интерфейсы реализуют.

> Честно говоря, я реализацию этих интерфейсов в исходниках хибера не нашел. Там везде используются именно интерфейсы и даже в EntityManagerFactoryBuilder в методе построения фабрики в качестве возвращаемого типа указан интерфейс EntityManagerFactory, а не конкретный хиберовский класс. Возможно, они называются там не EntityManagerFactory и EntityManager, а как-то по-другому, или заныканы в какие-нибудь конфигурационные файлы со странным форматом, но в любом случае реализации должны быть. Оставлю эти поиски на потом, т.к. на практическое пользование это не влияет.

Так что когда мы вызываем на классе Persistence метод createEntityManagerFactory, происходит поиск *конкретной* реализации класса EntityManagerFactory и создается конкретная фабрика. Какую именно реализацию нужно использовать, мы указываем в специальном файле *persistence.xml* (о нем будет [дальше](#persistence.xml (и hibernate.cfg.xml))), а первый параметр метода Persistence.createEntityManagerFactory позволяет в этом файле найти нужную настройку. Но об этом опять же, дальше, в разделе [persistence.xml](#persistence.xml)

# Persistence.createEntityManagerFactory()

Разберем метод создания фабрики, находящийся в JPA-классе Persistence (полный исходный код класса - [тут](https://github.com/javaee/jpa-spec/blob/master/javax.persistence-api/src/main/java/javax/persistence/Persistence.java).):

```java
package javax.persistence;

import javax.persistence.spi.PersistenceProvider;  // <-- SPI (Service Provider Interface)
import javax.persistence.spi.PersistenceProviderResolver;
...
public static EntityManagerFactory createEntityManagerFactory(String persistenceUnitName, Map properties) {
        EntityManagerFactory emf = null;
        PersistenceProviderResolver resolver = PersistenceProviderResolverHolder.getPersistenceProviderResolver();

        List<PersistenceProvider> providers = resolver.getPersistenceProviders();

        for (PersistenceProvider provider : providers) {
            emf = provider.createEntityManagerFactory(persistenceUnitName, properties);
            if (emf != null) {
                break;
            }
        }
        if (emf == null) {
            throw new PersistenceException("No Persistence provider for EntityManager named " + persistenceUnitName);
        }
        return emf;
    }
```

Еще раз отметим, что класс Persistence находится в пакете javax.persistence, т.е. относится к библиотеке JPA, а не конкретному вендору.

Во-первых, видим, что здесь используется технология SPI, чтобы искать PersistenceProvider'ов. PersistenceProvider'ы - это конкретные классы от вендоров, которые умеют создавать конкретные фабрики. Вот пример хиберовского провайдера ([исходники](https://github.com/hibernate/hibernate-orm/blob/main/hibernate-core/src/main/java/org/hibernate/jpa/HibernatePersistenceProvider.java)):

```java
package org.hibernate.jpa;
...
public class HibernatePersistenceProvider implements PersistenceProvider {
    ...
```

> SPI - Service Provider Interface, я писал о ней в конспекте про JDBC, но вкратце и тут напишу. Если посмотреть в локальном репозитории мавена .m2\repository\org\hibernate\orm\hibernate-core\6.1.0.Final архив hibernate-core-6.1.0.Final.jar, то в нем можно обнаружить папку *META-INF\services*, а в ней файл с названием jakarta.persistence.spi.PersistenceProvider, а внутри - строчку с именем класса org.hibernate.jpa.HibernatePersistenceProvider.
>
> Это стандартное SPI соглашение - имя файла это название интерфейса, а строчка внутри - это название класса, который этот интерфейс реализует. И все это лежит в фиксированной директории, так что классу Persistence не составляет труда найти все доступные в classpath'е реализации интерфейса PersistenceProvider.

Т.о., Persistence из javax.persistence в своем методе createEntityManagerFactory просто ищет все конкретные реализации в classpath'е и последовательно просит их создать фабрику. За счет того, что он им при этом передает настройки, в которых мы задаем, чья именно реализация нам нужна, каждая конкретная реализация может из этих настроек понять, должна ли именно она создавать фабрику, или уступить эту честь другой реализации. Например, если мы хотим реализацию от хибера, а в списке найденных провайдеров сначала идет EclipseLink, то он поймет, что мы хотим хибер, и не станет создавать фабрику. Цикл пойдет дальше и прервется как только фабрика будет создана или не закончатся доступные провайдеры.

# persistence.xml (и hibernate.cfg.xml)

persistence.xml является стандартным файлом для конфигурации в JPA. Нужно класть его в папку *resources\META-INF*.

В нем описывается т.н. "единица хранения" (persistence unit). В PU мы помещаем всю информацию, нужную для создания EntityManager'а, в том числе разные дополнительные вещи, вроде какие именно классы участвуют в хранении, возможно даже правила мапинга (альтернатива аннотациям) и может быть еще что-то другое.

Но я пока использовал этот файл только для хранения настроек соединения с БД, пары настроек самого хибера, да настройку, что как раз хибер и нужно использовать для создания фабрики.

```xml
<persistence xmlns="https://jakarta.ee/xml/ns/persistence"
             xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
             xsi:schemaLocation="https://jakarta.ee/xml/ns/persistence
             https://jakarta.ee/xml/ns/persistence/persistence_3_0.xsd"
             version="3.0">
    <persistence-unit name="dvdrentalpunit">
        <description>JPA experiment</description>
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

Конфигурация с помощью файла hibernate.cfg.xml - это нативный способ конфигурирования хибера. Он тоже помещается в *resources\META-INF*.

# Session vs EntityManger

Session - это тоже объект-контекст. Он является нативным в Hibernate, т.е. до появления JPA именно он использовался для сохранения, удаления, обновления объектов в БД, управления транзакциями и т.д. Можно сказать, что это "EntityManager на максималках" - в нем больше функциональности, но часть постепенно становится deprecated, а лучшие идеи переносятся в JPA и, соответственно, становятся доступны через EntityManager.

EntityManager является оберткой над Session, так что по сути мы все равно работаем с Session, хоть и через фасад EntityManager'а.

При желании воспользоваться уникальными возможностями Session, можно извлечь ее из EM'а методом `.unwrap(Session.class)`:

```java
import org.hibernate.Session;
import jakarta.persistence.Persistence;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
...
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdrentalpunit");
manager = factory.createEntityManager();
...
private void unwrapSessionTest() {
    Session session = manager.unwrap(Session.class);  // <--
    Actor actor = new Actor();
    actor.setName(new Name("Becky", "Thatcher"));
    session.getTransaction().begin();
    session.persist(actor);
    session.getTransaction().commit();
}
```



