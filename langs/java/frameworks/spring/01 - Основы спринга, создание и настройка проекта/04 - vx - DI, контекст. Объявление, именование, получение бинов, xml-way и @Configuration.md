# Вопросы

- [ ] Что представляет собой "контекст" в терминах спринга?
  - [ ] Что такое интерфейс ApplicationContext и его субинтерфейсы ConfigurableApplicationContext, ConfigurableWebApplicationContext, WebApplicationContext?
  - [ ] В чем отличие ClassPathXmlApplicationContext, AnnotationConfigApplicationContext, реализующих интерфейс ConfigurableApplicationContext?
- [ ] Что такое бин в спринге? Обязательно ли он должен входить в контекст или может существовать сам по себе?
- [ ] Как можно заставить спринг искать бины? Как это связано с аннотациями @Service, @Component, @Repository?
  - [ ] Для чего нужны классы, помеченные аннотацией @Configuration?
    - [ ] Зачем помечать методы аннотацией @Bean и какую роль играет имя такого метода?
  - [ ] Зачем нужна аннотация @ComponentScan? Что означают пакеты в ее параметре и что будет, если их не указать?
    - [ ] Можно ли использовать @ComponentScan отдельно от @Configuration?
  - [ ] Как с помощью аннотации @Import объединить несколько @Configuration-классов?
  - [ ] Если мы хотим объявить бины в xml-файле, то обязательно ли мы должны положить этот файл в папку *resources* (или ее подпапки) или возможны варианты? Есть ли у этого файла строго определенное имя?
- [ ] Что такое имя (id) бина?
  - [ ] Может ли в контексте быть два бина с одинаковым именем? Почему?
  - [ ] Можем ли мы задать бину произвольное имя, когда используем аннотации @Component, @Service, @Bean и т.д.? Если можем, то что будет, если мы этого не сделаем и какие тогда имена будут у бинов в каждом из трех случаев?
- [ ] Почему в контексте допускается наличие двух и более бинов одинакового типа?
  - [ ] В какой момент это становится проблемой для спринга? И как мы можем ему помочь аннотациями @Qualifier и @Primary
- [ ] Зачем нужна аннотация @Autowired? К чему ее можно применить?
  - [ ] Можно ли применять ее к конструктору? А к нескольким конструкторам? Почему?
  - [ ] Какие способы внедрения лучше подходят для случаев, когда зависимость является обязательной \ опциональной?
  - [ ] Почему все параметры autowired-конструктора должны быть бинами? Скомпилируется \ запустится ли программа, если один из параметров не будет бином?
  - [ ] Как применение @Autowired к полям усложняет проектирование иммутабельных классов? Как это связано с final-полями?
  - [ ] Почему в целом игнорирование конструкторов ухудшает контроль над зависимостями класса и привязывает нас к DI-контейнеру?
  - [ ] Можно \ нужно ли комбинировать разные стили внедрения зависимостей в пределах одного класса? А в разных классах?

# DI, ApplicationContext

К этому документу я приложил три небольших демки, чтобы не копировать сюда полностью код классов. Они максимально простые, так что даже если просто ее открыть без комментариев, не запутаешься.

В основе любого спринг-приложения лежит объект, управляющий бинами. Он называется "контекст" и реализует интерфейс *[ApplicationContext](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html)* (точнее, один из его [субинтерфейсов](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html) ConfigurableApplicationContext, ConfigurableWebApplicationContext, WebApplicationContext). Бином является любой объект, находящийся под управлением контекста. В нашем приложении могут быть разные объекты и не все они обязательно создаются при участии спринга - какие-то мы можем создавать руками. Вот такие "ручные" объекты не являются бинами и не входят в контекст.

DI контейнер является основой спринга и все спринг-технологии пользуются именно им. Поэтому его ценность не просто в собственно di-функционале, а именно в том, что он интергирует другие спринг-технологии друг с другом. Так что заменить его на какой-то другой third-party контейнер скорее всего невозможно.

Для пользования контейнером нужна следующая зависимость:

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.21</version>
</dependency>
```

Она автоматически подтягивает другую зависимость, *spring-core*.

# Описание примера

Допустим, есть городская служба, предоставляющая людям разные услуги ("сервисы"), среди них - возможность пожениться. Конкретно за функционал женитьбы отвечает ЗАГС, который выдает сертификат о браке, а в нем указаны двое людей, заключивших брак.

Реализуем задачу через классы Person, MarriageCertificate, MarriageService и CityService:

```java
public class MarriageService {
    public MarriageCertificate marry(Person man, Person woman) {
        return new MarriageCertificate(man, woman, LocalDate.now());
    }
}
```

```java
public class CityService {
    private final MarriageService marriageService;

    public CityService(MarriageService marriageService) {
        this.marriageService = marriageService;
    }

    public MarriageCertificate marry(Person man, Person woman) {
        return marriageService.marry(man, woman);
    }
}
```

Классы MarriageService и CityService будут бинами. Спринг создаст их при запуске приложения и соберет вместе как положено. Остальные классы, Person и MarriageCertificate, - это обычные программные объекты, которые мы создаем через new по мере необходимости.

# Регистрация бинов

## Явная и через автопоиск

Есть два принципиальных подхода для регистрации бинов в контексте:

* Можно явно указать бины
* Можно заставить спринг самостоятельно их искать

И каждый из этих способов можно реализовать через:

* xml-файл (демка service-spring-config-xml)
* Через класс, помеченный аннотацией @Configuration (демка service-spring-config-class)

Применим одновременно два способа: бин CityService зарегистрируем явно, а MarriageService пусть спринг ищет сам. Для этого класс MarriageService нам нам нужно отметить аннотацией @Service, чтобы спринг мог его найти:

```java
import org.springframework.stereotype.Service;

@Service  // <-- Благодаря аннотациям спринг сам найдет класс через автопоиск
public class MarriageService {
    public MarriageCertificate marry(Person man, Person woman) {
        return new MarriageCertificate(man, woman, LocalDate.now());
    }
}
```

## via xml

Xml-файл с описанием бинов выглядит примерно так:

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:beans="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context.xsd">
    <bean id="cityService" class="johny.dotsville.service.CityService"></bean>  // Явно
    <context:component-scan base-package="johny.dotsville.service"/>  // Автопоиск
</beans>
```

Бины в xml можно указывать явно, тогда аннотировать классы, из которых они создаются, не нужно. Так мы поступили с CityService.

А можно выбрать автопоиск бинов - тогда мы указываем пакет, откуда начать поиск, и спринг будет искать их в нем и в подпакетах. В этом случае нужно аннотировать бины через @Component, @Serive и т.д.

Конфигурационный файл можно назвать как угодно, например, *beans.xml* и положить его в папку *resources* (или подпапку, главное чтобы внутри resources, чтобы он после компиляции попал в classpath). Тогда использовать этот файл для создания бинов можно будет так:

```java
ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
```



## via @Configuration-класс

```java
@Configuration
@ComponentScan(basePackages = { "johny.dotsville.service" })
public class ServiceConfig {
    @Bean
    public CityService cityService(MarriageService marriageService) {
        return new CityService(marriageService);
    }
}
```

Аннотацией `@Configuration` мы отмечаем класс, который содержит методы по созданию бинов - это "явная" регистрация, т.к. мы сами создаем через new бин из нужного класса. Эти методы отмечаем аннотацией *@Bean*, а имя метода спринг возьмет в качестве id'шника для бина. Здесь имя метода cityService, значит бин CityService мы сможем получить по id "cityService".

Аннотацией `@ComponentScan` мы задаем набор пакетов, в которых спринг должен искать классы бинов, которые мы не указали явно (для которых не написали @Bean-методы). Если не указать в параметрах пакеты, то поиск будет идти в пакете, где располагается текущий класс, и в его подпакетах. Аннотация идет в паре с @Configuration, иначе не сработает.

Создать контекст с помощью @Configuration-класса можно так:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(ServiceConfig.class);
```

### Объединение конфигов

Можно использовать несколько классов-конфигураций для удобства, а потом объединить их в едином классе с помощью аннотации @Import. Сделаем все то же самое - CityService объявим явно, а MarriageService спринг будет искать сам, но каждую конфигурацию оформим в отдельном классе:

```java
@Configuration
public class ServiceConfigPart1 {
    @Bean
    public CityService cityService(MarriageService marriageService) {
        return new CityService(marriageService);
    }
}
```

```java
@Configuration
@ComponentScan(basePackages = { "johny.dotsville.service" })
public class ServiceConfigPart2 {

}
```

Теперь объединим их:

```java
@Configuration
@Import({
        ServiceConfigPart1.class,
        ServiceConfigPart2.class })
public class ServiceConfig {

}
```

Можно было бы в ServiceConfig дописать и другие методы, будь у нас еще сервисы, но поскольку их у нас только два, используем этот класс исключительно для объединения.

# @Autowired и стратегии внедрения зависимостей

Поскольку CityService пользуется MarriageService'ом, нам нужно отметить, что свойство с этим объектом спринг должен заполнить сам. Для этого воспользуемся аннотацией `@Autowired`. Сам класс не будем аннотировать через @Service, поскольку решили, что зарегистрируем его явно:

```java
// Нет аннотации, так что спринг не найдет этот класс сам. Мы зарегим его явно потом
public class CityService {
    private final MarriageService marriageService;

    @Autowired
    public CityService(MarriageService marriageService) {
        this.marriageService = marriageService;
    }

    public MarriageCertificate marry(Person man, Person woman) {
        return marriageService.marry(man, woman);
    }
}
```

@Autowired может применяться к свойствам класса, методам или конструктору. Эта аннотация означает, что спринг должен самостоятельно заполнить аннотированное поле.

### @Autowired и конструкторы

Часто является основным способом в программах, не являющихся фреймворками, когда состав объектов весьма жесток и не подразумевает возможностей по широкой кастомизации.

Этот способ гарантирует, что все зависимости будут установлены, потому что нельзя не передать в конструктор параметр, который он требует. Через конструктор нужно внедрять *обязательные* зависимости класса. При этом все зависимости должны быть бинами. Если кто-то не будет бином, программа скомпилируется, но не запустится - спринг на старте сообщит, что-то вроде "Parameter of constructor in TopService required a bean of type 'SubServiceB' that could not be found."

Применить ее можно только к одному конструктору, потому что если применить к нескольким, спринг не поймет, какой ему использовать.

### @Autowired и сеттеры

Часто используется в фреймворках или программах, подразумевающих возможности по широкой кастомизации поведения. :question: TODO: Каким образом - похоже вопрос широкий, и следует вернуться к нему позже, а пока есть более актуальные вещи.

### @Autowired и поля

Применение @Autowired непосредственно к полю считается плохой практикой. Тому есть несколько причин:

1. Основная, с которой идут все остальные: отпадает необходимость объявлять конструкторы. Это ведет к тому, что зависимости класса "размываются", становится труднее понимать, от чего он зависит, потому что обычно зависимости передаются именно через конструктор для наглядности. Становится очень легко напичкать класс десятками зависимостей и это будет плохо заметно - а конструктор с десятью параметрами быстро вызвал бы подозрение.
2. Если мы игнорируем конструкторы, то не сможем создавать иммутабельные объекты: не получится объявить в классе final-поле, потому что оно требует заполнения либо в конструкторе, либо тут же при объявлении. Но поскольку спринг создает бины автоматически уже после запуска программы, то до компиляции поле будет пустым, а значит программа даже не скомпилируется.
3. Получаем сильную привязку к DI-контейнеру (касается не только спринга, а вообще). Если конструкторы мы игнорируем, то заполнять поля без DI-контейнера сможем только через рефлексию, а это не удобно.

### Выводы

* Внедрение через конструктор подходит в большинстве случаев
* Внедрение через свойства - лучше вообще не использовать
* Внедрение через сеттеры - если понимаешь, зачем тебе это нужно
* Смешивать способы технически возможно, но логически смысла в этом мало

# Именование бинов и их уникальность

Каждый бин попадает в контекст с определенным id ("именем"). В контексте могут быть несколько бинов одного типа, но с разными id. Уникальность бина определяется именно именем, а не типом.

## Явное задание имени

В примере с явным указанием бина xml мы сами задали id бину:

```xml
<bean id="cityService" class="johny.dotsville.service.CityService"></bean>
```

Через аннотации, маркирующие бины (вроде @Component, @Service, @Bean и т.д.), мы тоже можем явно задавать имена:

```java
@Service("pipes")
public class PipeService {
    public String whoAmI() {
        return PipeService.class.toString();
    }
}
```

```java
@Configuration
public class Config {
    @Bean("pipes")
    public PipeService getPipeService() {
        return new PipeService();
    }
}
```

```java
PipeService pipeService = (PipeService) context.getBean("pipes");
```

## Стратегии автоименования

Если имена не задавать явно, то они сгенерируются автоматически. В случае с классом, будет взято имя класса, с *маленькой первой буквой*:

```java
@Service
public class PipeService {  // <-- id бина будет "pipeService"
```

В случае с методом, будет взято имя метода *как есть*:

```java
@Configuration
public class Config {
    @Bean
    public HiddenService hiddenService() {  // <-- id будет hiddenSerice
        return new HiddenService();
    }
    
    @Bean
    public HiddenService ExposedService() {  // <-- id будет ExposedService, с Большой буквы
        return new ...
    }
    
    @Bean
    public HiddenService getHiddenService() {  // <-- id будет getHiddenService
        return new ...
    }
}
```

# Бины одного типа

Для спринга не проблема, когда в контексте есть несколько бинов одного типа, потому что уникальность бина определяется именем, а не типом. Проблемой это становится, когда ему нужно при автопривязке выбрать конкретный бин, потому что если у нас три бина одного типа, то теоретически подходит любой. Так что мы должны ему дать подсказку, что и где выбирать в таком случае.

Пусть у нас сервис для работы со временем и две реализации:

```java
@Service
public class Watchmaker {
    private final TimeService localTs;
    private final TimeService greenwichTs;

    @Autowired
    public Watchmaker(TimeService localTs, TimeService greenwichTs) {  // <-- Неоднозначность
        this.localTs = localTs;
        this.greenwichTs = greenwichTs;
    }
    
    public void useLocalTimeService() {
        System.out.println(localTs.whoAmI());
    }

    public void useGreenwichTimeService() {
        System.out.println(greenwichTs.whoAmI());
    }
}
```

```java
public interface TimeService {
    String whoAmI();
}
...
@Service
public class GreenwichTimeService implements TimeService {
    @Override
    public String whoAmI() {
        return GreenwichTimeService.class.toString();
    }
}
...
@Service
public class LocalTimeService implements TimeService {
    @Override
    public String whoAmI() {
        return LocalTimeService.class.toString();
    }
}
```

Помогите Даше найти нужный бин.

## @Qualifier

Помечаем этой аннотацией всех кандидатов одного типа:

```java
@Service("lts")  // <-- Если хотим указать имя бина явно, то можем сделать это тут
@Qualifier
public class LocalTimeService implements TimeService {
...
@Service
@Qualifier("gts")  // <-- Или тут
public class GreenwichTimeService implements TimeService {
```

А в местах автопривязки указываем, какой именно кандидат нас интересует:

```java
@Autowired
public Watchmaker(@Qualifier("localTimeService") TimeService localTs,
                  @Qualifier("greenwichTimeService") TimeService greenwichTs) {
    this.localTs = localTs;
    this.greenwichTs = greenwichTs;
}
```

## @Primary

Если бы у нас была немного другая ситуация, когда требуются не все реализации, а только одна:

```java
@Service
public class Watchmaker {
    private final TimeService timeService;

    @Autowired
    public Watchmaker(TimeService timeService) {  // <-- Нам нужны не все, а одна
        this.timeService = timeService;
    }

    public void useTimeService() {
        System.out.println(timeService.whoAmI());
    }
}
```

Тогда мы могли бы снабдить одного из кандидатов аннотацией `@Primary` и спринг выбрал бы его:

```java
@Service
@Primary  // <-- Предпочитаемый кандидат
public class GreenwichTimeService implements TimeService {
    @Override
    public String whoAmI() {
        return GreenwichTimeService.class.toString();
    }
}
```

# Создание и работа с контекстом

Ну и наконец сформируем контекст из настроек и получим бин CityService:

```java
public static void main( String[] args )
{
    ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
    // или ApplicationContext context = new AnnotationConfigApplicationContext(ServiceConfig.class);
    CityService cityService = (CityService) context.getBean("cityService");

    Person harry = new Person(
            new Name("Гарри", "Поттер"),
            LocalDate.of(1980, 7, 31));
    Person ginny = new Person(
            new Name("Джинни", "Уизли"),
            LocalDate.of(1981, 8, 11));

    MarriageCertificate cert = cityService.marry(harry, ginny);

    System.out.println(cert);
}
```

В зависимости от того, какой способ конфигурирования мы выбрали (xml или классы), используем разные классы для создания контекста. А после создания работа с ним уже выглядит одинаково. По id'шнику запрашиваем бин, приводим его к нужному типу и работаем дальше в обычном режиме.

Конечно в таком примере использование контекста не оправдано, зато наглядно видно как что работает. В реальных приложениях обычно мы просто конфигурируем бины, а контекст не создаем - его спринг создает сам, регистрирует бины, сам их создает, использует и т.д.

