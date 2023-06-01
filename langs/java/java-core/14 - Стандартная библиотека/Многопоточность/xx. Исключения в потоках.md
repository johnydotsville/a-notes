# Вопросы

- [ ] Можно ли поймать исключение, возникшее в потоке, если обернуть код запуска потока в try-catch? Способ запуска потока, через Thread или пул, не важен. Как это связано с поговоркой "Все что было в Вегасе, остается в Вегасе"?
- [ ] Если мы запускаем задачу напрямую через поток, и в ней возникает непойманное исключение, то поток уничтожается.
  - [ ] Что будет, если непойманное исключение возникнет в потоке, который находится в пуле? Не "похудеет" ли от этого пул?
  - [ ] Текст непойманного исключения при организации потока напрямую через Thread выводится в стандартный поток ошибок. А выводится ли куда-нибудь текст исключения при использовании пула?
- [ ] Для чего нужен интерфейс *Thread.UncaughtExceptionHandler* с переопределяемым методом *uncaughtException(Thread t, Throwable e)*?
  - [ ] Как с его помощью можно реализовать обработку исключений разных типов?
  - [ ] Как можно привязать этот обработчик к обычному потоку, созданному через Thread? myThread.setUncaughtExceptionHandler(myHandler);
  - [ ] TODO: как привязывать обработчики к потокам в пуле?

TODO: Вписать про Future.get, он выбрасывает исключение, которое можно ловить в вызывающем потоке

# Исключения и потоки

Когда речь идет про исключения и потоки, то подходит фраза "Всё, что происходит в Вегасе, остается в Вегасе". Если в потоке произошло исключение и мы его там же не поймали, то никакой другой поток это исключение не увидит (без специальных ухищрений):

```java
public class ExceptionInThreadBasics {
    private static void taskWithException() {  // <-- Проблемный код
        System.out.println("Эта задача выбрасывает исключение");
        throw new ArithmeticException();
    }

    private static void directThread() {  // <-- Запуск задачи через поток напрямую
        Thread task = new Thread(ExceptionInThreadBasics::taskWithException);

        try {
            task.start();
        } catch (ArithmeticException ex) {
            System.out.println("Сюда мы не попадем. Все, что происходит в Вегасе, остается в Вегасе");
        }
    }

    private static void threadPool() {  // <-- Запуск задачи через пул потоков
        ExecutorService exec = Executors.newSingleThreadExecutor();
        try {
            exec.submit(ExceptionInThreadBasics::taskWithException);
        } catch (ArithmeticException ex) {
            System.out.println("Сюда мы не попадем. Все, что происходит в Вегасе, остается в Вегасе");
        }
    }

    public static void main(String[] args) {
        // directThread();
        threadPool();
        System.out.println("main() complete");
    }
}
```

При работе с потоками напрямую, информация об исключении выведется в стандартный поток ошибок:

```java
// Output при вызове метода directThread()
main() complete
Эта задача выбрасывает исключение
Exception in thread "Thread-0" java.lang.ArithmeticException
	at johny.dotsville.exceptions.ExceptionInThreadBasics.taskWithException(ExceptionInThreadBasics.java:6)
	at java.base/java.lang.Thread.run(Thread.java:829)
```

При работе с пулом потоков, никакой информации не будет. Исключение останется в пределах пула:

```java
// Output при вызове метода threadPool()
main() complete
Эта задача выбрасывает исключение
```

Одним словом, каким бы способом мы не отправляли задачу на выполнение, поймать исключение в "менеджер-потоке" стандартным try-catch не получится

# Не обработанные исключения и пул потоков

Если исключение возникает в задаче, которую мы запустили *напрямую* через поток (т.е. с помощью *new Thread(task)*), и мы это исключение не ловим, это приводит к уничтожению потока. Возникает вопрос, а что будет, если исключение возникнет в потоке, который находится в пуле? Не "похудеет" ли от этого пул?

Нет, не похудеет. Необработанные исключения не приводят к исчезновению потока из пула:

```java
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class UncaughtExceptionsAndThreadPoolDemo {
    public static void main(String[] args) {
        Runnable exceptionTask = () -> {  // <-- "Плохая" задача
            System.out.println("Эта задача выбрасывает необработанное исключение");
            throw new RuntimeException();
        };

        Runnable normalTask = () -> {  // <-- Нормальная задача
            System.out.println("Эта задача работает нормально");
        };

        ExecutorService exec = Executors.newFixedThreadPool(3);  // <-- Хотя пул всего на 3 потока,
        for (int i = 0; i < 50; i++) {  // <-- а мы отправляем в него 50 заведомо провальных задач,
            exec.submit(exceptionTask);
        }
        exec.submit(normalTask);  //  <-- нормальная задача все равно выполнится
        // А это значит, что пул не худеет
        System.out.println("main() complete");
    }
}

// Output
Эта задача выбрасывает исключение
... (много раз) Эта задача выбрасывает исключение
Эта задача выбрасывает исключение
Эта задача работает нормально
Эта задача выбрасывает исключение
```

# Обработка в обычных потоках

В этом случае мы на объекте потока устанавливаем *обработчик*, реализующий специальный интерфейс

## Интерфейс Thread.UncaughtExceptionHandler

У этого интерфейса единственный метод `.uncaughtException`, в который при возникновении непойманного исключения передается объект потока, где оно возникло, и само исключение.

1. Реализуем интерфейс:

   ```java
   class CommonThreadExceptionHandler
           implements Thread.UncaughtExceptionHandler {  // <-- Пишем реализацию
       @Override
       public void uncaughtException(Thread t, Throwable e) {
           System.out.println("Попали в общий для тредов обработчик ошибок");
   
           if (e instanceof ArithmeticException) {  // <-- Для известных проблем - известное решение
               System.out.println(String.format("С ошибкой %s я знаю как справиться", e.getClass()));
               return;
           }
   
           if (e instanceof NullPointerException) {
               System.out.println(String.format("Для %s у нас особая обработка", e.getClass()));
               return;
           }
   
           System.out.println(String.format("Насчет ошибки %s у меня указаний нет. Трейс:", e.getClass()));
           e.printStackTrace();
       }
   }
   ```

   Или можно сделать через анонимный класс:

   ```java
   // Если обработчик крошечный, можно не создавать отдельный класс, а воспользоваться анонимным
   ...
   Thread.UncaughtExceptionHandler handler = new Thread.UncaughtExceptionHandler() {
       @Override
       public void uncaughtException(Thread t, Throwable e) {
           System.out.println("Через анонимный класс не удобно писать объемные обработчики");
       }
   };
   ...
   ```

2. Привязываем этот обработчик к потоку:

   ```java
   public class ThreadUncaughtExceptionHandlerDemo {
       
       public static void main(String[] args) {
           Thread thread1 = new Thread(SimpleTasks.getTaskWithArithmeticException());
           Thread thread2 = new Thread(SimpleTasks.getTaskWithNullPointerException());
           Thread thread3 = new Thread(SimpleTasks.getTaskWithLayerInstantiationException());
   
           Thread.UncaughtExceptionHandler handler = new CommonThreadExceptionHandler();  // <-- Создаем обработчик
   
           thread1.setUncaughtExceptionHandler(handler);  // <-- И привязываем его тредам
           thread2.setUncaughtExceptionHandler(handler);
           thread3.setUncaughtExceptionHandler(handler);
   
           thread1.start();
           thread2.start();
           thread3.start();
       }
   }
   ```

Утилитарный класс с задачами:

```java
class SimpleTasks {
    public static Runnable getTaskWithArithmeticException() {
        return () -> {
            System.out.println("Эта задача выбросит ArithmeticException и не будет сама его ловить");
            throw new ArithmeticException();
        };
    }

    public static Runnable getTaskWithNullPointerException() {
        return () -> {
            System.out.println("Эта задача выбросит NullPointerException и не будет сама его ловить");
            throw new NullPointerException();
        };
    }

    public static Runnable getTaskWithLayerInstantiationException() {
        return () -> {
            System.out.println("Эта задача выбросит LayerInstantiationException и не будет сама его ловить");
            throw new LayerInstantiationException();
        };
    }
}
```





# Обработка в пуле

https://stackoverflow.com/questions/2248131/handling-exceptions-from-java-executorservice-tasks

Лучше использовать Callable.