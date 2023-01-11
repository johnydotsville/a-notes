# Вопросы

- [ ] Как передать параметры в поток, используя конструктор класса и замыкания? В каких случаях лучше подходит каждый способ?
- [ ] Как получить результат из потока? Зачем нужен интерфейс Callable? В чем его отличие от интерфейса Runnable? Какой метод мы должны переопределить в первом и втором случае?
  - [ ] Объект какого типа возвращает метод .submit, когда мы отправляем Callable или Runnable на выполнение в пул?
- [ ] Что будет с текущим потоком, если мы захотим получить результат из Future-объекта и вызовем его метод .get?
  - [ ] TODO: Что будет, если мы попробуем отменить задачу через Future методом .cancel, если задача уже запущена? А если не запущена? Что будут показывать методы .isDone и .isCancelled?

# Параметры для потока

Можно передать потоку параметры двумя способами:

* Объявить их полями класса и передать через конструктор
* Через замыкание

## Поля класса

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

    public static void main(String[] args) {
        Runnable code = new FieldParamsDemo("Hello, params!", 777);  // <-- Передаем значения до запуска
        new Thread(code).start();
    }
}
```

## Замыкания

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

        new Thread(code).start();
    }
}
```

# Возврат результата

## Callable

Поскольку метод run интерфейса Runnable не имеет возвращаемого значения (void), то получить какой-то результат из потока невозможно. Для этого существует интерфейс [Callable](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Callable.html), с единственным методом call:

```java
// Сделаем простой калькулятор
public class ThreadWithResultDemo implements Callable<Integer> {  // <-- Реализуем Callable
    private int a;  // <-- Данные
    private int b;
    private BiFunction<Integer, Integer, Integer> func;  // <-- Действие

    public ThreadWithResultDemo(int a, int b, BiFunction<Integer, Integer, Integer> func) {
        this.a = a;
        this.b = b;
        this.func = func;
    }

    public Integer call() throws Exception {  // <-- call как run, только возвращает значение
        System.out.println("Начинаем вычисление результата... " + LocalDateTime.now());
        Integer result = func.apply(a, b);
        System.out.println("Результат вычислен " + LocalDateTime.now());
        return result;
    }

    public static void main(String[] args) {
        Callable<Integer> sum = new ThreadWithResultDemo(5, 7, (a, b) -> a + b);  // <-- Создаем задачи
        Callable<Integer> sub = new ThreadWithResultDemo(10, 15, (a, b) -> a - b);
        Callable<Integer> mul = new ThreadWithResultDemo(2, 4, (a, b) -> a * b);
        Callable<Integer> div = new ThreadWithResultDemo(2, 0, (a, b) -> a / b);

        ExecutorService executor = Executors.newSingleThreadExecutor();  // <-- Для выполнения нужен пул
        try {
            System.out.println("Отправляем на выполнение Сложение " + LocalDateTime.now());
            Future<Integer> sumFuture = executor.submit(sum);  // <-- Сначала все отправляем на выполнение
            System.out.println("Отправляем на выполнение Вычитание " + LocalDateTime.now());
            Future<Integer> subFuture = executor.submit(sub);
            System.out.println("Отправляем на выполнение Умножение " + LocalDateTime.now());
            Future<Integer> mulFuture = executor.submit(mul);
            System.out.println("Отправляем на выполнение Деление " + LocalDateTime.now());
            Future<Integer> divFuture = executor.submit(div);

            System.out.println("Результат сложения: " + sumFuture.get());  // <-- Потом требуем резульат
            System.out.println("Результат вычитания: " + subFuture.get());
            System.out.println("Результат умножения: " + mulFuture.get());
            System.out.println("Результат деления: " + divFuture.get());
        } catch (Exception ex) {
            System.out.println("Произошла ошибка: " + ex.getMessage());
        }

        executor.shutdown();  // <-- Программа не закончится, пока пул держит потоки
    }
}
```

Результат:

```
* Отправляем на выполнение Сложение 2022-11-14T09:09:59.171817700
* Отправляем на выполнение Вычитание 2022-11-14T09:09:59.190818800
* Отправляем на выполнение Умножение 2022-11-14T09:09:59.191817900
- Начинаем вычисление результата... 2022-11-14T09:09:59.191817900
* Отправляем на выполнение Деление 2022-11-14T09:09:59.191817900
^  Результат вычислен 2022-11-14T09:09:59.191817900
- Начинаем вычисление результата... 2022-11-14T09:09:59.192818700
^ Результат вычислен 2022-11-14T09:09:59.193820700
- Начинаем вычисление результата... 2022-11-14T09:09:59.193820700
Результат сложения: 12
^ Результат вычислен 2022-11-14T09:09:59.193820700
- Начинаем вычисление результата... 2022-11-14T09:09:59.193820700
Результат вычитания: -5
Результат умножения: 8
Произошла ошибка: java.lang.ArithmeticException: / by zero
```

Видно, что действия идут вперемешку: какое-то действие уже начинает вычисляться еще до того как другое отправляется на обработку, а какое-то действие уже вычислилось до того как другое только начало.

## Future

Когда мы отправляем Callable на обработку в пул, то в ответ получаем объект с интерфейсом [*Future*](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Future.html), через который мы можем взаимодействовать с выполняемой задачей - например, опросить на результат, отменить:

```java
boolean cancel(boolean mayInterruptIfRunning)
V       get()
V       get(long timeout, TimeUnit unit)
boolean isCancelled()
boolean isDone()
```

Вот пример отмены и ожидания результата:

```java
public static void main(String[] args) {
    Callable<String> task = () -> {
        Thread.sleep(5_000);
        return "Some result";
    };

    ExecutorService exec = Executors.newSingleThreadExecutor();
    Future<String> futResult = exec.submit(task);

    // Логически, если мы не дождались результат за указанное время,
    // то можем прервать задачу
    try {
        futResult.get(2, TimeUnit.SECONDS);
    } catch (Exception ex) {  // <-- Сюда попадаем, если превышен таймаут
        System.out.println("Задача не успела выполниться за 2 секунды");
        // futResult.cancel(true);  <-- Прерывать или нет - дело наше
    }

    // Но технически, можем подождать и еще, не указывая время ожидания.
    // Правда есть риск, что задача зависла и так никогда и не дождемся.
    try {
        System.out.println("Но мы подождем до упора");
        String result = futResult.get();
        System.out.println(result);
    } catch (Exception ex) {
        System.out.println("Вообще не дождались");
    }

    exec.shutdown();
}
```

TODO: Поэкспериментировать с отменой задачи: будут ли исключения, можно ли поймать факт отмены внутри задачи и т.д.