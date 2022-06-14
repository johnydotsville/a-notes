# Подключение

Понадобится две зависимости:

```xml
<dependency>
    <groupId>junit</groupId>
    <artifactId>junit</artifactId>
    <version>4.13.2</version>
    <scope>test</scope>
</dependency>
<dependency>
    <groupId>org.junit.jupiter</groupId>
    <artifactId>junit-jupiter-api</artifactId>
    <version>5.9.0-M1</version>
    <scope>test</scope>
</dependency>
```



# Использование

Тесты размещаются в папке test мавена. Пример класса тестов:

```java
package johny.dotsville.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ActorTest {
    private Actor tom1;
    private Actor tom1Other;
    private Actor tom2;
    private Actor huck;
    private Actor anull;

    @BeforeEach
    public void setup() {
        tom1 = new Actor();
        tom1.setId(1);
        tom1.setFirstName("Tom");
        tom1.setLastName("Sawyer");

        tom1Other = new Actor();
        tom1Other.setId(1);
        tom1Other.setFirstName("Tom");
        tom1Other.setLastName("Sawyer");

        tom2 = new Actor();
        tom2.setId(2);
        tom2.setFirstName("Tom");
        tom2.setLastName("Sawyer");

        huck = new Actor();
        huck.setId(1);
        huck.setFirstName("Huck");
        huck.setLastName("Finn");

        anull = null;
    }

    @Test
    @DisplayName("Объект Actor равен самому себе")
//    @Disabled
    public void equalsItself() {
        Assertions.assertTrue(tom1.equals(tom1));
    }

    @Test
    @DisplayName("Объект Actor равен аналогично заполненному другому объекту Actor")
//    @Disabled
    public void equalsSameActor() {
        Assertions.assertAll("actor equality",
                () -> Assertions.assertTrue(tom1.equals(tom1Other)),
                () -> Assertions.assertTrue(tom1Other.equals(tom1)));
    }

    @Test
    @DisplayName("Объект Actor не равен другому Actor с такими же id, но разными другими полями")
//    @Disabled
    public void notEqualsActorWithSameIdButDifferentFields() {
        Assertions.assertFalse(tom1.equals(huck));
    }

    @Test
    @DisplayName("Объект Actor никогда не равен null-объекту")
//    @Disabled
    public void notEqualsNull() {
        Assertions.assertFalse(tom1.equals(anull));
    }
}
```

Из важного:

* Перед выполнением каждого тестирующего метода (т-метода для краткости) создается новый экземпляр класса теста. Поэтому метод, который должен выполняться *единожды* перед каждым т-методом, и должен быть статическим. Я этого факта сначала не знал и удивлялся, что за идиотизм - почему @BeforeAll метод обязан быть статическим. Потом стало понятно.
* Важно, чтобы аннотации были из одного пакета. Потому что изначально у меня не срабатывал @BeforeEach метод и оказалось потому, что @Test был из пакета junit, а не jupiter.api