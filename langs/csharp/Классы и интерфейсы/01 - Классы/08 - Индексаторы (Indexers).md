# Индексаторы

Если класс инкапсулирует какой-то список значений, то с помощью индексатора можно реализовать доступ к элементам этого списка по индексу с помощью привычного синтаксиса `[ ]`

```c#
internal class IndexerDemo
{
    private string[] data;

    public string this [int pos]  // <-- Объявляем свойство с именем this и параметром под позицию
    {
        get => data[pos];  // Пишем логику доступа к элементу из "списка", используя позицию
        set => data[pos] = value;
    }

    public string Phrase => string.Join(" ", data);

    public IndexerDemo(string phrase)
    {
        this.data = phrase.Split();
    }
}
```

Теперь можно использовать `[ ]` вместе с объектом IndexerDemo, чтобы получать элементы по индексу:

```c#
var indexer = new IndexerDemo("Раз прислал мне барин чаю и велел его сварить");

Console.WriteLine(indexer.Phrase);  // Раз прислал мне барин чаю и велел его сварить
Console.WriteLine(indexer[2]);  // мне
indexer[2] = "тебе";
Console.WriteLine(indexer.Phrase);  // Раз прислал тебе барин чаю и велел его сварить
```



# Using indices and ranges with indexers

TODO: доработать это место после того как разберусь с ренджами и индексами.

You can support indices and ranges (see “Indices and Ranges” on page 56) in your
own classes by defining an indexer with a parameter type of Index or Range. We
could extend our previous example, by adding the following indexers to the
Sentence class:
public string this [Index index] => words [index];
public string[] this [Range range] => words [range];
This then enables the following:

```c#
Sentence s = new Sentence();
Console.WriteLine (s[^1]); // fox
string[] firstTwoWords = s [..2]; // (The, quick)
```

