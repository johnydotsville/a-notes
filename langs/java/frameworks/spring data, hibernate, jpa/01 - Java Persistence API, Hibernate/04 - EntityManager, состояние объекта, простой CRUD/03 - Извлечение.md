# Direct fetching

Следующий способ называется direct fetching:

```java
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdrental-pu");
manager = factory.createEntityManager();

Actor actor = manager.find(Actor.class, actorId);

System.out.println(String.format("%s %s", actor.getFirstName(), actor.getLastName()));
```

У контекста есть метод *find*, который предназначен для поиска сущности по id. Есть *findAll* для выбора всех записей. Поиска по конкретному полюс нет - такие вещи идут из коробки в Spring Data JPA, а в голом JPA и хибере для них есть специальный язык "запроса к сущностям" JPQL и механизм Native Queries, позволяющий выполнять голые SQL запросы.

О них я сделал отдельный раздел, а здесь просто легкий пример для демонстрации.

# Entity query, JPQL

А вот такой способ называется entiry query:

```java
EntityManagerFactory factory = Persistence.createEntityManagerFactory("dvdrental-pu");
manager = factory.createEntityManager();

Actor actor = manager
    .createQuery("select a from Actor a where a.id = :id", Actor.class)
    .setParameter("id", actorId)
    .getSingleResult();

System.out.println(String.format("%s %s", actor.getFirstName(), actor.getLastName()));
```

