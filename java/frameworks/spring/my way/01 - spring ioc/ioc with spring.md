К этому документу я приложил небольшую демку, чтобы не копировать сюда полностью код классов. Она максимально простая, так что даже если просто ее открыть без комментариев, не запутаешься.

У любого спринг-приложения есть ApplicationContext - объект, который управляет бинами. Бином является любой объект, находящийся под управлением контекста. В нашем приложении могут быть разные объекты и не все они обязательно создаются при участии спринга - какие-то мы можем создавать руками. Вот такие "ручные" объекты не являются бинами и не входят в контекст.

DI контейнер является основой спринга и все спринг-технологии пользуются именно им. Поэтому его ценность не просто в собственно di-функционале, а именно в интеграции с другими спринг-технологиями. Так что заменить его на какой-то другой third-party контейнер скорее всего невозможно.

Для пользования контейнером нужна следующая зависимость:

```xml
<dependency>
    <groupId>org.springframework</groupId>
    <artifactId>spring-context</artifactId>
    <version>5.3.21</version>
</dependency>
```

Она автоматически подтягивает другую зависимость, *spring-core*.

# Пример

Я придумал несколько классов: Person, MarriageCertificate, MarriageService и CityService. По задумке, у нас будет городская служба, которая предоставляет людям разные услуги, одна из них - возможность пожениться. Конкретно за функционал женитьбы отвечает ЗАГС, который выдает сертификат о браке, а в нем указаны двое людей, заключивших брак.

Здесь бинами являются CityService и MarriageService, причем второй является зависимостью первого. Остальные классы мы создаем вручную.

```java
@Service
public class MarriageService {
    public MarriageCertificate marry(Person man, Person woman) {
        return new MarriageCertificate(man, woman, LocalDate.now());
    }
}
```

```java
@Service
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

Использование:

```java
public class App
{
    public static void main( String[] args )
    {
        ApplicationContext appcontext = new ClassPathXmlApplicationContext("beans.xml");
        CityService cityService = (CityService) appcontext.getBean(CityService.class);

        Person harry = new Person(
                new Name("Гарри", "Поттер"),
                LocalDate.of(1980, 7, 31));
        Person ginny = new Person(
                new Name("Джинни", "Уизли"),
                LocalDate.of(1981, 8, 11));

        MarriageCertificate cert = cityService.marry(harry, ginny);

        System.out.println(cert);
    }
}
// Данный документ свидетельствует о том, что гражданин Гарри Поттер
// и гражданка Джинни Уизли официально зарегистрировали свой брак 2022-07-01
```

Чтобы класс распознался как бин, нужно применить к нему одну из следующих аннотаций:

* @Component
* @Service
* @Repository

Честно говоря, сейчас не могу точно написать чем они отличаются, потому что нужен конкретный хороший пример, чтобы это понять. Могу только сказать, что @Component это самый общий вид бина, а @Service и @Respository тоже являются внутри компонентом. Лучшее понимание появится скорее всего на следующем витке спирали изучения.

@Autowired может применяться к свойствам класса или конструктору. Эта аннотация означает, что спринг должен самостоятельно заполнить это поле. Тут есть некоторая особенность. Во-первых, применение @Autowired непосредственно к полю считается плохой практикой. Тому есть несколько причин, можно их загуглить если нужно. Так что лучше применять ее к конструктору. Но можно применить ее только к одному конструктору, чтобы спринг точно знал, какой именно конструктор он должен использовать для создания бина. Ну и все параметры этого конструктора тоже должны быть бинами, потому что иначе спринг просто не будет знать, чем заполнить этот параметр.

# Регистрация бинов через xml

Файл можно назвать как угодно, например, *beans.xml* и положить его в папку *resources*:

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
    <context:component-scan base-package="johny.dotsville"/>
</beans>
```

Чтобы создать контекст, мы передаем этот файл через параметр:

```java
ApplicationContext appcontext = new ClassPathXmlApplicationContext("beans.xml");
```

Бины в xml можно указывать явно, тогда аннотировать классы не нужно. Так мы поступили с CityService. А можно выбрать автопоиск бинов - тогда мы указываем пакет, откуда начать поиск, и спринг будет искать бины в нем и в подпакетах. Так что если убрать аннотацию @Service с класса MarriageService, спринг все равно его найдет.

# Регистрация бинов через класс

Честно говоря, хотя и хвалят, но выглядит как по мне так себе. Много опций разных с этими аннотациями и вообще не понятно из куцых примеров зачем что нужно. Так что оставлю тут этот огрызок просто чтоб было и пойду дальше. Пока буду пользоваться xml'ем.

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ServiceConfiguration {
    @Bean
    public MarriageService marriageService() {
        return new MarriageService();
    }
    @Bean
    public CityService cityService(MarriageService marriageService) {
        return new CityService(marriageService);
    }
}
```



```java
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.register(ServiceConfiguration.class);
ctx.refresh();
CityService cityService = (CityService) ctx.getBean(CityService.class);
```

