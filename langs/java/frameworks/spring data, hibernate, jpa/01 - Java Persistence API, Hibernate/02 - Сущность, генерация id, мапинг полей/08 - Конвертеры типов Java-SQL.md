# ANSI SQL и вендоры

В SQL есть стандарт, определяющий типы данных, но многие СУБД его игнорируют, т.к. сформировали свои системы типов еще до появления стандарта. JDBC обеспечивает абстракцию, которая позволяет автоматически сопоставлять основные Java-типы и SQL-типы, специфичные для каждого вендора. Для этого мы указываем в настройках SQL-диалект.

# Создание собственного конвертера

Зачем? Например, сотрудники получают зарплату в разных единицах, кто-то в рублях, кто-то в долларах и т.д. В БД зарплата хранится в виде строки `150000 RUB` или `2000 USD`. Такое значение требует конвертации в наш тип `Salary`. Выглядит примерно так:

```java
public class Employee {

	@Column(name = "salary")  // <-- В БД это обычная строка.
	@Convert(converter = SalaryConverter.class)  // <-- Конвертер, который займется преобразованием туда-сюда.
	private Salary salary;  // <-- Поле требует конвертации при загрузке \ сохранении в БД.

}
```

## Конвертер

### Пишем класс SalaryConverter

Конвертер должен:

* Реализовывать интерфейс `jakarta.persistence.AttributeConverter`.

* Быть аннотирован через `@Converter`.

Конвертер содержит методы для преобразования исходного типа в БД'шный и обратно. Строго говоря, БД'шный тип это конечно не совсем БД'шный, а какой-нибудь простой стандартный, который хибер уже сумеет сам перевести в реальный БД'шный:

```java
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(  // <-- Аннотация обязательна
    autoApply = true)  // <-- Автоматически применять конвертер ко всем полям типа Salary
public class SalaryConverter
	  implements AttributeConverter<Salary, String> {  // <-- Программный тип и БД'шный

  @Override
  public String convertToDatabaseColumn(Salary salary) {  // <-- Преобразование из "нашего" в "БД'шный"
	  return salary.toString();
  }

  @Override
  public Salary convertToEntityAttribute(String s) {  // <-- Преобразование из "БД'шного" в "наш"
	  return Salary.fromString(s);
  }

}
```

Вместо аннотации `@Converter` можно зарегистрировать класс в файле orm.xml. Как это сделать я конечно сейчас разбираться не буду.

Опции `@Converter`:

* `autoApply = true | false` - по умолчанию false. Определяет, должен ли конвертер применяться автоматически.
  * `true` - никаких дополнительных аннотаций для полей типа Salary не нужно - конвертер будет применяться каждый раз, когда встретится поле типа Salary.
  * `false` - придется для каждого поля типа Salary, где нужна конвертация, писать аннотацию `@Convert` и указывать конвертер.

### Включение \ отключение конвертера

Указываем для поля сущности этот конвертер:

```java
@Entity
@Table(name = "employee")
@Getter @Setter
public class Employee {

	...

	@Column(name = "salary")
	@Convert(  // <-- Указываем конвертер
        converter = SalaryConverter.class, 
        disableConversion = false)
	private Salary salary;

}
```

Можно отключать конвертер при необходимости через `disableConversion = true`. Когда это может понадобиться? Допустим, тип Salary присутствует в нескольких таблицах и значит конвертер будет использоваться в нескольких сущностях. Потом в какой-то таблице значение зарплаты и валюту разнесут по двум разным полям. Тогда можно будет сделать мапинг через @Embeddable. Но в других таблицах Salary останется как одно поле. Получится, что конвертер все еще будет нужен, но не везде. И мы сможем сделать выборочное отключение. P.S. Пока я конечно не очень представляю как это будет выглядеть, как Salary одновременно будет и @Embeddable и конвертируемое, но в книге такое оправдание включения \ отключения было упомянуто, так что пусть будет.

## Класс Salary

Требования к классу:

* Класс должен реализовывать интерфейс `Serializable`, чтобы хибер мог хранить его в кэше.
* Нужно реализовать методы `equals` и `hashCode`, потому что тип сложный.

```java
@Getter
public class Salary 
    	implements Serializable {  // <-- Для хранения в кэше второго уровня

    private final BigDecimal value;
    private final Currency currency;

    public Salary(BigDecimal value, Currency currency) {
        this.value = value;
        this.currency = currency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Salary)) return false;

        final var sal = (Salary)o;
        if (!value.equals(sal.value)) return false;
        if (!currency.equals(sal.currency)) return false;

        return true;
    }

    public int hashCode() {
        return Objects.hash(value, currency);
    }

    @Override
    public String toString() {
        return value + " " + currency;
    }

    public static Salary fromString(String string) {  // <-- Инкапсулируем метод создания Salary из строки
        String[] s = string.split(" ");
        var salary = new Salary(
                new BigDecimal(s[0]),
                Currency.getInstance(s[1]));
        return salary;
    }
}
```

# Конвертирование и наследование

Пусть у нас будет класс `Адрес`, в котором `страна` и `город` будут строками, а `почтовый код` - отдельным классом. При этом будет несколько подклассов почтового кода. Например, российский из 6 цифр и американский из 5.

Напишем программу так, чтобы:

* При считывании данных из БД и формировании объекта Адрес создавался не общий почтовый код, а почтовый код определенной страны.
* Укажем конвертер не в самом классе адреса, а в классе Сотрудника, где адрес используется.

```java
@Embeddable
@Getter
public class Address {

    @Column(name = "country")
    private String country;

    @Column(name = "city")
    private String city;

    @Column(name = "zipcode")
    private Zipcode addrZipcode;  // <-- Отдельный класс под почтовый код

    private Address() { }

    private Address(String country, String city, Zipcode zipcode) {
        this.country = country;
        this.city = city;
        this.addrZipcode = zipcode;
    }

}
```

Базовый класс почтового кода не встроенный, его заполнение мы реализуем через конвертер, чтобы учесть наследование:

```java
@Getter
public class Zipcode {

    protected String code;

    private Zipcode() { }

    public Zipcode(String code) {
        this.code = code;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Zipcode)) return false;

        final var zip = (Zipcode)o;
        if (!code.equals(zip.code)) return false;

        return true;
    }

    public int hashCode() {
        return Objects.hash(code);
    }

}
```

Подклассы почтового кода:

```java
public class RussiaZipcode extends Zipcode {

    public RussiaZipcode(String code) {
        super(code);
    }

}
```

```java
public class UsaZipcode extends Zipcode {

    public UsaZipcode(String code) {
        super(code);
    }

}
```

Конвертер для почтового кода:

```java
@Converter
public class ZipcodeConverter
        implements AttributeConverter<Zipcode, String> {

    @Override
    public String convertToDatabaseColumn(Zipcode zipcode) {
        return zipcode.getCode();
    }

    @Override  // <-- Будем создавать объект нужного подкласса на основе длины почтового кода
    public Zipcode convertToEntityAttribute(String s) {
        if (s.length() == 6)
            return new RussiaZipcode(s);
        if (s.length() == 5)
            return new UsaZipcode(s);

        throw new IllegalArgumentException("Неизвестный почтовый код в БД: " + s);
    }

}
```

Класс Сотрудник, в котором есть встроенное поле адреса:

```java
@Entity
@Table(name = "employee")
@Getter @Setter
public class Employee {

    ...

    @Convert(  // <-- Конвертер почтового кода указываем не внутри Адреса, а внутри Сотрудника
        converter = ZipcodeConverter.class,
        attributeName = "addrZipcode")  // <-- Поле класса Адрес, для которого требуется конвертация
    private Address address;
    
    // Если несколько конвертеров
    @Converts({
        @Convert(converter = ZipcodeConverter.class, attributeName = "addrZipcode")
    })
	private Address address;
    
}
```

Здесь продемонстрировано как можно задать конвертер "косвенно", т.е. не напрямую в классе, где есть поле, требующее конвертации, а в более высоком классе. Если предположить, что в Адресе было бы больше полей, требующих конвертации, то мы могли бы задать все необходимые конвертеры в Сотруднике, используя аннотацию `@Converts`.

