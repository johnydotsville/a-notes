# Method reference

Когда лямбда сводится к вызову единственного метода, то вместо написания лямбды можно просто передать ссылку на этот метод.

Пример вывода списка в консоль:

```java
var names = Arrays.asList("Tom", "Huck", "Jim");
names.forEach(n -> System.out.println(n));  // <-- В теле лямбды всего один метод
names.forEach(System.out::println);  // и мы можем просто указать метод
```

Пример сортировки массива:

```java
var letters = new String[] { "C", "B", "A" };

Arrays.sort(letters, (a, b) -> a.compareToIgnoreCase(b));  // Через лямбду
Arrays.sort(letters, String::compareToIgnoreCase);  // Через метод
```

```java
// Для справки, устройство sort, компаратора и метода сравнения
public class Arrays {
    public static <T> void sort(T[] a, Comparator<? super T> c)
}
...
@FunctionalInterface
public interface Comparator<T> {
    int compare(T o1, T o2);
}
...
public final class String    
    public int compareToIgnoreCase(String str) {
        return CASE_INSENSITIVE_ORDER.compare(this, str);
    }
}
```

Когда компилятор генерирует реализацию функционального интерфейса (ФИ), то внутрь метода ФИ он помещает вызов указанного нами метода. Критично важно понимать, что указанный нами метод не подменяет собой метод ФИ, а вызывается *внутри* него. Для наглядности сказанного:

![lambda-and-method-to-fi-translation.drawio](img/lambda-and-method-to-fi-translation.drawio.svg)

# Как это работает

Несколько ссылок на эту тему: [oracle](https://docs.oracle.com/javase/tutorial/java/javaOO/methodreferences.html), [moandjiezana.com](https://moandjiezana.com/blog/2014/understanding-method-references/), [baddotrobot.com](http://baddotrobot.com/blog/2014/02/18/method-references-in-java8/), [cr.openjdk.org](https://cr.openjdk.org/~briangoetz/lambda/lambda-translation.html)

В целом есть 4 категории методов, которые можно использовать вместо лямбды:

| #    | Категория                | Пример                        | Лямбда-аналог                            |
| ---- | ------------------------ | ----------------------------- | ---------------------------------------- |
| 1    | Статический метод класса | `String::valueOf`             | `s -> String.valueOf(s)`                 |
| 2    | Метод экземпляра         | `System.out::println`         | `s -> System.out.println(s)`             |
| 3    | Метод *НА* экземпляре    | `String::compareToIgnoreCase` | `(s1, s2) -> s1.compareToIgnoreCase(s2)` |
| 4    | Конструктор класса       | `Person::new`                 | `() -> new Person`                       |

## Статический метод класса

Оригинальное название: Reference to a static method. "Ссылка на статический метод класса".

> Техника: указываем *статический метод* класса на *типе*.

Параметры метода и его результат должны подходить под параметры и результат метода ФИ.

Примеры:

```java
Function<Integer, String> intToStringFunction = String::valueOf;
String intAsString = intToStringFunction(5);  // "5"

// Для справки
@FunctionalInterface
public interface Function<T, R> {
    R apply(T t);
}

public static String valueOf(Object obj) {
    return (obj == null) ? "null" : obj.toString();
}
```

Переменная *intToStringFunction* имеет тип *Function<Integer, String>*, т.е. в нее можно положить функцию, которая принимает Integer, а возвращает String. Статический метод *valueOf*  класса String под эти требования подходит.

Предположим, мы создали вот такую функцию *foo*:

```java
class App {
    private static String foo(Integer i1, Integer i2) {
        return String.valueOf(i1 + i2);
    }
}

Function<Integer, String> intToStringFunction = App::foo;  // Ошибка!
```

Использовать ее для такого ФИ не получится, потому что она принимает два параметра, стало быть ее нельзя адаптировать под `R apply(T t)`.

## Обычный метод на экземпляре

Оригинальное название: Reference to an instance method of a particular object. "Ссылка на обычный метод объекта".

> Техника: указываем *обычный* метод на *экземпляре* класса.

Параметры метода и его результат должны подходить под параметры и результат метода ФИ.

### Пример 1

Типичный пример вывода консоль, который можно видеть чуть более, чем везде:

```java
var names = List.of("Huck", "Jim", "Tom");
names.forEach(System.out::println);  // names.forEach(n -> System.out.println(n));
```

```java
// Для справки
public interface Iterable<T> {
    default void forEach(Consumer<? super T> action) {
        Objects.requireNonNull(action);
        for (T t : this) {
            action.accept(t);  // <-- Вызов действия
        }
    }
}
    
@FunctionalInterface
public interface Consumer<T> {
    void accept(T t);
}

public class PrintStream {
    public void println(String x) {
}
```

Класс System - это сборник static final полей ("статических констант"), одним из которых является out. В нем лежит объект потока, т.е. экземпляр класса PrintStream. Как видно, метод println в нем не статический, значит для вызова этого метода необходим экземпляр. К экземпляру мы и получаем доступ с помощью `System.out`, ну а дальше конструкцией `::println` указываем метод этого экземпляра.

### Пример 2

По сути то же самое, только на более наглядном экземпляре из собственного класса:

```java
public class Helper {
    public Integer stringToInt(String s) {  // Обычный метод в каком-то классе
        return Integer.valueOf(s);
    }
}

var h = new Helper();  // Создаем экземпляр
Function<String, Integer> integerStringFunction = h::stringToInt;  // Через ссылку на экземпляр указываем метод
```

## Обычный метод на классе

Оригинальное название: Reference to an instance method of an arbitrary object of a particular type. "Ссылка на обычный метод *какого-то* объекта определенного типа" (имхо, плохое название).

> Техника: указываем *обычный* метод класса, но используем для этого *тип*.

Особенность этого подхода в том, что указанный метод вызывается на *первом аргументе метода ФИ*, а остальные аргументы ФИ служат параметрами указанному методу.

```java
isSame = Integer::equals;
// Преобразуется компилятором во что-то такое:
BiFunction<Integer, Integer, Boolean> isSame =
    new BiFunction<Integer, Integer, Boolean>() {
        @Override
        public Boolean apply(Integer i1, Integer i2) {
            return i1.equals(i2);  // Указанный нами метод вызывается на первом аргументе метода apply ФИ
        }
    };
```

Совет: вся эта роспись ниже может и будет понятной, если очень долго и внимательно в нее втыкать. Удалять я ее не хочу, пусть лежит в академических и исторических целях. Но для быстрого понимания предлагаю такую модель: просто представляем себе пример со сравнением двух строк. Используя синтаксис "обычный (не static) метод на типе", мы говорим компилятору "вызови этот метод на первой строке, а вторую передай в него как параметр".

### Пример 1

Пример + объяснение: сортировка списка:

```java
var names = Arrays.asList("Tom", "Huck", "Jim");

// Метод compareToIgnoreCase не static, т.е. экземплярный, но все равно указывается через тип, через String
names.sort(String::compareToIgnoreCase);  
// names.sort((s1, s2) -> s1.compareToIgnoreCase(s2));  // Лямбда-аналог
```

Метод sort принимает ФИ *Comparator*, который для своего метода compare требует *два параметра*:

```java
// Для справки
public interface Comparator<T> {
    int compare(T o1, T o2);  // <-- Методу ФИ надо два параметра
}

default void sort(Comparator<? super E> c) {
    // Если копать ооочень глубого, можно-таки найти несколько мест, где вызывается функция сравнения
    for (int j=i; j>low && 
         c.compare(dest[j-1], dest[j]) > 0  // <-- Туть! Передает два аргумента
         ; j--) {
}
```

Однако метод *String::compareToIgnoreCase*, который мы передаем в метод sort, имеет только *один параметр*:

```java
public int compareToIgnoreCase(String str) {
    ...
}
```

В этом случае, когда компилятор будет генерировать реализацию для ФИ Comparator на основе указанного нами *String::compareToIgnoreCase*, то попытается в теле метода compare вызвать указанный нами метод compareToIgnoreCase на первом аргументе compare, а второй аргумент compare передать в compareToIgnoreCase. В итоге как раз и получится то же, что мы писали бы через лямбду - `s1.compareToIgnoreCase(s2)`

### Пример 2

Полностью искусственный пример, но вероятно более наглядный, потому что все компоненты максимально просты:

```java
@FunctionalInterface
interface TriFunc<T1, T2, T3, R> {
    R func(T1 a, T2 b, T3 c);  // Этот ФИ требует три параметра
}
```

```java
public class TriFuncUsage {
    private String message;

    public TriFuncUsage(String message) {
        this.message = message;
    }

    // Этот экземплярный метод с ДВУМЯ параметрами мы будем использовать для ФИ, 
    // метод которого требует ТРИ параметра
    public String foobar(Integer i1, Integer i2) {
        return String.format("%s (%d, %d)", message, i1, i2);
    }
}
```

```java
class App {
    public static void main(String[] args) {
        // Указываем реализацию для ФИ - метод с ДВУМЯ параметрами.
        // Метод ЭКЗЕМПЛЯРНЫЙ, но мы указываем его через ТИП, т.к. это третий сценарий.
        TriFunc<TriFuncUsage, Integer, Integer, String> tf = TriFuncUsage::foobar;
        
        // Вызываем метод ФИ, передавая ему ТРИ аргумента.
        // Первый аргумент станет "вызыватором" метода foobar, а 7 и 12 сам foobar получит в качестве аргументов.
        String result = tf.func(new TriFuncUsage("Hello!"), 7, 12);
        
        System.out.println(result);  // Hello! (7, 12)
        // tf.func(5, 7, 12);  // Ошибка! Первый аргумент не годится: Required type: TriFuncUsage, Provided: int
    }
}
```

## Конструктор

Оригинальное название: Reference to a constructor.

Вместо лямбды указываем конструктор класса в виде `MyClass::new`

```java
var names = Arrays.asList("Tom", "Huck", "Jim");
var persons = names.stream()
    .map(Person::new)  // <-- Вот так вот
    .collect(Collectors.toList());
persons.forEach(System.out::println);
```

```java
class Person {
    private String name;

    public Person(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Person [name: %s]", name);
    }
}
```

За счет типа исходных данных (String) компилятор понимает, какой конструктор нужно вызвать. Если бы мы не объявили конструктор с единственным параметром String, тогда была бы ошибка:

```java
Required type: Function<? super java.lang.String, ? extends R>
Provided: <method reference>
reason: Incompatible parameter types in method reference expression
```

Важно понимать, что сама по себе запись `MyClass::new` не приводит к созданию экземпляра, а, как и в ранее упомянутых случаях, *помещает* код создания экземпляра внутрь тела метода в реализации ФИ. Метод map требует `<R> Stream<R> map(Function<? super T, ? extends R> mapper);`, значит при компиляции получается что-то вроде:

```java
.map(new Function<String, Person> () {
    @Override
    public Person apply(String s) {
        return new Person(s);
    }
})
```

# Вывод

В общем, главное помнить, что "положить лямбду" или "ссылку на метод" в место с функциональным интерфейсом (переменная, параметр), фактически означает заставить компилятор сгенерировать реализацию этого ФИ (допустим, в виде объекта какого-то класса), и внутрь его абстрактного метода запихнуть предоставленный нами код. Если код представляет собой:

* Лямбду, то запихнуть ее тело внутрь тела абстрактного метода ФИ.
* Ссылку на метод, то вызвать этот метод внутри тела метода ФИ, адаптировав этот вызов, если надо.