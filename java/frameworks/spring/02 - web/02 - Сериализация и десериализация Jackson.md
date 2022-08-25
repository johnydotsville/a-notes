Есть демка в demos/jackson.demo

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

Если сделать структуру классов, полностью соответствующую структуре JSON, тогда десериализация пройдет автоматически. Обычно лучше так и делать, работает нормально.

Но для демонстрации возможностей Jackson'а сделаем все вручную. У нас в json объект компании лежит в объекте *data*, который больше ничего не содержит и поэтому представим, что создавать отдельный класс для него нам не хочется, а значит автоматическая десериализация не сработает.

```java
@PostMapping("/serialization")
public CompanyFullDto serializationDemo(@RequestBody CompanyFullDto companyFullDto) {
    return companyFullDto;
}
```

Объект компании:

```java
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@JsonDeserialize(using = CompanyFullDtoDeserializer.class)
@JsonSerialize(using = CompanyFullDtoSerializer.class)
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

Чтобы сказать Jackson'у, какой класс нужно использовать для сериализации\десериализации объектов CompanyFullDto, используем на этом классе аннотацию `@JsonDeserialize` и `@JsonSerialize`. 

Остальные классы, вроде контактов, геолокации, исполняют вспомогательную роль и для них аннотаций, касающихся С\Д, не нужно.

Контакты:

```java
@Getter @Setter
public class Contacts {
    private DescriptionObject[] phones;
    private DescriptionObject[] emails;
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

## Почему массивы, а не списки?

В случае с полем activityTypes я не нашел удобного способа извлечь массив "вручную автоматически ", потому что метод treeToValue, во-первых, не работает с примитивами (поэтому использован Integer, а не int), а, во-вторых, синтаксис "ручного автоматического" извлечения в список показался мне слишком сложным по сравнению с извлечением в массив. В случае полностью автоматической десериализации списки подходят нормально. Но для ручной я пока что буду использовать массивы, пока не увижу хороший пример со списками. Для почты и телефонов массивы поставил просто для единообразия.

"Ручное автоматическое" извлечение значит, что хотя мы и сами пишем метод десериализации в целом, но при этом хотим в нем максимально использовать автоматические средства, чтобы например не обходить руками массив поэлементно, а извлечь все значения разом и положить в нужное поле объекта.

## Класс-десериализатор

Класс-десериализатор наследуем от `JsonDeserializer<>`, указывая, с каким типом он должен работать:

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
            CompanyFullDto dto = new CompanyFullDto();
            dto.setId(node.findValue("id").asText());
            dto.setShortName(node.findValue("shortName").asText());
            dto.setAddress(node.findValue("address").asText());
            dto.setUrl(node.findValue("url").asText());
            dto.setOgrn(node.findValue("ogrn").asLong());
            dto.setInn(node.findValue("inn").asLong());
            dto.setKpp(node.findValue("kpp").asLong());

            JsonNode registerDateNode = node.findValue("registerDate");
            dto.setRegisterDate(codec.treeToValue(registerDateNode, LocalDate.class));

            JsonNode contactsNode = node.findValue("contacts");
            dto.setContacts(codec.treeToValue(contactsNode, Contacts.class));

            dto.setDescription(node.findValue("description").asText());

            JsonNode activityTypesNode = node.findValue("activityTypes");
            dto.setActivityTypes(codec.treeToValue(activityTypesNode, Integer[].class));

            dto.setValidated(node.findValue("isValidated").asBoolean());

            JsonNode geoNode = node.findValue("geo");
            dto.setGeo(codec.treeToValue(geoNode, Geolocation.class));

            return dto;
        } catch (Exception ex) {
            RuntimeException rex = new RuntimeException(
                    "Ошибка при десериализации класса " + CompanyFullDtoDeserializer.class.getName());
            throw rex;
        }
    }
}
```

Общий процесс работы таков:

* Ищем с помощью `node.findValue("id")` нужный нам элемент по имени

* Если элемент простого типа, используем методы `.asText()`, `.asLong()` и т.д. для преобразования

* Если элемент сложного типа, используем метод кодека `codec.treeToValue(node, targetClass)`. Например:

  * Преобразование в дату `codec.treeToValue(registerDateNode, LocalDate.class)`
* Преобразование в массив целых `codec.treeToValue(activityTypesNode, Integer[].class)` причем здесь нельзя использовать массив примитивов, нужен именно объектный Integer
  * Преобразование в наш тип `codec.treeToValue(node.findValue("contacts"), Contacts.class)` При этом класс Contacts должен полностью соответствовать структуре элемента json, чтобы все заполнилось автоматически. Но если нужна какая-то другая структура, всегда можно отдельно найти нужные элементы и собрать из них желаемый объект вручную.
* Для особо хардкорных случаев можно совсем вручную обходить элементы, получать информацию о них (например, является ли элемент массивом), но я пока не знаю, зачем такой low-level может понадобиться, поэтому даже не разбирался.

# Сериализация

## Класс-сериализатор

Класс-сериализатор наследуем от `JsonSerializer<>`, указывая, с каким типом он должен работать. Здесь я попытался показать примеры записи похожих полей разными способами (опять же, автоматическая сериализация работает нормально, а все это - для демонстрации возможностей на случай, если придется делать руками):

```java
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

public class CompanyFullDtoSerializer extends JsonSerializer<CompanyFullDto> {
    @Override
    public void serialize(CompanyFullDto companyFullDto, JsonGenerator generator, SerializerProvider provider)
            throws IOException {
        generator.writeStartObject();  // {

        generator.writeFieldName("data");  // "data":
        generator.writeStartObject();  // {

        generator.writeFieldName("company");  // "company":
        generator.writeStartObject();  // {

        // Способ A записать поле - одной командой - разом имя и значение
        generator.writeStringField("id", dto.getId());
        generator.writeStringField("shortName", dto.getShortName());
        generator.writeStringField("address", dto.getAddress());
        generator.writeStringField("url", dto.getUrl());
        generator.writeNumberField("ogrn", dto.getOgrn());
        generator.writeNumberField("inn", dto.getInn());
        generator.writeNumberField("kpp", dto.getKpp());
        DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        generator.writeStringField("registerDate", dto.getRegisterDate().format(dateFormat));

        generator.writeFieldName("contacts");  // "contacts":
        generator.writeStartObject(); // {
        // 1й способ записать массив
        generator.writeArrayFieldStart("phones");  // "phones": [
        for (DescriptionObject dob : dto.getContacts().getPhones()) {
            generator.writeStartObject();  // {
            // Способ B записать поле - через две команды
            generator.writeFieldName("value");  // Сначала объявить поле
            generator.writeString(dob.getValue());  // Потом записать его значение
            generator.writeFieldName("description");
            generator.writeString(dob.getDescription());
            generator.writeEndObject();  // }
        }
        generator.writeEndArray();  // ] (phones)
        // 2й способ записать массив
        generator.writeFieldName("emails");  // "emails":
        generator.writeStartArray();  // [
        for (DescriptionObject dob : dto.getContacts().getEmails()) {
            generator.writeObject(dob);  // На этот раз пишем объект целиком, а не поле за полем
        }
        generator.writeEndArray();  // ] (emails)
        generator.writeEndObject();  // } (contacts)

        generator.writeStringField("description", dto.getDescription());

        int[] activityTypes = Arrays.stream(dto.getActivityTypes())
                .mapToInt(i -> i).toArray();
        // 3й способ записать массив
        generator.writeFieldName("activityTypes");
        // Метод умеет писать только простые типы, и кажется вообще не умеет пользовательские
        generator.writeArray(activityTypes, 0, activityTypes.length);

        generator.writeBooleanField("isValidated", dto.isValidated());
        
        generator.writeFieldName("geo");
        generator.writeObject(dto.getGeo());  // Тоже запишем этот объект разом

        generator.writeEndObject();  // } (company)
        generator.writeEndObject();  // } (data)

        generator.writeEndObject();  // }
    }
}
```

## Принципы. Запись простого поля

Как видно из примера, у нас два способа создать поле: сначала объявить его, а потом записать значение. Или записать разом и имя поля, и значение.

```json
{  // 1
	"id": "2ae1f337-0764-4dd1-a1a6-cba9f3a69de9"
}  // 2
```

* Первый способ - сначала создать поле, потом записать значение:

  ```java
  generator.writeStartObject();  // 1 просто для полноты картины, объяснения, если нужно, дальше
  ...        
  generator.writeFieldName("id");
  generator.writeString(companyFullDto.getId());
  ...
  generator.writeEndObject();  // 2
  ```

* Второй - записать разом и название поля, и значение:

  ```java
  generator.writeStringField("id", companyFullDto.getId());
  ```
  
  Такие "комбинированные" методы хорошо подходят, когда поле и значение можно записать  одновременно. С их помощью можно записать не только простые поля, но и комплексный объект, например, разом все контакты: `generator.writeObjectField("contacts", companyFullDto.getContacts());`
  
  Т.о. методы writeStringField, writeObjectField читаются как "создать и записать поле с именем X, являющееся строкой" и "создать и записать поле с именем X, являющееся объектом".

## Запись массива

```json
{
	"activityTypes": [ 
        400, 
        354, 
        768 
    ]
}
```

Опять же, есть два(+1) способа записать массив:

* С помощью отдельного метода *.writeArray* записать массив целиком:

  ```java
  int[] activityTypes = Arrays.stream(companyFullDto.getActivityTypes())
      .mapToInt(i -> i).toArray();
  generator.writeFieldName("activityTypes");
  generator.writeArray(activityTypes, 0, activityTypes.length);
  ```

  Нюансы: во-первых, для массива нету "комбинированного" метода записи имени поля + значения, поэтому сначала нужно создать поле, а потом уже записать массив. Во-вторых, этот метод *writeArray()* не поддерживает массивы оберток, поэтому предварительно приходится преобразовывать Integer[] в int[]. Также он похоже в принципе не умеет записывать ссылочные типы, так что записать так условный массив Person'ов не получится.

* Записать массив поэлементно:

  ```java
  generator.writeArrayFieldStart("activityTypes");
  for (Integer value: companyFullDto.getActivityTypes()) {
      generator.writeNumber(value);
  }
  generator.writeEndArray();
  ```

  В этом случае никаких преобразований уже делать не нужно.

* Сначала объявим поле, потом откроем массив:

  ```java
  generator.writeFieldName("emails");
  generator.writeStartArray();
  for (DescriptionObject dob : dto.getContacts().getEmails()) {
      generator.writeObject(dob);
  }
  generator.writeEndArray();
  ```

## "Ручная" запись объекта

Когда у нас есть готовый объект, который мы хотим записать в поле, мы можем это сделать методом *writeObjectField*. Но что если нам нужно создать поле с произвольным объектом? Допустим, мы хотим записать только телефоны из контактов. Объект contacts содержит и телефоны, и почты. Так что если мы запишем его целиком, то получим и то, и то. А если возьмем просто часть phones, потеряем вложенность в contacts.

Поэтому нам нужно сначала создать поле с желаемым именем под объект, потом объявить начало нового объекта, записать что нужно, и закрыть объект. Также новый объект открывается в самом начале json'а, это как бы корень, без какого-то имени:

```java
generator.writeStartObject();  // 1  корень
...
generator.writeFieldName("contacts");  // Создаем поле под объект contacts
generator.writeStartObject();  // <-- a
generator.writeObjectField("phones", companyFullDto.getContacts().getPhones());
generator.writeEndObject();  // <-- b
...
generator.writeEndObject();  // 2
```

```json
{  // 1 корень
	"contacts": {  // a
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
		]
	}  // b
}  // 2
```

# Привязка С\Д классов к DTO

На данный момент я знаю два способа указать Jackson'у, какой класс он должен использовать для С\Д:

* Через *@JsonDeserialize* непосредственно на DTO-классе, который будет С\Д-ваться:

  ```java
  @JsonDeserialize(using = CompanyFullDtoDeserializer.class)
  public class CompanyFullDto {
      private String id;
      private String shortName;
  	...
  }
  ```

* Через наследника класса `SimpleModule`:

  ```java
  @Service
  public class GoodsDtoJsonModule extends SimpleModule {
      public GoodsDtoJsonModule() {
          this.addSerializer(GoodsDto.class, new GoodsDtoSerializer());
          this.addDeserializer(GoodsDto.class, new GoodsDtoDeserializer());
      }
  }
  ```

Первый мне кажется удобнее и нагляднее, преимуществ второго пока не ощутил.

# @JsonProperty

Эта аннотация хорошо помогает, когда нужно явно задать имя, которое у элемента есть\должно быть в json'е при С\Д.

## Имя совпадает с ключевым словом

```json
"geo": {
    "lat": 55.638561,
    "long": 37.670021
}
```

long - ключевое слово, которое не удастся использовать в DTO в качестве имени поля, а lat просто выглядит не очень информативно:

```java
@Getter @Setter
public class Geolocation {
    @JsonProperty("lat")
    private double latitude;
    @JsonProperty("long")
    private double longitude;
}
```

При обратной сериализации поля так же попадут в lat и long, как и было.

## Причуды lombok

```json
"isValidated": true
```

У лобка есть особенность, когда дело касается boolean полей, названных по паттерну *is*Something. Он создает для них геттер getSomething, а сеттер делает setSomething и после сериализации поле isValidated в итоговом json будет под именем validated. Поэтому нужно явно указать целевое имя для таких *is* полей:

```java
@JsonDeserialize(using = CompanyFullDtoDeserializer.class)
public class CompanyFullDto {
    ...
    @JsonProperty("isValidated")
    private boolean isValidated;
    ...
}
```

