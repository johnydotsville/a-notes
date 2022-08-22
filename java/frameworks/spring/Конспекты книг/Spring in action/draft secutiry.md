Зависимость:

```xml
<dependency>
	<groupId>org.springframework.boot</groupId>
	<artifactId>spring-boot-starter-security</artifactId>
</dependency>
```

При запуске приложения теперь для доступа к любой странице потребуется ввести логин и пароль, т.к. спринг теперь использует дефолтную конфигурацию защиты.

При ручном тестировании защиты полезно пользоваться режимом инкогнито браузера, чтобы он ничего не запоминал и вы заходили в приложение каждый раз как будто в первый раз.

Дефолтный пользователь user, а дефолтный пароль спринг выводит в лог. Пароль выглядит примерно так: 438b46c5-36a5-47b0-90c6-2fd3039ebb76

Добавим простой класс настройки безопасности:

```java
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Configuration
public class SecurityConfig {
    @Bean
    // Попробовать потом назвать метод по-другому
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public UserDetailsService userDetailsService(PasswordEncoder encoder) {
        List<UserDetails> users = new ArrayList<>();
        users.add(new User("tom",
                encoder.encode("tomspass"),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
        users.add(new User("huck",
                encoder.encode("huckspass"),
                Arrays.asList(new SimpleGrantedAuthority("ROLE_USER"))));
        return new InMemoryUserDetailsManager(users);
    }
}
```

Здесь описаны два бина: один для шифрования паролей и второй - предоставляет список пользователей.

Из коробки поддерживается три варианта предоставления списка пользователей:

* In-memory
* JDBC
* LDAP (Lightweight Directory Access Protocol) - отдельная тема

