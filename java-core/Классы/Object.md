# .equals

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



 

