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

