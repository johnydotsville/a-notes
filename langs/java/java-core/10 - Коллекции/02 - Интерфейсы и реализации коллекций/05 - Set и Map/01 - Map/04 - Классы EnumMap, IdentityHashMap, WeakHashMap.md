## EnumMap

EnumMap не является абстрактным (в отличие от EnumSet). Используется, когда ключами нужно сделать элементы перечисления. Все операции работают как обычно:

```java
EnumMap<DaysOfWeek, String> days = new EnumMap<>(DaysOfWeek.class);
days.put(DaysOfWeek.Monday, "Понедельник");
days.put(DaysOfWeek.Tuesday, "Вторник");
days.put(DaysOfWeek.Wednesday, "Среда");
days.put(DaysOfWeek.Thursday, "Четверг");
days.put(DaysOfWeek.Friday, "Пятница");
days.put(DaysOfWeek.Saturday, "Суббота");
days.put(DaysOfWeek.Sunday, "Воскресенье");

System.out.println(days.get(DaysOfWeek.Friday));
```

## IdentityHashMap

В документации написано следующее:

> This class is \*not\* a general-purpose `Map` implementation!  While this class implements the `Map` interface, it intentionally violates `Map's` general contract, which mandates the use of the `equals` method when comparing objects.  This class is designed for use only in the rare cases wherein reference-equality semantics are required

Поэтому отложим его до поры до времени

## WeakHashMap

Мутная тема, связана с разными видами ссылок в джаве (strong, soft, weak, phantom https://habr.com/ru/post/169883/), явно не для каждодневного пользования