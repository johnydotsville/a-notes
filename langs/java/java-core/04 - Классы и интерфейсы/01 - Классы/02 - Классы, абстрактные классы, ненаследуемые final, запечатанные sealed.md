# Класс

Классы представляют собой пользовательские типы. TODO: вписать модификаторы, доступные для классов

```java
public class Person {
    // Поля
    // Блоки инициализации
    // Конструкторы
    // Методы
}
```



# Конечный класс

Имеет следующие характеристики:

* Обозначается ключевым словом `final`
* От него нельзя унаследоваться.
* Все его методы становятся final автоматически.

```java
public final class Asset  {
    
}
```

```java
public class House extends Asset {  // Ошибка! Нельзя наследоваться от final класса

}
```

## Конечные методы

Отдельные методы тоже можно помечать как final, чтобы их нельзя было ни перекрыть, ни переопределить в потомках.

```java
public class Asset  {
    public final void virtualDescription() {
        System.out.println("Asset.VirtualDescription()");
    }

    public final void description() {
        System.out.println("Asset.Description()");
    }
}
```

```java
public class House extends Asset {
    @Override
    public final void virtualDescription() {
        System.out.println("House.VirtualDescription()");
    }

    public void description() {
        System.out.println("House.Description()");
    }
}
```

```java
public class Castle extends House {
    @Override
    public void virtualDescription() {
        System.out.println("Castle.VirtualDescription()");
    }

    public void description() {
        System.out.println("Castle.Description()");
    }
}
```

# Абстрактные классы

Характеристики абстрактного класса (АК):

* Нельзя создать экземпляр АК
* АК может иметь абстрактные методы - методы без реализации
* АК может иметь обычные методы, с реализацией
* АК может иметь конструкторы (несмотря на то, что нельзя создать экземпляр АК)
* Потомок обязан реализовать все абстрактные методы родителя, либо сам должен быть объявлен как АК

Абстрактным также может быть:

* Метод

```java
public abstract class Asset {  // Абстрактный родитель
    
    protected String name;  // Имеет какие-то поля,

    public Asset(String name) {  // и даже конструктор
        this.name = name;
    }

    public String concrete() {
        return "Хоть я и абстрактный класс, но у меня мб методы с реализацией";
    }

    public abstract String description();  // Абстрактный метод - без реализации
}
```

```java
public class House extends Asset {  // Потомок абстрактного класса

    public House(String name) {
        super(name);
    }

    public String description() {  // Должен реализовать АМ
        return name;
    }
}
```

# Запечатанный sealed класс

Концепция запечатанного класса позволяет лучше контролировать наследование, путем явного указания классов, которые могут расширить запечатанный класс.

Характеристики запечатанного класса:

* Объявляется ключевым словом `sealed`
* Обязан иметь потомков.
* Классы, которым разрешено расширять запечатанный класс, перечисляются после ключевого слова `permits`
* Потомки запечатанного класса обязаны быть:
  * final, если дальнейшее наследование не подразумевается.
  * sealed, если они разрешают наследование в ограниченном виде.
  * non-sealed, если они разрешают наследование без ограничений.
* Если класс А разрешает наследование классам В и С, то они должны быть ему доступны. Например, они не могут быть package-private классами из другого пакета.
* Если класс А разрешает наследование классам В и С и они являются публичными, то они дб в том же пакете, что и класс А. Либо, в случае использования модулей, они должны находиться в том же модуле.
* Ключевое слово permits можно не писать, тогда все потомки должны находиться в том же файле, что и запечатанный класс.

```java
public abstract sealed class Asset  // <-- sealed, объявили класс запечатанным
        permits House, Stock {  // <-- permits, указали классы, которые могут быть потомками
    protected String name;

    public Asset(String name) {
        this.name = name;
    }
}
```

```java
// final, дальнейшее наследование от House запрещено
public final class House extends Asset {
    public long cost;

    public House(String name, long cost) {
        super(name);
        this.cost = cost;
    }
}
```

```java
// non-sealed, значит от Stock можно наследоваться без ограничений
public non-sealed class Stock extends Asset {
    public long sharesOwned;

    public Stock(String name, long sharesOwned) {
        super(name);
        this.sharesOwned = sharesOwned;
    }
}
```

```java
// Ошибка! Классу Product запрещено наследоваться от Asset
public class Product extends Asset {
    public String manufacturer;

    public Product(String name, String manufacturer) {
        super(name);
        this.manufacturer = manufacturer;
    }
}
```