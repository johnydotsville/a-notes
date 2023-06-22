# Наследование и private-поля

private-поля наследуются, просто в потомке напрямую не доступы. Чтобы работать с ними, потомкам необходимо пользоваться методами родителя:

```java
class Asset {
    private String name;
    
    public Asset(String name) {
        this.name = name;
    }
    
    public void changeName(String name) {
        this.name = name;
    }
    
    public void printName() {
        System.out.println(name);
    }
}
```

```java
class House extends Asset {
    public House(String name) {
        super(name);
    }

    public void changeName(String name) {
        // this.name = name;  // Ошибка! Прямого доступа нет
        super.changeName(name);  // Вот так правильно
    }
}
```

```java
var ass = new Asset("Asset");
var house = new House("House");

ass.printName();  // Asset
house.printName();  // House

house.changeName("Very big house");

ass.printName();  // Asset
house.printName();  // Very big house

// Как видно, у house свое собственное поле name, никак не связанное с name у Asset
```

