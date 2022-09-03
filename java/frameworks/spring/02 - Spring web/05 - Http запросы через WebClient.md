Есть много разных средств для работы с Http-запросами. Я пробовал Feign и WebClient. Feign это отдельная разработка, но спринг сделал свой фейн с блэкджеком и шлюхами, OpenFeign, в котором перемешались спринговые аннотации, фейновые и в общем чуть ли не на первом запросе сложнее обычного get'а посыпались проблемы с установкой дополнительных заголовков и т.д. В общем, фейн не нужен. До тех пор, пока не будет хорошей демонстрации его использования на чем-то сложнее hello word'а.

WebClient это уже спринговая вещь, с ним удалось сделать то, что нужно, хотя тоже не без проблем и костылей. Но все-таки удалось, поэтому я остановился на нем.

# Зависимости

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-webflux</artifactId>
</dependency>
```

# Создание клиента и простой get-запрос

Создается клиент с помощью билдера. Можно сразу указать дефолтные заголовки и прочие вещи, которые должны добавляться к *каждому* запросу:

```java
import org.springframework.web.reactive.function.client.WebClient;

WebClient client = WebClient.builder()
    .defaultHeaders(headers -> {
        headers.add("Accept-Encoding", "application/gzip");
        headers.add("X-RapidAPI-Key", "0debe16f9fmsh471a868bba9017ap1b06e1jsn55cc6a32f154");
        headers.add("X-RapidAPI-Host", "google-translate1.p.rapidapi.com");
    })
    .build();
```

Для отправки запроса мы должны с помощью клиента создать объект нужного типа запроса и настроить оставшиеся вещи, вроде собственно uri, дополнительных заголовков, нужные этому конкретному запросу, тип контента и прочее. Можно писать все сразу в стиле Stream API, но здесь объявляются переменные, просто чтобы показать промежуточные типы:

```java
import org.springframework.web.reactive.function.client.WebClient.UriSpec;
import org.springframework.web.reactive.function.client.WebClient.RequestBodySpec;

private static final String URL_ALL_LANGS = "https://google-translate1.p.rapidapi.com/language/translate/v2/languages";
...
    public String getAllLangs() {
    UriSpec<RequestBodySpec> uriSpec = client.method(HttpMethod.GET);
    RequestBodySpec bodySpec = uriSpec.uri(URL_ALL_LANGS);
    String result = bodySpec.retrieve()
        .bodyToMono(String.class)
        .block();

    return result;
}
```



# Дополнительные заголовки, параметры

В следующем примере показана отправка post-запроса, тело которого содержит данные не в виде привычного json, а в виде urlencoded-строки. В связи с этим нужно явно выставить соответствующий заголовок Content-Type:

>Вообще, по-хорошему, нужно пользоваться методом .body(BodyInserters.fromFormData(params)), тогда заголовок выставится автоматически. Однако, "каноничный" способ не работает. Возможно баги, возможно недокументированные особенности, но найти вменяемый ответ не удалось, поэтому приделано немножко на скотч - с помощью UriComponentsBuilder из словаря формируется строка, из нее удаляется первый символ (знак вопроса) и полученная строка явно вставляется в тело. Profit!

```java
import org.springframework.web.util.UriComponentsBuilder;

private static final String URL_TRANSLATE = "https://google-translate1.p.rapidapi.com/language/translate/v2";
...
public String translate(String sourceLang, String targetLang, String text) {
    MultiValueMap params = new LinkedMultiValueMap();
    params.add("source", sourceLang);
    params.add("target", targetLang);
    params.add("q", text);

    String urlEncodedParams = UriComponentsBuilder.newInstance().queryParams(params).build()
        .toUriString().substring(1);

    String result = client.method(HttpMethod.POST)
        .uri(URL_TRANSLATE)
        .headers(headers -> {
            headers.add("Content-Type", "application/x-www-form-urlencoded");
        })
        .bodyValue(urlEncodedParams)
        .retrieve()
        .bodyToMono(String.class)
        .block();

    return result;
}
```

# Извлечение результата