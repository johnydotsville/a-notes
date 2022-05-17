# JDBC

```java
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
```

## Резюме

* DriverManager.getConnection(), connection.close()
* Statement и PreparedStatement
* executeUpdate() и executeQuery()
* statement.getGeneratedKeys()
* connection.setAutoCommit(false), connection.rollback(), connection.commit()
* statement.addBatch(), statement.executeBatch()

## Соединение с БД

```java
Connection connection = null;
String connectionString = "jdbc:mysql://localhost:3306/sakila";
String username = "root";
String pass = "j123";
try
{
    connection = DriverManager.getConnection(connectionString, username, pass);  // <--
    System.out.println( "Connection established!" );
    // do some
}
catch (SQLException se) {
    se.printStackTrace();
}

try {
    connection.close();  // <--
}
catch (SQLException se) {
    se.printStackTrace();
}
```

## Выполнение запроса

Есть два класса для этих целей:

* Statement
* PreparedStatement

Честно говоря, причин использовать первый я не нагуглил. Через второй можно делать все то же самое, только больше и лучше. Поэтому про первый не буду писать, оставлю только упоминание.

И есть два метода запуска запроса на исполнение:

* executeUpdate() - для выполнения insert, update, delete, одним словом запросов, модифицирующих БД
* executeQuery() - для выполнения select

Выполним, например, запрос на вставку и устанавим значения параметров в нем:

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

## Транзакции

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

## Получение значений из выполненного запроса

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

## Обход результата

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

## Пакетное выполнение

```java
statement.addBatch();
statement.executeBatch();
```

```java
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