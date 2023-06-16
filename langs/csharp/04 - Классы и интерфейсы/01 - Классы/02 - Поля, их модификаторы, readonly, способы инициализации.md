# Поля

Поля - это переменные, принадлежащие классу или экземпляру класса (в зависимости от типа поля).

## Обычные поля

Обычные поля - это переменные, принадлежащие экземпляру класса.

```c#
internal class Player
{
    public string nickname;  // Поле
    public string race;  // Поле
}
```

Характеристики обычных полей:

* Получают значение по умолчанию при создании экземпляра класса.

## Модификаторы полей

TODO: описание сделать получше, когда вспомню про new и 

| Ключевое слово                    | Назначение                                        |
| --------------------------------- | ------------------------------------------------- |
| static                            | Поле статическое                                  |
| public internal private protected | Модификатор доступа                               |
| new                               | Поле перекрывает (hide) аналогичное поле родителя |
| unsafe                            | Неуправляемый код TODO                            |
| readonly                          | Поле только для чтения                            |
| volatile                          | TODO Многопоточность                              |

## readonly-поля

* Помечаются ключевым словом `readonly`
* Могут быть проинициализированы при объявлении или в конструкторе. После этого изменение значения невозможно.

```c#
internal class Player
{
    private readonly string nickname = "JohNy";  // Ok
        
    public Player(string nickname)
    {
        this.nickname = nickname;  // Ok
    }
    
    public void ChangeNickname(string nickname)
    {
        this.nickname = nickname;  // Ошибка!
    }
}
```

## Способы инициализации

В качестве инициализатора может быть:

* Литерал.
* Выражение.
* Статический метод (но не обычный. А в Java обычный метод может использоваться для инициализации поля).

```c#
internal class Player
{
    public string nickname = "JohNy";  // Литерал
    public int level = new Random().Next(1, 100);  // Выражение
    public string race = chooseRace();  // Статический метод
    public int defaultExp = getDefaultExp();  // Ошибка! Обычный метод нельзя использовать для инициализации поля

    private static string chooseRace()
    {
        return "human";
    }
    
    private int getDefaultExp()
    {
        return 100;
    }
}
```