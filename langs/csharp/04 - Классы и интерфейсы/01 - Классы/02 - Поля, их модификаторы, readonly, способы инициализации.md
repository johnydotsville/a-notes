# Поля

Поля - это переменные, принадлежащие классу.

## Модификаторы полей

TODO: описание сделать получше, когда вспомню про new и 

| Ключевое слово                    | Назначение                      |
| --------------------------------- | ------------------------------- |
| static                            | Указывает, что поле статическое |
| public internal private protected | Модификатор доступа             |
| new                               | TODO                            |
| unsafe                            | Неуправляемый код TODO          |
| readonly                          | Поле только для чтения          |
| volatile                          | TODO Многопоточность            |

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

* Литерал
* Выражение
* Статический метод:

```c#
internal class Player
{
    public readonly string nickname = "JohNy";  // Литерал
    public readonly int level = new Random().Next(1, 100);  // Выражение
    public readonly string race = chooseRace();  // Статический метод

	private static string chooseRace()
    {
        return "human";
    }
}
```