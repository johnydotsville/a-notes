# Примитивное сохранение

EntityManager, сохранение, извлечение и т.д. - отдельная тема. Этот документ - быстрогайд.

Вот грязный пример сохранения актера в БД:

```java
// Сперва формируем контекст по настройкам из указанного persistence-unit'а
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdrental-pu");
EntityManager manager = factory.createEntityManager();

// Создаем сущность
Actor tom = new Actor();
tom.setFirstName("Tom");
tom.setLastName("Sawyer");

// И через контекст управляем хранением сущности
System.out.println("Пытаемся сохранить Тома");
manager.getTransaction().begin();
manager.persist(tom);
manager.getTransaction().commit();
manager.close();
```

Грязный - потому что не понятно, нужна транзакция вообще или нет, а если нужна, то надо ли вот так явно ее начинать\заканчивать или для этого есть специальные средства.

Но по крайней мере все это запускается и работает.

Вот еще пример. Тоже грязь, но показывает, что нужно выполнять работу внутри try и откатывать транзакцию в случае ошибок:

```java
Actor actor = new Actor();
actor.setName(new Name("Tom", "Sawyer"));

EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdrental-pu");
EntityManager manager = factory.createEntityManager();
EntityTransaction tr = manager.getTransaction();
try {
    tr.begin();
    manager.persist(actor);
    actor.setName(new Name("Huck", "Finn"));
    tr.commit();
} catch (Exception ex) {
    System.out.println("Откат транзакции");
    tr.rollback();
} finally {
    if (manager != null && manager.isOpen()) {
        manager.close();
    }
}
```

Поскольку это грязь, тут создается и factory, но вообще она должна создаваться где-то гораздо выше и быть единственной на все время работы приложения. manager может создаваться чаще, например, на каждый веб-запрос. В каком-то локальном приложении я не знаю как правильно его создавать.