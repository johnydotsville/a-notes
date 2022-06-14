# Характеристики методов

Пара базовых свойств, касающихся equals и hashCode:

* Если два объекта равны, то и хэш-коды у них равны, всегда
* Если у двух объектов хэш-коды равны, это не всегда означает, что объекты равны. Потому что количество хэш-кодов в зависимости от реализации может быть меньше, чем количество объектов.

Обязательные свойства для функции equals:

1. Рефлексивность

   Т.е. сравнение с самим собой обязательно дает true:

   ```java
   a.equals(a) == true
   ```

2. Транзитивность ("перенос")

   Она же косвенность: если `a = b`, а `b = c`, тогда `a = c`

   ```java
   a.equals(b) == true;
   b.equals(c) == true;
   => a.equals(c) == true;
   ```

3. Симметричность: если `a = b`, то и `b = a`

   ```java
   a.equals(b) == true;
   => b.equals(a) == true;
   ```

4. Неравенство null: при сравнении объекта с null всегда дб false

5. Постоянность: если сравниваемые объекты не менялись, equals должен выдавать одинаковый результат



# Реализация

## equals

Рассмотрим способ реализации equals на примере класса и его подкласса. В суперклассе выполним, во-первых, "проверку на дурака" (сравнение с null, две одинаковые ссылки и на тип данных), а, во-вторых, сравним общие свойства (id):

```java
public abstract class AbstractEntity {
    ...
	@Override
    public boolean equals(Object that) {
        if (that == null || this.getClass() != that.getClass()) return false;
        if (this == that) return true;

        AbstractEntity entity = (AbstractEntity) that;
        return Objects.equals(id, entity.id);
    }
}
```

А в подклассе воспользуемся проверкой из суперкласса и добавим проверку личных полей подкласса (имя и фамилия):

```java
public class Actor extends AbstractEntity {
    ...
	@Override
    public boolean equals(Object that) {
        if (!super.equals(that)) return false;

        Actor actor = (Actor) that;
        return Objects.equals(firstName, actor.firstName)
            && Objects.equals(lastName, actor.lastName);
    }
}
```

## hashCode

Сам по себе хэш-код в Java является целым числом (int). Метод hashCode может использоваться сторонними типами. Например, реализациями HashMap, HashSet и т.д., поэтому не надо недооценивать его важность.

Реализация по умолчанию в классе Object заключается в генерации случайного случайного числа. Т.о. у одного и того же объекта при каждом запуске программы будет разный хэш-код.

Я видел вот такой способ реализации:

```java
@Override
public int hashCode() {
    int hash = super.hashCode();
    hash = 89 * hash + (firstName != null ? firstName.hashCode() : 0);
    hash = 89 * hash + (lastName != null ? lastName.hashCode() : 0);
    return hash;
}
```

Но потом встретил вот такой. Он кажется мне более правильным:

```java
public abstract class AbstractEntity {
    ...
	@Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
```

и теперь подкласс:

```java
@Override
public int hashCode() {
    return Objects.hash(super.hashCode(), firstName, lastName);
}
```

