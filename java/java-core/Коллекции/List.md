# ArrayList

## Резюме

TODO вписать здесь резюме

```java
import java.util.ArrayList;
```

Честно говоря, не знаю как лучше перевести ArrayList. Это не список, это не "списочный массив", это обычный массив, который автоматически расширяется при необходимости. Пожалуй поэтому подошло бы "динамический массив", но лучше буду писать просто ArrayList.

## Объявление

C Java 10 появилось слово var. Если var не использовать, то при создании объекта можно не указывать тип:

```java
var employees = new ArrayList<Employee>();
ArrayList<Employee> employees = new ArrayList<Employee>();
ArrayList<Employee> managers = new ArrayList<>();  // Тип указывать не обязательно
```

Инициализация при объявлении:

```java
ArrayList<String> arr1 = new ArrayList<>(
    Arrays.asList("Tom", "Huck", "Becky", "Sid", "Joe")
);
```

## Добавление, резервирование

При создании обычного массива все ячейки сразу инициализируются и доступны для использования. При создании ArrayList, несмотря на резервирование места под некоторое количество элементов, фактически никаких элементов до реального добавления не существует и работать с ними, соответственно, нельзя.

Для резервирования места под определенное количество элементов есть два способа:

```java
Employee john = new Employee("John Carter");
Employee abby = new Employee("Abby Lockhart");
Employee carry = new Manager("Carry Weaver");

var employees = new ArrayList<Employee>(128);  // 1. Указываем сразу при объявлении

var employees = new ArrayList<Employee>();
employees.ensureCapacity(40);  // 2. Расширяем память под элементы в произвольный момент
employees.add(john);  // Добавить элемент в конец
employees.add(abby);
employees.add(carry);
employees.ensureCapacity(500);
```

Добавлять можно не только в конец ArrayList, но и в указанную позицию, со сдвигом остальных элементов:

```java
employees.add(2, john);
```

При этом эта вторая позиция очевидно должна существовать, иначе `IndexOutOfBoundsException`. Конкретно в этом примере  у нас три элемента уже добавлено и можно было написать `.add(3, john)` и тогда это было бы аналогично обычному add и элемент добавился бы в конец списка. А вот `.add(4, john)` уже вызвало бы исключение, поскольку реально существует только три элемента.

Доступ к элементам возможен только через специальные методы `.get(i)` и `.set(i, item)` (в отличие от C#, где можно было использовать синтаксис [i] даже со списком):

```java
Employee emp = employees.get(2);
employees.set(3, emp);
```

## Перегон в обычный массив

Первый способ подразумевает предварительное создание массива, а потом уже копирование в него элементов. Характерен тем, что можно указать нужный тип массива самостоятельно:

```java
Employee[] arr = new Employee[employees.size()];
employees.toArray(arr);

for (Employee item : arr)
	System.out.println(item);
```

Второй способ подразумевает автоматическое создание массива и запись в него элементов. Но при этом создается массив типа Object:

```java
// Employee[] arr = employees.toArray();  // Ошибка, toArray() возвращает Object[]
Object[] arr = employees.toArray();

for (Object item : arr) {
	System.out.println(item);
    System.out.println(((Employee)item).getName());
}
```

Как следствие, придется приводить элементы, прежде чем обращаться с ними как с сотрудниками.

## Сводка методов

| Метод               | Что делает                                                   |
| ------------------- | ------------------------------------------------------------ |
| .size()             | Возвращает реальное количество хранящихся в списке элементов |
| .trimToSize()       | Срезает память до реального количества хранящихся элементов. Если зарезервировано под 100 элементов, а хранится 25, то метод срежет память до 25. |
| .add(item)          | Добавляет элемент в конец списка                             |
| .add(3,  item)      | Ставит новый элемент на третью позицию, а старый третий элемент ставится четвертым, старый четвертый пятым и т.д., т.е. все элементы сдвигаются |
| .ensureCapacity(10) | Расширяет память под указанное количество элементов          |
| .toArray(arr)       | Помещает элементы в заранее созданный массив                 |
| .toArray()          | Создает новый Object[] и помещает в него элементы списка     |
| .get(5)             | Получить пятый элемент списка                                |
| .set(7, item)       | Заменить седьмой элемент списка. Он должен существовать, иначе IndexOutOfBoundsException |
| .remove(5)          | Удалить пятый элемент. Все остальные сдвинутся.              |

## Совместимость

Хорстман, Том 1, стр. 237. Совместимость типизированных и обычных списочных массивов.

Больше показалось как для справки, нежели полезно на практике.