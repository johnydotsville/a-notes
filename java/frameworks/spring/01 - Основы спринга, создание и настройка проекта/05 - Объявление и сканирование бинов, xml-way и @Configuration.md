# DI, ApplicationContext

К этому документу я приложил три небольших демки, чтобы не копировать сюда полностью код классов. Они максимально простые, так что даже если просто ее открыть без комментариев, не запутаешься. Это больше практический документ, а более общие слова в первом документе серии.

У любого спринг-приложения есть ApplicationContext - объект, который управляет бинами. Бином является любой объект, находящийся под управлением контекста. В нашем приложении могут быть разные объекты и не все они обязательно создаются при участии спринга - какие-то мы можем создавать руками. Вот такие "ручные" объекты не являются бинами и не входят в контекст.

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

Я придумал несколько классов: Person, MarriageCertificate, MarriageService и CityService. По задумке, у нас будет городская служба, которая предоставляет людям разные услуги, одна из них - возможность пожениться. Конкретно за функционал женитьбы отвечает ЗАГС, который выдает сертификат о браке, а в нем указаны двое людей, заключивших брак.

Чистые классы:

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

Эти классы будут бинами.

# Регистрация бинов

Есть два принципиальных подхода для регистрации бинов в контексте:

* Можно явно указать бины
* Можно заставить спринг самостоятельно их искать

И каждый из этих способов можно реализовать через:

* xml-файл (демка service-spring-config-xml)
* Через класс, помеченный аннотацией @Configuration (демка service-spring-config-class)

Применим одновременно два способа: бин CityService зарегистрируем явно, а MarriageService пусть спринг ищет сам. Для этого класс MarriageService нам нам нужно отметить аннотацией @Service, чтобы спринг мог его найти:

```java
import org.springframework.stereotype.Service;

@Service
public class MarriageService {
    public MarriageCertificate marry(Person man, Person woman) {
        return new MarriageCertificate(man, woman, LocalDate.now());
    }
}
```

Поскольку CityService пользуется MarriageService'ом, нам нужно отметить, что свойство с этим объектом спринг должен заполнить сам. Для этого воспользуемся аннотацией @Autowired. Сам класс не будем аннотировать через @Service, поскольку решили, что зарегистрируем его явно:

```java
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

@Autowired может применяться к свойствам класса или конструктору. Эта аннотация означает, что спринг должен самостоятельно заполнить аннотированное поле. Тут есть некоторая особенность. Во-первых, применение @Autowired непосредственно к полю считается плохой практикой. Тому есть несколько причин, можно их загуглить если нужно. Так что лучше применять ее к конструктору, как мы и сделали. Но можно применить ее только к одному конструктору, чтобы спринг точно знал, какой именно конструктор он должен использовать при создания бина. Ну и все параметры этого конструктора тоже должны быть бинами. Если конструктор только один, применять @Autowired к нему не обязательно, спринг и сам поймет, какой конструктор использовать, раз он единственный. Но для чистоты я написал.

# Регистрация бинов через xml

Файл можно назвать как угодно, например, *beans.xml* и положить его в папку *resources* (или подпапку, главное чтобы внутри resources, чтобы он после компиляции попал в classpath):

```xml
<beans xmlns="http://www.springframework.org/schema/beans"
       xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
       xmlns:context="http://www.springframework.org/schema/context"
       xmlns:beans="http://www.springframework.org/schema/c"
       xsi:schemaLocation="http://www.springframework.org/schema/beans
           http://www.springframework.org/schema/beans/spring-beans-4.3.xsd
           http://www.springframework.org/schema/context
           http://www.springframework.org/schema/context/spring-context.xsd">
    <bean id="cityService" class="johny.dotsville.service.CityService"></bean>
    <context:component-scan base-package="johny.dotsville.service"/>
</beans>
```

Бины в xml можно указывать явно, тогда аннотировать классы, из которых они создаются, не нужно. Так мы поступили с CityService. А можно выбрать автопоиск бинов - тогда мы указываем пакет, откуда начать поиск, и спринг будет искать бины в нем и в подпакетах.

# Регистрация бинов через класс

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

Здесь сделано все то же самое, что и в xml'е. Аннотацией @Configuration мы отмечаем класс, который содержит методы по созданию бинов. Эти методы нужно отмечать аннотацией @Bean, а имя метода спринг возьмет в качестве id'шника для создаваемого бина. Здесь у нас есть метод, возвращающий CityService, это по сути и есть явное объявление бина, аналог тега bean в xml'е. Имя метода cityService, значит бин CityService мы сможем получить по id "cityService".

Аннотацией @ComponentScan мы задаем набор пакетов, в которых спринг должен искать классы бинов, которые мы не указали явно, т.е. по сути для которых мы не написали методы создания.

# Пример использования

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

# Объединение конфигов

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