# Встроенные классы, @Embeddable

TODO Переписать описание

Встроенные (embeddable) классы используются, когда, например, два Java-класса хранятся в БД в одной таблице. Обычно это речь про Value-Object'ы. Например, адрес, имя - все это можно представить отдельным объектом на стороне ООП, но при этом бывает нет смысла хранить это в отдельной таблице. Разберем на примере сущности актера и его имени:

Встроенный класс "Имя":

```java
@Embeddable  // <-- @Embeddable вместо @Entity используется для встроенных классов
// Таблицу указывать не надо, она возьмется из сущности, в которой мы объявим поле типа Name
@Getter @Setter  // Lombok
public class Name {
    
    @Column(name = "first_name", nullable = false)
    private String firstName;
    
    @Column(name = "last_name", nullable = false)
    private String lastName;

    private Name() { }  // <-- Обязательный конструктор без параметров, нужен хиберу

    public Name(String firstName, String lastName) {  // <-- Необязательный конструктор для удобства
        this.firstName = firstName;
        this.lastName = lastName;
    }
    
    @Override
    public boolean equals(Object o) {
        Name name = (Name) o;
        return Objects.equals(firstName, name.firstName) && Objects.equals(lastName, name.lastName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(firstName, lastName);
    }
    
}
```

Класс "Актер", содержит Имя:

```java
@Entity
@Table(name = "actor")
public class Actor {
    
    private Name name;  // <-- Объявляем поле встроенного типа

}
```

* Встроенный класс отмечаем аннотацией `@Embeddable`, поля мапим как обычно через @Column.
* Таблицу указываем только в "содержащем" классе и для пользования встроенным классом надо всего лишь объявить поле этого типа, дальше все происходит автоматически.
* Встроенный класс существует столько же, сколько и содержащий, не имеет собственного id и в общем не является самостоятельным.
* Важно переопределить методы equals и hashCode у встроенного класса, чтобы объекты сравнивались честно "по значению".
* Можно делать встроенные классы во встроенных классах и т.д. Степень вложения не ограничена.
* Способ доступа к свойствам (field-, property-access, см. конспект по важности расположения @Id) наследуется от содержащего класса.

```java
Actor actor = new Actor();
actor.setName(new Name(firstName, lastName));

System.out.println("Пытаемся сохранить актера");
manager.getTransaction().begin();
manager.persist(actor);
manager.getTransaction().commit();
...
Actor actor = manager.find(Actor.class, actorId);
System.out.println(String.format("%s %s", actor.getName().getFirstName(), actor.getName().getLastName()));
```

# null'ы во встроенном объекте

Если у встроенного объекта поля равны null и мы его сохраняем, то если БД допускает null в соответствующих столбцах, хибер сохранит объект нормально. Но при загрузке встроенного объекта, если в полях БД лежат null'ы, хибер не создаст объект, а вернет null.

На примере имени актера: можно сохранить актера, у которого в Name поля имени и фамилии равны null, но если мы попробуем потом загрузить этого актера, то объект Name создан не будет, хибер вернет вместо него null. Т.е. имеется ввиду, что вместо объекта Name с полями `имя, фамилия == null`, мы получим null вместо самого объекта Name.

# null'ы во встроенном объекте

Если у встроенного объекта поля равны null и мы его сохраняем, то если БД допускает null в соответствующих столбцах, хибер сохранит объект нормально. Но при загрузке встроенного объекта, если в полях БД лежат null'ы, хибер не создаст объект, а вернет null.

На примере имени актера: можно сохранить актера, у которого в Name поля имени и фамилии равны null, но если мы попробуем потом загрузить этого актера, то объект Name создан не будет, хибер вернет вместо него null. Т.е. имеется ввиду, что вместо объекта Name с полями имя, фамилия == null, мы получим null вместо самого объекта Name.

# Несколько одинаковых встроенных классов

Бывают ситуации, когда в одной таблице хранится несколько групп полей, которые могут быть представлены одним и тем же встроенным классом. Например, у писателей бывают псевдонимы наряду с настоящим именем:

| id   | first_name | last_name  | first_name_pseudo | last_name_pseudo |
| ---- | ---------- | ---------- | ----------------- | ---------------- |
| 1    | Антон      | Чехов      | Антоша            | Чехонте          |
| 2    | Александр  | Гриневский | Александр         | Грин             |

Класс `Name` под имя один, он подходит и для основного имени, и для псевдонима:

```java
@Embeddable
@Getter @Setter
public class Name {

    @Column(name = "first_name")
    private String first;  // <-- Поле специально названо так, чтобы сильно отличаться от имени в БД

    @Column(name = "last_name")
    private String last;  // <-- Это сделает пример переопределения нагляднее

    private Name() { }

    public Name(String first, String last) {
        this.first = first;
        this.last = last;
    }

    @Override
    public boolean equals(Object o) {
        Name name = (Name) o;
        return Objects.equals(first, name.first) && Objects.equals(last, name.last);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, last);
    }

}
```

А полей типа `Name` в классе Автор будет два - под имя и под псевдоним. И для одного из них нам придется переопределить мапинги с помощью аннотации `@AttributeOverride`:

```java
@Entity
@Table(name = "autor")
@Getter @Setter
public class Autor {
    
    ...
    private long id;

    private Name nameReal;  // <-- Для этого поля мапинги берутся как есть в классе Name
    
    @Embedded  // <-- Необязательная в данном случае аннотация
    @AttributeOverrides({
            @AttributeOverride(
                name = "first",  // <-- Имя переопределяемого поля в КЛАССЕ Name
                column = @Column(name = "first_name_pseudo")),  // <-- Новое значение
            @AttributeOverride(
                name = "last",
                column = @Column(name = "last_name_pseudo"))
    })
    private Name namePseudo;  // <-- Для этого поля маппинги переопределяем

}
```

Комментарии:

* `@Embedded` - конкретно в этом примере якобы не обязательно. Для чего нужна вообще - пока не понятно и не особо нужно. Связано что-то вроде с переопределением для third-party типов или вроде того.

# Вложенные встроенные классы

## Маппинг

Встроенные классы можно вкладывать друг в друга. Степень вложения не ограничена. Добавим в имя еще и прозвище (moniker), которое оформим отдельным классом:

```java
@Embeddable  // <-- ВЛОЖЕННЫЙ встроенный класс тоже отмечаем аннотацией @Embeddable
@Getter @Setter
public class Moniker {

    @Column(name = "moniker")  // <-- Мапим на поле таблицы
    private String nickname;

    private Moniker() { }

    public Moniker(String moniker) {
        nickname = moniker;
    }

    @Override  // <-- Не забываем переопределить методы равенства
    public boolean equals(Object o) {
        Moniker moniker = (Moniker) o;
        return Objects.equals(nickname, moniker.nickname);
    }

    @Override  // <-- и хэш-кода
    public int hashCode() {
        return Objects.hash(nickname);
    }

}
```

Используем прозвище в классе имени:

```java
@Embeddable
@Getter @Setter
public class Name {

    @Column(name = "first_name")
    private String first;

    @Column(name = "last_name")
    private String last;

    private Moniker moniker;  // <-- Просто добавляем прозвище в имя как обычное поле

    private Name() { }

    public Name(String first, String last, Moniker moniker) {  // <-- Учитываем его в конструкторе
        this.first = first;
        this.last = last;
        this.moniker = moniker;
    }

    @Override
    public boolean equals(Object o) {
        Name name = (Name) o;
        return Objects.equals(first, name.first)
                && Objects.equals(last, name.last)
                && Objects.equals(moniker, name.moniker);  // <-- И в методах сравнения
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, last, moniker);  // <-- и хэш-кода
    }

}
```

Класс актера:

```java
public class Autor {
    ...
    private long id;

    private Name name;
    
}
...
Actor actor = em.find(Actor.class, 3);
actor.getName().getMoniker().getNickname();
```

## Переопределение вложенных встроек

> Сейчас мне сложно придумать какой-нибудь хороший пример, потому что не могу представить адекватную ситуацию с вложением встроенных классов. Поэтому придется довольствоваться искусственным примером.
>
> Если вдруг понадобится перечитать оригинал, то эта тема разбирается в книге Java persistence with Hibernate - Christian Bauer, Gavin King, Gary Gregory (2016) на стр.95

Вернемся к примеру с именем и псевдонимом. Пусть у нас в таблице будет два поля с прозвищем. Одно прозвище будет для реального имени, а второе - для псевдонима. Поэтому нам придется для одного из них перемапить поле прозвища.

Вспомним структуру вложенных классов:

```java
@Entity
public class Autor {
    private Name name;
    private Name namePseudo;
    ...
@Embeddable
public class Name {
    private Moniker moniker;
    ...
@Embeddable
public class Moniker {
    @Column(name = "moniker")  // <-- Это поле нам предстоит перемапить на "moniker_folk" для одного из имен
    private String nickname;
```

Чтобы добраться до поля, которое надо переопределить, можно использовать последовательность полей через точку `.`, вот так:

```java
@AttributeOveride(name = "moniker.nickname")
```

```java
public class Autor {
    ...
    
    private Name name;
    
    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "first", column = @Column(name = "first_name_pseudo")),
            @AttributeOverride(name = "last", column = @Column(name = "last_name_pseudo")),
            @AttributeOverride(
                name = "moniker.nickname",  // <-- В Name есть поле moniker, а в Moniker есть поле nickname
                column = @Column(name = "moniker_folk"))
    })
    private Name namePseudo;
 
    ...
}
```

Поля специально названы по-разному, moniker и nickname, для наглядности.

## Одинаковые встроенные классы и вложение

Вспомним пример выше, когда один и тот же класс Name использовался для считывания \ записи реального имени и псевдонима. Добавить в Name прозвище в этом случае просто так не выйдет. Такая структура в целом логически некорректная. Потому что "РеальноеИмя" и "Псевдоним" фактически связаны с уникальными полями в таблице, а прозвище - связано с одним. 

> "Псевдоним" и прозвище тут разные вещи. Просто переделывать целый пример на адрес не хотелось, поэтому получилось несколько искусственно. Псевдоним пусть будет лично придуманное себе самим автором имя, а прозвище - это например дразнилка со времен детства или данное читателями прозвище.

Поэтому если мы сформируем например вот такие объекты:

```java
var real = new Name("Антон", "Чехов", "Тот что Каштанку написал");
var pseudo = new Name("Антоша", "Чехонте", "В очках с бородкой");

var author = new Author();
autor.nameReal = real;
autor.namePseudo = pseudo;

saveToDb(autor);
```

То получится, что мы передали хиберу два значения для прозвища. Но поле moniker в БД одно, и какое значение сохранить, хибер конечно же не поймет и выдаст ошибку. Придется либо для реального имени, либо для псевдонима переопределять атрибуты поля, чтобы либо там, либо там прозвище не использовалось при вставке и обновлении. В общем, получится каша.

Поэтому конкретно для этого случая прозвище не стоит встраивать в имя, его надо сделать отдельным полем.