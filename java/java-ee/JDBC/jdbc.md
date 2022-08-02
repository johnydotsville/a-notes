Здесь я попробовал собрать практические примеры использования JDBC и сопутствующую необходимую матчасть, стараясь не грузить лишними деталями, а взять только необходимое.

# Резюме

* Интерфейс Driver и реализации
* Класс DriverManager vs. интерфейса DataSource (с пулом соединений)
* DriverManager.getConnection(), connection.close()
* Statement, PreparedStatement и CallableStatement
* executeUpdate(), executeQuery() и просто execute()
* statement.getGeneratedKeys() или получение значений, сгенерированных БД'шкой
* connection.setAutoCommit(false), connection.rollback(), connection.commit()
* statement.addBatch(), statement.executeBatch() или пакетное выполнение

# Интерфейс java.sql.Driver

Чтобы работать с БД, нам нужна конкретная реализация интерфейса java.sql.Driver. Например:

```xml
<dependency>
    <groupId>org.postgresql</groupId>
    <artifactId>postgresql</artifactId>
    <version>42.4.0</version>
</dependency>
```

Драйвер поставляется в jar. В корне архива, кроме непосредственно кода с реализацией, есть папка *META-INF\services*, а в ней файл *java.sql.Driver*. В этом файле единственная строчка - с именем класса драйвера, например, *org.postgresql.Driver*

> Название директории, файла, и его содержимое - не просто так, а следование технологии SPI (Service Provider Interface). Если очень кратко, в основе этой технологии два понятия - *сервис* (*service*) и *поставщик сервиса* (*service provider*). Сервис представлен в виде интерфейса, а поставщик - это конкретная реализация этого интерфейса. В клиентском коде используется интерфейс, а конкретную реализацию можно обнаружить динамически во время выполнения как раз за счет договоренностей о структуре .jar-архива.
>
> В нашем случае интерфейс Driver объявляет сервис (*service*), а класс org.postgresql.Driver является реализацией этого сервиса (*service provider*).

Т.о., когда мы просим *DriverManager* создать соединение, он способен обнаружить все доступные драйвера и последовательно попросить каждый их них создать соединение. Мы указываем конкретный драйвер в url, в части jdbc:postgresql \ jdbc:mysql, поэтому не всякий обнаруженный драйвер откликнется на просьбу создать соединение, а только интересующий нас

# DriverManager vs DataSource

Вся работа начинается с получения соединения. А получить его можно двумя способами: через класс DriverManager и через какую-нибудь реализацию интерфейса DataSource.

DriverManager (java.sql) - это самый базовый ***класс*** для создания соединений с БД. У него такие характеристики:

* Требует непосредственного указания параметров соединения с БД (url, username, password, класс драйвера)
* При затребовании соединения каждый раз честно создает новое соединение с нуля, следовательно, операция получается весьма тяжеловесной
* При закрытии соединения честно уничтожает его, а значит воспользоваться им повторно становится невозможно

DataSource (java**x**.sql) - это ***интерфейс*** из JavaEE. Непосредственно в себе, судя по исходникам, никаких реализованных методов не содержит. Имеет такие характеристики:

* Конкретные реализации обычно реализуют connection pool, т.е. заранее создается некоторое количество соединений и когда у ds'а требуют соединение, он возвращает одно из пула. За счет того, что не приходится создавать соединение с нуля, клиент быстрее получает свое соединение.

  Примечательно, что соединение, которое возвращает ds, обычно является оберткой над java.sql.Connection и когда на таком "соединении" вызывается .close(), то вместо реального уничтожения соединения, оно возвращается в пул.
  
* Может получать информацию о соединении с БД через JNDI

Т.о. использование DataSource предпочтительнее, чем напрямую DriverManager как минимум за счет пулинга соединений. Контейнеры обычно умеют обнаруживать реализации DS или предоставляют свои реализации. Под капотом DS, вероятно, использует DriverManager для создания соединений (но это не точно)

Базовое правило для работы с соединениями - требовать их непосредственно в момент, когда они нужны, и закрывать сразу же, как только становятся не нужны.

# Соединение с БД

## Через DriverManager

В этом случае никаких дополнительных зависимостей не требуется.

```java
import java.sql.DriverManager;
import java.sql.Connection;
import java.sql.SQLException;
...
Connection connection = null;
String connectionString = "jdbc:mysql://localhost:3306/sakila";
String username = "root";
String pass = "j123";
try
{
    connection = DriverManager.getConnection(connectionString, username, pass);  // <--
    System.out.println( "Connection established!" );
    // Какой-то код из примеров ниже
}
catch (SQLException se) {
    se.printStackTrace();
}

try {
    connection.close();  // <-- Не забываем обязательно закрывать
    // Или пользуемся синтаксисом try с ресурсами
}
catch (SQLException se) {
    se.printStackTrace();
}
```

## Через DataSource (пул соединений)

Поскольку DataSource - это интерфейс, нам понадобится конкретная реализация. Например, HikariCP:

```xml
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>5.0.1</version>
</dependency>
```

Пример базовой конфигурации:

```java
import java.sql.Connection;
import java.sql.SQLException;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class ConnectionPool {
    private static HikariConfig config = new HikariConfig();
    private static HikariDataSource ds;

    static {
        config.setDriverClassName("org.postgresql.Driver");
        config.setJdbcUrl("jdbc:postgresql://localhost:5432/dvdrental");
        config.setUsername("postgres");
        config.setPassword("j123");

        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

        ds = new HikariDataSource(config);
    }

    private ConnectionPool() { }

    public static Connection getConnection()
            throws SQLException {
        return ds.getConnection();
    }
}
```

Пока что я в настройках не разбирался, но этот пример уже позволяет работать с пулом соединений, что само по себе уже хорошо.

# Выполнение запроса

Типичный процесс работы с БД:

```
connection -> "statement" -> resultset -> getdata
```

Для выполнения запроса есть три класса:

* Statement
* PreparedStatement
* CallableStatement

Честно говоря, причин использовать первый я не нагуглил. Через второй можно делать все то же самое, только больше и лучше. Поэтому про первый не буду писать, оставлю только упоминание. Третий используется для вызова хранимых процедур, про него тоже пока нет смысла писать.

И есть два метода запуска запроса на исполнение:

* executeUpdate() - для выполнения insert, update, delete, одним словом модификация БД. Возвращает int с количеством записей, которые затронул запрос.
* executeQuery() - для выполнения select. Возвращает ResultSet с результатами запроса.
* execute() - для выполнения любого запроса, когда неизвестно, модифицирующий он или нет. Возвращает boolean - вернул ли запрос какие-то данные. Если вернул, на стейтменте можно с помощью метода *.getResultSet()* получить результат.

Выполним, например, запрос на вставку и установим значения параметров в нем:

```java
try {
    PreparedStatement prep = connection.prepareStatement("insert into actor(actor_rating, first_name, last_name) values (?, ?, ?)");  // <-- Шаблон
    prep.setInt(1, actorRating);  // <-- Заполняем параметры
    prep.setString(2, firstName);
    prep.setString(3, lastName);

    int countInserted = prep.executeUpdate();  // <-- Выполняем запрос
}
catch (SQLException se) {
    System.out.println("Failed to insert actor");
    se.printStackTrace();
}

// Или использовать try с ресурсами
try (PreparedStatement prep = connection.prepareStatement(SOME_QUERY)) {
    // do some
}
```

Демонстрация execute():

```java
PreparedStatement prep = conn.prepareStatement("select * from actor where actor_id > ?");
prep.setInt(1, 199);

boolean hasResult = prep.execute();

if (hasResult) {
    ResultSet result = prep.getResultSet();
    ...
```

# Обход результата

```java
try {
    PreparedStatement prep = connection.prepareStatement("select * from actor limit ?");  // <--
    prep.setInt(1, 100);
    ResultSet result = prep.executeQuery();  // <--

    while (result.next()) {  // <--
        Actor actor = new Actor();
        actor.setId(result.getInt("actor_id"));  // <--
        actor.setFirstName(result.getString("first_name"));
        actor.setLastName(result.getString("last_name"));

        actors.add(actor);
    }
    result.close();
    prep.close();
}
catch (SQLException se) {
    System.out.println("Error occurred during gathering actors info");
    se.printStackTrace();
}

for (Actor actor : actors) {
    System.out.println(actor);
}
```

Если в запросе поля выбирали через *, то их порядок в результате не гарантируется и обращаться к полю через номер может быть опасно. Но оно работает быстрее, чем по имени столбца (хотя это зависит от драйвера, у постгри например разница минимальная). Чтобы гарантировать порядок выборки столбцов, нужно их явно писать в запросе.

Когда мы получаем ResultSet на стейтменте, то этот ResultSet с ним ассоциирован и если мы повторно выполним стейтмент, то ResultSet закроется и вернется новый. Так что нужно сначала полностью обработать результаты запроса, прежде чем выполнять стейтмент снова. Каждый объект стейтмент - это не "шаблон" запроса, а именно конкретный экземпляр запроса. И поэтому если нам нужно, например, выполнить одновременно два одинаковых запроса, мы должны создать два объекта стейтмента с одинаковым sql.

Все объекты, вроде стейтментов и ResultSet принятно закрывать явно. Хотя закрытие, например, соединения, приводит также и к автоматическому закрытию всех сопутствующих объектов, хорошим тоном является именно явное закрытие.

## Методы getXXX

Для многих типов есть свой метод get, например, getInt, getString и т.д. Но можно воспользоваться общим методом getObject() и потом самостоятельно привести данные к нужному типу.

## Если в столбце null

Если в столбце null, тогда он, похоже, при попытке достать значение через getXXX метод, приводится к какому-то дефолтному значению. Например, если в integer столбце таблицы будет null, то при `actor.setAge( getInt("age"));` в поле age объекта actor просто окажется 0, исключение не возникнет.

Поэтому, чтобы узнать, был ли там null или нет, у нас два пути. Представим, что в таблице есть столбец с названием nulltest типа int:

* Используем .getObject() и проверяем на null:

  ```java
  Object nulltest = result.getObject("nulltest");
  if (nulltest != null) {
      actor.setNulltest((int)nulltest);
  }
  ```

* Пользуемся методом wasNull() ResultSet'а:

  ```java
  int nulltest = result.getInt("nulltest");
  if (!result.wasNull()) {
      actor.setNulltest(nulltest);
  } else {
      System.out.println("nulltest был равен null");
}
  ```
  
  Проверять надо сразу же после извлечения значения столбца.

## Позиционирование

По умолчанию результат получается в виде курсора с движением только вперед. Но если нужно, мы можем указать при создании стейтмента дополнительные параметры и у нас появится возможность двигаться вперед\назад, на конкретную запись и прочие варианты:

```java
PreparedStatement prep = conn.prepareStatement("select * from actor where actor_id > ?",
                                               ResultSet.TYPE_SCROLL_SENSITIVE,
                                               ResultSet.CONCUR_UPDATABLE);
...
ResultSet result = prep.executeQuery();
result
    .first()
    .last()
    .beforeFirst()
    .beforeLast()
    .absolute(...)
    .relative(...)
```

Об этом лучше почитать отдельно. Есть в разделе JDBC книги JavaEE in Nutshell например.

# Транзакции

По умолчанию транзакция применяется после каждого выполнения запроса. Чтобы управлять ими вручную, нужно отключить автокоммит:

```java
try (Connection conn = getConnection();
     PreparedStatement stmt = conn.prepareStatement(INSERT_ORDER);

    conn.setAutoCommit(false);  // <-- Отключаем автокоммит
    try {
        stmt.setInt(1, StudentOrderStatus.START.ordinal());        
        stmt.executeUpdate();  // <-- Выполняем запрос
        saveChildren(conn, order, savedOrderId);  // Здесь еще какие-то модифицирующие БД запросы
    } catch (SQLException ex) {
        conn.rollback();  // <-- В случае ошибки все откатываем
        throw ex;
    }
    conn.commit();  // <-- Если все нормально, коммитим

    return savedOrderId;
} catch (SQLException ex) {
    logger.error(ex.getMessage(), ex);
    throw new DaoException(ex);
}
```

Транзакции имеют разные уровни изоляции. Задать уровень изоляции можно на объекте соединения:

```java
conn.setTransactionIsolation(TRANSACTION_READ_COMMITTED);
// Возможные значения
TRANSACTION_NONE
TRANSACTION_READ_UNCOMMITTED
TRANSACTION_READ_COMMITTED
TRANSACTION_REPEATABLE_READ
TRANSACTION_SERIALIZABLE
```

# Получение значений, заполненных БД'шкой

Типичный сценарий: есть таблица с автогенерируемым полем, например, ключом. После вставки новой записи надо получить этот ключ, чтобы и программа о нем знала.

```java
try (Connection conn = getConnection();
     PreparedStatement stmt = conn.prepareStatement(
         INSERT_ORDER,
         new String[] {"student_order_id"})) {    // <--
    // Это массив колонок, которые нужно вернуть после вставки для вставленных записей
	try {
		stmt.setInt(1, StudentOrderStatus.START.ordinal());
		// Заполняем остальные поля
		stmt.executeUpdate();

		ResultSet result = stmt.getGeneratedKeys();  // <-- 
		if (result.next()) {
			savedOrderId = result.getLong("student_order_id");  // <-- Имена как в массиве
		}
		result.close();

		saveChildren(conn, order, savedOrderId);
	} catch (SQLException ex) {
		...
	}
...
```

# Пакетное выполнение

```java
stmt.addBatch();
stmt.executeBatch();
```

```java
conn.setAutoCommit(false);
try (PreparedStatement stmt = conn.prepareStatement(INSERT_CHILD)) {
    int batchMaxSize = 10_000;
    int batchCurrentSize = 0;
    // Пакетная обработка. Не сразу будем выполнять вставку, а накопим "пачку" вставок
    // со всеми детьми разом
    for (Child child : order.getChildren()) {
        setParamsForChild(stmt, child, orderId);
        //stmt.executeUpdate(); Вставлять запись в каждом шаге цикла - это медленно
        // Если вдруг в пачке мб ну очень много записей, организуем "буфер"
        stmt.addBatch();  // <-- Начинаем формировать пачку
        batchCurrentSize++;
        if (batchCurrentSize >= batchCurrentSize) {
            stmt.executeBatch();  // <-- Выполняем пачку
            batchCurrentSize = 0;
        }
    }
    if (batchCurrentSize > 0) {
        stmt.executeBatch();
    }
}
```

*INSERT_CHILD* здесь - это константа с запросом. В методе *setParamsForChild* происходит не более чем вызов *setString, setInt* на объекте statement. Основные посылы:

* Выполнять вставку на каждом шаге цикла - это медленно. Выгоднее сделать сразу много вставок за раз.
* Есть некоторый предел количества операций, при котором пакетное выполнение может начать работать медленно. Например, если засунуть в пачку 100_000 запросов это будет работать плохо. Поэтому размер пачки можно ограничить. Выяснять его, вероятно, придется опытным путем.

Пакетно можно выполнить только запросы, которые возвращают количество затронутых записей, т.е. insert, update, delete, create, drop. Попытка выполнить select запрос даст исключение SQLException.

Запросы выполняются в том порядке, в котором их добавили в пачку. Собственно, после выполнения, метод executeBatch() возвращает массив, где соответственно порядку запросов лежат количества затронутых записей.

Если при выполнении какой-то запрос вызывает ошибку, выполнение прерывается и выбрасывается BatchUpdateException(). Внутри есть массив с затронутыми записями для запросов, которые выполнились нормально перед возникновением ошибки.







