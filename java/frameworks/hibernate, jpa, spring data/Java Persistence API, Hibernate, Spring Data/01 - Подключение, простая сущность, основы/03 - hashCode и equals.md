Для сущностей принципиально важно реализовывать эти методы, потому что их может использовать хибер для своих целей. В базовом классе можно реализовать "проверку на дурака", а в самих сущностях дополнить проверками специфичных полей.

Базовый класс:

```java
@MappedSuperclass
@Getter @Setter
public abstract class AbstractEntity {
    ...

    @Override
    public boolean equals(Object obj) {  // Проверки на дурака
        if (this == obj)
            return true;
        if (obj == null || obj.getClass() != this.getClass())
            return false;
        return true;
    }
}
```

Класс сущности:

```java
@Entity
@Table(name = "city", schema = "bl")
@Getter @Setter
public class City extends AbstractEntity {
    @Id
    @Column(name = "id")
    @SequenceGenerator(name = "city_id_gen", schema = "bl", sequenceName = "city_id_seq", allocationSize = 1)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "city_id_gen")
    private long id;

    @Column(name = "name")
    private String name;

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj))  // Проверка на дурака из базовой сущности
            return false;
        var city = (City) obj;  // Специфическая для сущности проверка
        return Objects.equals(id, city.id);
    }
}
```

В данном случае в сущности сравниваются только id, потому что имхо это логично - сущность на то и сущность, id должен однозначно определять уникальность. Но важен сам принцип - проверку на дурака делаем в базовом классе, чтобы не дублировать в сущностях, а все остальные проверки - уже в сущностях, по тем полям, которые сочтем нужными.