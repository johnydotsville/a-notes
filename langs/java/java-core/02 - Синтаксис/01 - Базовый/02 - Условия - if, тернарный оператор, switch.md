

# if

ололо



# Тернарный оператор

* В английском варианте может звучать как "conditional оператор". 
* Это условный оператор, который в зависимости от условия возвращает одно или другое значение:

```java
public int selectGreather(int x, int y) {
    return x > y ? x : y;
}
```

* Имеет формат `условие ? если true : если false`

* Может использоваться только для значений, т.е. вместо x и y нельзя, например, написать какой-то код:

  ```java
  public void printGreather(int x, int y) {
      x > y ? System.out.println("x > y") : System.out.println("y > x");  // Ошибка! Not a statement
  }
  ```

  Но можно, например, вызывать функции. Главное, чтобы возвращалось какое-то значение:

  ```java
  public int selectGreather(int x, int y) {
      return x > y ? foo() : bar();
  }
  
  public int foo() {
      return 1;
  }
  
  public int bar() {
      return 2;
  }
  ```

  







# switch

todo: проверить, работает ли это и с оператором.

```
switch value. For example:
enum Size { SMALL, MEDIUM, LARGE, EXTRA_LARGE }; . . .
Size itemSize = . . .;
String label = switch (itemSize)
{
case SMALL -> "S"; // no need to use Size.SMALL
case MEDIUM -> "M";
case LARGE -> "L";
case EXTRA_LARGE -> "XL";
};
```



## Оператор



## Выражение



```java
public String getSeasonName(int code) {
    return switch (code) {
        case 0 -> "Spring";
        case 1 -> "Summer";
        case 2 -> "Autumn";
        case 3 -> "Winter";
        case 4, 5, 6, 7 -> "Are you nuts?";  // Можно использовать несколько значений разом
        default -> "unknown";
    };
}
```

