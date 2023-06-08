https://docs.oracle.com/javase/8/docs/api/java/util/Collection.html

# Вопросы

- [ ] Какие операции (концептуально) поддерживает интерфейс Collection?
  - [ ] Почему коллекция поддерживает добавление\удаление элементов, умеет определять свой размер и есть ли в ней некоторый элемент, но при этом не умеет сортировать элементы?
- [ ] Как можно создать копию коллекции с помощью конструктора?
  - [ ] Можно ли так создать список из set'а и наоборот?
- [ ] Какая связь между методами .contains и .equals?
- [ ] Сколько элементов удаляет метод .remove? Какой тип он принимает? Как с ним связан метод .equals?
- [ ] Сколько элементов удаляет метод .removeIf? Как он связан с типом Predicate?
- [ ] Если в коллекции A есть элементы ${ 1, 2, 3 }$, а в коллекции В - ${ 2 }$, какой результат даст A.retainAll(B)?
- [ ] Можно ли сравнить две коллекции методом .equals? Если есть коллекция А { 1, 2, 3} и B { 1, 2, 3 }, что покажет A.equals(B)?
- [ ] Как с помощью метода .toArray преобразовать коллекцию в массив, чтобы тип результирующих элементов был не Object, а как в исходной коллекции?

# Резюме

Коллекция по сути умеет:

* Добавлять\удалять элементы
* Определять, есть ли в ней указанный элемент
* Возвращать свой размер

Полезные особенности:

* Имеет два конструктора: один пустой, а второй - с параметром типа Collection, что позволяет сделать копию коллекции, независимо от конкретного типа каждой коллекции. Например, на основе HashSet сделать ArrayList:

  ```java
  Set<String> namesSet = new HashSet<>();
  namesSet.add("Tom");
  namesSet.add("Huck");
  namesSet.add("Jim");
  
  List<String> namesList = new ArrayList<>(namesSet);  // <-- Удобно клонировать
  printCollection(namesList);  // Tom, Huck, Jim
  
  ...
      
  private static void printCollection(Collection coll) {
      for (Object data: coll) {
          System.out.println(data);
      }
  }
  ```

# Краткий обзор методов

```java
public interface Collection<E> 
    extends Iterable<E> {
        +.add(item);
        +.addAll(Collection);

        +.remove(Object);
        +.removeAll(Collection);    
        +.removeIf(Predicate);
    
        +.retainAll(Collection);

        +.contains(Object);
        +.containsAll(Collection);
    
        +.size();
        +.isEmpty();
        +.clear();
        
        +.equals(Object);
    ...
        +.stream();    
        +.parallelStream();
    
        +.spliterator();
        
        +.toArray();
        +.toArray(T[] arr);
}
```

## retainAll

retain - "удерживать, сохранять", `col1.retainAll(col2)` - в col1 оставит только те элементы, которые есть в col2, а остальные удалит:

```java
Set<String> namesSet = new HashSet<>();
namesSet.add("Tom");
namesSet.add("Huck");
namesSet.add("Jim");

List<String> namesList = new ArrayList<>();
namesList.add("Huck");
namesList.add("Mary");

namesSet.retainAll(namesList);

printCollection(namesSet);  // В namesSet остался только Huck
```

## contains

Использует метод equals для сравнения объектов. Допустим, есть класс Person:

```java
import lombok.Getter;  // + Setter, ToString

@Getter @Setter @ToString
class Person {
    private String name;
    private int age;

    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }

    @Override
    public boolean equals(Object obj) {  // <-- Сравниваем, например, только по имени
        if (this == obj) return true;
        if (obj == null || this.getClass() != obj.getClass()) return false;
        Person person = (Person)obj;

        return person.name == this.name;
    }
}
```

```java
List<Person> names = new ArrayList<>();
names.add(new Person("Tom", 28));
names.add(new Person("Tom", 14));

Person tom15 = new Person("Tom", 15);
System.out.println(names.contains(tom15));  // true
```

## remove

Удаляет *первый попавшийся элемент*, который равен указанному. Использует equals объекта для определения равенства:

```java
List<Person> names = new ArrayList<>();
names.add(new Person("Tom", 28));
names.add(new Person("Tom", 14));

Person tom15 = new Person("Tom", 15);
names.remove(tom15);

printCollection(names);  // Tom 28 удалился, а Tom 14 остался
```

## removeIf

Метод появился в Java 8, принимает лямбду с условием удаления:

```java
List<Person> names = new ArrayList<>();
names.add(new Person("Tom", 28));
names.add(new Person("Tom", 14));

names.removeIf(p -> p.getAge() < 20);
printCollection(names);  // Tom 14 удалился, а Tom 28 остался
```

## toArray

Простой метод .toArray() создает массив из Object'ов, что не особо удобно, поэтому можно заранее создать массив нужного типа и перегнать в него коллекцию методом .toArray(этот_массив):

```java
Set<Person> names = new HashSet<>();
names.add(new Person("Tom", 28));
names.add(new Person("Tom", 14));

Person[] arr = new Person[names.size()];
names.toArray(arr);

for (int i = 0; i < arr.length; i++) {
    System.out.println(arr[i]);
}
```

## equals

Этот метод *не* сравнивает две коллекции по содержимому. Две разные коллекции с одинаковыми элементами не являются равными по мнению этого метода.

## spliterator

Что-то связанное с обходом + параллелизмом

