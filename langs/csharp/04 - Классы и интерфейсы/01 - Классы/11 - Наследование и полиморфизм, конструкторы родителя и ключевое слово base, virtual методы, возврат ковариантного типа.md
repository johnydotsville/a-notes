# Наследование, характеристики и синтаксис

Характеристики наследования в C#:

* Наследоваться можно только от одного класса.
* Тип наследования - "классический" (для сравнения, в JavaScript - тип наследования "прототипный")
* Наследуются все поля, свойства, методы, конструкторы, не являющиеся приватными (приватные тоже наследуются, но компилятор не даст обращаться к ним напрямую в потомках)

```c#
internal class Asset
{
    public string Name { get; set; }
}
```

```c#
internal class Stock : Asset  // Для наследования указываем через двоеточие класс родителя
{
    public long SharesOwned { get; set; }
}
```

```c#
internal class House : Asset
{
    public decimal Cost { get; set; }
}
```

# Наследование и конструкторы, ключевое слово base

## Отношения между конструкторами родителя и потомка

Конструкторы не наследуются. Каждый потомок объявляет свои собственные конструкторы, но при их вызове имеет возможность вызвать конструктор родителя, чтобы например избавиться от дублирования инициализации полей.

Когда создается экземпляр потомка, *всегда* срабатывает также и конструктор родителя, даже если явно мы этого не пишем. По умолчанию вызывается конструктор родителя без параметров:

```c#
internal class Asset
{
    public Asset()
    {
        Console.WriteLine("Выполняется конструктор Asset.");
    }
}
```

```c#
internal class House : Asset
{
    public House()
    {
        Console.WriteLine("Выполняется конструктор House.");
    }
}
```

```c#
var house = new House();
// Вывод:
Выполняется конструктор Asset.
Выполняется конструктор House.
```

Это может быть не очевидно, но если поменять пример вот так:

```c#
internal class Asset
{
    protected string name;

    public Asset(string name)  // Объявим конструктор с одним параметром. Дефолтного К больше нет.
    {
        Console.WriteLine("Выполняется конструктор Asset.");
        this.name = name;
    }
}
```

```c#
internal class House : Asset
{
    private long cost;

    // Error: there is no argument given that corresponds to the required parameter 'name' of Asset.Asset(name)
    public House(string name, long cost)
    {
        Console.WriteLine("Выполняется конструктор House.");
        this.name = name;
        this.cost = cost;
    }
}
```

То компилятор укажет на ошибку, что мы не обеспечили конструктор Asset необходимым параметром. Все потому, что, как уже говорилось, при создании потомка обязательно вызывается конструктор родителя без параметров. Но поскольку мы явно описали конструктор с одним параметром в родителе, то у него исчез конструктор без параметров. Если бы он был, то вызвался бы он и компилятор бы не ругался. Но поскольку его нет, то будет попытка вызвать конструктор, который есть, а он требует параметр string name. Если бы в Asset было несколько конструкторов, с разным набором параметров, тогда бы компилятор сказал, что у Asset отсутствует конструктор без параметров.

Одним словом, надо помнить, что если вдруг у родителя отсутствует дефолтный конструктор, то нам в конструкторах потомка придется явно вызвать какой-нибудь конструктор родителя. Делается это через ключевое слово `base`:

```c#
internal class House : Asset
{
    private long cost;

    public House(string name, long cost) : base(name)  // Явно вызываем конструктор родителя
    {
        Console.WriteLine("Выполняется конструктор House.");
        this.cost = cost;
    }
}
```

## Ключевое слово base

Само по себе ключевое слово `base` предназначено для *невиртуального* доступа к элементу родительского класса. При этом base не является ссылкой на экземпляр родителя. Проще говоря, используя base мы можем из потомка вызвать родительскую реализацию нужного метода. Например, когда нам нужно дополнить родительскую реализацию:

```c#
internal class Asset
{
    public void HiddenDescription()
    {
        Console.WriteLine("Базовая реализация обычного метода HiddenDescription() в классе Asset.");
    }

    public virtual void VirtualDescription()
    {
        Console.WriteLine("Базовая реализация виртуального метода VirtualDescription() в классе Asset.");
    }
}
```

```c#
internal class House : Asset
{
    public void HiddenDescription()
    {
        base.HiddenDescription();
        Console.WriteLine("Дополнительный код для перекрытого метода HiddenDescription() из класса House.");
    }

    public override void VirtualDescription()
    {
        base.VirtualDescription();
        Console.WriteLine("Дополнительный код для overriden-метода VirtualDescription() из класса House.");
    }
}
```

Теперь посмотрим, как будет вести себя вызов в зависимости от типа ссылки:

```c#
Asset house = new House();  // <-- Базовый тип
house.VirtualDescription();
// Базовая реализация виртуального метода VirtualDescription() в классе Asset.
// Дополнительный код для overriden-метода VirtualDescription() из класса House.
house.HiddenDescription();
// Базовая реализация обычного метода HiddenDescription() в классе Asset.

House house = new House();  // <-- Тип потомка
house.VirtualDescription();
// Базовая реализация виртуального метода VirtualDescription() в классе Asset.
// Дополнительный код для overriden-метода VirtualDescription() из класса House.
house.HiddenDescription();
// Базовая реализация обычного метода HiddenDescription() в классе Asset.
// Дополнительный код для перекрытого метода HiddenDescription() из класса House.
```

Видно, что в виртуальных методах действительно успешно вызывается родительская реализация. Ну а перекрытый случай приведен просто до кучи, тут поведение ожидаемое - через базовую ссылку вызывается базовая реализация и о потомке речи не идет.

# Полиморфизм

Ссылки полиморфны, то есть через ссылку на базовый класс можно работать с объектами потомков:

```c#
var asset = new Asset() { Name = "Abstract thing" };
var stock = new Stock() { Name = "Shares", SharesOwned = 1_000 };
var house = new House() { Name = "Castle", Cost = 25_000_000 };

void printAsset(Asset asset)  // Метод принимает ссылку на общий тип
{
    Console.WriteLine(asset.Name);
}

// Но мы можем передать объект потомка
printAsset(asset);  // Abstract thing
printAsset(stock);  // Shares
printAsset(house);  // Castle
```

# Виртуальные и скрытые методы

## Виртуальные методы

Виртуальные методы - это методы родителя, которые подкласс может *переопределить*. При этом реализация метода будет браться из класса объекта, а не из класса ссылки, в которой объект лежит. В родителе ВМ помечаются ключевым словом `virtual`, а в потомке - ключевым словом `override`. "Виртуалить" можно все методоподобные элементы класса (свойства, индексаторы и т.д.):

```c#
internal class Asset
{
    public string Name { get; set; }

    public virtual string VirtualOverrideDescription() // Доб. методу модификатор virtual в родителе
    {
        return "Исходная Asset-реализация метода VirtualOverrideDescription()";
    }
}
```

```c#
internal class House : Asset
{
    public decimal Cost { get; set; }

    public override string VirtualOverrideDescription()  // <-- А в подклассе модификатор override
    {
        return "Переопределенная в House реализация метода VirtualOverrideDescription()";
    }
}
```

Создаем объект House, помещаем его в переменную родительского типа Asset и убеждаемся, что при этом вызывается House-реализация:

```c#
Asset house = new House() { Name = "Castle", Cost = 25_000_000 };  // Ссылка имеет тип родителя, Asset

Console.WriteLine(house.VirtualOverrideDescription());  // "Переопределенная в House реализация метода VirtualOverrideDescription()"
```

## Скрытые методы

Скрытые (или *перекрытые*) методы - это когда в родителе и подклассе есть методы с одинаковым именем, но при этом они должны трактоваться как независимые. При этом реализация метода будет браться из типа ссылки, а не типа объекта, который в ней лежит. Перекрытый метод помечается в потомке ключевым словом `new`, а в родителе никак помечать не надо:

> Более того, можно не писать и new, но тогда компилятор будет выводить предупреждения. Добавляя модификатор new, мы явно указываем, что методы перекрыты и мы об этом в курсе.

```c#
internal class Asset
{
    public string Name { get; set; }
    
    // Warning:...hides inherited member...use new keyword if hiding was intended
    public string Description()
    {
        return "Asset-реализация метода Description() без модификаторов";
    }

    // Нет ворнингов, потому что в потомке поставлен модификатор new
    public string DescriptionNewDescription()
    {
        return "Asset-реализация метода DescriptionNewDescription()";
    }
}
```

```c#
internal class House : Asset
{
    public decimal Cost { get; set; }

    public string Description()
    {
        return "House-реализация метода Description() без модификаторов";
    }

    public new string DescriptionNewDescription()  // new, подчеркиваем осознанность перекрытия
    {
        return "House-реализация метода DescriptionNewDescription() с модификатором new";
    }
}
```

Создаем объект типа House, размещаем его в ссылках двух типов и убеждаемся, что вызываются реализации из типа ссылки, а не типа самого объекта:

```c#
House houseLink = new House() { Name = "Castle", Cost = 25_000_000 };
Asset assetLink = houseLink;

Console.WriteLine(assetLink.Description());  // Asset-реализация метода Description() без модификаторов
Console.WriteLine(houseLink.Description());  // House-реализация метода Description() без модификаторов
Console.WriteLine(assetLink.DescriptionNewDescription());  // Asset-реализация метода DescriptionNewDescription()
Console.WriteLine(houseLink.DescriptionNewDescription());  // House-реализация метода DescriptionNewDescription() с модификатором new
```

## Возврат ковариантного типа

Начиная с C# 9 виртуальный метод может возвращать более конкретный тип ("ковариантный"). Например:

```c#
internal class Asset  // Родитель
{
    public string Name { get; set; }

    public virtual Asset CloneProp => new Asset() { Name = this.Name };  // возвращает Asset, логично

    public virtual Asset CloneMethod()
    {
        return new Asset() { Name = this.Name };
    }
}
```

```c#
internal class House : Asset  // Потомок
{
    public decimal Cost { get; set; }

    public override House CloneProp => new House() {  // может теперь вернуть House, а не только Asset
        Name = this.Name,
        Cost = this.Cost
    };

    public override House CloneMethod()
    {
        return new House()
        {
            Name = this.Name,
            Cost = this.Cost
        };
    }
}
```

До C# 9 потомок из переопределенного метода мог вернуть только тот же тип, что и оригинальный метод (в данном случае он мог бы вернуть только Asset).

> Уточнение: имеется ввиду, что House мог бы вернуть объект House, просто под типом Asset и нам пришлось бы полученный объект даункастить до House, а теперь можно возвращать объект сразу под нужным типом.

