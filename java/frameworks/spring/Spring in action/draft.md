

Нужны отдельные файлы для, например, контроллеров. Здесь буду пилить прогу целиком и общий черновик.

# Словарь

```
@Bean
@Configuration
autowiring
component scanning
Thymeleaf
Spring Framework
Spring Core
Spring MVC
Spring Boot
Lombok
Спецификация Servlet API
Паттерн Front Controller
```

Spring MVC построен на основе Servlet API. Далее буду просто писать спринг для краткости, подразумевая Spring MVC.

Спринг использует паттерн *Front Controller*. Это значит, что каждый запрос попадает в одну точку, где анализируется и отправляется дальше, в место конкретной обработки. Альтернативный подход - когда каждый запрос обрабатывается отдельным скриптом, вроде как я раньше на PHP писал по одному файлу на каждую задачу. А фронт контроллер удобнее тем, что придает обработке централизованный стиль - в нем можно создать какие-то объекты, которые понадобятся на всех других этапах, например, разобрать Http-запрос и упаковать все его элементы вроде заголовков, тела, и т.д. в удобный для прикладного программиста объект, а потом вызвать конкретный контроллер и передать ему этот объект. Таким образом можно действительно оформить рутинные типичные действия во фреймворк, а пользователь фреймворка должен будет просто написать свою конкретную бизнес-логику в дополнительных контроллерах, которые фреймворк потом сам распознает и сам вызовет в нужной ситуации.

В спринге этой центральной точкой является сервлет DispatcherServlet.

Дока https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc здесь подробнее про настройку



Надо будет отдельно почитать про Thymeleaf



# Вопросы

* @SessionAttributes("tacoOrder")



# Черновик

lombok - это библиотека, которая позволяет генерировать геттеры\сеттеры, а также остальные дефолтные методы автоматически. Во время компиляции она добавляет их и в итоге получается будто сам их написал. Еще позволяет автоматически приделывать логгер. В общем, разные штуки по уменьшению бойлерплейта.

# 50 оттенков Spring

Spring Framework - это собирательное название. Как такового "Spring Framework" не существует - есть около 20 разных фреймворков, которые вместе и называются "Spring Framework". Какие-то из них можно использовать отдельно, а какие-то сами пользуются другими. Например, Spring Core - это IoC-контейнер, можно использовать отдельно. Spring MVC - для разработки веб-приложений, он сам пользуется Spring Core.

Spring boot, насколько я пока понимаю, это просто набор автоматизаций и возможностей, вроде автоматической упаковки зависимостей в итоговый jar, настройка "встроенного веб-сервера" и прочая канитель, которая выглядит удобной для нубов для старта, но потом вероятно только мешает.



В своей основе Spring представляет собой контейнер, создающий и управляющий компонентами, из которых состоит приложение. Этот контейнер бывает называют Spring Application Context, а компоненты - бинами (Beans).

Процесс связывания бинов вместе в работающее приложение называется внедрением зависимостей. Это означает, что задача по созданию и передаче компоненту других компонентов, в которых он нуждается, становится обязанностью отдельного специального компонента, называемого *контейнером*.

Описывать бины можно через xml, через специально аннотированные Java-классы, или вообще почти автоматически (доступно благодаря Spring Boot).



# Создание Spring-проекта

Можно воспользоваться генератором шаблонных приложений https://start.spring.io/ Там в общем-то получается обычный мавен или градл проект, разве что подключены выбранные зависимости и в main написан код запуска spring-приложения.

Для начала понадобятся зависимости:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-thymeleaf</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-devtools</artifactId>
    <scope>runtime</scope>
    <optional>true</optional>
</dependency>
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-test</artifactId>
    <scope>test</scope>
</dependency>
...
<build>
    <plugins>
        <plugin>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-maven-plugin</artifactId>
        </plugin>
    </plugins>
</build>
```

*starter* здесь означает, что это не сама зависимость, а набор зависимостей. Т.е. вместо того чтобы писать здесь целую кучу библиотек, просто пишется такая вот "заглушка". 

spring плагин для мавена позволяет все нужные зависимости упаковать в конечный архив, а также запускать spring-приложение с помощью мавена.

Код запуска приложения такой:

```java
package johny.dotsville.tacos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TacoCloudApplication {

	public static void main(String[] args) {
		SpringApplication.run(TacoCloudApplication.class, args);
	}
}
```

Здесь стоит сказать, что аннотация @SpringBootApplication содержит в себе несколько других аннотаций, которые отвечают за разные вещи, вроде сканирования пакетов в поисках компонентов и разные другие штуки. Ну и собственно единственная строчка кода инициализирует приложение - ищет все компоненты, создает их, связывает и т.д.

Приложение идет со встроенным tomcat, так что можно запускать его само по себе.

# Запуск

Прямо из консоли идеи можно запустить проект:

```
./mvnw spring-boot:run
```

Программа запускается на localhost:8080

При внесении изменений придется перезапустить. Можно это делать автоматически, с помощью плагина spring devtools, но надо искать как настраивать. Перезапуск и обновление страницы выглядит не сильно напряжно, так что забей.



# Spring MVC

## Простой контроллер

Пример простого контроллера:

```java
package johny.dotsville.tacos;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeKont {
    @GetMapping("/")
    public String xome() {
        return "home";
    }
}
```

Что могу сказать сейчас: имя контроллера не принципиально, главное чтобы была аннотация @Controller. Эта аннотация позволяет спрингу обнаружить этот класс и создать бин в контексте приложения.

Само расположение контроллера тоже не важно: можно поместить в пакет controllers, или куда хочется.

@GetMapping говорит о том, что метод будет срабатывать на запрос на localhost:8080. Имя самого метода тоже не принципиально. А вот возврат строчки "home" критичен, т.к. это имя представления, которое ищется автоматически. ??? Насколько понимаю, это связано с шаблонизатором Thymeleaf.

Собственно шаблоны нужно класть в папку resources/templates. В данном случае называется шаблон home.html и содержит это:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Taco Cloud</title>
</head>
<body>
<h1>Welcome to Taco Cloud! Лучшие такос вы не ели, наверное...</h1>
<img th:src="@{/images/TacoCloud.png}"/>
</body>
</html>
```

Здесь есть картинка. Картинка относится к статическому контенту. Располагать его надо в папке resources/static. Путь в шаблоне рассчитывается именно он нее. Картинка лежит как видно в паке [resources/static]/images

### Альтернативный способ

Есть другой способ создать контроллер, если он просто отображает страницу:

```java
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("home");
    }
}
```

Любой класс может реализовать интерфейс WebMvcConfigurer, поэтому можно на свое усмотрение создавать такие классы-конфиги.

# Тако-тако, бурито-бурито

## Доменные классы

Доменные классы, ничего особенного, просто чтобы понимать о чем речь. Геттеры\сеттеры, toString, hashCode, equals генерирует библиотека lombok - @Data это его аннотация:

```java
@Data
public class Taco {
    private String name;
    private List<Ingredient> ingredients;
}
```

```java
@Data
public class Ingredient {
    private final String id;
    private final String name;
    private final Type type;

    public enum Type {
        WRAP, PROTEIN, VEGGIES, CHEESE, SAUCE
    }
}
```

```java
@Data
public class TacoOrder {
    private String deliveryName;
    private String deliveryStreet;
    private String deliveryCity;
    private String deliveryState;
    private String deliveryZip;
    private String ccNumber;
    private String ccExpiration;
    private String ccCVV;
    private List<Taco> tacos = new ArrayList<>();

    public void addTaco(Taco taco) {
        this.tacos.add(taco);
    }
}
```

## Контроллер конструктора тако

Контроллер здоровый, тут есть о чем понаписать:

```java
import johny.dotsville.tacos.domain.Taco;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Controller
@RequestMapping("/design")
@SessionAttributes("tacoOrder")
public class DesignTacoController {
    @ModelAttribute
    public void addIngredientsToModel(Model model) {
        List<Ingredient> ingredients = Arrays.asList(
                new Ingredient("FLTO", "Flour Tortilla", Type.WRAP),
                new Ingredient("COTO", "Corn Tortilla", Type.WRAP),
                new Ingredient("GRBF", "Ground Beef", Type.PROTEIN),
                new Ingredient("CARN", "Carnitas", Type.PROTEIN),
                new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES),
                new Ingredient("LETC", "Lettuce", Type.VEGGIES),
                new Ingredient("CHED", "Cheddar", Type.CHEESE),
                new Ingredient("JACK", "Monterrey Jack", Type.CHEESE),
                new Ingredient("SLSA", "Salsa", Type.SAUCE),
                new Ingredient("SRCR", "Sour Cream", Type.SAUCE)
        );
        Type[] types = Ingredient.Type.values();
        for (Type type : types) {
            model.addAttribute(type.toString().toLowerCase(),
                    filterByType(ingredients, type));
        }
    }
    @GetMapping
    public String showDesignForm(Model model) {
        model.addAttribute("taco", new Taco());
        return "design";
    }
    private Iterable<Ingredient> filterByType(List<Ingredient> ingredients, Type type) {
        return ingredients
                .stream()
                .filter(x -> x.getType().equals(type))
                .collect(Collectors.toList());
    }
}
```

* @Slf4j - тоже ломбочная аннотация, создает свойство с логгером.

* @RequestMapping("/design") - аналог RoutePrefix, реагирует на запросы, которые начинаются с /design

  @RequestMapping("/design") на классе + @GetMapping("/tacos") на методе дадут обработку ссылки /design/tacos

* sd

??? Расписать остальное

### ModelAttribute

[Дока](https://docs.spring.io/spring-framework/docs/current/reference/html/web.html#mvc-ann-modelattrib-method-args)

Тут придется еще попотеть-почитать, но пока вот что могу сказать: если добавить несколько методов, помеченный @ModelAttribute'ом, то выполняются они каждый раз в разном порядке с одним исключением: я добавил по сути два пустых таких метода, и они всегда выполнялись до addIngredientsToModel, в котором был какой-то код. Не знаю, имеет ли это значение.

Но факт в том, что они энивей выполняются до методов @GetMapping.

Model - поскольку такого типа у меня нет, очевидно, что это спринговый тип. У него есть методы добавления и получения атрибутов. Атрибуты - это пара "имя-значение", где значение - Object, т.е. положить можно все что угодно.

В методе addIngredientsToModel мы напихали туда ингредиентов, а перед возвратом из showDesignForm поместили объект Taco под именем taco. Вот кусок получившейся модели:

```
sauce=[Ingredient(id=SLSA, name=Salsa, type=SAUCE), Ingredient(id=SRCR, name=Sour Cream, type=SAUCE)], taco=Taco(name=null, ingredients=null)
```

И эта модель, похоже, добавляется в объект запроса(или ответа, хз) сервлета и уже из этого объекта сервлета ее получает шаблонизатор. Таким образом фреймворк и шаблонизатор остаются развязанными, за счет того что взаимодействуют не напрямую, а через элемент сервлета.



## Thymeleaf-шаблон

Шаблон для конфигурации тако помещаем в resources/templates/design.html

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml"
      xmlns:th="http://www.thymeleaf.org">
<head>
    <title>Taco Cloud</title>
    <link rel="stylesheet" th:href="@{/styles.css}" />
</head>
<body>
<h1>Welcome to Taco Cloud!</h1>
<img th:src="@{/images/TacoCloud.png}"/>

<form method="POST" th:object="${taco}">
    <div class="grid">
        <div class="ingredient-group" id="wraps">
            <h3>Designate your wrap:</h3>
            <div th:each="ingredient : ${wrap}">
                <input th:field="*{ingredients}" type="checkbox"
                       th:value="${ingredient.id}"/>
                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="proteins">
            <h3>Pick your protein:</h3>
            <div th:each="ingredient : ${protein}">
                <input th:field="*{ingredients}" type="checkbox"
                       th:value="${ingredient.id}"/>
                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="cheeses">
            <h3>Choose your cheese:</h3>
            <div th:each="ingredient : ${cheese}">
                <input th:field="*{ingredients}" type="checkbox"
                       th:value="${ingredient.id}"/>
                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="veggies">
            <h3>Determine your veggies:</h3>
            <div th:each="ingredient : ${veggies}">
                <input th:field="*{ingredients}" type="checkbox"
                       th:value="${ingredient.id}"/>
                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="sauces">
            <h3>Select your sauce:</h3>
            <div th:each="ingredient : ${sauce}">
                <input th:field="*{ingredients}" type="checkbox"
                       th:value="${ingredient.id}"/>
                <span th:text="${ingredient.name}">INGREDIENT</span><br/>
            </div>
        </div>
    </div>
    <div>
        <h3>Name your taco creation:</h3>
        <input type="text" th:field="*{name}" />
        <br/>
        <button>Submit Your Taco</button>
    </div>
</form>
</body>
</html>
```

Объяснения:

```html
// Вот такой фрагмент:
<div class="ingredient-group" id="wraps">
    <h3>Designate your wrap:</h3>
    <div th:each="ingredient : ${wrap}">
        <input th:field="*{ingredients}" type="checkbox"
               th:value="${ingredient.id}"/>
        <span th:text="${ingredient.name}">INGREDIENT</span><br/>
    </div>
</div>

/ /Превращается в итоге вот в такой html:
<div class="ingredient-group" id="wraps">
    <h3>Designate your wrap:</h3>
    <div>
        <input type="checkbox"
               value="FLTO" 
               id="ingredients1" 
               name="ingredients"/>
        <input type="hidden" name="_ingredients" value="on"/>
        <span>Flour Tortilla</span><br/>
    </div>
    <div>
        <input type="checkbox"
               value="COTO" 
               id="ingredients2" 
               name="ingredients"/>
        <input type="hidden" name="_ingredients" value="on"/>
        <span>Corn Tortilla</span><br/>
    </div>
</div>
```

Полный html:

```html
<!DOCTYPE html>
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
    <title>Taco Cloud</title>
    <link rel="stylesheet" href="/styles.css" />
</head>
<body>
<h1>Welcome to Taco Cloud!</h1>
<img src="/images/TacoCloud.png"/>

<form method="POST">
    <div class="grid">
        <div class="ingredient-group" id="wraps">
            <h3>Designate your wrap:</h3>
            <div>
                <input type="checkbox"
                       value="FLTO" id="ingredients1" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Flour Tortilla</span><br/>
            </div>
            <div>
                <input type="checkbox"
                       value="COTO" id="ingredients2" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Corn Tortilla</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="proteins">
            <h3>Pick your protein:</h3>
            <div>
                <input type="checkbox"
                       value="GRBF" id="ingredients3" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Ground Beef</span><br/>
            </div>
            <div>
                <input type="checkbox"
                       value="CARN" id="ingredients4" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Carnitas</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="cheeses">
            <h3>Choose your cheese:</h3>
            <div>
                <input type="checkbox"
                       value="CHED" id="ingredients5" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Cheddar</span><br/>
            </div>
            <div>
                <input type="checkbox"
                       value="JACK" id="ingredients6" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Monterrey Jack</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="veggies">
            <h3>Determine your veggies:</h3>
            <div>
                <input type="checkbox"
                       value="TMTO" id="ingredients7" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Diced Tomatoes</span><br/>
            </div>
            <div>
                <input type="checkbox"
                       value="LETC" id="ingredients8" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Lettuce</span><br/>
            </div>
        </div>
        <div class="ingredient-group" id="sauces">
            <h3>Select your sauce:</h3>
            <div>
                <input type="checkbox"
                       value="SLSA" id="ingredients9" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Salsa</span><br/>
            </div>
            <div>
                <input type="checkbox"
                       value="SRCR" id="ingredients10" name="ingredients"/>
                <input type="hidden" name="_ingredients" value="on"/>
                <span>Sour Cream</span><br/>
            </div>
        </div>
    </div>
    <div>
        <h3>Name your taco creation:</h3>
        <input type="text" id="name" name="name" value="" />
        <br/>
        <button>Submit Your Taco</button>
    </div>
</form>
</body>
</html>
```

Про таймлиф придется отдельно почитать, но основа такая: 

* Поскольку мы добавляли в модель объекты, то они доступны здесь по именам, под которыми мы их добавляли: ${wrap}, ${protein} и т.д. Каждый такой объект у нас - коллекция элементов, относящихся к виду ингредиента - соус, обертка и т.д.

* Аналогично с `<form method="POST" th:object="${taco}">` - мы добавляли в модель новый пустой объект Taco под именем taco

* `th:each="ingredient : ${wrap}` - это таймлифовский цикл. Поскольку wrap и остальное - коллекция, обходим ее и каждый элемент помещается в переменную с именем ingredient

* `<form method="POST" th:object="${taco}">` - это вероятно значит, что при отправке все значения формы упакуются в объект с именем "taco"

  UPD. Кажется нет?

* Эту переменную `th:value="${ingredient.id}"`,  `th:text="${ingredient.name}"` мы используем для заполнения свойств. th:value превратится в свойство html value, а его значением будет значение ingredient.id

* Вот эта канитель `th:field="*{ingredients}"` похоже что-то вроде спонсора автонумерации, судя по итоговому html. Надо читать, потому что field ведь такого свойства html вроде нет.

  UPD. Скорее всего нет, скорее всего это значит, что этот элемент относится к полю ingredients объекта по имени taco, а звездочка - значит что этих элементов много, т.е. надо оформить их списком

  UPD. Частично нет. Именно th:field означает привязку к объекту, указанному в th:object тега формы. А ingredients - да, это поле. Но звездочка не имеет отношения к тому, что ингредиенты - это список. Внизу у нас есть th:field="*{name}", а  это очевидно не список. Так что звездочка это просто такой синтаксис, маркирующий поле объекта.

Не понятно, почему value становится FLTO и COTO (это id в типе Ingredient). Но это потом.

### Кэширование шаблона

Шаблон парсится один раз, при первом обращении, а потом используется кешированная версия для улучшения производительности. Во время разработки можно добавить в файл *resources/application.properties* строку:

```
spring.thymeleaf.cache=false
```

которая отключит кэширование и тогда можно будет вносить изменения в шаблон и видеть их при обновлении страницы в браузере, без необходимости целиком перезапускать приложение.

P.S. У меня не заработало)

## Отправка, Post

Если для формы не задать атрибут action, то форма пошлется туда же, откуда пришла. А пришла она с design/tacos

??? Не понятно, зачем у формы мы пишем object = taco

UPD. Вероятно затем, чтобы на сервер объект ушел под именем taco

Вот эта хрень

```html
<input th:field="*{ingredients}" type="checkbox"
               th:value="${ingredient.id}"/>
```

Неспроста именно так написана наверное. indredients - такое же имя поля в объекте Taco. Это как-то связано? Да, читай чуть выше.

#### Очень важная вещь

Про порядок отработки методов и что происходит с моделью

* Методы, аннотированные @ModelAttribute, выполняются *каждый* раз *перед* вызовом методов обработки запроса. Это я знаю.

* Модель каждый раз при этом изначально пустая, т.е. она заполняется каждый раз заново. Т.е. когда мы заходим на design/taco первый раз, через get, у нас вызывается метод заполнения ингредиентов. Модель на его старте пустая. Потом когда юзер сделал тако и отправил post, у нас снова вызывается метод заполнения ингредиентов и модель пустая. Но когда мы попадаем в метод, который этот post обрабатывает, processTaco, то у нас в модели кроме ингредиентов уже появляется объект taco.

  Плюс к этому доказательство очистки модели: если в методе getmapping'а добавить условный объект stub, то потом при выполнении postmapping метода этого объекта stub там не будет.

* POST и данные. Похоже, что данные сами по себе не выдергиваются из запроса в модель. Если написать метод обработки тако вот так: `public String processTaco(Model model) {` и  запросить model.getAttribute("taco"), то получим null, хотя на странице заполнения мы все заполнили и отправили, т.е. инфа-то есть. А вот если объявить вот так `public String processTaco(Taco taco, Model model) {` или более явно `public String processTaco(@ModelAttribute Taco taco, Model model) {` то наш объект тако и в модели появляется, и в переменной taco тоже.

* Причем! Появляется он под именем типа. Т.е. если тип назывется Taco, то объект появится в модели под атрибутом taco - т.е. имя типа маленькими буквами. А если тип Tapok, значит под атрибутом tapok.

* Короче говоря, если спринг видит в методе обработки запроса какой-то параметр, он создает пустой объект для этого параметра. А дальше походу если может его заполнить, например, из данных запроса, то заполняет. Если не может - объект остается пустой. И в конце все эти параметры добавляются в модель. Т.е. если метод будет ololo(Taco taco, Burito burito, Model model), то в итоге в модели окажутся объекты Taco и Burito под именами taco и burito соответственно.

Остается не понятным как все же идет привязка того, что пришло в запросе к тому, что есть в параметрах метода?

В общем, похуй. Это очень мутная тема, раскапывать которую - себе дороже. Monkey see, monkey do:

* Перед возвратом страницы с формой, добавь в модель объект, который хочешь заполнить с помощью этой формы, под каким-нибудь именем
* Объяви в thymelead-шаблоне формы объект через th:object и укажи имя из предыдущего пункта
* Среди параметров метода, который обрабатывает запрос с пришедшими от формы данными, объяви параметр типа из пункта 1
* Кайфуй от заполненного объекта

## Converter

Как бы то ни было, после сбора всех галочек, с фронта на бэк уходит набор строк OLOLO с кодом ингредиента. И по этим строкам нам надо создать объекты Ingredient. Это делается с помощью конвертеров:

```java
package johny.dotsville.tacos.converters;

import java.util.HashMap;
import java.util.Map;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import johny.dotsville.tacos.domain.Ingredient;
import johny.dotsville.tacos.domain.Ingredient.Type;

@Component
public class IngredientByIdConverter implements Converter<String, Ingredient> {
    private Map<String, Ingredient> ingredientMap = new HashMap<>();
    public IngredientByIdConverter() {
        ingredientMap.put("FLTO",
                new Ingredient("FLTO", "Flour Tortilla", Type.WRAP));
        ingredientMap.put("COTO",
                new Ingredient("COTO", "Corn Tortilla", Type.WRAP));
        ingredientMap.put("GRBF",
                new Ingredient("GRBF", "Ground Beef", Type.PROTEIN));
        ingredientMap.put("CARN",
                new Ingredient("CARN", "Carnitas", Type.PROTEIN));
        ingredientMap.put("TMTO",
                new Ingredient("TMTO", "Diced Tomatoes", Type.VEGGIES));
        ingredientMap.put("LETC",
                new Ingredient("LETC", "Lettuce", Type.VEGGIES));
        ingredientMap.put("CHED",
                new Ingredient("CHED", "Cheddar", Type.CHEESE));
        ingredientMap.put("JACK",
                new Ingredient("JACK", "Monterrey Jack", Type.CHEESE));
        ingredientMap.put("SLSA",
                new Ingredient("SLSA", "Salsa", Type.SAUCE));
        ingredientMap.put("SRCR",
                new Ingredient("SRCR", "Sour Cream", Type.SAUCE));
    }
    @Override
    public Ingredient convert(String id) {
        return ingredientMap.get(id);
    }
}
```

Все наглядно: аннотируем @Component и реализуем интерфейс Converter.

Ок, благодаря этому спринг сам его найдет. Но как его применить? Мне не нравится то, что оно все автоматически делает. Походу когда придет объект запроса, то данные из него спринг попытается приделать к объекту Taco. Вероятно по имени "ingredients" в объекте запроса он поймет, что прибивать надо к свойству ingredients объекта Taco. В запросе это строка, а в Taco это тип Ingredient. Возможно так он и найдет нужный конвертер.



## Валидация

Для валидации есть разные аннотации:

```java
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
```

Чтобы использовать, понадобится вот такая зависимость:

```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-validation</artifactId>
</dependency>
```

Примеры применения:

```java
@Data
public class Taco {
    // Почему тут нету поля id? В ингредиенте есть, а тут нету.
    @NotNull
    @Size(min = 5, message = "Name must be at least 5 characters long")
    private String name;
    @NotNull
    @Size(min = 1, message = "You must chose at least 1 ingredient")
    private List<Ingredient> ingredients;
}

@Data
public class TacoOrder {
    @NotBlank(message = "Delivery name is required")
    private String deliveryName;
    @NotBlank(message = "Delivery street is required")
    private String deliveryStreet;
    @NotBlank(message = "Delivery city is required")
    private String deliveryCity;
    @NotBlank(message = "Delivery state is required")
    private String deliveryState;
    @NotBlank(message = "Delivery zip is required")
    private String deliveryZip;
    @CreditCardNumber(message = "Not a valid credit card number")
    private String ccNumber;
    @Pattern(regexp="^(0[1-9]|1[0-2])([\\/])([1-9][0-9])$",
            message="Must be formatted MM/YY")
    private String ccExpiration;
    @Digits(integer=3, fraction=0, message="Invalid CVV")
    private String ccCVV;
    private List<Taco> tacos = new ArrayList<>();

    public void addTaco(Taco taco) {
        this.tacos.add(taco);
    }
}
```

Пример проверки ошибок:

```java
@PostMapping
public String processOrder(@Valid TacoOrder order, Errors errors) {
    if (errors.hasErrors()) {
        return "orderForm";
    }
```

Применяем @Valid к параметру, которых хотим валидировать и среди параметров объявляем Error, чтобы спринг его заполнил.

## Показ ошибок валидации

```html
<span class="validationError"
        th:if="${#fields.hasErrors('ccNumber')}"
        th:errors="*{ccNumber}">Credit card errors</span>
```

Интуитивно дб понятно. Только про fields загуглить надо отдельно.