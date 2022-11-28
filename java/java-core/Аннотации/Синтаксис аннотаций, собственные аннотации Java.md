# Вопросы

- [ ] Что означает синтаксис `public @interface X`?
- [ ] Что такое мета-аннотации и как это связано с возможностью применять к элементу сразу несколько аннотаций? Какой синтаксис у мета-аннотаций?
- [ ] Аннотации @Target, @Retention, @Inherited, @Documented, @Repeatable, @Native являются частью джавы или относятся к спрингу? Для чего нужна каждая из них?
  - [ ] Какие значения (концептуально) есть у @Retention?
  - [ ] Как под капотом работает @Repeatable? Для чего нужен контейнер? Как через рефлексию получить все значения, добавленные с помощью repeatable-аннотации?

P.S. Я был удивлен, когда обнаружил этот файл пустым. Поэтому просто скопировал сюда часть конспекта по спрингу, посвященную аннотациям. Тема не то что большая и будет сильно меняться, поэтому такое копирование думаю не повредит.

# Формат аннотаций

```java
@Target(value={METHOD, ANNOTATION_TYPE})
   @Retention(value=RUNTIME)
   @Documented
   @Inherited
public @interface X
```

* Конструкцией `@interface` объявляется аннотация. Соответственно, `public @interface X` объявляет аннотацию X, которую мы потом будем использовать как `@X`

* Применение к X аннотаций @Target, @Retention, @Documented, @Inherited говорит о том, что Х является комбинацией этих аннотаций (с указанными параметрами). Т.е., применяя к элементу аннотацию X, мы применяем к нему все эти четырея аннотации

Аннотации, которые применяются к другим аннотациям, называются *мета-аннотациями*. В данном случае @Target, @Retention, @Documented, @Inherited - это мета-аннотации

# Собственные аннотации джавы

Нужно понимать, какие аннотации являются частью джавы, а какие принадлежат спрингу. В самой джаве не так уж много встроенных аннотаций (см. [тут](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/package-summary.html)):

```
@Target
@Retention
@Inherited
@Documented
@Repeatable
@Native
```

* `@Target` - определяет, к какому элементу можно применить аннотацию @X

  Например, `@Target(value={METHOD, ANNOTATION_TYPE})` значит, что аннотацию @X можно применить к методу и к аннотациям

  [Полный список возможных целей с описаниями](https://docs.oracle.com/javase/8/docs/api/java/lang/annotation/ElementType.html). Из наиболее интересных: TYPE (к классу, но и не только), CONSTRUCTOR, FIELD, METHOD

  ```java
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.ANNOTATION_TYPE)
  public @interface Target
  ```

  P.S. Интересно, как аннотация применяется к самой себе...

* `@Retention` (пер., "удержание", "сохранение") - определяет "как долго аннотация @X будет прилеплена к элементу", например: 

  * `@Retention(value = RetentionPolicy.RUNTIME)` - аннотация будет доступна во время выполнения. Тогда, например, с помощью рефлексии мы сможем узнать, что у класса есть аннотация @X
  * `@Retention(value = RetentionPolicy.SOURCE)` - аннотация доступна только во время компиляции. Во время выполнения у класса уже не будет @X

  ```java
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.ANNOTATION_TYPE)
  public @interface Retention
  ```

* `@Documented` - что-то связанное с автогенерацией документации

  ```java
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.ANNOTATION_TYPE)
  public @interface Documented
  ```

* `@Inherited` - если аннотация @X отмечена как @Inherited, то применение @X к классу приводит к тому, что и у его потомков автоматически будет @X

  ```java
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.ANNOTATION_TYPE)
  public @interface Inherited
  ```

* @Repeatable - аннотацию можно применять к элементу несколько раз, с разными значениями

  ```java
  @Documented
  @Retention(RetentionPolicy.RUNTIME)
  @Target(ElementType.ANNOTATION_TYPE)
  public @interface Repeatable
  ```

* @Native - такой аннотацией отмечают поля-константы, на которые можно ссылаться из нативного кода

  ```java
  @Documented
  @Target(ElementType.FIELD)
  @Retention(RetentionPolicy.SOURCE)
  public @interface Native
  ```

## @Repeatable

Чтобы пользоваться этой аннотацией, нужно подготовить ей техническую основу:

* Создаем аннотацию, которую может понадобиться использовать несколько раз:

  ```java
  @Repeatable(Marks.class)  // <-- Этот класс не создается сам по себе, он - наша задача
  public @interface Mark {
      String value() default "unmarked";
  }
  ```

  Обратим внимание на класс Marks. Это контейнер для аннотаций, который тоже нужно создать самим

* Создаем контейнер:

  ```java
  @Retention(value = RetentionPolicy.RUNTIME)
  public @interface Marks {
      Mark[] value();
  }
  ```

  У контейнера задаем характеристики аннотации, например, ее доступность

* Теперь можем применять @Mark сколько нам нужно:

  ```java
  @Mark(value = "feature")  // <-- Раз
  @Mark(value = "new")  // <-- И два
  public class MarriageService {
      public static void main(String[] args) {
          MarriageService marser = new MarriageService();
          
          Marks marks = marser.getClass().getAnnotation(Marks.class);  // <-- А Mark.class нету
          
          for (Mark mark : marks.value()) {
              System.out.println(mark.value());
          }
      }
  }
  ```

Важно - у класса MarriageService не будет аннотации @Mark, а только @Marks. Все потому, что @Repeatable - просто синтаксический сахар, который преобразуется вот в такую конструкцию (которую в принципе можно использовать и напрямую при желании):

```java
@Marks({
    @Mark(value = "feature"),
    @Mark(value = "new")
})
public class MarriageService
```
