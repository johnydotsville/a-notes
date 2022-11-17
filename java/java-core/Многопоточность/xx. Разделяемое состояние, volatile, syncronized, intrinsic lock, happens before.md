# Вопросы

- [x] Чем могут повредить многопоточной программе такие системные оптимизации как отложенная запись, изменение порядка команд, кэширование данных и команд?
- [x] К чему применим модификатор volatile? Защищает ли он от race condition и почему?
- [x] К чему применим модификатор syncronized? Что такое intrinsic lock и reentrancy и как это связано с syncronized?
- [x] У объекта есть два синхронизированных метода - А и В. Если один поток выполняет метод А, то может ли другой поток параллельно выполнять метод В? Как это работает?
- [ ] Что такое happens-before гарантия? Как это связано с syncronized и volatile?



# Опасные оптимизации

Процессор и сама JVM могут выполнять различные оптимизации, как например:

* Отложенная запись - когда команды записи данных выполняются не сразу же как возникли, а ставятся в очередь и потом выполняются разом
* Изменение порядка команд - иногда с точки зрения процессора\JVM последовательность команд не критична и когда они видят, что можно выполнить оптимальнее, то могут выполнить команды программы не в том порядке, в котором мы их написали. Например, какие-нибудь последовательные команды присвоения
* Кэширование данных и команд - во время работы процессоры наполняют свой кэш данными и командами, чтобы не обращаться за ними в оперативку. А поскольку у каждого ядра собственный кэш, то если два потока используют общие данные, но выполняются на разных ядрах, то у каждого потока будет своя копия этих данных в кэше. Они будут менять эти данные, каждый свою копию, и понятия не иметь о том, что делает с данными "сосед".

В многопоточных приложениях это может вызывать трудноуловимые ошибки. Грубый пример:

```java
public class TaskRunner {

    private static int number;
    private static boolean ready;

    private static class Reader extends Thread {

        @Override
        public void run() {
            while (!ready) {
                Thread.yield();
            }

            System.out.println(number);
        }
    }

    public static void main(String[] args) {
        new Reader().start();
        number = 42;
        ready = true;
    }
}
```

Если поменять местами присвоение значений в ready и number, параллельный поток может вывести нам 0 вместо 42. И прочие неожиданности. Поэтому в джаве есть специальные средства, чтобы таких проблем не было.

## happened-before гарантия

В многопоточных приложениях невозможно сказать, какой метод будет выполняться первым, а какой вторым, третьим и т.д., потому что это целиком зависит от внутренних механизмов виртуальной машины\операционной системы и непредсказуемо. Однако есть возможность за счет разных техник дать гарантию, что некоторый код выполнится *перед* другим кодом. Это и называется happens-before. Будет упоминаться в конкретных примерах дальше.

## visibility гарантия





# syncronized, intrinsic lock

Синхронизация - это про код. Она нужна для обозначения границ критических секций. В этом случае только один поток может выполнять код КС.

## intrinsic lock

В джаве у *каждого объекта* есть так называемый `intrinsic lock` (альтернативное название - monitor lock), "внутренний замок\внутренняя блокировка" - некая внутренняя вспомогательная структура, которая используется для синхронизации.

> Я надеюсь, что intrinsic lock и monitor lock - это синонимы. Из [доки](https://docs.oracle.com/javase/tutorial/essential/concurrency/locksync.html) однозначно не понятно, так это или нет, да и нагуглить на верочку не удалось

Важно понимать, как именно работает блокировка с помощью intrinsic lock: у каждого объекта - один лок и если тред захватывает этот лок, то пока он его не освободит, никакой другой тред этот же лок захватить не сможет. В примерах ниже важность этого момента объяснена на практике:

Модификатор syncronized применяется только к методам и блокам кода:

* В случае объявления **синхронизированных методов**, intrinsic lock используется неявно:

  ```java
  public class SynchronizedCounter {
      private int c = 0;
  
      public synchronized void increment() {  // <-- Неявно используется intrinsic lock
          c++;
      }
  
      public synchronized void decrement() {  // <-- Неявно используется intrinsic lock
          c--;
      }
  
      public synchronized int value() {  // <-- Неявно используется intrinsic lock
          return c;
      }
      
      public void doSome() {
          System.out.println("Несинхронизированный метод для примера");
      }
  }
  ```

  Допустим, тред А начинает выполнять метод increment. Этот метод синхронизированный, значит А захватывает лок объекта SynchronizedCounter. Допустим, этот метод не успел выполниться до конца, и некий тред В хочет выполнить метод value. Этот метод тоже синхронизированный, значит треду В нужно захватить лок. Но лок уже занят тредом А, так что тред В останавливается и будет ждать освобождения лока. Пусть тред С хочет выполнить метод doSome. Этот метод не синхронизированный, поэтому треду С не нужно захватывать лок, а значит он спокойно выполняет метод.

  Таким образом делаем вывод, что из всех *синхронизированных* методов объекта может выполняться только один за раз, в том смысле, что параллельно они выполняться не могут, т.к. используют для синхронизации *один и тот же лок*.

* В случае **синхронизированных блоков**, мы синхронизируем не весь метод, а только какой-то его фрагмент (или несколько фрагментов) и должны явно указать объект, intrinsic lock которого нужно использовать:

  ```java
  public void addName(String name) {
      // Здесь может быть еще какой-то код
      synchronized(this) {  // <-- Текущий объект, как и любой объект, имеет собственный intrinsic lock
          lastName = name;
          nameCount++;
      }
      nameList.add(name);
  }
  ```

  Или вот так:

  ```java
  public class MsLunch {
      private long c1 = 0;
      private long c2 = 0;
      private Object lock1 = new Object();
      private Object lock2 = new Object();
  
      public void inc1() {
          // Здесь может быть еще какой-то код
          synchronized(lock1) {  // <-- Используем лок объекта lock1 для синхронизации
              c1++;
          }
      }
  
      public void inc2() {
          // Здесь может быть еще какой-то код
          synchronized(lock2) {  // <-- Используем лок другого объекта, lock2, для синхронизации
              c2++;
          }
      }
  }
  ```

  Этот пример примечателен тем, что здесь показано как для синхронизации используются разные объекты, в результате чего у нас получается два синхронизированных, но независимых друг от друга, блока. Если какой-то тред выполняет метод inc1(), то другой тред легко может выполнять inc2()

  Более интересная ситуация, если тред А выполняет метод inc1(), а тред В решит выполнить его же, то они вполне могут выполнять inc1() параллельно, но только до того момента, пока один из них не достигнет строчки 9. Тогда он возьмет себе лок и второй тред будет вынужден ждать освобождения лока, прежде чем продолжить.

## Освобождение лока

Лок освобождается, когда:

* Весь код в методе или блоке успешно выполняется
* Мы вылетаем из метода\блока с исключением. И даже если исключение не поймано, все равно лок освобождается

## reentrancy

Еще один важный момент: тред, который взял лок, может взять его повторно. Допустим, когда из одного синхронизированного метода вызывается другой синхронизированный метод. Если бы тред не мог этого сделать, то он бы просто завис. А так он просто берет его повторно и как бы "растет счетчик взятий" лока. А по мере освобождения, этот счетчик падает. Так что в итоге лок освобождается, дойдя до 0.











Synchronization *also* creates a "happens-before" memory barrier, causing a memory visibility constraint such that anything done up to  the point some thread releases a lock *appears* to another thread subsequently acquiring ***the same lock*** to have happened before it acquired the lock



syncronized





In this example, the `addName` method needs to synchronize changes to `lastName` and `nameCount`, but also needs to avoid synchronizing invocations of other objects'  methods. (Invoking other objects' methods from synchronized code can  create problems that are described in the section on [Liveness](https://docs.oracle.com/javase/tutorial/essential/concurrency/liveness.html).

:question: Из синхронизированного блока лучше не вызывать другие методы объекта, если они несинхронизированные...



# volatile

Модификатор volatile применяется к полям класса (к локальным переменным - нет, исключительно к полям).

> When you write to a Java `volatile` variable the value is guaranteed to be written directly to main memory

Волатильность (volatile - "изменчивый, непостоянный") - это про данные. Поскольку процессор\ядро работают каждый со своей кэш-памятью, а в оперативку пишут\читают от случая к случаю, то если два потока будут работать на разных ядрах, это может привести к таким проблемам:

* Для записи это значит, что новое значение D может осесть в кэше, и когда оно перенесется в оперативку - доподлинно не известно. Важно, что не сразу, а это проблема, потому что другой поток вероятно хотел бы прочитать это новое значение
* Для чтения это значит, что при попытке узнать значение D, оно будет взято из кэша, а не из оперативки. То есть если даже в первом случае получилось так, что новое значение все-таки попало в оперативку "оперативно", то это не важно, потому что чтение все равно произвелось из кэша

Так вот, применение к данным D модификатора volatile приводит к тому, что при записи D, новое значение гарантированно попадает в оперативку "оперативно", а при чтении берется из оперативки, а не из кэша. Как на техническом уровне это реализовано - то ли кэш синхронизируется с оперативкой, то ли данные пишутся\читаются сразу из оперативки - не важно. Важно то, что проблема несогласованности именно из-за кэширования больше не возникает.

Однако проблему race condition волатильность сама по себе не решает, это уже совсем другое дело.

This way, we communicate with runtime and processor to not reorder any instruction involving the *volatile* variable.











For multithreaded applications, we need to ensure a couple of rules for consistent behavior:

- Mutual Exclusion – only one thread executes a critical section at a time
- Visibility – changes made by one thread to the shared data are visible to other threads to maintain data consistency

*synchronized* methods and blocks provide both of the above properties at the cost of application performance.



*volatile* is quite a useful keyword because it **can help ensure the visibility aspect of the data change without providing mutual exclusion**. Thus, it's useful in the places where we're ok with multiple threads  executing a block of code in parallel, but we need to ensure the  visibility property.



**Happens-Before Ordering**

**Technically speaking, any write to a \*volatile\* field happens before every subsequent read of the same field**. This is the *volatile* variable rule of the Java Memory Model ([JMM](https://docs.oracle.com/javase/specs/jls/se8/html/jls-17.html)).

"A write to a volatile field happens-before every subsequent read of that same volatile" (volatile variable rule)





volatile piggybacking - сомнительная техника, при которой можно добиться эффекта волатильности на нескольких переменных, даже если они не отмечены волатильными, но при этом в коде логически связаны с волатильной переменной.





# Из concurrency на практике

Синхронизациюв Java обеспечивают ключевое слово synchronized, дающее эксклюзивную блокировку, а также волатильные (volatile) и атомарные переменные и явные замки.