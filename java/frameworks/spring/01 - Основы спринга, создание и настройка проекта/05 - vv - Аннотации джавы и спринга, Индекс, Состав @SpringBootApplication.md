# Вопросы

- [ ] Что означает синтаксис `public @interface X`?
- [ ] Что такое мета-аннотации и как это связано с возможностью применять к элементу сразу несколько аннотаций? Какой синтаксис у мета-аннотаций?
- [ ] Аннотации @Target, @Retention, @Inherited, @Documented, @Repeatable, @Native являются частью джавы или относятся к спрингу? Для чего нужна каждая из них?
  - [ ] Какие значения (концептуально) есть у @Retention?
  - [ ] Как под капотом работает @Repeatable? Для чего нужен контейнер? Как через рефлексию получить все значения, добавленные с помощью repeatable-аннотации и какую роль в этом играет контейнер?
- [ ] Что такое fail-fast принцип, который спринг использует при создании бинов? Что будет, если какой-то бин не удается создать?
- [ ] Как по умолчанию спринг ищет классы для создания бинов?
  - [ ] Чем отличается подход с использованием индекса? Что представляет собой индекс физически? Какая информация и в каком виде в нем хранится?
  - [ ] Зачем нужна зависимость spring-context-indexer? За что отвечает файл */classes/META-INF/spring.components* и в какой директории следует его искать?
  - [ ] В каком случае класс попадает в индекс? Как это связано с аннотацией @Indexed?
  - [ ] От чего зависит, под какой аннотацией класс попадет в индекс? Если одна аннотация имеет в своем составе @Indexed, а другая нет?
  - [ ] Можно ли использовать индекс только в одном модуле программы, а в других пользоваться обычным поиском?
- [ ] Чем отличаются аннотации @Component, @Service, @Repository и какая между ними связь? Как они связаны с @Indexed?
- [ ] Как связаны аннотации @Configuration и @Bean? Какая из них применяется к классу, а какая к методу? Как с ними связана аннотация @Import?

# Формат аннотаций

Все аннотации спринга в доке описаны так:

```java
@Target(value={METHOD, ANNOTATION_TYPE})
   @Retention(value=RUNTIME)
   @Documented
   @Inherited
public @interface X
```

* Конструкцией `@interface` объявляется аннотация. Соответственно, `public @interface X` объявляет аннотацию X, которую мы потом будем использовать как `@X`

* Применение к X аннотаций @Target, @Retention, @Documented, @Inherited говорит о том, что Х является комбинацией этих аннотаций (с указанными параметрами). Т.е., применяя к элементу аннотацию X, мы применяем к нему все эти четырея аннотации

Аннотации, которые применяются к другим аннотациям, называются *мета-аннотациями*. В данном случае @Target, @Retention, @Documented, @Inherited - это мета-аннотации

# Собственные аннотации джавы

Нужно понимать, какие аннотации являются частью джавы, а какие принадлежат спрингу. В самой джаве не так уж много встроенных аннотаций (см. [тут](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/package-summary.html)):

```
@Target
@Retention
@Inherited
@Documented
@Repeatable
@Native
```

## @Target

Определяет, к какому элементу можно применить аннотацию @X

Например, `@Target(value={METHOD, ANNOTATION_TYPE})` значит, что аннотацию @X можно применить к методу и к аннотациям

[Полный список возможных целей с описаниями](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/ElementType.html). Из наиболее интересных: TYPE (к классу, но и не только), CONSTRUCTOR, FIELD, METHOD

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Target
```

P.S. Интересно, как аннотация применяется к самой себе...

## @Retention

Переводится как "удержание", "сохранение". Определяет "как долго аннотация @X будет прилеплена к элементу", например: 

* `@Retention(value = RetentionPolicy.RUNTIME)` - аннотация будет доступна во время выполнения. Тогда, например, с помощью рефлексии мы сможем узнать, что у класса есть аннотация @X
* `@Retention(value = RetentionPolicy.SOURCE)` - аннотация доступна только во время компиляции. Во время выполнения у класса уже не будет @X

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Retention
```

## @Documented

Что-то связанное с автогенерацией документации

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Documented
```

## @Inherited

Если аннотация @X отмечена как @Inherited, то применение @X к классу приводит к тому, что и у его потомков автоматически будет @X

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Inherited
```

## @Native

Такой аннотацией отмечают поля-константы, на которые можно ссылаться из нативного кода

```java
@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.SOURCE)
public @interface Native
```

## @Repeatable

Аннотацию можно применять к элементу несколько раз, с разными значениями

```java
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.ANNOTATION_TYPE)
public @interface Repeatable
```

Чтобы пользоваться этой аннотацией, нужно подготовить ей техническую основу:

* Создаем аннотацию, которую может понадобиться использовать несколько раз:

  ```java
  @Repeatable(Marks.class)  // <-- Этот класс не создается сам по себе, он - наша задача
  public @interface Mark {
      String value() default "unmarked";
  }
  ```

  Обратим внимание на класс Marks. Это контейнер для аннотаций, который тоже нужно создать самим

* Создаем контейнер:

  ```java
  @Retention(value = RetentionPolicy.RUNTIME)
  public @interface Marks {
      Mark[] value();
  }
  ```

  У контейнера задаем характеристики аннотации, например, ее доступность

* Теперь можем применять @Mark сколько нам нужно:

  ```java
  @Mark(value = "feature")  // <-- Раз
  @Mark(value = "new")  // <-- И два
  public class MarriageService {
      public static void main(String[] args) {
          MarriageService marser = new MarriageService();
          
          Marks marks = marser.getClass().getAnnotation(Marks.class);  // <-- А Mark.class нету
          
          for (Mark mark : marks.value()) {
              System.out.println(mark.value());
          }
      }
  }
  ```

Важно - у класса MarriageService не будет аннотации @Mark, а только @Marks. Все потому, что @Repeatable - просто синтаксический сахар, который преобразуется вот в такую конструкцию (которую в принципе можно использовать и напрямую при желании):

```java
@Marks({
    @Mark(value = "feature"),
    @Mark(value = "new")
})
public class MarriageService
```

# Состав @SpringBootApplication

Основной класс, запускающий магию спринга, аннотируется @SpringBootApplication. Ее состав:

```java
@Target(value=TYPE)
   @Retention(value=RUNTIME)
   @Documented
   @Inherited
   @SpringBootConfiguration
   @EnableAutoConfiguration
   @ComponentScan(excludeFilters={@ComponentScan.Filter(type=CUSTOM,classes=TypeExcludeFilter.class),})
public @interface SpringBootApplication
```

Рассмотрим каждую из этих аннотаций (аннотации спринга для удобства выделены зеленым):

![SpringBootApplication-composition.drawio](img/SpringBootApplication-composition.drawio.svg)

Начнем с самых "нижних".

## @Indexed и индекс

 [Интересная статья про индекс](http://www.javabyexamples.com/spring-candidate-component-index)

### classpath vs index

Суть спринга - сформировать приложение из бинов. А бины надо создавать из каких-то классов. Спринг сразу создает все бины на старте приложения и если при этом возникают какие-то ошибки, приложение даже не запускается. Это называется fail-fast принцип. По умолчанию спринг смотрит классы в classpath'е и анализирует их аннотации. Это не такая уж долгая операция, но все же время занимает.

Вместо анализа classpath, можно сформировать *индекс* - это список классов-кандидатов, из которых будут создаваться бины. Если подключить зависимость **spring-context-indexer**, то индекс автоматически сформируется при компиляции и сохранится в файл *target/classes/META-INF/spring.components* (в target папке, а не в исходниках).

> Если вдруг зависимость вызывает ошибки компиляции, возможно следует попробовать более раннюю версию

### Как выглядит индекс

Пример индекса (он представляет собой обычный текстовый файл):

```
com.somegroup.spronk.cure.indexedbased.SampleComponent1=org.springframework.stereotype.Component
com.somegroup.spronk.cure.indexedbased.SampleRepository1=org.springframework.stereotype.Component
com.somegroup.spronk.cure.javaxbased.NamedService=javax.inject.Named
```

Ключ - имя класса, а значение - аннотация, которой он помечен.

Классы попадают в индекс, если они отмечены аннотацией @Indexed (ну или другими, которые включают в себя эту аннотацию. UPD. И не только, еще в javax есть аннотации, которые ведут к попаданию класса в индекс, например, @Entity, @Named. В общем, не суть какие именно, важно что попадают).

### "Повторение" @Indexed

Если посмотреть на аннотацию @SpringBootConfiguration, то она включает в себя @Configuration и @Indexed. При этом @Configuration включает @Component, а у него тоже есть @Indexed. Сперва мне это показалось странным - получается, что @SpringBootConfiguration как бы два раза содержит в себе @Indexed. Однако это не случайно - от *непосредственного* наличия аннотации @Indexed зависит, какая аннотация будет указана в ключе у класса в файле индекса.

Поскольку у @SpringBootConfiguration аннотация @Indexed указана *непосредственно*, то в индекс класс попадает именно с аннотацией @SpringBootConfiguration. А вот к примеру аннотация @Repository:

```java
@Target(value=TYPE)
   @Retention(value=RUNTIME)
   @Documented
   @Component  // <-- @Indexed у @Repository напрямую нет, но есть косвенно через @Component
public @interface Repository
```

У нее нет непосредственно аннотации @Indexed, зато у нее есть @Component, который *непосредственно* содержит @Indexed. Поэтому класс, помеченный @Repository, попадет в индекс не с аннотацией @Repository, а с аннотацией @Component. Надеюсь теперь принцип понятен, и почему @Indexed может как бы "дублироваться". Пример настоящего индекса:

```
j.d.typehereyourprogid.BeanScopesDemoApp=org.springframework.stereotype.Component,org.springframework.boot.SpringBootConfiguration
j.d.core.MorningGreetingService=johny.dotsville.core.MorningGreetingService  // when @Indexed
j.d.core.MorningGreetingService=org.springframework.stereotype.Component     // when @Component
j.d.core.MorningGreetingService=org.springframework.stereotype.Component     // w @Service/Repository
```

На второй строчке показан эффект, когда через @Index помечается непосредственно класс

### Правила пользования индексом

Если использовать индекс, то нужно это делать во всех модулях проекта. Можно отключить использование индекса

```
spring.index.ignore=true
```

Эта настройка помещается в файл */src/main/resources/spring.properties*

## @Component и @Service

```java
@Target(value=TYPE)
   @Retention(value=RUNTIME)
   @Documented
   @Indexed
public @interface Component
```

Такой аннотацией отмечается компонент, иначе и не скажешь. Сама концепция "компонент" означает класс, который будет автоматически найден в classpath или в индексе.

Компонент - самый общий вид, хм, "компонента". Есть более специализированные "компоненты":

```java
@Target(value=TYPE)
   @Retention(value=RUNTIME)
   @Documented
   @Component  // <-- Все это тоже компоненты по своей сути
public @interface Repository / Service / etc
```

## @Configuration

```java
@Target(value=TYPE)
   @Retention(value=RUNTIME)
   @Documented
   @Component
public @interface Configuration
```

Этой аннотацией мы отмечаем классы, которые содержат методы, создающие бины. Пример:

```java
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

Пример использования этого класса:

```java
AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
ctx.register(ServiceConfiguration.class);
ctx.refresh();
```

Есть возможность разносить конфигурацию бинов по нескольким классам, потом объединять их, но вообще ни это, ни собственно использование конфигураций не является темой этого мануала. Тут важно понять просто для чего эта аннотация нужна, а не как ею пользоваться.

В демки записал хорошие примеры.

## @Bean

```java
@Target(value={METHOD,ANNOTATION_TYPE})
   @Retention(value=RUNTIME)
   @Documented
public @interface Bean
```

Отмечает методы, которые создают бины.

## @Import

Используется для объединения нескольких классов, аннотированных @Configuration, в один класс, в котором получатся все суммарные настройки.

## @SpringBootConfiguration

```java
@Target(value=TYPE)
   @Retention(value=RUNTIME)
   @Documented
   @Configuration
   @Indexed
public @interface SpringBootConfiguration
```


