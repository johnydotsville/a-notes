# Концепция Spring Security

Обработка HTTP-запросов в джаве строится на сервлетах. Объект запроса попадает в сервлет, там происходит его обработка и сервлет записывает результат в объект ответа, который и уходит к клиенту. Есть особая категория сервлетов - *фильтры*. Их можно расположить в любом количестве перед "конечным" сервлетом для выполнения разных вспомогательных задач. Например, добавлять к объекту запроса какую-то информацию, или прерывать прохождение запроса к сервлету (например, если клиент не авторизован). Т.о. в лучшем случае запрос проходит по всей цепочке фильтров к сервлету, а потом обратно по всей цепочке (уже в обратном порядке) идет назад и уходит пользователю. Ну а в худшем просто не доходит до конечного сервлета.

Spring Security технически представляет собой как раз такой вот фильтр. Единственный, т.е. в цепочке фильтров он будет занимать один "слот". Однако внутри он сам может состоять из цепочки своих внутренних фильтров. Все это можно изобразить примерно так:

![spring-security-idea.drawio](img/spring-security-idea.drawio.svg)

# Аутентификация и авторизация

Эти понятия означают следующее:

* Аутентификация - процесс определения "Кто вы?".

  "Вы - администратор".

* Авторизация - процесс определения "Что вы можете делать?" после того, как стало известно, кто вы.

  "Вы - администратор, поэтому вы можете получать доступ ко всем ресурсам ("ходить по всем ссылкам приложения")"

# Базовый пример

## Зависимости

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

## Бины

Общая идея в том, что нам нужно настроить три вещи:

* Сервис получения информации о пользователях
* Способ кодирования пароля
* Настройки безопасности для ресурсов ("доступ к ссылкам")

Все эти вещи объединяются в конфиге безопасности.

### Бин конфига безопасности

```java
@EnableWebSecurity(debug = true)  // 1
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService userDetailsService;  // 2

    @Autowired
    public SecurityConfig(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean  // 4
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override  // 3
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override  // 5
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()  // 5.1
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()  // 5.2
//                .antMatchers("/api/admin/**").hasAuthority("admin")
//                .antMatchers("/api/user/**").hasAnyAuthority("admin", "user")
                .antMatchers("/api/admin/**").hasRole("admin")
                .antMatchers("/api/user/**").hasAnyRole("admin", "user")
                .anyRequest().permitAll()
                .and()
            .httpBasic();  // 5.3
    }
}
```

Комментарии:

1. Помечаем класс как конфигурационный бин, чтобы спринг его нашел

2. Мы должны предоставить фреймворку информацию о пользователях. Для этого можно написать собственный сервис или воспользоваться готовыми (например, in-memory список пользователей). Потребуем такой сервис в качестве бина через конструктор.

3. В этом переопределенном методе передадим фреймворку этот сервис, предоставляющий информацию о пользователях. В данном случае используется сервис, реализующий интерфейс `UserDetailsService`. Другие варианты будут рассмотрены отдельно.

4. Пароли должны подвергаться шифрованию перед передачей на сервер и храниться в зашифрованном виде.  TODO: ??? Поэтому в данный момент я не понимаю, зачем указывать способ шифрования какой-то кроме `NoOpPasswordEncoder`. Ведь если к нам приходит зашифрованный пароль, и в базе хранится зашифрованный, то мы должны просто сравнить их. Может быть как раз поэтому и указывается NoOpPasswordEncoder, чтобы спринг понимал, что с паролем ничего дополнительно делать не надо. Энивей, в данном примере используется незашифрованный пароль для простоты и варианты с шифрованием разумнее оформить отдельно, чтобы не перегружать каркас деталями.

5. В этом переопределенном методе осуществляется комплексная настройка безопасности.

   1. По умолчанию, если послать данные авторизации через, например Insomnia, спринг их запомнит в сессии и в последующих запросах их уже можно будет не передавать. Мы же переопределяем это поведение, чтобы он не запоминал и данные нужно было передавать в каждом запросе.

   2. Настраиваем доступы к ресурсам. В начале указываем конкретные, в конце - общие, по принципу как и в обработке исключений.

      Шаблон `.antMatchers("/api/admin/**")` означает, что следующие правила (в данном случае правило, что доступ только для админов) будет применяться ко всем путям, которые начинаются с `/api/admin` (и в том числе к самому этому пути). Далее аналогично для `/api/user`, ну и `.anyRequest().permitAll()` означает, что все остальные ресурсы доступны всем (даже не авторизованным), а `.authenticated()` означало бы, что всем аутентифицированным.

      `Authority` - это этакая "базовая" характеристика пользователя. `Role` является подвидом Authority. Поэтому здесь можно использовать любой из этих элементов для настройки доступа к ресурсам.

      > Важная деталь: если использовать методы Role, то нужно учитывать, что роли в спринге префиксуются как `ROLE_`. Поэтому когда мы пишем `.hasRole("admin")`, то предполагается, что значение, с которым будет происходить сравнение, выглядит как `ROLE_admin`. Это ROLE_ будет автоматически убрано и получится совпадение, admin == admin. Об этом еще будет упомянуто в комментариях к сервису MyUserDetailsService, который предоставляет фреймворку информацию о пользователях

   3. `.httpBasic()` означает, что используется т.н. [базовая схема аутентификации](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication), т.е. в запрос добавляется заголовок `Authorization` с логином \и паролем.
   
   В общем, при необходимости можно кастомизировать достаточно сильно.
   
   ### Бин сервиса информации о пользователях
   
   asdf
   
   ```java
   @Service
   public class MyUserDetailsService implements UserDetailsService {
       private UserCredentialsRepo userCredentialsRepo;
   
       @Autowired
       public MyUserDetailsService(UserCredentialsRepo userCredentialsRepo) {
           this.userCredentialsRepo = userCredentialsRepo;
       }
   
       @Override
       public UserDetails loadUserByUsername(String login)
               throws UsernameNotFoundException {
           UserCredentials userCredentials = userCredentialsRepo.findByLogin(login);
   
           if (userCredentials == null) {
               throw new UsernameNotFoundException("Unknown user: " + login);
           }
   
           UserDetails user = User.builder()
                   .username(userCredentials.getLogin())
                   .password(userCredentials.getPassword())
                   .roles(userCredentials.getRole())
   //                .authorities(userCredentials.getRole())
                   .build();
   
           return user;
       }
   }
   ```
   
   
   
   







# TODO

Написать, какой код возвращает в случае недостатка прав. Например, 403, 401 в каких случаях