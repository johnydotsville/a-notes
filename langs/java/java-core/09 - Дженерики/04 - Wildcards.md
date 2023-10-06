# Концепция вайлдкардов

Вайлдкарды тесно связаны с вариантностью, так что если нет понимания инвариантности, ковариантности и контравариантности, следует почитать про них соответствующий конспект в разделе `cs`. Хотя и здесь я на конкретных примерах постараюсь максимально продублировать и разъяснить эти понятия.

## Вводный пример

Дженерики называются производными типами по отношению к закрывающему их типу. Т.е. `List<Cat>` является производным от `Cat`.

Дженерики сами по себе инвариантны, т.е. у них отсутствует совместимость присваивания, в отличие от закрывающих их типов. Практически это означает следующее (Integer - наследник Number):

```java
Number numb = new Integer();  // Ok, у "оригинальных" типов есть совместимость присваивания
List<Number> numbers = new ArrayList<Integer>();  // Ошибка, у производных типов нет СП
```

```java
void dosome(Animal animal) { ... }
var cat = new Cat();
dosome(cat);  // Ok

void dosome(List<Animal> animals) { ... }
List<Cat> cats = new ArrayList<Cat>();
dosome(cats);  // Ошибка
```

Однако с логической точки зрения, если с кошкой можно работать как с животным, то должна быть возможность работать и со списком кошек как со списком животных.

Вот для этого и существуют вайлдкарды - они позволяют нам задействовать вариантность:

```java
List<? extends Number> numbers = new ArrayList<Integer>();  // Теперь Ok
```

```java
void dosome(List<? extends Animal> animals) { ... }
var cats = new ArrayList<Cat>();
dosome(cats);  // Теперь Ok
```

## Место использования вайлдкарда

Вайлдкарды `?` применяются не при объявлении классов-дженериков, а в месте их использования. Я не сразу это понял в первый раз. Например:

```java
class A<? extends T> {  // Логически бессмысленно
    ... 
}  
```

```java
// Присваивание переменной, передача параметров - вот где нужно использовать вайлдкарды
List<? extends Number> lnumb = ...
void dosome(List<? extends Animal> animals) { ... }
```

# Синтаксис

Для примеров будем пользоваться несколькими классами:

```java
class Animal {

    protected String name;

    public Animal() {
        name = "Неизвестное науке существо";
    }

    protected Animal(String name) {
        this.name = name;
    }

    public void voice() {  // Пусть все животные способны как-то подать голос
        System.out.println(name + " говорит: абырвалг!");
    }

    @Override
    public String toString() {  // Также все животные могут представиться
        return "Я - " + name;
    }

}
```

```java
class Cat extends Animal {

    public Cat(String name) {
        super(name);
    }

    @Override
    public void voice() {
        System.out.println(name + " говорит: Мяу!");
    }

    public void scratch() {  // Только кошка умеет царапаться
        System.out.println(name + " царапается.");
    }

    @Override
    public String toString() {
        return "Я - котэ и зовут меня " + name;
    }

}
```

```java
class Dog extends Animal {

    public Dog(String name) {
        super(name);
    }

    @Override
    public void voice() {
        System.out.println(name + " говорит: Р-р-р! Гав!");
    }

    public void bite() {  // Только собака умеет кусаться
        System.out.println(name + " кусается.");
    }

    @Override
    public String toString() {
        return "Я - собака-улыбака по имени " + name;
    }

}
```

Есть два синтаксиса вайлдкардов:

* `SomeGenericClass<? extends T>` - подстановочный тип является типом T или его потомком.

  Например,  если `<? extends Animal>`, то `? = { Animal | Dog | Cat }`.

* `SomeGenericClass<? super T>` - подстановочный тип является типом T или его родителем.

  Например, если `<? super Dog>`, то `? = { Dog, Animal, Object }`, но не может быть `Cat`.

## <? extends T>

По сути это синтаксис, реализующий ковариантность, т.е. возможность для дженериков "поработать с потомками как с родителями". Классический пример - коллекции:

```java
List<Dog> dogs = Arrays.asList(new Dog("Шарик"), new Dog("Бобик"), new Dog("Тузик"));
List<Cat> cats = Arrays.asList(new Cat("Барсик"), new Cat("Мурзик"), new Cat("Кузя"));

voice(dogs);
voice(cats);

void voice(List<? extends Animal> animals) {
    animals.forEach(a -> a.voice());  // Какие бы животные сюда не попали, все умеют подать голос
}
```

## <? super T>

Это синтаксис, реализующий контравариантность, т.е. невозможность для дженериков "поработать с родителями как с потомками". Классический пример - передача функций: если функцию можно применить к родителю, то ее гарантированно можно применить и к потомку; но если функцию можно применить к потомку, это не значит, что ее можно применить к родителю.

Объявим несколько функций:

```java
Consumer<Object> toString = o -> System.out.println(o.toString());  // Функция для любого объекта
Consumer<Animal> voice = a -> a.voice();  // Функция для животного сработает и для кошки, и для собаки
Consumer<Dog> bite = d -> d.bite();  // Функция, которая будет работать только на собаках
Consumer<Cat> scratch = c -> c.scratch();  // Функция, которая будет работать только на кошках
```

Объявим несколько методов, принимающих функции. Причем предполагается, что каждый метод рассчитан на работу с определенным объектом - в одном методе ждет команд собака, в другом - кот, в третьем - любое животное:

```java
void animalDoCommand(Consumer<? super Animal> command) {
    var animal = new Animal();
    command.accept(animal);  // Заставляем животное выполнить переданную в метод функцию
}

void sharikDoCommand(Consumer<? super Dog> command) {
    var sharik = new Dog("Шарик");
    command.accept(sharik);  // Заставляем собаку выполнить переданную в метод функцию
}

void barsikDoCommand(Consumer<? super Cat> command) {
    var barsik = new Cat("Барсик");
    command.accept(barsik);  // Заставляем кота выполнить переданную в метод функцию
}
```

Теперь попробуем передать функции в методы:

```java
// Функцию, работающую с объектом, смогут выполнить все
animalDoCommand(toString);  // Я - Неизвестное науке существо
sharikDoCommand(toString);  // Я - собака-улыбака по имени Шарик
barsikDoCommand(toString);  // Я - котэ и зовут меня Барсик

// Функцию, работающую с животным, смогут выполнить и кошки, и собаки, и прочие животные
animalDoCommand(voice);  // Неизвестное науке существо говорит: абырвалг!
sharikDoCommand(voice);  // Шарик говорит: Р-р-р! Гав!
barsikDoCommand(voice);  // Барсик говорит: Мяу!

// Т.е. bite работает с собакой, то кот и абстрактное животное не могут кусаться
animalDoCommand(bite);  // Ошибка компиляции
sharikDoCommand(bite);  // Шарик кусается.
barsikDoCommand(bite);  // Ошибка компиляции

// Т.к. scratch работает с котом, то собака и абстрактное животное не могут царапаться
animalDoCommand(scratch);  // Ошибка компиляции
sharikDoCommand(scratch);  // Ошибка компиляции
barsikDoCommand(scratch);  // Барсик царапается.
```



