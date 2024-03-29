# Методы

## Доступные модификаторы

| Категория модификатора           | Ключевые слова                       |
| -------------------------------- | ------------------------------------ |
| Статичный метод                  | static                               |
| Модификаторы доступа             | public internal private protected    |
| Модификаторы наследования        | new virtual abstract override sealed |
| Частичный метод                  | partial                              |
| Модификаторы неуправляемого кода | unsafe extern                        |
| Модификатор асинхронного кода    | async                                |

Про модификаторы - соответствующих конспектах, тут только сводка.

## Сигнатура метода

Сигнатура метода включает в себя:

* Имя метода
* Тип и *последовательность* параметров

Сигнатура НЕ включает:

* Возвращаемое методом значение
* Имена параметров

## Обычный синтаксис

```c#
internal class MethodDemo
{
    public static string foo()
    {
        return "public static method foo()";
    }
}
```

В C# для методов характерен следующий стиль:

* Имя метода пишется с маленькой буквы.
* Открывающая фигурная скобка ставится на следующей строке после объявления метода.

## Expression-bodied синтаксис

```c#
internal class MethodDemo
{
    public static string bar() => "public static method bar()";
}
```

Используется, если тело метода можно уместить в одно выражение. `=>` заменяет скобки и return.

## Перегрузка (overload)

Перегрузка - *overload*, когда в классе есть несколько методов с одинаковым именем, но разным набором параметров. Порядок параметров имеет значение, поэтому (int, string) и (string, int) считается разным набором параметров.

> Не путать с *переопределением - override*, когда один и тот же метод ведет себя по-разному в родительском классе и подклассе.

Для перегрузки нет ключевых слов, достаточно просто правильно написать методы:

```c#
internal class OverloadDemo
{
    public int Foo() => 5;
    public void Foo(string message) => Console.WriteLine(message);
    public string Foo(int x, string message) => x.ToString() + message;
    public string Foo(string message, int x) => x.ToString() + message;  // Ok, т.к. порядок разный
}
```

## Локальные (local) методы

Это "метод, описанный внутри метода", иначе говоря, вложенный:

```c#
internal class MethodDemo
{
    public int SomeMath(int x)
    {
        string message = "Hello, local methods!";
        return Square();

        int Square()  // Локальный метод
        {
            Console.WriteLine(message);  // Видит переменные содержащего метода
            return x * x;  // и его параметры напрямую
        }

        int Cube() => Square() * x;  // ЛМ видят друг друга, если они вложены в один и тот же метод
        
        static int Quad(int x)  // Статический локальный метод
        {
            // Console.WriteLine(message);  // Не видит напрямую ни переменных, ни параметров,
            // return Cube(x) * x;  // ни других ЛМ (не static) содержащего метода
            return x * x * x * x;
        }
        
        static int Penta(int x) => Quad(x) * x;  // Но статические ЛМ видят другие статические ЛМ
    }
    
    public int OtherMath(int x)
    {
        return cube(x);  // Ошибка! Отсюда не видно вложенные в SomeMath() методы
    }
    
    // Методы можно вкладывать сколько угодно раз
    public void CrazyNestingMethods()
    {
        InnerMethodA();

        void InnerMethodA()
        {
            Console.WriteLine("Какая-то совершенно");
            InnerMethodB();

            void InnerMethodB()
            {
                Console.WriteLine("безумная степень");
                InnerMethodC();

                void InnerMethodC()
                {
                    Console.WriteLine("вложения методов");
                }
            }
        }
    }
}
```

Характеристики локальных методов:

* Они доступны только внутри того метода, в который они вложены.
* Они видят другие локальные методы, которые вложены в этот же метод, что и они сами.
* Им напрямую доступны параметры и локальные переменные содержащего их метода.
* Методы можно вкладывать друг в друга сколько угодно раз.
* Локальные методы можно использовать в любых методо-подобных местах, вроде конструкторов, геттеров \ сеттеров, лямбдах и т.д.
* Статический локальный метод не видит напрямую параметры, переменные и другие ЛМ содержащего метода. Но видит другие его статические ЛМ. Статические ЛМ можно использовать для более строгого стиля, когда для вызова потребуется явно передать в ЛМ параметры из содержащего метода.
* Локальные методы нельзя перегрузить (*overload*, не путать с *override*).