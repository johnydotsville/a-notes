# Вопросы

- [ ] Что такое "главный поток"?
- [ ] Чем технически и концептуально отличается подход "implements Runnable" от "extends Thread", если и там, и там нужно переопределить метод .run?
  - [ ] Чем отличается запуск потока через .run() и .start()?
  - [ ] Как запустить параллельный код, оформленный через Thread?
  - [ ] Как запустить параллельный код, оформленный через Runnable? Зачем для этого нужен объект Thread?
- [ ] Как оформить код для потока через лямбду или анонимный класс? Какая тут связь с функциональными интерфейсами?
- [ ] Почему нельзя запустить поток дважды, используя один и тот же объект Thread? Как это работает концептуально?
  - [ ] Что делает ОС, когда мы запускаем поток через Thread? Если была бы возможность использовать один и тот же объект Thread для запуска нескольких потоков, к каким проблема это могло бы привести?
- [ ] Почему не стоит использовать непосредственно объекты Thread для организации параллельного выполнения задач? Как это связано с ОС?
  - [ ] Что происходит с потоком после того как он отработал, если был оформлен через Thread?
  - [ ] Какую альтернативу прямому использованию Thread предоставляет интерфейс ExecutorService? Что такое пул потоков?
- [ ] Что представляет собой приоритет потока в Java? Какие у него границы?
  - [ ] Если у одного потока приоритет 1, а у другого 5, то какой из них более приоритетный?
  - [ ] В какой момент можно установить приоритет через `myThread.setPriority(8)`?
- [ ] В джаве два вида потоков - user-thread'ы и daemon-thread'ы. Чем они отличаются?
  - [ ] Каким из них является главный поток?
  - [ ] Если из UT создать новый поток П1, то какой вид будет у П1? А если создать его из DT? Какое выходит правило?
  - [ ] У какого из этих видов потоков приоритет выше - у UT или у DT?
  - [ ] В каком случае главный поток будет дожидаться выполнения остальных потоков, а в каком - не будет? Как это связано с UT и DT?
  - [ ] В какой момент можно сделать поток daemon'ом через `myThread.setDaemon(true)`?

# Два типа задач, их объявление

Задачи условно можно разделить на два типа:

* Не возвращающие никакого результата

  Представлены интерфейсом `java.lang.Runnable`, параллельный код пишется в методе `run`

* Возвращающие результат

  Представлены интерфейсом `java.util.concurrent.Callable`, параллельный код пишется в методе `call`

Оба вида можно объявить несколькими способами: написать отдельные классы, реализующие эти интерфейсы, или воспользоваться лямбдами, или анонимными классами.

## Runnable

Через отдельный класс:

```java
public class MyRunnable implements Runnable {
    @Override
    public void run() {
        String message = "Эта простая задача ничего не возвращает";
        System.out.println(message);
    }
}
```

Через лямбды:

```java
Runnable counter = () -> {
    for (int i = 0; i < 100; i++) {
        System.out.println("i: " + i);
    }
};
```

Через анонимный класс (реальная применимость сомнительна, выглядит громоздко и неудобно):

```java
Runnable myRunnable =
    new Runnable() {
        public void run() {
            System.out.println("Runnable running");
        }
    }
```

## Callable

Через отдельный класс:

```java
public class MyCallable implements Callable<String> {
    @Override
    public String call() {
        String message = "Эта простая задача возвращает это сообщение";
        System.out.println(message);
        return message;
    }
}
```

Через лямбды:

```java
Callable<String> callable = () -> {
    String message = "Результат работы callable";
    return message;
};
```

Через анонимные классы:

```java
Callable<String> callable =
    new Callable<String>() {
        @Override
        public String call() throws Exception {
            String message = "Результат работы callable";
            return message;
        }
};
```

# Передача параметров задачам

Параметры для задачи можно установить только *до* того как она отправлена на выполнение. Сделать это можно двумя способами:

* Объявить их полями класса и передать через конструктор
* Через замыкание

## Через поля класса

Хорошо подходит, когда мы оформляем параллельный код в отдельном классе:

```java
public class FieldParamsDemo implements Runnable {
    private String message;  // <-- Обычные поля класса
    private int magicNumber;

    public FieldParamsDemo(String message, int magicNumber) {  // <-- Требуем их в конструкторе
        this.message = message;                                // или задаем через сеттеры, по вкусу
        this.magicNumber = magicNumber;
    }

    @Override
    public void run() {
        System.out.println("Message: " + message);  // <-- Используюем в рассчетах
        System.out.println("Magic number: " + magicNumber);
    }
}

...
    
Runnable code = new FieldParamsDemo("Hello, params!", 777);  // <-- Передаем значения до запуска
```

## Через замыкания

Этот способ хорошо подходит, когда мы оформляем параллельный код через лямбды.

Замыкания, как они устроены и какие проблемы возникают - это отдельная тема для изучения. Но в данном контексте можно просто сказать, что под замыканием понимается использование переменной, расположенной вне тела лямбды:

```java
public class ClosureParamsDemo {
    public static void main(String[] args) {
        
        String message = "Hello, params";
        int magicNumber = 777;

        Runnable code = () -> {
            System.out.println("Message: " + message);
            System.out.println("Magic number: " + magicNumber);
        };
        
    }
}
```

# Запуск задач на выполнение

## Класс Thread и интерфейс Executor Service

Задачи запускаются и работают в *потоках* (threads).

Когда программа запускается, существует только один поток, называемый *главным*. В нем выполняется функция main(). Когда функция main() выполнится, программа завершается, если нет других потоков (если точнее, то *user-потоков*, об этом в разделе "Виды потоков и приоритет" ниже). Если они есть, то программа завершится только после их выполнения.

В основе многопоточности лежит класс `java.lang.Thread`, но использовать его напрямую для выполнения параллельных задач считается плохой практикой. Когда мы работаем через Thread, ОС создает настоящий поток, а когда он отработал, она его уничтожает. Если мы создаем еще один объект Thread, все повторяется. Все эти накладные расходы со стороны ОС на создание, уничтожение, переключение вносят замедление и поэтому существуют специальные интерфейсы, в которых все эти вопросы учтены. Например, интерфейс *ExecutorService*, позволяющий работать с заранее подготовленным *пулом потоков*, откуда потоки берутся по мере необходимости, а после отработки не уничтожаются, а возвращаются в него и доступны для повторного использования.

Подробнее о пуле в отдельном конспекте. Далее только про использование Thread напрямую.

## extends Thread vs implements Runnable

### Подход extends Thread

Самый базовый способ создать задачу, не возвращающую результат, - унаследоваться напрямую от класса Thread и переопределить его метод run:

```java
public class Counter extends Thread {  // <-- Наследуемся от класса
    @Override
    public void run() {
        System.out.println("Counter начал работу");

        for (int i = 0; i < 100; i++) {
            System.out.println("i: " + i);
        }

        System.out.println("Counter завершил работу");
    }
}
...
// К слову, сам класс Thread тоже реализует Runnable
public class Thread implements Runnable
```

Запуск:

```java
public static void main(String[] args) {
    System.out.println("main начал выполнение");

    Counter counter = new Counter();  // Просто создаем объект из нашего класс
    counter.start();                  // и запускаем работу прямо с него

    System.out.println("main завершен");
}
```

### Подход implements Runnable

Второй способ - реализовать интерфейс Runnable:

```java
public class Counter implements Runnable {  // <-- Реализуем интерфейс
    @Override
    public void run() {
        System.out.println("Counter начал работу");

        for (int i = 0; i < 100; i++) {
            System.out.println("i: " + i);
        }

        System.out.println("Counter завершил работу");
    }
}
```

Запуск:

```java
public static void main(String[] args) {
    System.out.println("main начал выполнение");

    Runnable counter = new Counter();     // Нужно создать объект runnable
    Thread thread = new Thread(counter);  // и передать его в объект треда.
    thread.start();  // Тогда при вызове метода .start() треда он вызовет метод .run() переданного объекта

    System.out.println("main завершен");
}
```

### Что лучше?

Результат работы один и тот же:

```java
main начал выполнение
main завершен
Counter начал работу
i: 0
i: 1
...
i: 99
Counter завершил работу
// Видно, что main выполнился мгновенно, но программа не завершилась, 
// а дождалась, пока отработает счетчик, т.к. это т.н. "user-thread"
// Еще есть "daemon-thread", и вот его бы JVM ждать не стала
```

Идейно вариант через implements Runnable лучше, поскольку реализует принцип "предпочитайте композицию наследованию". Ввиду отсутствия множественного наследования в джаве, реализация интерфейса поможет нам, если нужно наследоваться от какого-то своего класса, но при этом хочется параллельного выполнения.

## Запуск лямбд

Делается также как и с Runnable - лямбду нужно передать в Thread:

```java
public class ClosureParamsDemo {
    public static void main(String[] args) {
        
        String message = "Hello, params";
        int magicNumber = 777;

        Runnable code = () -> {
            System.out.println("Message: " + message);
            System.out.println("Magic number: " + magicNumber);
        };

        new Thread(code).start();  // <-- Передаем лямбду в объект Thread и вызываем на нем .start()
    }
}
```

## Отличие запуска через .run() и .start()

Запуск через `.start()` выполняет код в *новом потоке*, а `.run()` - в *текущем*:

```java
public static void main(String[] args) {
    System.out.println("main начал выполнение");

    Counter counter = new Counter();
    // counter.start();  // Заменим .start() на .run()
    counter.run();
    
    System.out.println("main завершен");
}
// P.S. Для случая с Runnable все аналогично
```

```java
main начал выполнение
Counter начал работу
i: 0
i: 1
...
i: 99
Counter завершил работу
main завершен
```

Так что нужно пользоваться .start(), иначе никакой параллельности не будет.

Запомнить, что start работает в новом потоке, а run в текущем можно например так: слово start сложнее слова run, т.к. более длинное. Работа в новом потоке сложнее, чем в текущем, т.к. требует затраты на создание нового потока. Поэтому start как более сложное слово означает "сложную" работу в новом потоке, а run как более простое слово означает "простую" работу в текущем.

## Повторный запуск

Чтобы запустить одну и ту же задачу второй раз, нам необходимо создать новый объект Thread - мы не можем переиспользовать старый:

```java
Runnable code = () -> System.out.println("Hello, thread!");

Thread thread1 = new Thread(code);
thread1.start();  // Ok
thread1.start();  // Ошибка!

Thread thread2 = new Thread(code);
thread2.start();  // Ok
```

Как это устроено технически - вопрос сложный, но логически все объяснимо: когда один объект связан с одним реальным потоком, это упрощает понимание, что сейчас происходит с этим потоком - то ли он работает, то ли завершился, то ли спит и т.д. Если же мы могли бы запускать второй поток, используя тот же объект thread1, то как бы мы тогда понимали, что происходит с первым потоком?

# Приоритет потоков

У любого потока в джаве есть приоритет. Выражается целым числом *от 1 до 10*. Больше число - выше приоритет. В классе Thread есть константы для приоритетов:

```java
Thread.MIN_PRIORITY   // 1
Thread.NORM_PRIORITY  // 5
Thread.HIGH_PRIORITY  // 10
```

По умолчанию приоритет средний и равен 5:

```java
Thread thread1 = new Thread();
System.out.println(thread1.getPriority());  // 5

Thread thread2 = new Thread();
System.out.println(thread2.getPriority());  // 5
```

Узнать\установить приоритет можно методами объекта треда:

```java
Thread thread = new Thread();
thread.getPriority();   // 5
thread.setPriority(8);  // Увеличиваем
thread.getPriority();   // 8
```

Установить приоритет можно только *до* запуска.

# Виды потоков

Потоки в джаве бывают двух видов:

* User threads
* Daemon threads

Вид потока наследуется от создавшего потока. Главный поток является UT, поэтому если просто создать новый поток П1 из главного, то П1 тоже будет UT.

## User Threads

Характерны тем, что JVM не прекращает работу, пока выполняется хотя бы один user-тред.

## Daemon Threads

Чтобы поток стал daemon'ом, нужно установить это явно, *до* запуска потока:

```java
Thread thread = new Thread();
thread.setDaemon(true);
sout(thread.isDaemon());  // true
```

Проверить наследование вида можно например так:

```java
Runnable task = () -> {
    Thread thrIn = new Thread();
    System.out.println("I'm thrIn. Is daemon: " + thrIn.isDaemon());  // Будет true, если 
    	// thrOut является daemon-потоком
};

Thread thrOut = new Thread(task);
// thrOut.setDaemon(true);
thrOut.start();
System.out.println(thrOut.isDaemon());

try {
    thrOut.join();
} catch (Exception ex) {

}
```

