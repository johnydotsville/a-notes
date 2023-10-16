# Способы регистрации бинов

Есть два принципиальных подхода для регистрации бинов в контексте:

* Указать бины явно.
* Заставить спринг искать бины самостоятельно.

И каждый из этих способов можно реализовать через:

* xml-файл (демка service-spring-config-xml)
* Через класс, помеченный аннотацией `@Configuration` (демка service-spring-config-class)

## via xml

Конфигурационный файл можно назвать как угодно, например, *beans.xml* и положить его в папку *resources* (или подпапку, главное что внутри resources, чтобы он после компиляции попал в classpath). Тогда использовать этот файл для создания бинов можно будет так:

```java
ApplicationContext context = new ClassPathXmlApplicationContext("spring-beans.xml");
```

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

Если бины в xml описаны явно (в данном случае это бин cityService), тогда не нужно аннотировать классы, из которых они создаются.

Если мы хотим, чтобы спринг сам нашел классы, из которых он должен создать бины, то указываем базовый пакет, с которого надо начать поиск, а нужные классы аннотируем через `@Component`, `@Service` и т.д.

## via @Configuration-класс

В @Configuration-классе мы указываем:

* Пакет, с которого надо начать автоматический поиск бинов.
* Методы, создающие бины явно.

Создать контекст с помощью @Configuration-класса можно так:

```java
ApplicationContext context = new AnnotationConfigApplicationContext(ServiceConfig.class);
```

1. Бин *CityService* зарегистрируем явно:

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

   Аннотацией `@Configuration` мы отмечаем класс, который содержит методы по созданию бинов - это явная регистрация, т.к. мы сами создаем через new бин из нужного класса. Эти методы отмечаем аннотацией `@Bean`, а имя метода спринг возьмет в качестве id'шника для бина. Здесь имя метода cityService, значит бин CityService мы сможем получить по id "cityService".

   Аннотацией `@ComponentScan` мы задаем набор пакетов, в которых спринг должен искать классы бинов, которые мы не указали явно (для которых не написали @Bean-методы). Если не указать в параметрах пакеты, то поиск будет идти в пакете, где располагается текущий класс, и в его подпакетах. Аннотация идет в паре с @Configuration, иначе не сработает.

2. Бин *MarriageService* пусть спринг ищет сам. Для этого класс MarriageService аннотируем как `@Service` (строго говоря, можно использовать не только @Service, но и например @Component, но об этом в конспектах дальше):

   ```java
   import org.springframework.stereotype.Service;
   
   @Service  // <-- Благодаря аннотации спринг сам найдет класс через автопоиск.
   // P.S. Если это не Spring-boot, нужен еще @Configuration-класс, как выше.
   public class MarriageService {
       
       public MarriageCertificate marry(Person man, Person woman) {
           return new MarriageCertificate(man, woman, LocalDate.now());
       }
       
   }
   ```

## Объединение конфигов

Можно использовать несколько классов-конфигураций для удобства, а потом объединить их в едином классе с помощью аннотации `@Import`. Сделаем все то же самое - CityService объявим явно, а MarriageService спринг будет искать сам, но каждую конфигурацию оформим в отдельном классе:

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
