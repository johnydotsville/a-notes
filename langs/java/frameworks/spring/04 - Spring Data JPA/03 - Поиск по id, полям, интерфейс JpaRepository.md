# Схема интерфейсов

![repository-iface-hier.drawio](img/repository-iface-hier.drawio.svg)

Довольно объемная, но в итоге весь функционал сходится в интерфейсе JpaRepository.

# Интерфейс JpaRepository

## Объявление репозитория для сущности

Чтобы пользоваться возможностями JpaRepository, нужно создать свой интерфейс, расширяющий `JpaRepository<T, ID>`, где T - это сущность, для которой создается репозиторий, а ID - тип ключа.

```java
public interface ActorRepo extends JpaRepository<Actor, Long> {
    
}
```

Теперь можем требовать этот интерфейс и пользоваться:

```java
@RestController
@RequestMapping(path = "/api/actor")
public class ActorController {

    private ActorRepo actorRepo;

    @Autowired
    public ActorController(ActorRepo actorRepo) {
        this.actorRepo = actorRepo;
    }
    
}
```

В данном примере у нас сущность Actor, замапленная по всем правилам JPA:

```java
@Entity
@Table(name = "actor", schema = "public")
@Getter @Setter
public class Actor {

    @Id
    @Column(name = "actor_id")
    @SequenceGenerator(name = "actor_id_seq", sequenceName = "actor_actor_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "actor_id_seq")
    private long id;

    @Column(name = "first_name", length = 45)
    private String firstName;

    @Column(name = "last_name", length = 45)
    private String lastName;

}
```

## Базовые методы

Хотя мы не объявили ни одного метода в своем интерфейсе ActorRepo, нам уже доступно много методов, полученных от JpaRepository:

### findById

```java
Optional<Actor> actor = actorRepo.findById(id);
```

TODO: Здесь потом может быть заморочиться и про все методы написать с примерами.

## "Префиксные" методы

Можно конструировать более сложные методы репозитория, придерживаясь правил составления имен методов. Документация по префиксам [раз](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.subject) и [два](https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#appendix.query.method.predicate).

Такая возможность хорошо подходит для составления простых методов, вроде поиска по имени, фамилии, простой сортировки и т.д. Для более сложных запросов лучше пользоваться аннотацией @Query.

Примеры:

* Выбрать первых пять актеров, у которых имя начинается на букву N (без учета регистра) и отсортировать этот список по имени в обратном порядке:

  ```java
  public interface ActorRepo extends JpaRepository<Actor, Long> {
      List<Actor> findFirst5ByFirstNameStartsWithIgnoreCaseOrderByFirstNameDesc(String startsWith);
  }
  ```

## Методы с @Query

Можно объявить метод с любым именем и через аннотацию @Query задать ему запрос с помощью JPQL:

* Именованные параметры:

  ```java
  @Query("select ac from Actor ac where ac.firstName like :startsWith%")
  List<Actor> nameStartsWith(@Param("startsWith") String startsWith);
  ```

* Позиционные параметры:

  ```java
  @Query("select ac from Actor ac where ac.firstName like ?1%")
  List<Actor> nameStartsWith(String startsWith);
  ```

> % имеет значение как в обычных sql-запросах, like N% - начинается с N, а дальше любые символы. like %n% - просто где-то встречается n, не обязательно в начале

Это не нативный запрос, а JPQL, поэтому вместо таблиц и полей БД используется имя класса сущности и его полей.

Имхо этот способ так себе, потому что JPQL какой-то не интуитивный.

## Нативные запросы

Делаются тоже через @Query, с явным указанием, что это нативный запрос, используют непосредственно sql:

* Именованные параметры:

  ```java
  @Query(nativeQuery = true, value =
      "select ac.* from actor ac where ac.first_name like :startsWith% order by ac.first_name desc")
  List<Actor> nameStartsWith(@Param("startsWith") String startsWith);
  ```

* Позиционные параметры:

  ```java
  @Query(nativeQuery = true, value =
      "select ac.* from actor ac where ac.first_name like ?1% order by ac.first_name desc")
  List<Actor> nameStartsWith(String startsWith);
  ```

Здесь уже используется имя таблицы и поля из БД, т.к. запрос нативный.

TODO: а можно через параметр задать столбец сортировки?

## Дополнение интерфейса собственными методами

Если нужно добавить к JpaRepository какие-то свои методы, тогда:

* Объявляем собственный интерфейс с нужным методом:

  ```java
  public interface CustomActorRepo<Actor> {
      List<Actor> findMatthew();
  }
  ```

* Пишем класс, реализующий этот интерфейс. Важно, чтобы его имя заканчивалось на `Impl`, иначе спринг его не найдет:

  ```java
  public class CustomActorRepoImpl implements CustomActorRepo<Actor> {
  
      @PersistenceContext
      private EntityManager context;  // <-- Требует от спринга дать нам контекст
  
      @Override
      public List<Actor> findMatthew() {
          String name = "Matthew";
          Query query = context.createNativeQuery("select ac.* from actor ac where first_name = :name", Actor.class);
          query.setParameter("name", name);
          List<Actor> actors = query.getResultList();
  
          return actors;
      }
      
  }
  ```

* Добавляем новый интерфейс рядом с JpaRepository для интерфейса репозитория:

  ```java
  public interface ActorRepo extends 
          JpaRepository<Actor, Long>, 
          CustomActorRepo<Actor> {  // <-- Вот так вот
      ...
  }
  ```

## override стандартных методов

Стандартные методы JpaRepository можно переопределять. Например, переопределим метод delete, чтобы запись не удалялась, а просто в поле mark_deleted ей заносилось false. Делается почти так же как и в добавлении своих методов в интерфейс:

> Закомментированные строчки - это способ, который вероятно работал с более старыми версиями фреймворка. Теперь если так написать, то компилятор ругается на двойственность метода. Оставил на всякий случай, мб пригодится.

* Объявляем собственный интерфейс с методом, который хотим переопределить:

  ```java
  // public interface CustomActorRepo<Actor> {
  public interface CustomActorRepo<T> {
      List<T> findMatthew();
      // void delete(Object entity);
      <S extends T> void delete(T entity);
  }
  ```

* Пишем класс, реализующий этот интерфейс. Важно, чтобы его имя заканчивалось на `Impl`, иначе спринг его не найдет:

  ```java
  public class CustomActorRepoImpl implements CustomActorRepo<Actor> {
  
      @PersistenceContext
      private EntityManager context;
  
      ...
  
      @Transactional
      @Override
      // public void delete(Object entity) {
      public <S extends Actor> void delete(Actor entity) {
          Actor employees = (Actor) entity;
          employees.setMarkDeleted(true);
          context.persist(employees);
      }
  
  }
  ```

* Добавляем новый интерфейс рядом с JpaRepository для интерфейса репозитория:

  ```java
  public interface ActorRepo extends
          JpaRepository<Actor, Long>,
          CustomActorRepo<Actor> {
      ...
      // Теперь метод delete тут переопределен
  }
  ```

  















# TODO

- [ ] Аннотация @Transactional - что это?
- [ ] @Modifying
- [ ] Можно сделать побольше примеров на выборку с разным синтаксисом. Хотя бы на нативные запросы. Поэкспериментировать, можно ли задать например список столбцов сортировки, направление сортировки и т.д.
- [ ] Pagebale - написать, в каких методах можно использовать постраничность
- [ ] Как создать базовый свой интерфейс? Чтобы например для всех сущностей был переопределен метод delete