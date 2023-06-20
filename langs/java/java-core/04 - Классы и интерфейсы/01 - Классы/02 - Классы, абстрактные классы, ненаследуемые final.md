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

Конечный класс обозначается ключевым словом `final`. Имеет следующие характеристики:

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

