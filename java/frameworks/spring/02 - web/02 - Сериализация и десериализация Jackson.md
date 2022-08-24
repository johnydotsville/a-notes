# Зависимости

```xml
<dependency>
    <groupId>com.fasterxml.jackson.core</groupId>
    <artifactId>jackson-databind</artifactId>
    <version>2.13.3</version>
</dependency>
```

Основные классы Jackson:

* JsonParser - это самый базовый класс, можно сказать, low-level. Относится к Streaming API и другие классы используют под капотом именно его. При использовании Streaming API не происходит загрузка всего json в память, а происходит обход элементов (вроде бы). Можно обойти все элементы руками, получать их имя и т.д., а можно использовать методы для прямого поиска элементов и преобразования их в нужные типы. Зачем обходить что-то руками - не знаю, нормально работает поиск по имени и преобразование в нужное.

* ObjectMapper - характеризуется тем, что загружает весь json в память, где он представляется в виде дерева из элементов типа JsonNode. По заявлениям, имеет более широкий функционал. Не пользовался им, что за функционал не знаю.

  

# JSON-объект

Исходный json будет такой:

```json
{
	"data": {
		"company": {
			"id": "2ae1f337-0764-4dd1-a1a6-cba9f3a69de9",
			"shortName": "ООО Золотое Кольцо",
			"address": "г. Москва, ул. Кошкина, дом. 20",
			"url": "golden-ring.ru",
			"ogrn": 1172406846802,
			"inn": 2448774304,
			"kpp": 301244001,
			"registerDate": "2005-07-12",
			"contacts": {
				"phones": [
					{
						"value": "8(495)342-15-10",
						"description": "Отдел продаж"
					},
					{
						"value": "8(495)342-15-15",
						"description": "Отдел закупок"
					},
					{
						"value": "8(495)342-15-20",
						"description": "Общий"
					}
				],
				"emails": [
					{
						"value": "golden.ring@rings.ru",
						"description": "Общий"
					},
					{
						"value": "sales.golden.ring@rings.ru",
						"description": "Отдел продаж"
					},
					{
						"value": "comment.golden.ring@rings.ru",
						"description": "Отзывы и предложения"
					}
				]
			},
			"description": "Производитель ювелирных украшений со столетней историей",
			"activityTypes": [
				400,
				354,
				768
			],
			"isValidated": true,
			"geo": {
				"lat": 55.638561,
				"long": 37.670021
			}
		}
	}
}
```

Я попробовал уместить в него все интересные случаи:

* Значение объекта - тоже сложный объект
* Поля разных типов: целое, десятичное, логическое, строка, дата, массив примитивов, массив объектов
* Поле называется как ключевое слово в языке

# Десериализация

## Классы для хранения данных

Если сделать структуру классов, полностью соответствующую структуре JSON, тогда десериализация пройдет автоматически. Однако у нас в json объект компании лежит в объекте *data*, который больше ничего не содержит и поэтому делать отдельный класс для него не хочется. Поэтому сделаем ручную десериализацию.

Объект компании:

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonDeserialize(using = CompanyFullDtoDeserializer.class)
public class CompanyFullDto {
    private String id;
    private String shortName;
    private String address;
    private String url;
    private long ogrn;
    private long inn;
    private long kpp;
    private LocalDate registerDate;
    private Contacts contacts;
    private String description;
    private Integer[] activityTypes;
    @JsonProperty("isValidated")
    private boolean isValidated;
    private Geolocation geo;
}
```

Чтобы сказать Jackson'у, какой класс нужно использовать для десериализации объектов CompanyFullDto, используем аннотацию *@JsonDeserialize*.

Контакты:

```java
@Getter @Setter
public class Contacts {
    private List<DescriptionObject> phones;
    private List<DescriptionObject> emails;
}
```

Геолокация:

```java
@Getter @Setter
public class Geolocation {
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("long")
    private double longitude;
}
```

Общий объект для хранения любых пар "значение-описание", как в телефонах и почтах:

```java
@Getter @Setter
public class DescriptionObject {
    private String value;
    private String description;
}
```

## Класс-десериализатор

```java
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;

import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

public class CompanyFullDtoDeserializer extends JsonDeserializer<CompanyFullDto> {
    @Override
    public CompanyFullDto deserialize(JsonParser parser, DeserializationContext context)
            throws IOException, JacksonException {
        ObjectCodec codec = parser.getCodec();
        JsonNode node = codec.readTree(parser);

        try {
            CompanyFullDto companyFullDto = new CompanyFullDto();
            companyFullDto.setId(node.findValue("id").asText());
            companyFullDto.setShortName(node.findValue("shortName").asText());
            companyFullDto.setAddress(node.findValue("address").asText());
            companyFullDto.setUrl(node.findValue("url").asText());
            companyFullDto.setOgrn(node.findValue("ogrn").asLong());
            companyFullDto.setInn(node.findValue("inn").asLong());
            companyFullDto.setKpp(node.findValue("kpp").asLong());

            JsonNode registerDateNode = node.findValue("registerDate");
            companyFullDto.setRegisterDate(codec.treeToValue(registerDateNode, LocalDate.class));

            Contacts contacts = codec.treeToValue(node.findValue("contacts"), Contacts.class);
            companyFullDto.setContacts(contacts);

            companyFullDto.setDescription(node.findValue("description").asText());

            JsonNode activityTypesNode = node.findValue("activityTypes");
            companyFullDto.setActivityTypes(codec.treeToValue(activityTypesNode, Integer[].class));

            companyFullDto.setValidated(node.findValue("isValidated").asBoolean());

            JsonNode geoNode = node.findValue("geo");
            companyFullDto.setGeo(codec.treeToValue(geoNode, Geolocation.class));

            return companyFullDto;
        } catch (Exception ex) {
            RuntimeException rex = new RuntimeException(
                    "Ошибка при десериализации класса " + CompanyFullDtoDeserializer.class.getName());
            throw rex;
        }
    }
}
```

Класс-десериализатор наследуем от `JsonDeserializer<CompanyFullDto>`, указывая, с каким типом должен работать наш десериализатор.

Общий процесс работы таков:

* Ищем с помощью `node.findValue("id")` нужный нам элемент по имени

* Если элемент простого типа, используем методы `.asText()`, `.asLong()` и т.д. для преобразования

* Если элемент сложного типа, используем метод кодека `codec.treeToValue(node, targetClass)`. Например:

  * Преобразование в дату `codec.treeToValue(registerDateNode, LocalDate.class)`

  * Преобразование в массив целых `codec.treeToValue(activityTypesNode, Integer[].class)` причем здесь нельзя использовать массив примитивов, нужен именно объектный Integer
  * Преобразование в наш тип `codec.treeToValue(node.findValue("contacts"), Contacts.class)` При этом класс Contacts должен полностью соответствовать структуре элемента json, чтобы все заполнилось автоматически. Но если нужна какая-то другая структура, всегда можно отдельно найти нужные элементы и собрать из них желаемый объект вручную.

# Сериализация





# Привязка С\Д классов к DTO





# @JsonProperty