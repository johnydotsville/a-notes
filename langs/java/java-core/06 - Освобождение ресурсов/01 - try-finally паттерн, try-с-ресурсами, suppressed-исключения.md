# Освобождение ресурсов

Если программа пользуется файлами или прочими ресурсами, работа с которыми затрагивает ОС, то надо эти ресурсы освобождать. Метод `finalize()` больше не рекомендуется к использованию в прикладных программах. Вместо этого есть другие способы:

* Если освобождение может подождать до завершения работы виртуальной машины, используем метод `Runtime.addShutdonwHook()` для ввода “перехватчика завершения”. 

* Начиная с Java 9 можно через класс `Cleaner` зарегистрировать действие, которое выполнится, когда объект станет недоступен.

# try-finally паттерн

```java
try {
    try {
        throw new Exception();
    } finally {
        throw new Exception();
    }
}
catch (Exception ex) {
    System.out.println("Сделали вид, что обработали");
}
```

Плюсы такого паттерна в том, что мы разом ловим потенциальные исключения и в блоке try, и в блоке finally. При этом если какие-то ошибки происходят в try, их гарантированно можно обработать в finally, например, освободить ресурсы.

# try с ресурсами

## Интерфейс AutoCloseable

try c ресурсами это что-то вроде шарпового using'а, совмещенного с блоком catch. С Java 7 можно вместо такого:

```java
try {
    FileInputStream fin = new FileInputStream(args[0]);
    try {
        byte[] bytes = fin.readAllBytes();
    } finally {
        fin.close();
    }
}
catch (IOException ex) {
    System.out.println("А у вас молоко убежало!");
}
```

писать вот так:

```java
try (FileInputStream fin = new FileInputStream(args[0]),    // Можно указывать сразу несколько
    FileInputStream fin2 = new FileInputStream(args[1])) {  // ресурсов в одном блоке
    byte[] bytes = fin.readAllBytes();
}
catch (IOException ex) {  // Выполнится после освобождения ресурсов
    System.out.println("А у вас молоко убежало!");
}
finally {  // Выполнится после освобождения ресурсов
    ...
}
```

При этом класс, использованный таким образом в try, должен реализовывать интерфейс `AutoCloseable`. В нем единственный метод `.close()`, содержащий код по освобождению ресурсов.  Он выполняется перед catch и finally.

Сначала всегда выполнится тело .close(), а только потом поймается исключение. Пример:

```java
class MyAutoCloseable implements AutoCloseable {
    
    public void close() {
        System.out.println("Выполняем MyAutoCloseable.close()");
    }
    
}
```

```java
try (MyAutoCloseable fin = new MyAutoCloseable()) {
    throw new Exception();
}
catch (Exception ex) {
    System.out.println("А у вас молоко убежало!");
}
finally {
    System.out.println("Выполяется finally");
}

// Вывод
Выполняем MyAutoCloseable.close()
А у вас молоко убежало!
Выполяется finally
```

## Интерфейс Closable

До Java 7 был интерфейс `Closeable`, тоже с одним методом *.close()*. Его особенностью было то, что он выбрасывал IOException. Поэтому, чтобы сделать возможность выбрасывать более общие исключения, они добавили AutoCloseable (он выбрасывает Exception), а Closeable сделали его потомком. Так что теперь предпочтительнее использовать AutoCloseable.

# suppressed-исключения

Применительно к интерфейсу Autoclosable, может быть ситуация, когда внутри .close() возникнет исключение. При этом есть два сценария:

1. Исключение было только в .close()
2. Исключение было и в try, и в .close()

В первом сценарии, исключение из .close() попадает в catch.

Во втором сценарии исключение из .close() называется *подавленным* (suppressed). Оно добавляется вовнутрь исходного try-исключения и try-исключение выбрасывается повторно. Таким образом, если нам надо получить именно исключение, возникшее в .close(), нужно искать его в исходном исключении через метод `.getSuppressed()`, который возвращает массив Throwable[].

Пример:

```java
class MyAutoCloseable implements AutoCloseable {

    public void close() throws Exception {
        throw new Exception("Исключение внутри MyAutoCloseable.close()");
    }

}
```

Сценарий 1:

```java
try (MyAutoCloseable fin = new MyAutoCloseable()) {
    // Сценарий 1, в try нет исключения
}
catch (Exception ex) {
    System.out.println(ex.getMessage());
    // Массив пуст, т-к в try не было исключений
    Throwable[] suppressed = ex.getSuppressed();
    if (suppressed.length != 0)
        for (Throwable sup : suppressed)
            System.out.println(sup.getMessage());
}
finally {
    System.out.println("Выполяется finally");
}

// Вывод
Исключение внутри MyAutoCloseable.close()
Выполяется finally
```

Сценарий 2:

```java
try (MyAutoCloseable fin = new MyAutoCloseable()) {
    throw new Exception("Исключение в try");  // Сценарий 2
}
catch (Exception ex) {
    System.out.println(ex.getMessage());
    // Исключение из .close() попало в этот массив
    Throwable[] suppressed = ex.getSuppressed();
    if (suppressed.length != 0)
        for (Throwable sup : suppressed)
            System.out.println(sup.getMessage());
}
finally {
    System.out.println("Выполяется finally");
}

// Вывод
Исключение в try
Исключение внутри MyAutoCloseable.close()
Выполяется finally
```