# Spring-логирование

В спринге по умолчанию используется логгер *logback*.

## Использование другого логгера

Чтобы использовать какой-то другой логгер, надо исключить из стартера logback и отдельной зависимостью подключить нужный. На примере log4j2:

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

# Logback, получение логера

По умолчанию logback настроен на вывод в консоль сообщений уровня DEBUG и выше.

Уровни логирования, от менее важного к самому важному `ALL < TRACE < DEBUG < INFO < WARN < ERROR`. Выставление логеру уровня логирования INFO означает, что будут логироваться только INFO и все что справа. Если при этом залогировать debug, то такое сообщение в лог не попадет.

Как получить объект логгера:

```java
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class App {
	static final Logger log = LoggerFactory.getLogger(App.class);  // <-- Требуем объект логера

	public static void main(String[] args) {
		log.info("INFO: Подключили логгер, должен работать.");
    }
}
```

Стандартная практика - сохранять логгер в статическое поле и давать логгеру полное имя класса, в котором он используется. Это позволяет фреймворку найти в конфиге настройки для создаваемого логгера. Как именно ищутся настройки - см. конспект по обычному логированию и настройке log4j, принцип одинаковый.

# Пример конфигурации logback

Полноценная конфигурация логера делается через xml, а через application.properties можно только подправить некоторое поведение. Там довольно много, почитать можно например [тут](https://docs.spring.io/spring-boot/docs/2.1.8.RELEASE/reference/html/howto-logging.html).

В конфиге logback нет отдельных секций под свойства, аппендеры и т.д., как например в log4j. Здесь все добавляется непосредственно в конфигурацию:

```xml
<configuration scan="true" scanPeriod="15 seconds">
    <!-- Свойства -->
    <!-- Аппендеры -->
    <!-- Логгеры -->
</configuration>
```

Доступные атрибуты конфигурации:

* `scan="true" scanPeriod="15 seconds"` - пара атрибутов, которые позволяют активировать проверку, не изменилась ли конфигурация логгера. Если изменилась, то настройки обновятся. Может быть удобно, когда нужно например сделать дополнительные логгеры, не останавливая приложение.

  В scanPeriod по умолчанию значение идет в миллисекундах, поэтому лучше указывать тайм-юнит явно.

## Свойства

Объявление:

```xml
<property name="logs" value="D:\tmp\logs" />
```

Использование `${propName}`:

```xml
<file>${logs}\test.txt</file>
```

## Аппендеры

[Документация](https://logback.qos.ch/manual/appenders.html). Здесь показано объявление аппендера в общих чертах. Более детальное описание конкретных аппендеров в разделе [Конкретные аппендеры](#конкретные-аппендеры).

```xml
<appender name="STDOUT" class="ch.qos.logback.core.ConsoleAppender">
    <!-- Специфичные для конкретного аппендера элементы -->
    <encoder>
        <pattern>%msg%n</pattern>
    </encoder>
</appender>
```

Настройки:

* `name` - произвольное уникальное имя, которое используется в логгерах для указания, какой аппендер нужно использовать. К регистру не чувствительно.
* `class` - определяет тип аппендера. Некоторые возможные значения:
  * `ch.qos.logback.core.ConsoleAppender` - консольный аппендер.
  * `ch.qos.logback.core.FileAppender` - файловый аппендер.
  * `ch.qos.logback.core.rolling.RollingFileAppender` - файловый аппендер, который умеет писать в разные файлы в зависимости от условий.
  * `ch.qos.logback.classic.net.SocketAppender` - TODO
  * `ch.qos.logback.classic.net.SSLSocketAppender` - TODO
  * `ch.qos.logback.classic.net.server.ServerSocketAppender` - TODO
  * `ch.qos.logback.classic.net.SMTPAppender` - TODO
  * `ch.qos.logback.classic.db.DBAppender` - TODO
  * `ch.qos.logback.classic.net.SyslogAppender` - TODO
  * `ch.qos.logback.classic.sift.SiftingAppender` - TODO

## Логгеры

Корневой логгер:

```xml
<root level="debug">
    <appender-ref ref="konsole"/>
    <appender-ref ref="file"/>
</root>
```

Обычный логгер:

```xml
<logger name="com.example.demo" level="warn" additivity="false">
    <appender-ref ref="konsole"/>
</logger>
```

Все логгеры образуют иерархию в зависимости от своих имен. Например, для логгера com.example.demo.app выше по иерархии находится логгер com.example.demo. Корневой логгер находится на вершине иерархии.

Атрибуты:

* `name` - уникальное имя логгера, дается по имени пакета, в котором планируется использовать логгер с этими настройками. Более детально о том, как работает имя, см. конспект по обычному логированию. Вкратце: если затребовать логгер в пакете com.example.demo.some.thing.else, то логгер создастся с настройками как в com.example.demo, потому что это ближайшее совпавшее имя. Если описать в конфиге еще один логгер, например com.example.demo.some, тогда создастся с этими настройками, потому что это имя ближе.
* `level` - доступные значения: `off`, `all`, `trace`, `debug`, `info`, `warn`, `error`. Есть еще два значения: `inherited` и `null` (синонимы), в этом случае уровень берется такой же как у ближайшего по иерархии логгера.
* `additivity="true | false"` - определяет, пойдет ли сообщение вверх по иерархии логгеров или остановится на текущем логгере. Тут важно понимать, что этот параметр не блокирует работу логгеров, находящихся выше, а именно определяет, пойдет ли сообщение, полученное текущим логгером, также и в логгеры выше. Например, пусть у нас логгер `com.example.app` пишет в консоль, а root-логгер пишет в файл. Root-логгер для всех логгеров является "всевышним", поэтому если мы залогируем через com.example.app, то сообщение также уйдет и в root-логгер и появится не только в консоли, но и в файле. Если мы для com.example.app поставим additivity=false, то сообщение будет только в консоли, т.к. к root-логгеру оно уже не попадет.

# Паттерн записи лога

TODO

# Конкретные аппендеры

## Файловый аппендер

```xml
<appender name="file" class="ch.qos.logback.core.FileAppender">
    <file>${logs}\test.txt</file>
    <append>false</append>
    <encoder>
        <pattern>%-4relative [%thread] %-5level %logger{35} - %msg%n</pattern>
    </encoder>
</appender>
```









# Параметризация сообщений

В зависимости от настроек не все сообщения попадают в лог. Если в сообщении есть параметры, их много и они сложные (например, какие-нибудь манипуляции с датами), тогда мы потратим время на вычисление конечного сообщения, а оно в итоге может даже не пройти фильтры и не попасть в лог.

Поэтому имеет смысл параметризировать сообщения. В этом случае фреймворк выполняет фактическое вычисление сообщения только тогда, когда достоверно известно, что оно должно попасть в лог:

```java
public static void main(String[] args) {
    String name = "JohNy";
    logger.info("Сообщение уровня info: {}", name);
}
```













# Из старого

Чтобы настроить логер через xml, создаем файл `resources\logback-spring.xml`

UPD. logback-spring.xml это спринговая надстройка, а исходный файл конфига называется logback.xml

```xml
<?xml version="1.0" encoding="UTF-8"?>
<configuration>

    <property name="LOGS" value="./logs" />
    <property name="filename" value="ati-remake.log" />

    <appender name="Console" class="ch.qos.logback.core.ConsoleAppender">
        <layout class="ch.qos.logback.classic.PatternLayout">
            <Pattern>
                %black(%d{ISO8601}) %highlight(%-5level) [%blue(%t)] %yellow(%C{1.}): %msg%n%throwable
            </Pattern>
        </layout>
    </appender>

    <appender name="RollingFile" class="ch.qos.logback.core.rolling.RollingFileAppender">
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



