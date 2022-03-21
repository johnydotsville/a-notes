# .equals()

При сравнении объектов должны соблюдаться следующие правила (для упрощения буду под x == y подразумевать x.equals(y)):

* Симметричность - если `x == y` дает true, то `y == x` тоже должно давать true
* Транзитивность - если `x == y` true и `y == z` true, то `x == z` тоже должно быть true
* `x == null` всегда должно давать false
* Рефлексивность - `x == x` должно всегда давать true
* Согласованность - если состояние объектов x и y не меняется, то повторные сравнения `x == y` должны давать одинаковые результаты

Реализация сравнения может выглядеть так:

```java
import java.util.Objects;

class Employee {
    ...
	@Override public boolean equals(Object other) {
		if (this == other)
			return true;
		if (other == null)
			return false;
		if (!(other instanceof Employee))  // <-- Тут возможны варианты: делать так
			return false;
		//if (this.getClass() != other.getClass())  // <-- Или так
		//	return false;
		Employee emp = (Employee) other;
		return Objects.equals(this.name, emp.name)
			&& this.salary == emp.salary;
    }
}
```

Сравниваем два объекта Employee:

```java
Employee john1 = new Employee("John Carter");
Employee john2 = new Employee("John Carter");

System.out.println(john1.equals(john2));  // true
```

Теперь сравним Employee и Manager, где Manager - это подкласс Employee:

```java
Employee john1 = new Employee("John Carter");
Manager john2 = new Manager("John Carter");

System.out.println(john1.equals(john2));  // тоже true, хотя классы разные
```

В этом и заключается нюанс - считать ли в этом случае объекты одинаковыми, если сравниваются объекты разных, но родственных классов? Если да, тогда реализация как в первом варианте. Если нет, тогда второй вариант.

Для подклассов есть правило - в методе сравнения сперва вызывать метод сравнения суперкласса, а потом уже добавлять сравнения собственных уникальных полей:

```java
class Manager extends Employee {
    ...
    @Override public boolean equals(Object other) {
        if (super.equals(other) == false)  // <-- Если тут false, дальше даже не смотрим
            return false;
        Manager manager = (Manager) other;
        return this.bonus == manager.bonus;
    }
}
```

И вот тут важен тот самый нюанс: если в суперклассе пользоваться instanceof, а не .getClass(), то при сравнении Manager с Employee возникнет исключение:

```java
Employee john1 = new Employee("John Carter");
Manager john2 = new Manager("John Carter");

System.out.println(john2.equals(john1));  // CastClassException
```

Поскольку Employee очевидно является Employee, то проверка в суперклассе пройдет нормально. Но в подклассе будет попытка преобразовать Employee к Manager, а это конечно же вызовет ошибку.

Поэтому при реализации метода сравнения нужно учитывать эти моменты.

P.S. Небольшое дополнение. Вроде очевидно, но пусть будет на всякий случай:

```java
Employee john1 = new Employee("John Carter");
Manager john2 = new Manager("John Carter");

boolean employeeIsManager = john1 instanceof Manager;  // false
boolean managerIsEmployee = john2 instanceof Employee;  // true
```

# .hashCode()

Хэш код для объекта представляет собой целое число (может быть отрицательным). Для объектов его реализация по умолчанию подразумевает вычисление на основе адреса в памяти. Для строк вот такое:

```java
int hash = 0;
for (int i = 0; i < length(); i++)
    hash = 31 * hash + charAt(i);
```

Методы hashCode и equals связаны. Т.е. если `x.equals(y)` дает true, значит и хэш-коды у этих объектов должны быть одинаковыми. Как этого добиться? Использовать для вычисления хэш-кода те же поля, что используются в .equals() для сравнения. Если для сотрудника это id, значит и хэш-код вычисляется от id.

Пример реализации метода hashCode:

```java
import java.util.Objects;

class Employee {
    ...
    @Override public int hashCode() {
        return Objects.hash(name, salary);
    }
}
```

Objects.hash(...) удобен для случаев, когда для вычисления хэш-кода используется несколько полей.

Для вычисления хэш-кода для одного элемента есть специальные методы, например:

```java
@Override public int hashCode() {
    return Objects.hash(name)
        + Integer.hashCode(salary);
}
```

У них есть преимущества. Например, если name будет null, то вызов name.hashCode() даст исключение, в то время как Objects.hash(null) безопасно даст 0.

Если среди полей есть массив, то на этот случай есть `Arrays.hashCode()` TODO можно потом сделать пример на этот случай

# .toString()

У многих объектов метод преобразования в строку заключается в выводе имени класса и значений полей в квадратных скобках:

```java
class Employee {
    ...
    @Override public String toString() {
        return getClass().getName() + "[name=" + this.name
            + ", salary=" + this.salary + "]";
    }
}

class Manager extends Employee {
    ...
    @Override public String toString() {
        return super.toString()  // <-- Утилизируем метод суперкласса
            + "[bonus=" + bonus + "]";  // и дополняем собственными полями
    }
}
```

Результат использования:

```java
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Employee john1 = new Employee("John Carter");
        john1.setSalary(10_000);
        Employee carry = new Manager("Carry Weaver");
        carry.setSalary(10_000);

        System.out.println(john1);
        // com.company.sampleprog.Employee[name=John Carter, salary=10000]
        System.out.println(carry);
        // com.company.sampleprog.Manager[name=Carry Weaver, salary=10000][bonus=0]

        int[] arr = new int[] { 1, 3, 10, 448 };
        System.out.println(arr);  // [I@6f496d9f
        System.out.println(Arrays.toString(arr));  // [1, 3, 10, 448]

        int[][] arr2Dim = new int[][] {
            { 5, 4, 7},
            { 8, 25, 16},
            { 30, 45, 1678}
        };
        System.out.println(Arrays.deepToString(arr2Dim));
        // [[5, 4, 7], [8, 25, 16], [30, 45, 1678]]
    }
}
```

Массив не переопределяет метод .toString(), он такой же как у Object, а тот выводит просто тип и некое число. Поэтому для вывода строкового представления массива есть специальный метод в утилите Arrays.

