# Создание и сравнение строк, пул строк

Во-первых, строки - это объекты, а при сравнении объектов используется сравнение ссылок. Строки можно создать разными способами. Вот два из них:

```java
String message1 = "Hello, world!";  // 1 способ
String message2 = "Hello, world!";  // 1 способ
String message3 = new String("Hello, world!");  // 2 способ
System.out.println(message1 == message2);  // true
System.out.println(message1 == message3);  // false
```

Есть такая вещь как *пул строк*. Когда мы создаем строки первым способом, то ВМ сперва проверяет, а нет ли уже строки с таким текстом в пуле. Если есть, то новая строка не создается, а просто возвращается ссылка на существующую. Если нету, то создается новый объект строки и размещается в пуле. Поэтому message1 и message2 равны - они указывают на один и тот же объект в пуле строк.

Когда мы создаем строку вторым способом, то это означает принудительное выделение памяти под новую строку, минуя пул. Поэтому message1 и message3 не равны - хотя текст в них одинаковый, они указывают на разные объекты.

Для корректного сравнения строк нужно пользоваться методом *.equals()* (или *.equalsIgnoreCase()* если не нужно учитывать регистр) - в классе строки он переопределен и производит сравнение посимвольно, а не по адресам объектов в памяти:

```java
String message1 = "Hello, world!";
String message2 = "Hello, world!";
String message3 = new String("Hello, world!");
String message4 = "HELLO, WORLD!";
System.out.println(message1.equals(message2));  // true
System.out.println(message1.equals(message3));  // true
System.out.println(message1.equalsIgnoreCase(message4));  // true
```