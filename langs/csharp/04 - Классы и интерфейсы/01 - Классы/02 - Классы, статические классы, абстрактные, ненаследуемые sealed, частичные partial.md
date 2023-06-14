# Класс

Классы представляют собой пользовательские типы. Доступные модификаторы: `public`, `internal`, `abstract`, `sealed`, `static`, `unsafe`, `partial`. По умолчанию у классов модификатор `internal`.

```c#
internal class Foobar {
    // Поля
    // Константы
    // Свойства
    // Индексаторы
    // Конструкторы
    // Деконструкторы
    // Методы
}
```

# Статический класс

```c#
internal static class Foobar {  // <-- Добавляется модификатор static
    // Поля
    // Константы
    // Свойства
    // Индексаторы
    // Конструкторы
    // Деконструкторы
    // Методы
}
```

Характеристики статических классов:

* Могут содержать только статические элементы.
* От статических классов нельзя наследоваться.

# Partial классы

Partial или "частичные" - это классы, описание которых находится более чем в одном файле.

Причина появления - возможность удобно дополнять автогенерируемые классы своим кодом, например, в WindowsForms, EntityFramework. Если в автогенерируемый класс что-то дописать, то после повторной генерации все изменения будут утеряны. Частичные классы позволяют создать класс с таким же именем, внести желаемые дополнения в него и тогда повторная генерация их не затронет, а при компиляции содержимое частичных классов сольется в одно целое.

Замечания:

* Классы должны находиться в одной сборке и в одном неймспейсе (про один неймспейс вроде не написано нигде, это личное наблюдение - если были в разных, то классы не видели элементы друг друга).
* Каждая часть видит все, что написано в другой части (поля, свойства, методы и т.д.), как будто бы все написано в одном файле.
* Поэтому элементы не должны совпадать - нельзя написать в обоих частях методы с одинаковой сигнатурой.
* Частичными могут быть и методы. В этом случае одна часть содержит только объявление метода, а вторая - реализацию. И только так, т.е. написать кусочек реализации метода в одной части, и кусочек в другой, а потом объединить это все в один метод - нельзя.
* Область видимости можно написать обоим частям, а можно только одной. Возьмется та, которая написана явно. Например, если вообще ничего не писать, то будет internal. Если у одной написать public, а у другой ничего - будет public. Но вот указать разные области видимости конечно же нельзя.
* Наследование, интерфейсы можно указывать в одной части, а реализовывать в другой. В общем, все пишем так как будто это один и тот же файл.
* При компиляции нет гарантий, какие поля (из какой части) будут инициализированы в первую очередь.

Первый фрагмент частичного класса:

```c#
namespace MainAss  // Неймспейсы дб одинаковые
{
    internal partial class PartialDemo : ICall
    {
        public string PropertyFoo { get; private set; } = "prop foo value";

        public PartialDemo(string propertyFoo)
        {
            this.PropertyFoo = propertyFoo;
        }

        public void MethodA()
        {
            Console.WriteLine("This is MethodA() call.");
        }

        public partial void PartialMethodP();  // Частичный метод. Объявлен, но не реализован
    }
}
```

```c#
internal interface ICall
{
    void Call(string message);
}
```

Второй фрагмент частичного класса:

```c#
namespace MainAss  // Неймспейсы дб одинаковые
{
    internal partial class PartialDemo
    {
        public string PropertyBar { get; set; } = "prop bar value";

        public PartialDemo(string propertyFoo, string propertyBar)
            : this(propertyFoo)
        {
            this.PropertyBar = propertyBar;
        }

        public void MethodB()
        {
            MethodA();  // Спокойно вызываем метод, написанный в другой части
        }


        public partial void PartialMethodP()  // Во второй части частичный метод дб реализован
        {
            Console.WriteLine("This is PartialMethodP() call.");
        }
        
        public void Call(string message)
        {
            Console.WriteLine(message);
        }
    }
}
```

# Абстрактные классы

Характеристики абстрактного класса (АК):

* Нельзя создать экземпляр АК
* АК может иметь абстрактные методы - методы без реализации
* АК может иметь обычные методы, с реализацией
* АК может иметь конструкторы (несмотря на то, что из АК нельзя создать объект)
* Потомок обязан реализовать все абстрактные методы родителя, либо сам должен быть объявлен как АК

Абстрактным также может быть:

* Метод
* Свойство (целиком, но не аксессор отдельно)

```c#
internal abstract class Asset  // Абстрактный родитель
{
    protected string name;  // Имеет какие-то поля,
    public abstract string Prop { get; set; }  // свойства, обычные или абстрактные

    public Asset(string name)  // и даже конструктор
    {
        this.name = name;
    }
    
    public string Concrete()
    {
        return "Хоть я и абстрактный класс, но у меня мб методы с реализацией";
    }

    public abstract string Description();  // Абстрактный метод - без реализации
}
```

```c#
internal class House : Asset  // Потомок абстрактного класса
{
    public override string Prop { 
            get => name; 
            set => name = value; 
    	}
    
    public House(string name) : base(name)
    {
        // Если не понятно, зачем тут такой конструктор, см. конспект про наследование
    }

    public override string Description()  // Должен реализовать АМ, исппользуя модификатор override
    {
        return name;
    }
}
```

# Запечатанные классы

Запечатанный класс обозначается модификатором `sealed` и означает, что от него нельзя наследоваться:

```c#
internal sealed class Asset  // Модификатор sealed
{
}
```

```c#
internal class House : Asset  // Ошибка! От sealed-класса нельзя наследоваться
{
}
```

## Запечатанные методы

В большинстве случаев класс запечатывается целиком. Однако можно запечатать и отдельные методы, а именно - переопределенный метод в наследнике можно запечатать, чтобы следующий наследник не мог этот метод переопределить еще раз.

Перекрытые методы запечатывать нельзя. Например:

```c#
internal class Asset
{
    public virtual void VirtualDescription()  // #1 Виртуальный метод в базовом классе
    {
        Console.WriteLine("Asset.VirtualDescription()");
    }

    public void Description()  // #2 Обычный метод
    {
        Console.WriteLine("Asset.Description()");
    }
}
```

```c#
internal class House : Asset
{
    public sealed override void VirtualDescription()  // #1 Мы переопределяем и запечатываем
    {
        Console.WriteLine("House.VirtualDescription()");
    }

    public new void Description()  // #2 Можно перекрыть, но sealed добавить нельзя
    {
        Console.WriteLine("House.Description()");
    }
}
```

```c#
internal class Castle : House
{
    public override void VirtualDescription()  // #1 Ошибка! Нельзя дальше переопределять запечатанный метод
    {
        Console.WriteLine("Castle.VirtualDescription()");
    }

    public new void Description()  // #2 Поэтому можно перекрывать и дальше
    {
        Console.WriteLine("Castle.Description()");
    }
}
```

