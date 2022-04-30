Тестовый набор данных:

```java
enum Sex { M, F }

class Character {
    public String firstname;
    public String lastname;
    public Sex sex;
    public int age;

    public Character(String firstname, String lastname, Sex sex, int age) {
        this.firstname = firstname;
        this.lastname = lastname;
        this.sex = sex;
        this.age = age;
    }
}
...
Character tom = new Character("Tom", "Sawyer", Sex.M, 13);
Character huck = new Character("Huckleberry", "Finn", Sex.M, 14);
Character becky = new Character("Becky", "Thatcher", Sex.F, 12);
Character sid = new Character("Sid", "Sawyer", Sex.M, 12);
Character joe = new Character("Joe", "Harper", Sex.M, 13);
Character marry = new Character("Marry", "Sawyer", Sex.F, 16);

ArrayList<Character> peeps = new ArrayList<>(
    Arrays.asList(tom, huck, becky, sid, joe, marry)
);
```

Простой пример:

```java
long shortNames = peeps.stream()
    .filter(p -> p.firstname.length() == 3)  // Промежуточная операция (intermediate), ret Stream<T>
    .count();  // Терминальная операция (terminal), ret long

long shortNames = peeps.parallelStream()
    .filter(p -> p.firstname.length() == 3)
    .count();
```



## Характеристики стримов:

* Появились в Java 8
* Работают по принципу "что, а не как делать"
* Выполняются по требованию, т.е. работа начнется только когда потребуют результат
* Поток не изменяет свой источник (коллекцию, поток), а выдает новый поток
* Поток не сохраняет свои элементы (???)



## Создание стримов

### Из коллекции

У коллекций есть методы .stream() и parallelStream(), которые возвращают поток.

### Из массива

У массивов, в отличие от коллекций, нету методов .stream() и parallelStream(). Поток на массиве получается так:

```java
Character[] peeps = new Character[] {  // массив персонажей
    tom, huck, becky, sid, joe, marry
};

Stream<Character> stream = Stream.of(peeps);  // 1 способ
Stream<Character> stream = Arrays.stream(peeps);  // 2 способ
Stream<Character> stream = Stream.of(tom, huck, becky, sid, joe, marry);  // 3 способ

long shortNames = stream
    .filter(p -> p.firstname.length() == 3)
    .count();
```



## Методы потоков

### filter

```java
long shortNames = peeps.stream()
    .filter(p -> p.firstname.length() == 3)  // Промежуточная операция (intermediate), ret Stream<T>
    .count();  // Терминальная операция (terminal), ret long
```

### map

```java
List<String> fullnames = peeps.stream()
    .map(p -> p.firstname + " " + p.lastname)  // Можно передать лямбду
    .map(String::toUpperCase)  // А можно просто функцию
    .toList();
```

### flatMap

TODO: вписать нормальный пример с реализацией

Концептуально, делает многомерный набор одномерным:

```
[ 
	[2, 3, 5], 
    [7, 11, 13], 
    [17, 19, 23] 
] -> [ 2, 3, 5, 7, 11, 13, 17, 19, 23 ]
```







# Черновик

С помощью стрим апи можно не писать конкретную реализацию обработки коллекции, а просто "сказать, что хотим получить", т.е. выполнять обработку на более высоком уровне. Плюс можно оптимизировать выполнение за счет распараллеливания обработки.



## Дебаг стримов

Если точка останова стоит на стриме, то в меню дебага рядом с кнопками step into, step over и другими есть кнопка *Trace Current Stream Chain* (у меня она была последняя). Возникает окно с интуитивно понятным интерфейсом, где можно смотреть, во что превращается стрим после каждой операции.

Хз как на винде, но на маке чтобы это работало, должен быть установлен плагин. В File > Settings > Plugin, плагин java stream debugger. На винде работало сразу.



# Исключения

Если внутри стримовой операции возникает исключение (например, внутри map), то обработка прекращается. Стоит помнить, что стримы выполняются не сразу, а по мере требования данных. Поэтому могут возникать неочевидные ситуации, например:

```java
List<String> letters = Arrays.asList("a", "b", "v", "g", "d");

long count = letters.stream()
    .map(l -> throwsUncheck(l))
    .count();
...
public String throwsUncheck(String letter)
{
    System.out.println(letter);
    if (true) {
        throw new RuntimeException();
    return letter + letter;
}
```

Здесь операция map вообще не выполнится, потому что фактически мы выбираем количество элементов. Поэтому наверное ВМ оптимизирует код и не выполняет map. Как бы то ни было, исключение не возникнет и можно подумать, что оно просто подавляется, но нет. Если выполнить вот такой код:

```java
List<String> result = letters.stream()
    .map(l -> throwsUncheck(l))
    .collect(Collectors.toList());
```

Исключение честно возникнет и обработка сразу прервется.