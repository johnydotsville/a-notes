# Зависимость, адрес и порт приложения

Зависимость для работы с вебом:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
```

Запускается веб-приложение по умолчанию на localhost:8080, но можно изменить порт через application.yml или application.properties:

```yaml
# yaml
server:
  port: 49080
  
# properties
server.port=49080
```

# Общий вид контроллера

```java
@RestController(value = "customControllerName")  // Имя опционально
@RequestMapping(path = "/api/currency")  // Общий префикс для всех методов, если надо
public class CurrencyController {
    private final CurrencyValuesGenerationService service;
    private final ApplicationProps props;
    
    @Autowired
    public CurrencyController(CurrencyValuesGenerationService currencyValuesGenerationService, 
                              ApplicationProps props) {
        this.service = currencyValuesGenerationService;
        this.props = props;
    }
    
    @GetMapping("/values/{currency}")  // Полный путь будет /api/currency/values/RUB
    public ResponseEntity currencyValues(@PathVariable String currency) {
        List<CurrencyValue> result = service.getValues(currency);
        if (result.size() == 0) {
            return ResponseEntity.ok("Нет значений для указанной валюты");
        }
        return ResponseEntity.ok(result);
    }
}
```

По умолчанию id бина контроллера будет сформировано по его имени, т.е. currencyController. Но если в разных пакетах есть контроллеры с одинаковыми именами, будет конфликт. Вот в этом случае и может пригодиться явное указание имени.

# Методы контроллера

## Get, Post и прочие методы

Тут два способа:

* Использовать специальные аннотации *@GetMapping*, *@PostMapping* и т.д.:

  ```java
  @GetMapping("/hello")
  public String hello() {
      return "Hello, get!"
  }
  ```

* Использовать более общую аннотацию *@RequestMapping*:

  ```java
  @RequestMapping(path = "/hello", method = GET)
  public String hello() {
      return "Hello, get!"
  }
  ```

У всех аннотаций обычно есть параметр value, который является алиасом для какого-либо типичного параметра. Например, для @XxxMapping это алиас для параметра path (они взаимозаменяемы), так что url можно указывать через любой из этих параметров. Или можно вообще не указывать, как в первом примере, если параметр всего один.

## Извлечение параметров

* Если параметры - часть пути, аннотация *@PathVariable*:

  ```java
  @GetMapping("/just/test/{title}/nomatter/{rating}")
  public String pathVariableDemo(@PathVariable String title, @PathVariable int rating) {
      return title + " " + rating;
  }
  ```

  ```
  localhost:8080/api/currency/just/test/resident/nomatter/5
  ```

  Если хочется другие имена параметров:

  ```java
  @GetMapping("/just/test/{title}/nomatter/{rating}")
  public String pathVariableDemo(@PathVariable(name = "title") String argTitle,
                                 @PathVariable(name = "rating") int argRating) {
      return argTitle + " " + argRating;
  }
  ```

  Параметр name актуален и для всех других аннотаций, предназначенных для извлечения параметров.

* Если параметры в query string, аннотация *@RequestParam*:

  ```java
  @GetMapping("/just/test")
  public String requestParamDemo(@RequestParam String title, @RequestParam int rating) {
      return title + " " + rating;
  }
  ```

  ```
  localhost:8080/api/currency/just/test?title=Resident%20evil&rating=8
  ```

* Если объект передается в теле запроса, тогда используем аннотация *@RequestBody*:

  ```java
  @PostMapping("/just/test/bodydemo")
  public String bodyDemo(@RequestBody SomeData someData) {
      return String.format("Принято, title: %s, rating: %d", someData.title, someData.rating);
  }
  ```

  При этом десериализация в простых случаях происходит автоматически.

* Если нужно извлечь параметр из заголовков, аннотация *@RequestHeader*:

  ```java
  @PostMapping("/just/test/headerparam")
  public String headerParamDemo(@RequestHeader String correlationId) {
      return correlationId;
  }
  ```


## Возврат\приём JSON

Спринг использует *Jackson* для сериализации\десереализации. Он срабатывает автоматически. Так что если вернуть какой-нибудь объект, он автоматически сериализуется и вернется в виде JSON:

```java
@GetMapping("/just/test/autoserialization")
public SomeData autoSerializationDemo() {
    return new SomeData("Resident evil 2", 6);
}
...
@Getter @Setter @AllArgsConstructor
private static class SomeData {
    private String title;
    private int rating;
}
```

```json
// Ответ
{
	"title": "Resident evil 2",
	"rating": 6
}
```

Если же принимать JSON, то в случае корректного объекта в параметре метода десериализация тоже произойдет автоматически:

```java
@PostMapping("/just/test/autodeserialization")
public String autoDeserializationDemo(@RequestBody SomeData someData) {
    return String.format("Принято, title: %s, rating: %d", someData.title, someData.rating);
}
```

Ну а если нужно написать правила для сериализации\десериализации, то это отдельная тема в самостоятельном конспекте.

## Если хочется вернуть просто 200, 500 и т.д.

Используем класс *ResponseEntity*:

```java
@GetMapping("/generation/stop")
public ResponseEntity stop() {
    try {
        service.stop();
        return ResponseEntity.ok().build();
    } catch (Exception ex) {
        return ResponseEntity.internalServerError().build();
    }
}
```
