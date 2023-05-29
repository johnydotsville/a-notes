# Типы и null

В kotlin два вида типов: поддерживающие null и неподдерживающие.

Поддерживающие null пишутся со знаком `?` в конце, например:

```kotlin
String?
Int?
и т.д.
```



# Операторы для работы с null

## Оператор безопасного вызова ?.

kotlin не позволяет вызывать функции на объектах, которые потенциально могут быть null. В таких случаях нужно пользоваться оператором безопасного вызова `?.`, который в случае вызова на null вернет null, а в другом случае выполнит функцию нормально:

```kotlin
val username = readLine()  // Если просто нажать Enter, ничего не вводя, вернет пустую строку
val capitalized = username?.capitalize()
println(capitalized)
```

### Безопасный вызов и функция let

Функцию let можно вызвать на не-null значении и передать в нее лямбду, в которой со значением можно что-то сделать. В качестве единственного параметра лямбда принимает это самое значение, доступное через ключевое слово it. let возвращает значение, которое необходимо поместить в переменную:

```kotlin
val raw = null
val username = raw?.let {
    if (it.isNotBlank())
        it.capitalize()
    else
        "noname"
}
println(username)
// Если raw = null, вернет null. Если пробел - noname, иначе - имя пользователя с большой буквы
```

## Оператор контроля non-null !!.

Этот оператор позволяет вызвать метод на объекте, игнорируя факт, что там может быть null. И если там действительно null, то будет выброшено исключение NullPointerException:

```kotlin
val raw = null
val username = raw!!.capitalize()
println(username)
```

## if и null

Имеется старая-добрая возможность сравнить значение переменной с null обычными операторами сравнения:

```kotlin
val username = null
    if (username != null)
        println(username)
    else if (username == null)
        println("username is null")
```

## Элвис-оператор ?: null-coalescing operator

Если слева от этого оператора null, то возвращает то что справа от оператора. Удобно для присвоения дефолтных значений в случае null:

```kotlin
val raw = null
val username : String = raw?: "anonymous"
println(username)  // anonymous
```

# Исключения

В kotlin все исключения НЕпроверяемые.

## Выброс исключения

```kotlin
throw IllegalStateException("Player cannot juggling swords.")
```

## Объявление своего исключения

Делается через создание класса. Про классы будет дальше, так что тут простенький пример:

```kotlin
class UnskilledSwordsJugglerException() :
    IllegalStateException("Player cannot juggle swords.")
```

```kotlin
fun excTest() {
    var swordsJuggling: Int? = null  // Сколькими мечами может жонглировать игрок
    val isSkilled = (1..3).shuffled().last() == 3
    if (isSkilled) {
        swordsJuggling = 2
    }
    checkSwordsJuggling(swordsJuggling)
    swordsJuggling = swordsJuggling!!.plus(1)
}

fun checkSwordsJuggling(swordsJuggling: Int?) {
    swordsJuggling ?: throw UnskilledSwordsJugglerException()
}
```

## Обработка исключений

Классический try-catch

```kotlin
try {
    checkSwordsJuggling(swordsJuggling)
    swordsJuggling = swordsJuggling!!.plus(1)
} catch (ex: UnskilledSwordsJugglerException) {
    println("Поймали исключение UnskilledSwordsJugglerException.")
    println(ex)
}
```

# Проверка условий

В kotlin есть несколько функций, упрощающих проверку значений и выброс исключений в случае их некорректности. У всех похожий синтаксис:

```kotlin
функцияПроверки(перем) { 
    // Какой-то код, не обязательно
    "Сообщение в случае ошибки" // Текст для ошибки, который попадет в выброшенное исключение
}
```

* `checkNotNull`

  Выбрасывает IllegalStateException, если указанная переменная равна null, иначе возвращает ее значение:

  ```kotlin
  try {
      checkNotNull(swordsJuggling, { "Нет мечей, чтобы жонглировать" })
      swordsJuggling = swordsJuggling!!.plus(1)
      // Или так
      // swordsJuggling = checkNotNull(swordsJuggling, { "Нет мечей, чтобы жонглировать" }).plus(1)
  } catch (ex: IllegalStateException) {
      println(ex)
  }
  ```

* `require`

  Выбрасывает IllegalArgumentException, если условие не соблюдено. Ничего не возвращает.

  ```kotlin
  fun juggleSwords(swordsCount: Int) {
      require(swordsCount >= 3, { "Жонглировать можно минимум тремя мечами" })
      println("Игрок жонглирует $swordsCount мечами.")
  }
  ```

* `requireNotNull`

  Выбрасывает IllegalArgumentException, если переменная равна null, иначе возвращает ее значение. Отличие от checkNotNull чисто логическое - require... используются в начале функций для проверки входных параметров, а check - в любом другом месте.

* `error("Просто ошибка и ниипёт")`

  Просто выбрасывает IllegalStateException с указанным сообщением. Может быть поймано в catch.

  ```kotlin
  try {
      error("Просто ошибка и ниипёт")
  } catch (ex: IllegalStateException) {
      println("Поймали ошибку: $ex")
  }
  ```

* `assert`

  TODO: Отдельная тема

