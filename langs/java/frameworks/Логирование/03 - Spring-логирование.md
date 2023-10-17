# Spring логирование

## logback, получение логера

В спринге по умолчанию используется логер *logback* (можно заменить на другой). По умолчанию он настроен на вывод в консоль сообщений уровня INFO и выше.

> Напоминание: стандартные уровни логирования, от менее важного к самому важному `ALL < TRACE < DEBUG < INFO < WARN < ERROR < FATAL`. Выставление логеру уровня логирования INFO означает, что будут логироваться только INFO и все что справа. Если залогировать debug, то такое сообщение в лог не попадет.

Как получить объект логгера:

```java
public class App {

	static final Logger log = LoggerFactory.getLogger(App.class);  // <-- Получаем объект логера

	public static void main(String[] args) {
		log.debug("DEBUG уровень по умолчанию не логируется.");
		log.info("INFO: Подключили логгер, должен работать.");
    }
    
}
```

Стандартная практика - сохранять логер в статическое поле и давать логеру имя класса, в котором он используется. Почему это так - см. конспект по обычному логированию и настройке log4j.

# Настройки логера

Полноценная конфигурация логера делается через xml, а через application.properties можно только подправить некоторое поведение. Там довольно много, почитать можно например [тут](https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/howto-logging.html).

## xml

Чтобы настроить логер через xml, создаем файл `resources\logback-spring.xml`

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOGS" value="./logs" />
    <property name="filename" value="ati-remake.log" />

    <appender name="Console"
              class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>

    <appender name="RollingFile"
              class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${LOGS}/${filename}</file>
        <encoder
                class="ch.qos.logback.classic.encoder.PatternLayoutEncoder">
            <Pattern>%d %p %C{1.} [%t] %m%n</Pattern>
        </encoder>

        <rollingPolicy
                class="ch.qos.logback.core.rolling.TimeBasedRollingPolicy">
            <!-- rollover daily and when the file reaches 10 MegaBytes -->
            <fileNamePattern>${LOGS}/archived/${filename}-%d{yyyy-MM-dd}.%i.log
            </fileNamePattern>
            <timeBasedFileNamingAndTriggeringPolicy
                    class="ch.qos.logback.core.rolling.SizeAndTimeBasedFNATP">
                <maxFileSize>10MB</maxFileSize>
            </timeBasedFileNamingAndTriggeringPolicy>
        </rollingPolicy>
    </appender>

    <!-- LOG everything at INFO level -->
    <root level="info">
        <appender-ref ref="RollingFile" />
        <appender-ref ref="Console" />
    </root>

    <logger name="ati.remake" level="trace" additivity="false">
        <appender-ref ref="RollingFile" />
        <appender-ref ref="Console" />
    </logger>

</configuration>
```

# Другие логгеры

Чтобы использовать в спринге какой-то другой логгер, нужно исключить из стартера logback и отдельно подключить нужный логгер. На примере log4j2:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter</artifactId>
    <exclusions>
        <exclusion>  <!-- Отключаем стандартный -->
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-logging</artifactId>
        </exclusion>
    </exclusions>
</dependency>

<dependency>  <!-- Подключаем нужный -->
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-log4j2</artifactId>
    <version>1.3.8.RELEASE</version>
</dependency>
```

Далее создаем в resources файл *log4j2.xml* и пользуемся как обычно (см. конспект по обычному логированию log4j)