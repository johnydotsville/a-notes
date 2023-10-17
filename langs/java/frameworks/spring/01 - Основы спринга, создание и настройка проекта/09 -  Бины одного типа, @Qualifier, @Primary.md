# Бины одного типа

Для спринга не проблема, когда в контексте есть несколько бинов одного типа, потому что уникальность бина определяется именем, а не типом. Проблемой это становится, когда ему нужно при автопривязке выбрать конкретный бин, потому что если у нас три бина одного типа, то теоретически подходит любой. Так что мы должны ему дать подсказку, что и где выбирать в таком случае.

Пусть у нас сервис для работы со временем и две реализации:

```java
@Service
public class Watchmaker {
    private final TimeService localTs;
    private final TimeService greenwichTs;

    @Autowired
    public Watchmaker(TimeService localTs, TimeService greenwichTs) {  // <-- Неоднозначность
        this.localTs = localTs;
        this.greenwichTs = greenwichTs;
    }
    
    public void useLocalTimeService() {
        System.out.println(localTs.whoAmI());
    }

    public void useGreenwichTimeService() {
        System.out.println(greenwichTs.whoAmI());
    }
}
```

```java
public interface TimeService {
    String whoAmI();
}
...
@Service
public class GreenwichTimeService implements TimeService {
    @Override
    public String whoAmI() {
        return GreenwichTimeService.class.toString();
    }
}
...
@Service
public class LocalTimeService implements TimeService {
    @Override
    public String whoAmI() {
        return LocalTimeService.class.toString();
    }
}
```

Помогите Даше найти нужный бин.

## @Qualifier

Помечаем этой аннотацией всех кандидатов одного типа:

```java
@Service("lts")  // <-- Если хотим указать имя бина явно, то можем сделать это тут
@Qualifier
public class LocalTimeService implements TimeService {
...
@Service
@Qualifier("gts")  // <-- Или тут
public class GreenwichTimeService implements TimeService {
```

А в местах автопривязки указываем, какой именно кандидат нас интересует:

```java
@Autowired
public Watchmaker(@Qualifier("localTimeService") TimeService localTs,
                  @Qualifier("greenwichTimeService") TimeService greenwichTs) {
    this.localTs = localTs;
    this.greenwichTs = greenwichTs;
}
```

## @Primary

Если бы у нас была немного другая ситуация, когда требуются не все реализации, а только одна:

```java
@Service
public class Watchmaker {
    private final TimeService timeService;

    @Autowired
    public Watchmaker(TimeService timeService) {  // <-- Нам нужны не все, а одна
        this.timeService = timeService;
    }

    public void useTimeService() {
        System.out.println(timeService.whoAmI());
    }
}
```

Тогда мы могли бы снабдить одного из кандидатов аннотацией `@Primary` и спринг выбрал бы его:

```java
@Service
@Primary  // <-- Предпочитаемый кандидат
public class GreenwichTimeService implements TimeService {
    @Override
    public String whoAmI() {
        return GreenwichTimeService.class.toString();
    }
}
```
