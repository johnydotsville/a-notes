# Запрет наследования и переопределения методов

Запрет наследования в Java осуществляется через конечный класс. Можно также запретить переопределять отдельные методы, объявив их конечными.

## Конечный класс

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

Можно помечать через `final` отдельные методы, чтобы их нельзя было переопределить в потомках:

```java
public class Asset  {
    public void commentary() {  // Обычный метод можно переопределить в потомке.
        System.out.println("Asset.commentary()");
    }

    public final void description() {  // <-- final метод нельзя переопределить в потомке.
        System.out.println(getClass().getSimpleName() + ".description()");
    }
}
```

```java
public class House extends Asset {
    @Override
    public final void commentary() {  // <-- Переопределяем и блокируем дальнейшее переопределение.
        System.out.println("Заменили реализацию родительского метода commentary.");
    }

    public void description() { // Ошибка! Нельзя переопределить final метод.
        System.out.println("Тщетны попытки заменить описание.");
    }
}
```

```java
public class Castle extends House {
    @Override
    public void commentary() {  // Ошибка! House при переопределении сделал метод final.
        System.out.println("Теперь и этот метод нельзя переопределить.");
    }
}
```

# Ограничение наследования

Кроме полного запрета наследования, в Java есть возможность частичного ограничения наследования от класса А, указав список классов, которые от него могут наследоваться. Это делается с помощью запечатанных классов.

## Запечатанный sealed класс

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