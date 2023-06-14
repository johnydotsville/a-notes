# nameof

Этот оператор может перевести в текстовый вид любой идентификатор в программе. Например, имя метода или параметра:

```c#
void NameofDemo(int count)
{
    Console.WriteLine(nameof(NameofDemo)); // NameofDemo, имя метода
    Console.WriteLine(nameof(count));  // count, имя параметра
    Console.WriteLine(nameof(obmanka));  // Ошибка! Нет такого идентификатора obmanka
}
```

Удобно, например, в логах использовать этот оператор вместо хардкода имен, т.к. имена идентификаторов могут измениться и мы можем забыть поменять их в тексте:

```c#
void NameofDemo(int count)  // Если поменяем имя параметра на amount
{
    Console.WriteLine("count = {0}", count);  // можем забыть поменять count на amount в строке
    Console.WriteLine("{0} = {1}", nameof(count), count);  // а так не забудем
}
// Вывод одинаковый:
count = 5
count = 5
```

