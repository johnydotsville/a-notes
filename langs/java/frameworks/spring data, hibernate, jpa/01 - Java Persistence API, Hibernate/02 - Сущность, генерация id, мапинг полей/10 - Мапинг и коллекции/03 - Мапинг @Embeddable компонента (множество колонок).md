Зачем это? Предположительно, это можно использовать, когда какой-то класс не является сущностью, но имеет собственную таблицу в БД.



Не работает, ошибка "Unable to evaluate the expression Method threw 'org.hibernate.InstantiationException' exception."

Учебник стр. 154



```java
@Entity
@Table(name = "employee")
@Getter @Setter
public class Employee {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.TABLE, generator = "employee_id_gen")
    @SequenceGenerator(name = "employee_id_gen", sequenceName = "employee_id_seq", allocationSize = 1)
    private long id;

    @Column(name = "name")
    private String name;

    @ElementCollection // <--
    @CollectionTable(name = "emp_photo_2", joinColumns = @JoinColumn(name = "emp_id"))
    private Set<Photo> photos = new HashSet();

}
```





```java
@Embeddable
@Getter @Setter
public class Photo {

    @Column(name = "filename")
    private String filename;

    @Column(name = "size_kb")
    private int sizeKb;

    @Column(name = "width_px")
    private int widthPx;

    @Column(name = "height_px")
    private int heightPx;

    @Column(name = "description")
    private String description;

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != getClass()) return false;

        var p = (Photo) obj;

        if (filename != p.filename) return false;
        if (sizeKb != p.sizeKb) return false;
        if (widthPx != p.widthPx) return false;
        if (heightPx != p.heightPx) return false;
        if (description != p.description) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hash(filename, sizeKb, widthPx, heightPx, description);
    }
    
}
```

