# Пример

Эти классы использованы в объяснении работы с бинами.

Городская служба предоставляет людям разные услуги ("сервисы"), среди них - возможность пожениться. Конкретно за функциональность женитьбы отвечает ЗАГС, который выдает сертификат о браке, а в нем указаны двое людей, заключивших брак.

Реализуем задачу через классы Person, MarriageCertificate, MarriageService и CityService:

```java
public class MarriageService {
    
    public MarriageCertificate marry(Person man, Person woman) {
        return new MarriageCertificate(man, woman, LocalDate.now());
    }
    
}
```

```java
// Это класс-агрегатор разной функциональности от разных сервисов, "фасад"
public class CityService {
    
    private final MarriageService marriageService;
    private final SomeOtherService unknown;

    public CityService(MarriageService marriageService, SomeOtherService unknown) {
        this.marriageService = marriageService;
        this.unknown = unknown;
    }

    public MarriageCertificate marry(Person man, Person woman) {
        return marriageService.marry(man, woman);
    }
    
    public Something someOtherService(...) {
        return unknown.doSome(...);
    }
    
}
```

Классы MarriageService и CityService будут бинами. Спринг создаст их при запуске приложения и соберет вместе как положено. Остальные классы, Person и MarriageCertificate, - это обычные программные объекты, которые мы создаем через new по мере необходимости.

```java
@Getter @AllArgsConstructor
public class Person {
    private Name name;
    private LocalDate birth;
}
```

```java
@Getter @AllArgsConstructor
public class Name {
    private String firstName;
    private String lastName;

    @Override
    public String toString() {
        return String.format("%s %s", firstName, lastName);
    }
}
```

```java
@Getter @AllArgsConstructor
public class MarriageCertificate {
    private Person husband;
    private Person wife;
    private LocalDate registeredAt;

    @Override
    public String toString() {
        return "Данный документ свидетельствует о том, что гражданин " + husband.getName() +
                " и гражданка " + wife.getName() + " официально зарегистрировали свой брак " +
                registeredAt.toString();
    }
}

```

