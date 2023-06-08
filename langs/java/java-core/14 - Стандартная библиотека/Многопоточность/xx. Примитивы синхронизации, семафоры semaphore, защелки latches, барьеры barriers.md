

# Semaphore

## Концепция

[Семафор](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Semaphore.html) концептуально предназначен для ограничения доступа нескольким потокам к некоторому ресурсу, количество которого ограничено. У семафора есть исходное количество "разрешений" (*permits*). При начале работы с ресурсом поток берет у семафора разрешение, и их количество уменьшается, а при окончании работы поток должен вернуть семафору разрешение. Если доступных разрешений не осталось, поток блокируется до тех пор, пока не появится хотя бы одно, и тогда он сможет продолжить работу.

Хороший реальный пример пока не удалось нагуглить, но чисто теоретически это может быть что-то вроде "БД одновременно может нормально держать 10 соединений, значит если образовалось 30 потоков, то одновременно могут работать только 10, а остальные должны подождать".

## Методы

```java
import java.util.concurrent.Semaphore
    
void acquire()
void acquire(int permits)
void release()
void release(int permits)

int availablePermits()
int drainPermits()
void reducePermits(int reduction)
boolean tryAcquire()
boolean tryAcquire(int permits)
boolean tryAcquire(int permits, long timeout, TimeUnit unit)
boolean tryAcquire(long timeout, TimeUnit unit)
```

Остальные методы - в [документации](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Semaphore.html), здесь только некоторые, для наглядности в целом:

* accure, accure(int) - взять одно разрешение или указанное количество разрешений

* release, release(int) - вернуть одно разрешение или указанное количество разрешений

* availablePermits - узнать количество оставшихся на данный момент разрешений

* drainPermits - взять все разрешения, которые на данный момент имеются

* reducePermits - сократить количество максимальных разрешений на указанное число

* tryAcquire - пытается взять разрешение. Если удалось - возвращает true, если нет - false

  TODO: Я не понял как это работает. Если acquire обычный, то в случае отсутствия разрешения поток блокируется. А если try, тогда возвращает false и идет дальше, не блокируется. Как тогда этот false обрабатывать надо? Примеров нормальных не нашел.

## Пример

Пример "Компьютерный клуб", когда компьютеров всего пять, а детей поиграть пришло восемь. Каждый игрок - это отдельный процесс, занимающий первый свободный компьютер на некоторое время. Когда все компьютеры заняты, следующий процесс при попытке занять компьютер - блокируется. Для синхронизации используется семафор, у которого количество разрешений равно количеству компьютеров:

```java
public class GameClub {

    private Semaphore access;
    private boolean[] computers;

    public GameClub(int computersCount) {
        this.computers = new boolean[computersCount];  // <-- Количество компьютеров
        this.access = new Semaphore(computersCount, true);  // и количество разрешений - одинаковое
    }

    public void service(String[] clients) {
        ExecutorService exec = Executors.newCachedThreadPool();

        for (int i = 0; i < clients.length; i++) {  // <-- Каждый игрок - это отдельный процесс. Все процессы
            Runnable gamer = new Gamer(clients[i], access, computers);  // должны получить один и тот же семафор
            exec.submit(gamer);
        }
    }

    public static void main(String[] args) {
        GameClub club = new GameClub(5);  // <-- Компьютеров будет 5, а игроков 8
        club.service(new String[] { "Саня", "Лёха", "Димон", "Славик", 
                                   "Серёга", "Ярик", "Вован", "Витёк" });
    }
    
}
```

Класс игрока, использующий семафор:

```java
public class Gamer implements Runnable {

    private String name;
    private Semaphore access;
    private boolean[] computers;

    public Gamer(String name, Semaphore access, boolean[] computers) {
        this.name = name;
        this.access = access;
        this.computers = computers;
    }

    @Override
    public void run() {
        try {
            access.acquire();  // <-- Семафор на всех игроков один, пробуем получить разрешение. Если нет, блок

            int computer = getFirstFreeComputer();
            int rentTime = TimeGen.getTime();

            System.out.println(String.format("Игрок %s занял компьютер %d на %d минут", name, computer, rentTime));
            Thread.sleep(rentTime * 100);
            freeComputer(computer);
            System.out.println(String.format("Игрок %s освободил компьютер %d", name, computer));

            access.release();  // <-- Возвращаем разрешение
        } catch (Exception iex) {
            System.out.println("Ошибка: " + iex.getMessage());
        }
    }

    private int getFirstFreeComputer() {
        // Нужна синхронизация на массиве, чтобы два процесса не могли одновременно искать свободное место
        synchronized (computers) {
            for (int i = 0; i < computers.length; i++) {
                if (computers[i] == false) {
                    computers[i] = true;
                    return i;
                }
            }
            throw new RuntimeException("Все компьютеры заняты. Как такое могло получиться?");
        }
    }

    private void freeComputer(int i) {
        synchronized (computers) {
            computers[i] = false;
        }
    }
}
```

Результат:

```java
Игрок Серёга занял компьютер 4 на 61 минут
Игрок Саня занял компьютер 3 на 94 минут
Игрок Димон занял компьютер 2 на 75 минут
Игрок Лёха занял компьютер 0 на 51 минут
Игрок Славик занял компьютер 1 на 86 минут
// ^ Пять игроков заняли компьютеры, теперь шестой и остальные должны ждать
// Пошли поочередные освобождения с мгновенным занятием, причем освобождение идет в порядке длительности:
Игрок Лёха освободил компьютер 0  // Занимал на 51
Игрок Витёк занял компьютер 0 на 47 минут
Игрок Серёга освободил компьютер 4  // 61
Игрок Вован занял компьютер 4 на 52 минут
Игрок Димон освободил компьютер 2  // 75
Игрок Ярик занял компьютер 2 на 80 минут
Игрок Славик освободил компьютер 1  // 86
Игрок Саня освободил компьютер 3  // 94
Игрок Витёк освободил компьютер 0
Игрок Вован освободил компьютер 4
Игрок Ярик освободил компьютер 2
```

# CountDownLatch

## Концепция

[CountDownLatch](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CountDownLatch.html), она же "защелка". Концептуально защелка позволяет потоку дождаться наступления каких-то событий и после этого продолжить работу.

Механика работы такая: защелка создается с некоторым стартовым числом, которое можно условно назвать "количество событий, которых требуется дождаться". Защелка концептуально позволяет два действия: снижение этого числа ("наступление события") и ожидание, пока число не станет равно нулю ("все события произошли"). Например, у нас есть главный поток и пять рабочих и задача сделать так, чтобы главный поток запустил эти пять работяг и ждал, когда они все выполнятся (выполнение будет "событием"), а потом продолжил свою работу.

В главном потоке создаем защелку на пять "событий" и передаем ее работягам. Запускаем работяг и в главном потоке вызываем на защелке ожидание. Работяги в конце своей работы делают на защелке декремент (сигнализируют о наступлении "события"), и когда она становится равной нулю (все потоки выполнились), главный поток снова начинает работу. Можно использовать несколько защелок для более сложных композиций. Каждую защелку можно сравнить с этаким событием, которое можно ожидать и о наступлении которого можно сигнализировать.

Защелку нельзя использовать повторно. Когда она достигает нуля, то она по сути всё, в помоечку.

## Методы

```java
void await()
Causes the current thread to wait until the latch has counted down to zero, unless the thread is interrupted.
boolean	await(long timeout, TimeUnit unit)
Causes the current thread to wait until the latch has counted down to zero, unless the thread is interrupted, or the specified waiting time elapses.
void countDown()
Decrements the count of the latch, releasing all waiting threads if the count reaches zero.
long getCount()
```

* await - вызывающий поток переходит в ожидание до тех пор, пока защелка не станет равной нулю
* countDown - уменьшает значение защелки на единицу
* getCount - возвращает текущее значение защелки

## Пример

Пример "Создание игрового лобби": подбираем игроков для лобби. Пока все игроки не подобраны, они не могут начать подключаться к лобби. Когда подобраны - разрешаем им подключиться и ждем, пока все подключатся. Затем начинаем игру.

Создаем несколько процессов, сразу же их запускаем. Благодаря защелке каждый процесс на старте уходит в ожидание и возобновляет работу, только когда все остальные процессы будут созданы. Затем они возобновляют работу, а главный поток ждет завершения их работы и потом уже только идет дальше:

```java
public class Player implements Runnable {

    private String nickname;
    private CountDownLatch latchLobbyReady;
    private CountDownLatch latchPlayerConnected;

    public Player(String nickname, CountDownLatch latchLobbyReady,
                  CountDownLatch latchPlayerConnected) {
        this.nickname = nickname;
        this.latchLobbyReady = latchLobbyReady;
        this.latchPlayerConnected = latchPlayerConnected;
    }

    @Override
    public void run() {
        System.out.println(String.format("Игрок %s ожидает создания лобби.", nickname));
        try {
            latchLobbyReady.await();  // Ждем, пока все игроки будут подобраны, и только потом начнем подключение
            connectToLobby();
            latchPlayerConnected.countDown();  // Сигнализируем, что мы подключились
        } catch (InterruptedException iex) {
            System.out.println(String.format("Игрок %s не смог подключиться к лобби.", nickname));
        }
    }

    private void connectToLobby() throws InterruptedException {
        long connectionTime = (long) (Math.random() * 5000 + 1000);
        System.out.println(String.format("Игрок %s подключается к лобби. Примерное время подключения: %d мс",
                nickname, connectionTime));
        Thread.sleep(connectionTime);
        System.out.println(String.format("Игрок %s подключился к лобби.", nickname));
    }
}
```

Главный поток:

```java
public class GameServer {

    public static void main(String[] args) {
        String[] players = new String[] { "benztruck", "johny", "ubershrei", "ghouliweargucci", "leel pip" };

        // У нас два события - "Лобби создано" и "Игроки подключаются", под каждое создаем защелку
        CountDownLatch latchLobbyReady = new CountDownLatch(1);
        CountDownLatch latchPlayerConnected = new CountDownLatch(players.length);

        ExecutorService exec = Executors.newCachedThreadPool();
        System.out.println(String.format("Подбор игроков для лобби..."));
        for (int i = 0; i < players.length; i++) {
            exec.submit(new Player(players[i], latchLobbyReady, latchPlayerConnected));
        }
        try {
            Thread.sleep(1000);
            System.out.println(String.format("Подбор закончен, лобби создано. Ожидание присоединения игроков..."));
            // Когда все процессы игроков созданы и запущены, считаем что лобби готово и можно к нему подключаться
            latchLobbyReady.countDown();
            // Теперь главный поток ждет, пока все процессы отработают ("игроки присоединятся к лобби")
            latchPlayerConnected.await();
        } catch (InterruptedException iex) {
            System.out.println("Сервер прекратил работу.");
            return;
        }
        System.out.println("Все игроки зашли в лобби, игра начинается.");
    }

}
```

Результат:

```java
Подбор игроков для лобби...
// Создаем-запускаем процессы и тормозим их перед первой же командой, чтобы они ждали, пока все будут созданы
Игрок johny ожидает создания лобби.
Игрок ghouliweargucci ожидает создания лобби.
Игрок leel pip ожидает создания лобби.
Игрок benztruck ожидает создания лобби.
Игрок ubershrei ожидает создания лобби.
// Когда все процессы игроков созданы и запущены, можно разрешить им продолжить выполнение
Подбор закончен, лобби создано. Ожидание присоединения игроков...
// Процессы начали выполнять свои команды
Игрок benztruck подключается к лобби. Примерное время подключения: 3290 мс
Игрок johny подключается к лобби. Примерное время подключения: 5842 мс
Игрок leel pip подключается к лобби. Примерное время подключения: 3314 мс
Игрок ghouliweargucci подключается к лобби. Примерное время подключения: 2870 мс
Игрок ubershrei подключается к лобби. Примерное время подключения: 4523 мс
Игрок ghouliweargucci подключился к лобби.
Игрок benztruck подключился к лобби.
Игрок leel pip подключился к лобби.
Игрок ubershrei подключился к лобби.
Игрок johny подключился к лобби.
// Все процессы выполнились, главный поток может продолжить выполнение
Все игроки зашли в лобби, игра начинается.
```

# CyclicBarrier

## Концепция

[CyclicBarrier](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/CyclicBarrier.html) позволяет нескольким потокам дойти до какой-то точки и ожидать, когда и остальные до нее дойдут. Потом барьер выполняет некоторое заданное действие (или не выполняет, если оно не требуется) и потоки продолжают работу.

TODO: По ощущениям то же самое, что и защелка, кроме того что в защелке мы можем делать декремент в одном и том же потоке несколько раз, а здесь await как бы делает -1 и поток блокируется. Ну и еще барьер - не одноразовый, в отличие от защелки. UPD. Нет, не то же самое. Технически похоже, да, но полной взаимозаменяемости нет. Взять хоть тот же пример из раздел CountDownLatch. Первую защелку, когда поток игрока должен ждать, пока все остальные потоки игроков будут запущены, можно было бы заменить на барьер размером с количество игроков. Но вот реализовать в потоке сервера ожидание, когда все потоки игроков дойдут до конца, с помощью барьера невозможно - тут нужна защелка, которую будет ждать поток сервера. Получается назначение барьера - заблокировать поток до тех пор, пока другие потоки дойдут до барьера, а потом отпустить все эти потоки. Ожидание на барьере уже как бы начинает "приоткрывать" его, а ожидание на защелке - нет, она начинает приоткрываться при вызове декремента, т.е. ожидание и продвижение разнесены по разным местам.

## Методы

```java
int await()
int await(long timeout, TimeUnit unit)

int getNumberWaiting()
int getParties()

boolean isBroken()

void reset()
```

* asd

## Пример

Несколько друзей решили поиграть вместе в какую-нибудь игру. Они заходят в общую группу, ждут пока все придут и тогда выбирают, во что поиграть. Потом запускают выбранную игру.

В главном потоке создаем барьер и задачу, которая должна выполниться по достижении всеми потоками барьера. Эта задача - выбор игры:

```java
public class Fellowship {

    public static void main(String[] args) {
        Game game = new Game();  // <-- Общие данные
        Runnable chooseGame = () -> {  // <-- Задача по выбору игры. Выполнится, когда все потоки дойдут до барьера
            System.out.println("Выбираем игру.");
            game.chooseGame();
            System.out.println(String.format("Выбрали %s.", game.title));
        };
        
        CyclicBarrier barrierAllReady = new CyclicBarrier(4, chooseGame);  // <-- Размер барьера и задача

        String[] friends = new String[] { "Саня", "Димон", "Лёха", "Вовчик" };
        for (int i = 0; i < friends.length; i++) {  // <-- Создаем потоки, отдаем им барьер и общие данные
            new Thread(new Friend(friends[i], barrierAllReady, game)).start();
        }
    }

}
```

Класс рабочих потоков:

```java
public class Friend implements Runnable {

    private String name;
    private Game game;
    private CyclicBarrier barrierAllReady;

    public Friend(String name, CyclicBarrier barrierAllReady, Game game) {
        this.name = name;
        this.barrierAllReady = barrierAllReady;
        this.game = game;
    }

    @Override
    public void run() {
        try {
            long connectionTime = (long)(Math.random() * 5000 + 1000);
            System.out.println(String.format("%s заходит в группу. Примерное время подключения %d мс.",
                    name, connectionTime));
            Thread.sleep(connectionTime);

            System.out.println(String.format("%s зашел в группу и ждет остальных, чтобы договориться об игре.", name));
            barrierAllReady.await();  // <-- Ждем на барьере

            System.out.println(String.format("%s заходит в игру %s.", name, game.title));
        } catch (InterruptedException iex) {
            System.out.println(String.format("%s не смог подключиться.", name));
        } catch (BrokenBarrierException bbex) {
            System.out.println(String.format("Кто-то не смог подключиться. Расходимся."));
        }
    }
}
```

Вспомогательный класс игры:

```java
public class Game {
    public String title;
    private String[] games = new String[] { "Dota 2", "CS: GO", "World of Warcraft", "PUBG" };

    public void chooseGame() {
        int gameIndex = (int)(Math.random() * 2 + 1);
        title = games[gameIndex];
    }
}
```

Результат:

```java
// Некоторая работа, которую выполняют потоки
Лёха заходит в группу. Примерное время подключения 2774 мс.
Димон заходит в группу. Примерное время подключения 5549 мс.
Вовчик заходит в группу. Примерное время подключения 3449 мс.
Саня заходит в группу. Примерное время подключения 4214 мс.
// Часть работы выполнена, дошли до точки синхронизации, ждем когда и остальные потоки до нее дойдут
Лёха зашел в группу и ждет остальных, чтобы договориться об игре.
Вовчик зашел в группу и ждет остальных, чтобы договориться об игре.
Саня зашел в группу и ждет остальных, чтобы договориться об игре.
Димон зашел в группу и ждет остальных, чтобы договориться об игре.
// Все потоки дошли до точки синхронизации, теперь барьер выполнит заданное действие
Выбираем игру.
Выбрали World of Warcraft.
// Барьер выполнил заданное действие и потоки продолжают работу, выполняют код после .await()
Димон заходит в игру World of Warcraft.
Лёха заходит в игру World of Warcraft.
Саня заходит в игру World of Warcraft.
Вовчик заходит в игру World of Warcraft.
```

# Exchanger

## Концепция

[Exchanger](https://docs.oracle.com/javase/7/docs/api/java/util/concurrent/Exchanger.html) позволяет *двум* потокам поработать до какой-то точки, подождать на ней второй поток, обменяться данными, и продолжить работу. Может использоваться повторно.



## Методы



## Пример

Двое друзей загадывают числа. Когда числа придуманы, они называют их друг другу и у кого число больше, тот выиграл:

```java
public class Friend implements Runnable {

    private String name;
    private Exchanger<Integer> number;

    public Friend(String name, Exchanger<Integer> number) {
        this.name = name;
        this.number = number;
    }

    @Override
    public void run() {
        while (true) {  // <-- Бесконечный цикл показывает, что exchanger может использоваться повторно
            int thinkingTime = (int) (Math.random() * 5000 + 1000);
            System.out.println(String.format("%s: придумываю число. Дай подумать %d мс.", name, thinkingTime));
            try {
                Thread.sleep(thinkingTime);  // <-- Долгая работа
                int myNumber = (int) (Math.random() * 9 + 1);
                System.out.println(String.format("%s: придумал! %d.", name, myNumber));

                int friendsNumber = number.exchange(myNumber);  // <-- Обмен данными

                if (myNumber > friendsNumber) {  // <-- Обработка полученных от второго потока данных
                    System.out.println(String.format("%s: я выиграл! %d больше %d.", name, 
                                                     myNumber, friendsNumber));
                } else if (myNumber < friendsNumber) {
                    System.out.println(String.format("%s: я проиграл! %d меньше %d.", name, 
                                                     myNumber, friendsNumber));
                } else {
                    System.out.println(String.format("%s: ничья! %d равно %d.", name, myNumber, friendsNumber));
                }
            } catch (InterruptedException iex) {
                System.out.println(String.format("%s: я пошел домой."));
            }
            System.out.println("Сыграем еще раз!");
        }
    }

    public static void main(String[] args) {
        Exchanger<Integer> number = new Exchanger<>();

        ExecutorService exec = Executors.newFixedThreadPool(2);
        exec.submit(new Friend("Саня", number));
        exec.submit(new Friend("Петя", number));
    }
}
```

Результат:

```java
Петя: придумываю число. Дай подумать 5820 мс.
Саня: придумываю число. Дай подумать 3584 мс.
Саня: придумал! 1.
Петя: придумал! 5.
Петя: я выиграл! 5 больше 1.
Сыграем еще раз!
Саня: я проиграл! 1 меньше 5.
Сыграем еще раз!
Петя: придумываю число. Дай подумать 4704 мс.
Саня: придумываю число. Дай подумать 3994 мс.
Саня: придумал! 3.
Петя: придумал! 5.
Саня: я проиграл! 3 меньше 5.
Сыграем еще раз!
Петя: я выиграл! 5 больше 3.
Сыграем еще раз!
```





# TODO

Базовый класс `java.util.concurrent.locks.Lock`

ReentrantLock

ReentrantReadWriteLock ReadWriteLock

StampedLock

- [x] CowntDownLatch защелка

- [ ] CyclicBarrier

- [x] Semaphore

- [ ] Exchanger

Phaser



