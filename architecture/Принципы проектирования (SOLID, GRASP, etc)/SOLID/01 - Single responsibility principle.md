# Терминология

* Модуль - это любой код, имеющий границы. Размер этих границ физически не ограничен. Это может быть метод, класс, пакет, сервис и т.д. - все можно назвать модулем.
* Бизнес-правила (логика) - это то, как модули взаимодействуют друг с другом и внешним миром.
* Причина для изменений - это обновление бизнес-правил, при котором требуется вносить изменения в модули, чтобы они соответствовали этим новым бизнес-правилам.

# Формулировка принципа

SRP, или "Принцип единственной ответственности", имеет две формулировки, обе от Роберта Мартина. Обе в целом об одном и том же, но немного разными словами и, по личным ощущениям, первая имеет более технический характер и хорошо подходит для классов, формирующих утилиты, а вторая - для классов, формирующих приложения, автоматизирующие какой-то бизнес.

## Формулировка 1

> Код должен иметь одну и только одну причину для изменения (ориг. "A module should have one, and only one, reason to change")

Именно так, только одну причину для изменения, а не "выполнять только одну функцию", потому что это не одно и то же. Выполнение "только одной функции" больше относится к принципам clean code и означает обычно, что если метод очень большой, то его вероятно можно разделить на несколько методов поменьше. 

Но даже если все методы максимально маленькие и каждый делает только одну вещь, класс в целом может на основе этих методов выполнять несколько бизнес-задач, относящихся к разным "акторам". И вот тут вступает в дело вторая формулировка.

Кроме того, "единственная причина для изменения" может относиться и к чисто техническим причинам, не касающимся логики. Например, в классе А используется логирование и логгер создается явно прямо в классе. Пусть он пишет лог в файл, а нам требуется через какое-то время записывать лог в БД. Придется переписывать класс А, чтобы заменить тип логгера на другой, хотя бизнес-логика самого класса не изменилась. Тут конечно имеется ввиду самописный логгер, т-к библиотечные гибко настраиваются через конфиги и заменять их не требуется. Но просто как пример, что "единственная причина для изменения" может нарушаться не только за счет слияния концептуально разной бизнес-логики.

## Формулировка 2

> Код должен работать на одного и только одного актора (ориг. "A module should be responsible to one, and only one, actor").

Под актором, насколько я понял, имеются ввиду бизнес-группы пользователей ("бухгалтерия", "отдел менеджмента" и т.д.). В книге "Чистая архитектура" есть пример:

```java
class Employee {
    calculatePay();  // Зависит от желаний бухгалтерии
    reportHours();  // Зависит от желаний менеджеров
    save();  // Зависит от желаний администратора БД
}
```

Этот класс связал трех акторов друг с другом. Если бухгалтерия решила по-другому считать оплату, надо менять класс. Если менеджеры решили по-другому учитывать рабочие часы, надо менять класс. Если администратор БД решил поменять СУБД, надо менять класс. То есть появляется ТРИ причины для изменения класса, хотя согласно принципу должна быть только одна.

Кроме того, совмещение кода, относящегося к разным областям бизнеса чревато появлением случайных зависимостей:

```java
class Employee {
    private calculateHoursWorked() { ... }  // Общий алгоритм вычисления отработанных часов
    
    public int calculatePay() {  // область бухгалтерии
        int hWorked = calculateHoursWorked();
        int payForHour = 10;
        return hWorked * payForHour;
    }
    
    public int reportHours() {  // область менеджмента
        return calculateHoursWorked();
    }
}
```

Видно, что обе области полагаются на общий метод расчета отработанных часов. В какой-то момент, менеджеры могут захотеть учитывать часы по-другому, например, брать только отработанные в офисе, а не из дома, или исключать время, проведенное на больничном, и т.д. При программировании этих изменений, если программист не заметит, что метод calculateHoursWorked используется также и в области бухгалтерии и перепишет его под требования менеджеров, у бухгалтеров будет косяк в расчетах.

## Резюме

* "Ответственность" - это "причина для изменения".
* При SRP всегда есть два вида элементов - "работяги" и "менеджеры". Работяги - это элементы, которые выполняют конкретные действия. Менеджеры - это фасады, которые эти конкретные действия собирают в единый алгоритм, выполняющий поставленную задачу. Из-за этого может казаться, что менеджер имеет много ответственностей, но это не так - его единственная ответственность - объединить работяг. Поэтому важно вовремя увидеть менеджера и не пытаться его дробить.
* Проводить границы между модулями надо так, чтобы при изменении бизнес-правил изменения нужно было вносить в как можно меньшее количество модулей, в идеале - только в один.
* "Разбивать проще, чем склеивать". Ошибаться в определении границ ответственности лучше в б*о*льшую сторону. Т.е. лучше  ситуация, когда несколько разных ответственностей слеплены в один модуль и надо их разделить, потому что это проще, чем искать и объединять множество разрозненных мелких действий, которые на самом деле относятся к одной ответственности.
* Реализовать SRP позволяют несколько паттернов, например такие как фасад, прокси, выделение класса, стратегия.

# Как приблизиться к SRP?

В общем случае проблема классов-"мастеров на все руки" обычно решается так:

* Делегирование части обязанностей другим классам.

  Вводим интерфейс и пусть делегирующий класс им пользуется. В случае изменения требований к реализации, класс-пользователь трогать не придется, изменению подлежит только класс-исполнитель.

* Полный вынос обязанности в другой класс и соединения с исходным классом каким-либо иным образом.

Т.е. все сводится к обдуманному перемещению функций в разные классы. Обдуманность заключается не только в чисто техническом разделении функционала, но и в логическом, "бизнесовом" его разделении.

## Пример полного выноса обязанности

Валидация продукта. Пусть у нас есть простой класс продукта и какое-нибудь начальное требование к его валидности. Например, чтобы цена была больше нуля:

```java
@Getter @Setter
public class Product {
    private String name;
    private int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }

    public boolean isValid() {
        return price > 0;
    }
}
```

```java
var product = new Product("Cake", 350);
boolean valid = product.isValid();
System.out.println(valid);
```

Размещая код валидации в самом классе продукта, мы нарушаем SRP, потому что у нас появляется две причины для изменения класса:

1. Изменение структуры продукта - добавление или изменение полей.
2. Изменение условий валидации. Например, "подарочный" продукт должен иметь цену 0. Или наоборот цена не должна быть меньше какого-то значения. К тому же, если валидация станет сложнее, например, добавятся разные требования к проверке названия, то метод может стать к тому же и очень сложным.

В итоге получается, что выгоднее отделить валидацию от продукта.

```java
@Getter @Setter
public class Product {
    private String name;
    private int price;

    public Product(String name, int price) {
        this.name = name;
        this.price = price;
    }
    
    // Убрали метод валидации
}
```

Определяем интерфейс для валидаторов и пишем несколько реализаций:

```java
public interface ProductValidator {
    boolean isValid(Product product);
}
```

```java
public class DefaultProductValidator implements ProductValidator {
    @Override
    public boolean isValid(Product product) {
        return product.getPrice() > 0;
    }
}
```

```java
public class GiftProductValidator implements ProductValidator {
    @Override
    public boolean isValid(Product product) {
        return product.getPrice() == 0;
    }
}
```

```java
public class TitleProductValidator implements ProductValidator {
    @Override
    public boolean isValid(Product product) {
        return Character.isUpperCase(product.getName().charAt(0));
    }
}
```

Для удобного комбинирования валидаторов воспользуемся шаблоном Composite и создадим валидатор, образующий цепочку из нескольких валидаторов.

```java
public interface ProductValidationChain {
    boolean isValid(Product product);
    void addValidator(ProductValidator validator);
    void removeValidator(ProductValidator validator);
}
```

```java
public class SimpleProductValidationChain implements ProductValidationChain {
    private final Set<ProductValidator> chain;

    public SimpleProductValidationChain() {
        chain = new LinkedHashSet<>();
    }

    public SimpleProductValidationChain(Collection<ProductValidator> validators) {
        chain = new LinkedHashSet<>(validators.size());
        chain.addAll(validators);
    }

    public void addValidator(ProductValidator validator) {
        chain.add(validator);
    }

    public void removeValidator(ProductValidator validator) {
        chain.remove(validator);
    }

    @Override
    public boolean isValid(Product product) {
        System.out.println(chain.size());
        return chain.stream().allMatch(v -> v.isValid(product));
    }
}
```

Проведем валидацию продукта новым способом:

```java
var product = new Product("Cake", 350);

var giftValidator = new GiftProductValidator();
var titleValidator = new TitleProductValidator();
// ProductValidator validationChain = new SimpleProductValidationChain(
//     List.of(giftValidator, titleValidator));
ProductValidationChain chain = new SimpleProductValidationChain();
chain.addValidator(giftValidator);
chain.addValidator(titleValidator);
chain.removeValidator(giftValidator);
boolean valid = chain.isValid(product);
```

Можно было бы улучшить, чтобы возвращался список проблем, но это уже другая история. Важно то, что теперь функциональность разнесена и изменение условий валидации не затрагивают класс Product.

## Пример делегирования обязанностей

Из книги "Adaptive Code via C#" (2017, 2-е изд), автор Gary McLean Hall. Утилита "Обработчик акций".

Рассмотрим на примере класса, обрабатывающего акции. Он выполняет разом три функции - читает данные из файла, форматирует их и вставляет в базу данных. Этот класс нарушает SRP не потому, что выполняет сразу три функции, а потому что *содержит реализацию всех трех функций* разом. То есть при изменении деталей любой из функций потребуется править код этого класса:

```c#
public class TradeProcessor
{
    public void ProcessTrades(Stream stream)
    {
        // <-- Читаем данные из файла
        var lines = new List<string>();
        using (var reader = new StreamReader(stream))
        {
            string line;
            while((line = reader.ReadLine()) != null)
            {
                lines.Add(line);
            }
        }

        var trades = new List<TradeRecord>();

        // <-- Анализируем данные, форматируем
        var lineCount = 1;
        foreach (var line in lines)
        {
            var fields = line.Split(new char[] { ',' });

            if(fields.Length != 3)
            {
                Console.WriteLine("WARN: Line {0} malformed. Only {1} field(s) found.", lineCount, fields.Length);
                continue;
            }

            if(fields[0].Length != 6)
            {
                Console.WriteLine("WARN: Trade currencies on line {0} malformed: '{1}'", lineCount, fields[0]);
                continue;
            }

            int tradeAmount;
            if (!int.TryParse(fields[1], out tradeAmount))
            {
                Console.WriteLine("WARN: Trade amount on line {0} not a valid integer: '{1}'", lineCount, fields[1]);
            }

            decimal tradePrice;
            if (!decimal.TryParse(fields[2], out tradePrice))
            {
                Console.WriteLine("WARN: Trade price on line {0} not a valid decimal: '{1}'", lineCount, fields[2]);
            }

            var sourceCurrencyCode = fields[0].Substring(0, 3);
            var destinationCurrencyCode = fields[0].Substring(3, 3);

            var trade = new TradeRecord
            {
                SourceCurrency = sourceCurrencyCode,
                DestinationCurrency = destinationCurrencyCode,
                Lots = tradeAmount / LotSize,
                Price = tradePrice
            };

            trades.Add(trade);

            lineCount++;
        }

        // <-- Записываем информацию в БД
        using (var connection = new System.Data.SqlClient
               .SqlConnection("Data Source=(local);Initial Catalog=TradeDatabase;Integrated Security=True;"))
        {
            connection.Open();
            using(var transaction = connection.BeginTransaction())
            {
                foreach(var trade in trades)
                {
                    var command = connection.CreateCommand();
                    command.Transaction = transaction;
                    command.CommandType = System.Data.CommandType.StoredProcedure;
                    command.CommandText = "dbo.insert_trade";
                    command.Parameters.AddWithValue("@sourceCurrency", trade.SourceCurrency);
                    command.Parameters.AddWithValue("@destinationCurrency", trade.DestinationCurrency);
                    command.Parameters.AddWithValue("@lots", trade.Lots);
                    command.Parameters.AddWithValue("@price", trade.Price);

                    command.ExecuteNonQuery();
                }

                transaction.Commit();
            }
            connection.Close();
        }

        Console.WriteLine("INFO: {0} trades processed", trades.Count);
    }

    private static float LotSize = 100000f;
}
```

Исправить ситуацию можно, если вынести код этих трех функций в отдельные классы, а в исходном классе, `TradeProcessor`, оставить только интерфейсы и запросить реализацию извне. В этом случае, если например понадобится считывать данные из другого источника - не из файла, а из БД - то TradeProcessor править не придется, потому что он не будет ответственен за реализацию этого функционала. Общая схема работы "считать-отформатировать-записать" останется неизменной. 

Таким образом, можно сказать, что TradeProcessor ответственен не за "чтение", "форматирование", "запись", а за *организацию этих трех функций в единый рабочий процесс*. И вот эта организация и становится его единственной ответственностью. Единственной причиной изменения TradeProcessor будет изменение в этом процессе, если к примеру после форматирования нужно будет произвести еще какие-нибудь вычисления.

### Подготовка к рефакторингу

Начать можно с разделения этого монолитного метода на несколько специализированных, оставив для начала все получившиеся методы в исходном классе:

```c#
public class TradeProcessor
{
    // Этот метод будет выделен в абстракцию ITradeDataProvider
    private IEnumerable<string> ReadTradeData(Stream stream)
    {
        var tradeData = new List<string>();
        using (var reader = new StreamReader(stream))
        {
            string line;
            while ((line = reader.ReadLine()) != null)
            {
                tradeData.Add(line);
            }
        }
        return tradeData;
    }

    // Этот метод - в абстракцию ITradeParser
    private IEnumerable<TradeRecord> ParseTrades(IEnumerable<string> tradeData)
    {
        var trades = new List<TradeRecord>();
        var lineCount = 1;
        foreach (var line in tradeData)
        {
            var fields = line.Split(new char[] { ',' });

            // Валидацию тоже делегируем, максимально дробя функционал на самостоятельные части
            if (!ValidateTradeData(fields, lineCount))
            {
                continue;
            }

            // И форматирование тоже
            var trade = MapTradeDataToTradeRecord(fields);

            trades.Add(trade);

            lineCount++;
        }

        return trades;
    }

    // Будущая абстракция ITradeValidator
    private bool ValidateTradeData(string[] fields, int currentLine)
    {
        if (fields.Length != 3)
        {
            LogMessage("WARN: Line {0} malformed. Only {1} field(s) found.", currentLine, fields.Length);
            return false;
        }

        if (fields[0].Length != 6)
        {
            LogMessage("WARN: Trade currencies on line {0} malformed: '{1}'", currentLine, fields[0]);
            return false;
        }

        int tradeAmount;
        if (!int.TryParse(fields[1], out tradeAmount))
        {
            LogMessage("WARN: Trade amount on line {0} not a valid integer: '{1}'", currentLine, fields[1]);
            return false;
        }

        decimal tradePrice;
        if (!decimal.TryParse(fields[2], out tradePrice))
        {
            LogMessage("WARN: Trade price on line {0} not a valid decimal: '{1}'", currentLine, fields[2]);
            return false;
        }

        return true;
    }

    // ILogger позволит в высокоуровневом классе отвязаться от консоли
    private void LogMessage(string message, params object[] args)
    {
        Console.WriteLine(message, args);
    }

    // Сопоставлением полей займется абстракция ITradeMapper
    private TradeRecord MapTradeDataToTradeRecord(string[] fields)
    {
        var sourceCurrencyCode = fields[0].Substring(0, 3);
        var destinationCurrencyCode = fields[0].Substring(3, 3);
        var tradeAmount = int.Parse(fields[1]);
        var tradePrice = decimal.Parse(fields[2]);

        var trade = new TradeRecord
        {
            SourceCurrency = sourceCurrencyCode,
            DestinationCurrency = destinationCurrencyCode,
            Lots = tradeAmount / LotSize,
            Price = tradePrice
        };

        return trade;
    }

    // Персистентность нам обеспечит абстракция ITradeStorage
    private void StoreTrades(IEnumerable<TradeRecord> trades)
    {
        using (var connection = new System.Data.SqlClient
               .SqlConnection("Data Source=(local);Initial Catalog=TradeDatabase;Integrated Security=True;"))
        {
            connection.Open();
            using (var transaction = connection.BeginTransaction())
            {
                foreach (var trade in trades)
                {
                    var command = connection.CreateCommand();
                    command.Transaction = transaction;
                    command.CommandType = System.Data.CommandType.StoredProcedure;
                    command.CommandText = "dbo.insert_trade";
                    command.Parameters.AddWithValue("@sourceCurrency", trade.SourceCurrency);
                    command.Parameters.AddWithValue("@destinationCurrency", trade.DestinationCurrency);
                    command.Parameters.AddWithValue("@lots", trade.Lots);
                    command.Parameters.AddWithValue("@price", trade.Price);

                    command.ExecuteNonQuery();
                }

                transaction.Commit();
            }
            connection.Close();
        }

        LogMessage("INFO: {0} trades processed", trades.Count());
    }

    // В итоге весь процесс обработки сведется к вызову всего трех высокоуровневых команд,
    // которые и останутся в классе TradeProcessor
    public void ProcessTrades(Stream stream)
    {
        var lines = ReadTradeData(stream);
        var trades = ParseTrades(lines);
        StoreTrades(trades);
    }

    private static float LotSize = 100000f;
}
```

```c#
// Тип для отдельной биржевой записи
public class TradeRecord
{
    public string DestinationCurrency;
    public float Lots;
    public decimal Price;
    public string SourceCurrency;
}
```

### Рефакторинг к абстракциям

Обозначим абстракции с помощью интерфейсов и напишем их реализации

#### ITradeDataProvider

```c#
public interface ITradeDataProvider
{
    IEnumerable<string> GetTradeData();
}
```

```c#
public class StreamTradeDataProvider : ITradeDataProvider
{
    private readonly Stream stream;
    
    public StreamTradeDataProvider(Stream stream)
    {
        this.stream = stream;
    }

    public IEnumerable<string> GetTradeData()
    {
        var tradeData = new List<string>();
        using (var reader = new StreamReader(stream))
        {
            string line;
            while ((line = reader.ReadLine()) != null)
            {
                tradeData.Add(line);
            }
        }
        return tradeData;
    }
}
```

#### ITradeParser

```c#
public interface ITradeParser
{
    IEnumerable<TradeRecord> Parse(IEnumerable<string> tradeData);
}
```

```c#
public class SimpleTradeParser : ITradeParser
{
    private readonly ITradeValidator tradeValidator;
    private readonly ITradeMapper tradeMapper;

    public SimpleTradeParser(ITradeValidator tradeValidator, ITradeMapper tradeMapper)
    {
        this.tradeValidator = tradeValidator;
        this.tradeMapper = tradeMapper;
    }

    public IEnumerable<TradeRecord> Parse(IEnumerable<string> tradeData)
    {
        var trades = new List<TradeRecord>();
        var lineCount = 1;
        foreach (var line in tradeData)
        {
            var fields = line.Split(new char[] { ',' });

            if (!tradeValidator.Validate(fields))
            {
                continue;
            }

            var trade = tradeMapper.Map(fields);

            trades.Add(trade);

            lineCount++;
        }

        return trades;
    }
}
```

#### ITradeValidator

```c#
public interface ITradeValidator
{
    bool Validate(string[] tradeData);
}
```

```c#
public class SimpleTradeValidator : ITradeValidator
{
    private readonly ILogger logger;

    public SimpleTradeValidator(ILogger logger)
    {
        this.logger = logger;
    }

    public bool Validate(string[] tradeData)
    {
        if (tradeData.Length != 3)
        {
            logger.LogWarning("Line malformed. Only {0} field(s) found.", tradeData.Length);
            return false;
        }

        if (tradeData[0].Length != 6)
        {
            logger.LogWarning("Trade currencies malformed: '{0}'", tradeData[0]);
            return false;
        }

        int tradeAmount;
        if (!int.TryParse(tradeData[1], out tradeAmount))
        {
            logger.LogWarning("Trade not a valid integer: '{0}'", tradeData[1]);
            return false;
        }

        decimal tradePrice;
        if (!decimal.TryParse(tradeData[2], out tradePrice))
        {
            logger.LogWarning("Trade price not a valid decimal: '{0}'", tradeData[2]);
            return false;
        }

        return true;
    }
}
```

#### ITradeMapper

```c#
public interface ITradeMapper
{
    TradeRecord Map(string[] fields);
}
```

```c#
public class SimpleTradeMapper : ITradeMapper
{
    private static float LotSize = 100000f;
    
    public TradeRecord Map(string[] fields)
    {
        var sourceCurrencyCode = fields[0].Substring(0, 3);
        var destinationCurrencyCode = fields[0].Substring(3, 3);
        var tradeAmount = int.Parse(fields[1]);
        var tradePrice = decimal.Parse(fields[2]);

        var trade = new TradeRecord
        {
            SourceCurrency = sourceCurrencyCode,
            DestinationCurrency = destinationCurrencyCode,
            Lots = tradeAmount / LotSize,
            Price = tradePrice
        };

        return trade;
    }
}
```

#### ILogger

```c#
public interface ILogger
{
    void LogWarning(string message, params object[] args);

    void LogInfo(string message, params object[] args);
}
```

```c#
public class ConsoleLogger : ILogger
{
    public void LogWarning(string message, params object[] args)
    {
        Console.WriteLine(string.Concat("WARN: ", message), args);
    }

    public void LogInfo(string message, params object[] args)
    {
        Console.WriteLine(string.Concat("INFO: ", message), args);
    }
}
```

#### ITradeStorage

```c#
public interface ITradeStorage
{
    void Persist(IEnumerable<TradeRecord> trades);
}
```

```c#
public class AdoNetTradeStorage : ITradeStorage
{
    private readonly ILogger logger;

    public AdoNetTradeStorage(ILogger logger)
    {
        this.logger = logger;
    }

    public void Persist(IEnumerable<TradeRecord> trades)
    {
        using (var connection = new System.Data
               .SqlClient.SqlConnection("Data Source=(local);Initial Catalog=TradeDatabase;Integrated Security=True;"))
        {
            connection.Open();
            using (var transaction = connection.BeginTransaction())
            {
                foreach (var trade in trades)
                {
                    var command = connection.CreateCommand();
                    command.Transaction = transaction;
                    command.CommandType = System.Data.CommandType.StoredProcedure;
                    command.CommandText = "dbo.insert_trade";
                    command.Parameters.AddWithValue("@sourceCurrency", trade.SourceCurrency);
                    command.Parameters.AddWithValue("@destinationCurrency", trade.DestinationCurrency);
                    command.Parameters.AddWithValue("@lots", trade.Lots);
                    command.Parameters.AddWithValue("@price", trade.Price);

                    command.ExecuteNonQuery();
                }

                transaction.Commit();
            }
            connection.Close();
        }

        logger.LogInfo("{0} trades processed", trades.Count());
    }
}
```

### Собираем их всех вместе

Вот таким компактным становится класс TradeProcessor после делегирования обязанностей специализированным классам:

```c#
public class TradeProcessor
{
    private readonly ITradeDataProvider tradeDataProvider;
    private readonly ITradeParser tradeParser;
    private readonly ITradeStorage tradeStorage;
    
    public TradeProcessor(ITradeDataProvider tradeDataProvider, 
                          ITradeParser tradeParser, 
                          ITradeStorage tradeStorage)
    {
        this.tradeDataProvider = tradeDataProvider;
        this.tradeParser = tradeParser;
        this.tradeStorage = tradeStorage;
    }

    public void ProcessTrades()
    {
        var lines = tradeDataProvider.GetTradeData();
        var trades = tradeParser.Parse(lines);
        tradeStorage.Persist(trades);
    }
}
```

Теперь остается создать экземпляры всех абстракций и правильно соединить из друг с другом:

```c#
class Program
{
    static void Main(string[] args)
    {
        var tradeStream = Assembly.GetExecutingAssembly().
            GetManifestResourceStream("SingleResponsibilityPrinciple.trades.txt");

        var logger = new ConsoleLogger();
        var tradeValidator = new SimpleTradeValidator(logger);
        var tradeDataProvider = new StreamTradeDataProvider(tradeStream);
        var tradeMapper = new SimpleTradeMapper();
        var tradeParser = new SimpleTradeParser(tradeValidator, tradeMapper);
        var tradeStorage = new AdoNetTradeStorage(logger);

        var tradeProcessor = new TradeProcessor(tradeDataProvider, tradeParser, tradeStorage);
        tradeProcessor.ProcessTrades();

        Console.ReadKey();
    }
}
```

### Вывод

В итоге класс TradeProcessor все еще занимается тремя вещами - получает данные из источника, форматирует их и записывает в хранилище. Но теперь реализация этих вещей скрыта за абстракциями и в случае изменений требований к любому компоненту правки затронут только этот компонент, но не TradeProcessor, в отличие от исходного примера. Поэтому TradeProcessor теперь соответствует SRP в полной мере.

## Пример сериализации

Допустим, нужно написать функциональность сериализации объекта `Person`. Мы можем создать два класса - один будет сериализовывать, второй - десериализовывать Person. С виду SRP соблюден, но на самом деле нарушен, потому что обе операции непосредственно связаны - если меняются правила сериализации, то надо переделывать и сериализатор, и десериализатор. Поэтому обе функции должны быть объединены в один класс, тогда в случае изменений нам придется выполнить их в одном классе, а не в двух.