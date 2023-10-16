# Типы приложений и область видимости бинов (scope)

Условно приложения можно разделить на два вида - веб-приложения и обычные ("не-веб-приложения"). Они пользуются разными скопами.

# Scope обычных приложений

В основе обычных приложений лежит объект с интерфейсом ApplicationContext.

## singleton

Это скоуп *по умолчанию*. Используется чаще всего. Когда кому-то требуется бин, который является синглтоном, спринг возвращает один и тот же объект и все "пользователи" работают с одним объектом.

```java
@Service
@Scope("singleton")  
// @Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class CounterService {
    ...
}
```

## prototype

Этот скоуп подразумевает, что спринг создает новый объект каждый раз, когда кому-то требуется этот бин. В итоге у каждого "пользователя" - своя собственная копия бина.

```java
@Service
@Scope("prototype")  
//@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class CounterService {
    ...
}
```

# Scope веб-приложений

В основе обычных приложений лежит объект с интерфейсом WebApplicationContext.

TODO: вернуться к этому позже

### @RequestScope



### @SessionScope



### @ApplicationScope



### @Scope(scopeName = "websocket", proxyMode = ScopedProxyMode.TARGET_CLASS)

