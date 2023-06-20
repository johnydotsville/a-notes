# Record

## Концепция, терминология

Концепция записи - это класс с неизменяемыми полями. Окончательно оформились в JDK 16.

Поля записи называются *компонентами*.

## Базовый синтаксис

```java
public record Point(int x, int y, Date created) {
    
}
```

Характеристики записи:

* Запись трансформируется в класс с двумя полями:

  ```java
  private final double x;
  private final double y;
  ```

* От записи нельзя наследоваться.

* Компоненты доступны для чтения по одноименным методам (без всяких приписок вроде get), которые генерируются автоматически.

  Перезаписать компонент невозможно. Но если компонент содержит изменяемый объект, например дату, то сам этот объект изменить можно:

  ```java
  var point = new Point(100, 200, new Date());
  int x = point.x();  // Доступ к x
  int y = point.y();  // Доступ к y
  point.created().setTime(100);  // Изменяем изменяемый объект
  System.out.println(point);  // Point[x=100, y=200, created=Thu Jan 01 03:00:00 MSK 1970]
  ```

* Объявить поля экземпляра в записи нельзя. Но статические поля добавлять можно:

  ```java
  public record Point(int x, int y) {
      public final int z;  // Ошибка!
      public static Point ORIGIN = new Point(0, 0);  // Ok
  }
  ```

* Можно добавлять методы, как статические, так и обычные:

  ```java
  public record Point(int x, int y) {
      public static double distance(Point p1, Point p2)
      {
          return Math.hypot(p1.x - p2.x, p1.y - p2.y);
      }
  
      public double distance(Point p)
      {
          return Math.hypot(this.x - p.x, this.y - p.y);
      }
  }
  ```

* Можно переопределять стандартные методы доступа к компонентам, но это считается плохой практикой. Хотя как по мне, это вполне можно использовать для защиты изменяемых объектов:

  ```java
  public record Point(int x, int y, Date created) {
      
      public int x() {  // Моветон
          return y;
      }
      
      public Date created() {
          return (Date) created.clone();
      }
  }
  ```

# Конструкторы

## Канонический конструктор и дополнительные

Конструктор, написанный в объявлении записи, называется *канонический*:

```java
public record Point(int x, int y, Date created) {
    
}
```

К нему можно добавить любое количество дополнительных конструкторов. Они обязаны вызывать канонический или другой дополнительный, который вызовет канонический. Одним словом, канонический обязательно должен быть вызван в итоге:

```java
public record Point(int x, int y, Date created) {

    public Point(int x, int y) {  // Объявляем дополнительный конструктор, без третьего параметра
        this(x, y, new Date(100));  // И вызываем канонический
    }

}
```

## Переопределение канонического конструктора

Канонический конструктор можно переписать, если необходимо снабдить его какой-то логикой:

```java
public record Range(int from, int to) {
    
    public Range(int from, int to) {  // Переписываем канонический конструктор
        if (from < to) {
            this.from = from;
            this.to = to;
        } else {
            throw new IllegalArgumentException("from должно быть меньше to");
        }
    }
    
}
```





