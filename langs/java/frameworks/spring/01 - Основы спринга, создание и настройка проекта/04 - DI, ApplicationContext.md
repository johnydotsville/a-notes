# DI, ApplicationContext

К этому документу я приложил три небольших демки, чтобы не копировать сюда полностью код классов. Они максимально простые, так что даже если просто ее открыть без комментариев, не запутаешься.

В основе любого спринг-приложения лежит объект, управляющий бинами. Он называется "контекст" и реализует интерфейс *[ApplicationContext](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html)* (точнее, один из его [субинтерфейсов](https://docs.spring.io/spring-framework/docs/current/javadoc-api/org/springframework/context/ApplicationContext.html) `ConfigurableApplicationContext`, `ConfigurableWebApplicationContext`, `WebApplicationContext`).

Бином является любой объект, находящийся под управлением контекста. В нашем приложении могут быть разные объекты и не все они обязательно создаются при участии спринга - какие-то мы можем создавать руками. Вот такие "ручные" объекты не являются бинами и не входят в контекст.

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
