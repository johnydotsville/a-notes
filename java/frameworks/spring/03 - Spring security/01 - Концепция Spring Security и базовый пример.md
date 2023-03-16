# Концепция Spring Security

Обработка HTTP-запросов в джаве строится на сервлетах. Объект запроса попадает в сервлет, там происходит его обработка и сервлет записывает результат в объект ответа, который и уходит клиенту. Есть особая категория сервлетов - *фильтры*. Их можно расположить в любом количестве перед "конечным" сервлетом для выполнения разных вспомогательных задач. Например, добавлять к объекту запроса какую-то информацию, или прерывать прохождение запроса к сервлету (например, если клиент не авторизован). Т.о. в лучшем случае запрос проходит по всей цепочке фильтров к сервлету, а потом обратно по всей цепочке (уже в обратном порядке) идет назад и уходит пользователю. Ну а в худшем просто не доходит до конечного сервлета.

Spring Security технически представляет собой как раз такой вот фильтр. Монолитный, т.е. в цепочке фильтров он будет занимать один "слот". Однако внутри он сам может состоять из цепочки своих внутренних фильтров. Все это можно изобразить примерно так:

![spring-security-idea.drawio](img/spring-security-idea.drawio.svg)

Пример дефолтной цепочки фильтров в Spring Security, через которые проходит запрос:

```
Запрос -> SecurityContextPersistenceFilter, HeaderWriterFilter, CsrfFilter, LogoutFilter, UsernamePasswordAuthenticationFilter, DefaultLoginPageGeneratingFilter, DefaultLogoutPageGeneratingFilter, BasicAuthenticationFilter, RequestCacheAwareFilter, SecurityContextHolderAwareRequestFilter, AnonymousAuthenticationFilter, SessionManagementFilter, ExceptionTranslationFilter, FilterSecurityInterceptor -> Запрос наконец-то попадает в контроллер
```

Актуальную цепочку можно посмотреть в документации к нужной версии спринга. Например, [тут для 5.3.9](https://docs.spring.io/spring-security/site/docs/5.3.9.RELEASE/reference/html5/)

# Аутентификация и авторизация, http-коды

Эти понятия означают следующее:

* Аутентификация - процесс определения "Кто вы?".

  "Вы - администратор".

  На запрос, не прошедший *аутентификацию*, возвращается код `401 - Unauthorized`. Хотя по названию созвучно с "не авторизован", но на самом деле этот код означает именно "не аутентифицирован", т.е. не был определен как пользователь, зарегистрированный в системе.

* Авторизация - процесс определения "Что вы можете делать?" после того, как стало известно, кто вы.

  "Вы - администратор, поэтому вы можете получать доступ ко всем ресурсам ("ходить по всем ссылкам приложения")"
  
  На запрос аутентифицированного пользователя, который не имеет прав доступа к запрошенному ресурсу, возвращается код `403 - Forbidden`.

# Зависимости

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

# Общие классы

Используются во всех примерах.

## Внутреннее представление пользователя

Именно наш доменный юзер, не зависит от требований спринга:

```java
@Getter @Setter
public class UserCredentials {
    private String login;
    private String password;
    private String role;

    public UserCredentials() { }

    public UserCredentials(String login, String password, String role) {
        this.login = login;
        this.password = password;
        this.role = role;
    }
}
```

## Представление ролей

Для простоты просто константы:

```java
public class UserRolesEnum {
    public static final String ADMIN = "admin";
    public static final String USER = "user";
    public static final String OWNER = "owner";
}
```

## Репозиторий пользователей

Наш сервис, имитирующий извлечение пользователя из БД:

```java
@Service
public class UserCredentialsRepo {
    private static Map<String, UserCredentials> users = new HashMap<>() {{
        put("tom", new UserCredentials("tom", "ptom", UserRoleEnum.USER));
        put("sid", new UserCredentials("sid", "psid", UserRoleEnum.USER));
        put("polly", new UserCredentials("polly", "ppolly", UserRoleEnum.ADMIN));
    }};

    public UserCredentials findByLogin(String login) {
        return users.get(login);
    }
}
```

# Контроллер

Теперь для вполне обычного контроллера будут работать правила безопасности:

```java
@RestController
@RequestMapping(path = "/api")
public class HelloController {

    @GetMapping("/hello/{name}")
    public ResponseEntity hello(@PathVariable String name) {
        String message = String.format("Hello, %s", name);
        return ResponseEntity.ok(message);
    }

    @GetMapping("/admin")
    public ResponseEntity admin() {
        String message = "Admin start page";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/admin/hello")
    public ResponseEntity adminHello() {
        String message = String.format("Hello, admin!");
        return ResponseEntity.ok(message);
    }

    @GetMapping("/user")
    public ResponseEntity user() {
        String message = "User start page";
        return ResponseEntity.ok(message);
    }

    @GetMapping("/user/hello")
    public ResponseEntity userHello() {
        String message = "Hello, user!";
        return ResponseEntity.ok(message);
    }
}
```

# Отправка запроса

Запрос можно отправить с помощью, например, клиента `Insomnia`:

* Создаем новый запрос, адрес ресурса например `http://localhost:8080/api/user/hello`
* Есть разные способы передать данные аутентификации (логин, пароль), например, в теле через форму или через специальный заголовок аутентификации. Корректный способ передачи зависит от того, как это настроено в бине конфига безопасности спринга. Если например там используется `.httpBasic()`, то для добавления заголовка с данными аутентификации необходимо в инсомнии в интерфейсе запроса на вкладке `Basic` выбрать пункт `Basic Auth` и заполнить username и password.

# Сценарии

Логически можно выделить три сценария работы spring security:

1. Наше приложение самостоятельно проводит аутентификацию (авто-"спринговая" аутентификация)
2. Наше приложение поручает аутентификацию другому приложению
3. Наше приложение использует сторонние данные (аутентификация через гугл-аккаунт, твиттер-аккаунт и т.д.)

Сценарии отличаются набором бинов, которые мы должны дать спрингу и тем, что спринг передает в эти бины.

# С1: Спринговая аутентификация

Характеризуется тем, что спринг передает нам *только логин* из запроса. Предполагается, что вся информация о пользователях (пароли, права) нам доступна (например, хранится в нашей БД) и, следовательно, мы по этому логину сможем найти пользователя и вернуть данные о нем в понятном спрингу виде. Тогда спринг сам сравнит пароли и решит, аутентифицирован пользователь или нет.

В этом сценарии нам нужно сделать четыре вещи:

* Определить бин сервиса получения информации о пользователях
* Определить бин способа кодирования пароля
* Настроить правила безопасности для ресурсов ("доступ к ссылкам")
* Объединить все это в бин конфига безопасности

## Бин конфига безопасности

```java
@EnableWebSecurity(debug = true)  // <-- 1
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private MyUserDetailsService userDetailsService;  // <-- 2

    @Autowired
    public SecurityConfig(MyUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean  // <-- 4
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Override  // <-- 3
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    @Override  // <-- 5
    protected void configure(HttpSecurity http) throws Exception {
        http
            .sessionManagement()  // <-- 5.1
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
            .authorizeRequests()  // <-- 5.2
//                .antMatchers("/api/admin/**").hasAuthority("admin")
//                .antMatchers("/api/user/**").hasAnyAuthority("admin", "user")
                .antMatchers("/api/admin/**").hasRole("admin")
                .antMatchers("/api/user/**").hasAnyRole("admin", "user")
                .anyRequest().permitAll()
                .and()
            .httpBasic();  // <-- 5.3
    }
}
```

Комментарии:

1. Помечаем класс как конфигурационный бин, чтобы спринг его нашел.

2. Мы должны предоставить спрингу информацию о пользователях. Для этого можно написать собственный сервис или воспользоваться готовыми (например, in-memory список пользователей). Потребуем такой сервис в качестве бина через конструктор.

3. В переопределенном методе `.configure(AuthenticationManagerBuilder auth)` передадим фреймворку сервис информации о пользователях. В данном случае используется сервис, реализующий интерфейс `UserDetailsService`. Другие варианты будут рассмотрены отдельно.

4. Пароли на сервере должны храниться в зашифрованном виде. С клиента же они могут передаваться как простой текст, в зависимости от ситуации (например, если используется HTTPS, TODO: это насколько я понял, надо уточнить, безопасно ли это???). Соответственно, по приходу на сервер этот пароль надо сначала зашифровать, чтобы сравнить с хранящимся. В данном примере у нас пароли хранятся незашифрованные для наглядности, поэтому мы в качестве бина шифратора используем класс, который на деле не будет шифровать пароль.

5. В переопределенном методе `.configure(HttpSecurity http)` осуществляется комплексная настройка безопасности.
   1. По умолчанию, если послать данные авторизации через, например Insomnia, спринг их запомнит в сессии и в последующих запросах их уже можно будет не передавать. Мы же переопределяем это поведение, чтобы он не запоминал, и данные нужно было передавать в каждом запросе.

   2. Настраиваем доступы к ресурсам. В начале указываем конкретные, в конце - общие, по принципу как и в обработке исключений.

      Шаблон `.antMatchers("/api/admin/**")` означает, что следующие правила (в данном случае правило, что доступ только для админов) будет применяться ко всем путям, которые начинаются с `/api/admin` (и в том числе к самому этому пути). Далее аналогично для `/api/user`, ну и `.anyRequest().permitAll()` означает, что все остальные ресурсы доступны всем (даже не авторизованным), а `.authenticated()` означало бы, что всем аутентифицированным.

      `Authority` - это этакая "базовая" характеристика пользователя. `Role` является подвидом Authority. Поэтому здесь можно использовать любой из этих элементов для настройки доступа к ресурсам.

      Важная деталь: если использовать методы Role, то нужно учитывать, что роли в спринге префиксуются как `ROLE_`. Поэтому когда мы пишем `.hasRole("admin")`, то предполагается, что значение, с которым будет происходить сравнение, выглядит как `ROLE_admin`. Это ROLE_ будет автоматически убрано и получится совпадение, admin == admin. Об этом еще будет упомянуто в комментариях к сервису MyUserDetailsService, который предоставляет фреймворку информацию о пользователях.

      Но вообще надо быть крайне осторожным и внимательным с понятиями role и authority. В этом простом примере разница не видна, но она есть и рассматривается в других разделах. TODO: ??? когда до конца разберусь, надо будет все реорганизовать, чтобы не было каши.

6. `.httpBasic()` означает, что используется т.н. [базовая схема аутентификации](https://developer.mozilla.org/en-US/docs/Web/HTTP/Authentication), т.е. в запрос добавляется заголовок `Authorization` с логином \и паролем.

В общем, при необходимости можно кастомизировать достаточно сильно.

## Бин сервиса информации о пользователях

Этот сервис отвечает за формирование объекта пользователя, который содержал бы всю необходимую спрингу информацию о пользователе. Например, логин, пароль, его права (authorities), роль. Информацию эту можно брать откуда угодно - из БД, из сторонненго сервиса, хранить в памяти. В общем, способ хранения не важен, для некоторых есть уже готовые классы, главное что на выходе должен получаться объект, понятный спрингу. В данном примере сымитируем хранение в БД:

```java
@Service
public class MyUserDetailsService implements UserDetailsService {  // <-- 1
    private UserCredentialsRepo userCredentialsRepo;  // <-- 2

    @Autowired
    public MyUserDetailsService(UserCredentialsRepo userCredentialsRepo) {
        this.userCredentialsRepo = userCredentialsRepo;
    }

    @Override  // <-- 3
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        UserCredentials userCredentials = userCredentialsRepo.findByLogin(login);  // <-- 4

        if (userCredentials == null) {  // <-- 5
            throw new UsernameNotFoundException("Unknown user: " + login);
        }

        UserDetails user = User.builder()  // <-- 6
                .username(userCredentials.getLogin())
                .password(userCredentials.getPassword())
                .roles(userCredentials.getRole())
//                .authorities(userCredentials.getRole())
                .build();

        return user;
    }
}
```

Комментарии:

1. Реализуем выбранный интерфейс. Для нашего сценария это `UserDetailsService`
2. Внедрим собственный сервис, который по логину найдет нам пользователя в нашей БД. Этот пользователь может быть в каком угодно формате, мы его впоследствии преобразуем в понятный для фреймворка вид.
3. Переопределяем метод, через который спринг передаст нам логин пользователя
4. Используя собственный сервис, ищем пользователя.
5. Если не нашли, положено выбросить исключение `UsernameNotFoundException`
6. Пользуемся строителем, чтобы создать объект пользователя, который будет понятен фреймворку. В частности, когда мы используем метод `.roles`, то к переданной роли строитель добавит префикс `ROLE_`. Об этом упоминалось выше, в настройке доступа к ресурсам, когда используется метод `.hasRole()`. Поэтому важно пользоваться именно строителем, чтобы получился объект правильного формата и все подобные тонкости спринг брал на себя.

# С2: Делегированная аутентификация

Характеризуется тем, что спринг передает нам *и логин, и пароль* из запроса. Предполагается, что мы передадим эту информацию стороннему сервису, он сам сравнит пароли и вернет нам информацию о пользователе (в частности, его права), а мы уже эту информацию преобразуем и вернем спрингу в понятном ему виде.

Сервис может быть и не сторонним, а нашим микросервисом или даже быть частью этой же программы. Суть в том, что провести аутентификацию (читай "сравнить пароли") придется именно этому сервису, а спринг этим заниматься не будет.

## Бин конфига безопасности

В целом похож на бин из первого сценария:

```java
@EnableWebSecurity
public class SecurityConfigThirdPartyAuth {
	// 1. Требуем объект провайдера аутентификации
    private MyAuthenticationProvider myAuthenticationProvider;

    @Autowired
    public SecurityConfigThirdPartyAuth(MyAuthenticationProvider myAuthenticationProvider) {
        this.myAuthenticationProvider = myAuthenticationProvider;
    }

    @Bean  // 2. Передаем этот объект спрингу
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.authenticationProvider(myAuthenticationProvider);

        return authenticationManagerBuilder.build();
    }

    @Bean  // 3. Настраиваем доступ к ресурсам
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .sessionManagement()
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeRequests()
            .antMatchers("/api/admin/**").hasAuthority("admin")
            .antMatchers("/api/user/**").hasAnyAuthority("admin", "user")
            .anyRequest().permitAll()
            .and()
            .httpBasic();

        return http.build();
    }
}
```

Просто используется интерфейс `AuthenticationProvider` вместо `UserDetailsService`.

## Бин провайдера аутентификации

Здесь в целом все должно быть понятно - класс по сути нужен для получения от спринга логина и пароля для передачи в сервис, занимающийся аутентификацией. В данном примере для наглядности просто передадим в самописный сервис и по сути получится некий аналог первого сценария. Но если воспользоваться реальной библиотекой стороннего сервиса или послать в другую программу http-запрос за аутентификацией в методе .callSomeAuthenticationService, тогда все будет больше походить на то, для чего все это задумывалось:

```java
@Component
public class MyAuthenticationProvider implements AuthenticationProvider {
    // 1. В этом примере просто используем обычный самописный класс, поэтому требуем его
    private SomeAuthenticationService someAuthenticationService;

    @Autowired
    public MyAuthenticationProvider(SomeAuthenticationService someAuthenticationService) {
        this.someAuthenticationService = someAuthenticationService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) 
            throws AuthenticationException {
        // 2. Спринг отдаем нам и логин, и пароль из пришедшего http-запроса
        String username = authentication.getPrincipal().toString();
        String password = authentication.getCredentials().toString();

        // 3. Передаем их сервису аутентификации
        User user = callSomeAuthenticationService(username, password);
        if (user == null) {
            throw new BadCredentialsException("could not login");
        }

        return new UsernamePasswordAuthenticationToken(user.getUsername(), 
            user.getPassword(), user.getAuthorities());
    }

    @Override  // ???
    public boolean supports(Class<?> authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

    // 4. Анализируем ответ сервиса и возвращаем (если можем) объект юзера, понятный спрингу
    private User callSomeAuthenticationService(String username, String password) {
        UserThirdParty utp = someAuthenticationService.auth(username, password);

        if (utp == null) {
            return null;
        }

        User springUser = new User(username, password, utp.getPrava().stream()
            .map(a -> new SimpleGrantedAuthority(a)).collect(Collectors.toList()));
        return springUser;
    }
}
```

## Вспомогательные классы

**Класс пользователя**, как он может быть представлен в стороннем сервисе:

```java
@Getter  @Setter
public class UserThirdParty {
    private List<String> prava = null;
    private boolean authenticated = false;
}
```

Собственно сам **сервис, проводящий аутентификацию**:

```java
@Service
public class SomeAuthenticationService {

    private UserCredentialsRepo userCredentialsRepo;

    @Autowired
    public SomeAuthenticationService(UserCredentialsRepo userCredentialsRepo) {
        this.userCredentialsRepo = userCredentialsRepo;
    }

    // Важно что этот сервис возвращает свой тип пользователя, который нам предстоит
    // перевести в вид, понятный спрингу
    public UserThirdParty auth(String username, String password) {
        UserCredentials userCredentials = userCredentialsRepo.findByLogin(username);

        // Сами "проводим аутентификацию" ("сравниваем пароли").
        // Для наглядности сравниваем просто как есть, без шифрования
        if (!userCredentials.getPassword().equals(password)) {
            return null;
        }

        UserThirdParty resultUser = new UserThirdParty();
        resultUser.setPrava(userCredentials.getRoles());
        resultUser.setAuthenticated(true);

        return resultUser;
    }
}
```

# Дополнения

## InMemory пользователи

Хорошо подходит для тестирования, т.к. можно задать набор пользователей и он будет храниться в памяти:

```java
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {
    ...
    @Override
    public void configure(AuthenticationManagerBuilder auth) throws Exception {
//        auth.userDetailsService(userDetailsService);
        auth.inMemoryAuthentication()
                .withUser("tom").password("ptom").roles("user")
                .and()
                .withUser("sid").password("psid").roles("user")
                .and()
                .withUser("polly").password("ppolly").roles("admin");
    }
    ...
}
```

## Алгоритмы шифрования пароля

Есть два сценария:

* Для всех паролей используется один и тот же алгоритм, мы можем указать его в бине:

  ```java
  @EnableWebSecurity
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
      ...
      @Bean
      public PasswordEncoder passwordEncoder() {
          // return NoOpPasswordEncoder.getInstance();  // Нет шифрования
          return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 12);  // BCrypt с указанием версии и сложности шифрования
          // И т.д., т.е. бин возвращает один объект алгоритма
      }
      ...
  }
  ```

* Пароли зашифрованы разными алгоритмами. Например, БД существует давно и раньше использовался другой алгоритм. На этот случай мы можем указать набор алгоритмов, а сам хранимый пароль должен начинаться с префикса `{видАлгоритма}`, чтобы спринг понял, какой из зарегистрированных алгоритмов надо использовать для шифрования пришедшего пароля перед сравнением с хранимым:

  ```java
  @EnableWebSecurity
  public class SecurityConfig extends WebSecurityConfigurerAdapter {
      ...
      @Bean
      public PasswordEncoder passwordEncoder() {
          Map<String, PasswordEncoder> encoders = new HashMap<>();
          encoders.put("bcrypt", new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2A, 12));
          encoders.put("noop", NoOpPasswordEncoder.getInstance());
          encoders.put("sha256", new MessageDigestPasswordEncoder("SHA-256"));
          encoders.put("md5", new MessageDigestPasswordEncoder("MD5"));
  
          return new DelegatingPasswordEncoder("noop", encoders);
      }
  
      @Override
      public void configure(AuthenticationManagerBuilder auth) throws Exception {
          auth.inMemoryAuthentication()
                  .withUser("tom").password("{bcrypt}$2a$12$vHngx4IwtTXMcljpmlSYpeBJaM3K9.9UaoYBkPXempXIEM6lN9m0u").roles("user")
              .and()
                  .withUser("sid").password("{sha256}72a7442f244a82e48f8b3eae055c1490ccc19fe970e7b6083d294c479067f758").roles("user")
              .and()
                  .withUser("polly").password("{noop}ppolly").roles("admin");
      }
      ...
  }
  
  // Список алгоритмов, которые удалось найти. Многие считаются устаревшими:
  // Пакет org.springframework.security.crypto.password (osscp)
  "ldap", new osscp.LdapShaPasswordEncoder()
  "MD4", new osscp.Md4PasswordEncoder()
  "MD5", new osscp.MessageDigestPasswordEncoder("MD5")
  "noop", osscp.NoOpPasswordEncoder.getInstance()
  "pbkdf2", new Pbkdf2PasswordEncoder()
  "scrypt", new SCryptPasswordEncoder()
  "SHA-1", new osscp.MessageDigestPasswordEncoder("SHA-1")
  "SHA-256", new osscp.MessageDigestPasswordEncoder("SHA-256")
  "sha256", new osscp.StandardPasswordEncoder()
  ```
  
  Префиксы у паролей должны совпадать с ключами в словаре, под которыми мы регистрируем алгоритмы. При использовании такого подхода пароль не может быть без префикса. Я думаю в реальном сценарии можно было бы создать отдельную таблицу, где было бы сопоставление строк с паролями и префиксами, чтобы не модифицировать сами пароли. Выбираем пароль, префикс, склеиваем их и таким образом получаем нужный формат.

## Несколько ролей \ аторити

У пользователя может быть несколько ролей \ прав, например:

```java
@Getter @Setter
public class UserCredentials {
    ...
    private List<String> roles;
    ...
}

@Service
public class UserCredentialsRepo {
    private static Map<String, UserCredentials> users = new HashMap<>() {{
        ...
        put("polly", getUserPolly());
        ...
    }};
    ...
    private static UserCredentials getUserPolly() {
        UserCredentials polly = new UserCredentials();
        polly.setLogin("polly");
        polly.setPassword("ppolly");
        polly.setRoles(List.of(UserRolesEnum.USER, UserRolesEnum.ADMIN));  // <-- И user, и admin
        return polly;
    }
    ...
}
```

Для работы с аторити в спринге есть специальный класс `SimpleGrantedAuthority`. В нем всего одно поле role. Тогда сформировать пользователя для спринга можно было бы например так:

```java
@Service
public class MyUserDetailsService implements UserDetailsService {
    ...
    @Override
    public UserDetails loadUserByUsername(String login)
            throws UsernameNotFoundException {
        ...
        // Оборачиваем наши строковые роли в SimpleGrantedAuthority и собираем их в список
        List<SimpleGrantedAuthority> roles = userCredentials.getRoles().stream()
                .map(r -> new SimpleGrantedAuthority(r))
                .collect(Collectors.toList());

        UserDetails user = new User(userCredentials.getLogin(), userCredentials.getPassword(), 
                authorities);  // <-- Уже не пользуемся строителем

        return user;
    }
}
```

TODO: ??? Здесь вот что не понятно: если применить после .roles еще и .authorities, то это применение затрет роли. Все потому что роли и аторити - технически вроде как одно и то же, т.е. роль - это просто аторити с префиксом. Соответственно, когда поверх .roles применяется .authority, то сформированная строка перезаписывается новым значением. Не понимаю, почему они не могли просто сделать конкатенацию. Т.е. выходит, что как будто бы и нельзя задать роль и еще накинуть несколько аторитей отдельно. В общем, нужно потом смотреть исходники и разбираться детальнее. Пока и так пойдет.



# Непонятки

- [ ] Есть как будто бы как минимум два способа настроить безопасность:

  ```java
  @Bean
  public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
      http.authorizeRequests()
          ...
          .httpBasic();
      return http.build();
  }
  ```

  и

  ```java
  @Override
  protected void configure(HttpSecurity http) throws Exception {
      http
          .sessionManagement()
          ...
          .httpBasic();
      return http.build();
  }
  ```

  Интересно, чем они отличаются принципиально? Мб какой-то из них уже считается легаси?

  

  

