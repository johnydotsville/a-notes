Этот вопрос я задал на тостере и сам же нашел позже ответ.

На каждом углу пишут, что, грубо говоря, "JPA - это набор интерфейсов  для работы с БД. JPA не реализует их, а только описывает. А Hibernate -  это конкретная реализация JPA".

 Это все здорово и понятно, но не понятно самое главное: как именно  реализация "подтягивается" в программу? Ведь смотрите: пусть мы хотим  использовать Hibernate. Мы указываем зависимость javax.persistence-api  (JPA), зависимость hibernate-core (реализация) и в программе повсюду  используем аннотации\классы из пакета javax.persistence - @Entity, @Id,  Persistence, EntityManager и т.д. - никаких хибернейтов нет.

 Мы создаем файл persistence.xml, в котором указываем

```
<persistence-unit name="dvdPU">
        <provider>org.hibernate.jpa.HibernatePersistenceProvider</provider>
```


 а потом пишем что-то вроде

```
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdPU");
```


 Т.е. загружаем файл, в котором и указано по сути, что мы пользуемся  хибером. Но ведь если в JPA нет никакой реализации, кто же реализует  этот метод загрузки Persistence.createEntityManagerFactory, если пока  приложение, по идее, не в курсе о том, что мы хотим использовать хибер?

 Выходит, что в JPA все-таки есть какие-то реализации? Например эта самая начальная загрузка из persistence.xml?  



В общем ответ такой:
 JPA - это не просто какая-то абстрактная спецификация, там есть вполне  конкретные классы, выполняющие конкретные действия. В частности:

```
import javax.persistence.Persistence;
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdPU");
```


 В классе Persistence метод createEntityManagerFactory выполняет вполне конкретные действия (можно посмотреть исходники тут [https://github.com/javaee/jpa-spec/blob/master/jav...](https://github.com/javaee/jpa-spec/blob/master/javax.persistence-api/src/main/java/javax/persistence/Persistence.java)), а именно: 

1. Сначала находит в classpath все возможные классы, которые могут создать EntityManagerFactory.
2. Потом перебирает все, которые нашел, по очереди, каждый раз передавая очередному "создателю" конфиг из persistence.xml.
3. Как только "создатель" создает фабрику, цикл прекращается и фабрика возвращается в нашу программу.

 Если мы не указываем в persistence.xml через тег provider конкретного  провайдера, тогда фабрику нам создаст первый попавшийся провайдер. Если  же указываем, тогда фабрику нам создаст именно тот, который мы хотим.  Пусть Persistence нашел в classpath два провайдера в таком порядке:  EclipseLink и Hibernate. Он сперва просит EclipseLink создать фабрику и  передает ему конфиг. EclipseLink  видит, что в конфиге написано  "Hibernate" и отвечает: "Извини, братишка, не в этот раз, в конфиге  написано, что это хибер должен делать. Так что я не буду". Тогда  Persistence идет дальше по списку, просит хибер и передает ему конфиг.  Хибер видит, что в provider стоит  org.hibernate.jpa.HibernatePersistenceProvider и создает фабрику.

 Вот так у нас и оказывается КОНКРЕТНАЯ, хиберовская, реализация фабрики, из которой мы уже дальше получаем такие же конкретные, хиберовские,  реализации EntityManager и прочее.

 Ну а в classpath все эти провайдеры попадают за счет того, что у нас в pom есть зависимости.      