В джаве есть полезный класс Properties, с помощью которого удобно читать конфигурации из файла:

```java
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Config {
    public static final String DB_URL = "db.url";
    public static final String DB_LOGIN = "db.login";
    public static final String DB_PASSWORD = "db.password";

    private static Properties properties = new Properties();  // <--

    public synchronized static String getProperty(String name) {
        if (properties.isEmpty()) {  // <--
            try (InputStream stream = Config.class.getClassLoader()
                    .getResourceAsStream("dao.properties")) {
                properties.load(stream);  // <--
            } catch (IOException ex) {
                ex.printStackTrace();
                throw new RuntimeException(ex);
            }
        }
        return properties.getProperty(name);  // <--
    }
}
```

Пример файла конфигурации:

```
db.url=jdbc:postgresql://localhost:5432/jc_student
db.login=postgres
db.password=j123
```

Основной принцип: в данном случае файл конфига лежал в папке resources мавен-проекта. Поэтому после сборки он оказался в папке со скомпилированными классами. 

Чтобы до него добраться, мы использовали конструкцию `Config.class.getClassLoader()`. Как это работает? Каждый класс загружается в память загрузчиком классов. А у загрузчика классов есть метод, который позволяет загрузить ресурс как поток. Поскольку классы и ресурсы лежат в итоге в одной папке, мы получили через рефлексию загрузчик, который загрузил в память наш класс *Config*, и он из той же директории считал файл с конфигурацией.

Выглядит конечно странновато если честно, но оно работает, так что до поры до времени оставлю так.