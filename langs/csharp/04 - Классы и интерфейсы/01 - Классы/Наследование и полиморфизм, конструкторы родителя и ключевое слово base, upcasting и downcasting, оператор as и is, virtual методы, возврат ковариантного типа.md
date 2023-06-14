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

Само по себе ключевое слово `base` предназначено для *невиртуального* доступа к элементу родительского класса. Проще говоря, используя base мы можем из потомка вызвать родительскую реализацию нужного метода. Например, когда нам нужно дополнить родительскую реализацию:

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

# Приведение ссылок, upcasting и downcasting

Ссылка - это "трафарет", который дает доступ к элементам, которые есть в типе ссылки. Ссылки можно трансформировать между типами потомка и родителя.

## Upcasting

Приведение "вверх", от частного к общему, от типа потомка к типу родителя. Как запомнить: иерархия наследования обычно изображается сверху вниз, где сверху - базовый класс, а снизу - потомок:

![inheritance-example.drawio](img/inheritance-example.drawio.svg)

Соответственно, *up*-casting это движение вверх, т.е. от потомка к родителю.

Апкастинг проводится автоматически, неявно, для этого достаточно просто положить объект потомка в ссылку родителя:

```c#
Asset ass = new House() { 
    Name = "Castle",   // Свойство доступно через ссылку типа Asset
    Cost = 25_000_000  // Свойство не доступно, но присутствует
};
```

При этом через ссылку ass теперь будут доступны только те элементы, которые есть у типа Asset. Элементы типа House будут присутствовать в самом объекте, но через ссылку ass доступны не будут, потому что их нет в типе Asset:

```c#
Console.WriteLine(ass.Name);  // Ok
Console.WriteLine(ass.Cost);  // Ошибка!
```

## Downcasting

Приведение "вниз", от общего к частному, от родителя к потомку, проводится только явно и только в том случае, если по приводимой ссылке лежит объект правильного типа:

```c#
Asset ass = new House() { Name = "Castle", Cost = 25_000_000 };
House house = (House)ass;  // <-- Downcasting

Console.WriteLine(house.Name);  // Через ссылку "полного" типа оба свойства доступны
Console.WriteLine(house.Cost);
```

Поскольку в переменной Asset может лежать и другой потомок, например, типа Stock, то в этом случае в процессе выполнения программы мы получим исключение `System.InvalidCastException`:

```c#
Asset ass = new Stock() { Name = "Shares", SharesOwned = 1_000 };
House house = (House)ass; // Исключение: 'Unable to cast object of type 'MainAss.Inheritance.Stock' to type 'MainAss.Inheritance.House'.'
```

## Оператор as

Этот оператор выполняет даункаст и если не удается, возвращает null:

```c#
Asset assStock = new Stock() { Name = "Shares", SharesOwned = 1_000 };
Asset assHouse = new House() { Name = "Castle", Cost = 25_000_000 };

House house = assHouse as House;  // Ok
Console.WriteLine(house.Cost);  // 25_000_000

House house = assStock as House;  // null
Console.WriteLine(house.Cost);  // System.NullReferenceException
```

## Оператор is

Этот оператор проверяет, относится ли ссылка к указанному типу, и возвращает bool:

```c#
Asset ass = new House() { Name = "Castle", Cost = 25_000_000 };
if (ass is House)
{
    House h = (House)ass;  // Делаем даункаст, уже не боясь неправильного типа в ссылке
    Console.WriteLine(h.Cost);
} else if (ass is Stock) {
    Stock s = (Stock)ass;
    Console.WriteLine(s.SharesOwned);
}
```

Оператор позволяет ввести временную переменную, чтобы немного сократить код:

```c#
Asset ass = new House() { Name = "Castle", Cost = 25_000_000 };
if (ass is House h)  // Если ass относится к типу House, сделать даункаст и положить объект в h
{
    Console.WriteLine(h.Cost);
} else if (ass is Stock s) {
    Console.WriteLine(s.SharesOwned);
}
```

Полезно, т.к. тут же в условии при удачном преобразовании можно воспользоваться объектом:

```c#
if (ass is Stock s && s.SharesOwned > 500) {
    Console.WriteLine("Rich");
}
```

> По сути этот код после компиляции превращается во что-то такое:
>
> ```c#
> Asset ass = new House() { Name = "Castle", Cost = 25_000_000 };
> House h;
> Stock s;
> if (ass is House)
> {
>     h = (House)ass;
>     Console.WriteLine(h.Cost);
> } else (ass is Stock) {
>     s = (Stock)ass;
>     Console.WriteLine(s.SharesOwned);
> }
> ```
>
> Но несмотря на то, что h и s объявлены вне условных блоков, использовать их за пределами блоков не получится. Будет ошибка "use of unassigned local variable", которая говорит о том, что переменная видна, но не инициализирована.

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

До C# 9 потомок из переопределенного метода мог вернуть только тот же тип, что и оригинальный метод (в данном случае он мог бы вернуть только Asset). Уточнение: имеется ввиду, что House мог бы вернуть объект House, просто под типом Asset и нам пришлось бы полученный объект даункастить до House, а теперь можно возвращать объект сразу под нужным типом.

