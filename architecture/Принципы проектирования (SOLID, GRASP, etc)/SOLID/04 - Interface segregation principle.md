# Формулировка

Принцип сформулировал Роберт Мартин:

> Программные сущности не должны зависеть от методов, которые они не используют.

Или "лучше несколько маленьких интерфейсов, чем один большой". Когда класс реализует какой-то интерфейс, то должен предоставить реализацию для всех методов интерфейса. Если получается ситуация, что реализацией каких-то методов становится простая заглушка, реализация-пустышка, то это признак того, что интерфейс слишком большой и его можно разделить.

# Пример

Пусть есть такой интерфейс для отправки сообщения:

```csharp
interface IMessage {
    void Send();
    string Text { get; set;}
    string Subject { get; set;}
    string ToAddress { get; set; }
    string FromAddress { get; set; }
}
```

Нам необходимо реализовать на его основе класс, отправляющий email-сообщения:

```csharp
class EmailMessage : IMessage {
    public string Subject { get; set; } = "";
    public string Text { get; set; } = "";
    public string FromAddress { get; set; } = "";
    public string ToAddress { get; set; } = "";
 
    public void Send() => Console.WriteLine($"Отправляем Email-сообщение: {Text}");
}
```

 Все поля пригодились, все выглядит нормально. Теперь попробуем реализовать класс sms-сообщений:

```csharp
class SmsMessage : IMessage {
    public string Text { get; set; } = "";
    public string FromAddress { get; set; } = "";
    public string ToAddress { get; set; } = "";
 
    public string Subject    {
        get => throw new NotImplementedException();
        set => throw new NotImplementedException();
    }
 
    public void Send() => Console.WriteLine($"Отправляем Sms-сообщение: {Text}");
}
```

Не пригодилось поле "Subject", потому что в sms нет понятия "тема", как есть в email. Выявилась проблема базового интерфейса - он содержит элементы, являющиеся слишком специфическими.

Если попробовать реализовать голосовое сообщение, то выяснится, что в базовом интерфейсе `IMessage`  нет элемента, в котором можно было бы хранить это сообщение, потому что голос - не текст. Если добавить в интерфейс поле `byte[] Voice { get; set; }`, тогда придется править существующие реализации `EmailMessage` и `SmsMessage`, добавляя в них заглушки для этого свойства.

Таким образом, приходим к выводу, что надо разделить базовый интерфейс на несколько, выделив специфичные вещи в отдельные интерфейсы:

```csharp
interface IMessage {
    void Send();
    string ToAddress { get; set; }
    string FromAddress { get; set; }
}
```

```csharp
interface IVoiceMessage : IMessage {
    byte[] Voice { get; set; }
}
```

```csharp
interface ITextMessage : IMessage {
    string Text { get; set; }
}
```

```csharp
interface IEmailMessage : ITextMessage {
    string Subject { get; set; }
}
```

Тогда в реализациях не придется писать никаких заглушек:

```csharp
class VoiceMessage : IVoiceMessage {
    public string ToAddress { get; set; } = "";
    public string FromAddress { get; set; } = "";
 
    public byte[] Voice { get; set; } = Array.Empty<byte>(); 
    public void Send() => Console.WriteLine("Передача голосовой почты");
}
```

```csharp
class EmailMessage : IEmailMessage {
    public string Text { get; set; } = "";
    public string Subject { get; set; } = "";
    public string FromAddress { get; set; } = "";
    public string ToAddress { get; set; } = "";
 
    public void Send() => Console.WriteLine("Отправляем по Email сообщение: {Text}");
}
```

