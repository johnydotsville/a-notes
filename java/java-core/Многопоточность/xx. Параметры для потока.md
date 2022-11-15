# Вопросы

- [x] Как передать параметры в поток, используя конструктор класса и замыкания?

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

s