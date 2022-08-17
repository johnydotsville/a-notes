# Генерация значений на стороне БД

При сохранении объектов некоторые поля может генерировать БД. Например, время создания\последнего изменения объекта. Id сюда не относится, это отдельная тема. Чтобы поместить эти "автозначения" сразу в сохраняемый объект, а не тащить их как-то отдельно, делаем вот так:

```java
import jakarta.persistence.Column;
import jakarta.persistence.Temporal;

@MappedSuperclass
public abstract class AbstractEntity {
    @Column(name = "last_update", insertable = true, updatable = true)  // 1
    @Temporal(TemporalType.TIMESTAMP)  // 2
    @org.hibernate.annotations.Generated(  // 3
            value = org.hibernate.annotations.GenerationTime.ALWAYS)  // .INSERT, .NEVER
    private LocalDateTime lastUpdate;
    
    
@MappedSuperclass
@Getter @Setter
public abstract class AbstractEntity {
    @Column(name = "last_update", insertable = false, updatable = false)  // 1
    @Temporal(TemporalType.TIMESTAMP)  // 2
    @org.hibernate.annotations.Generated(  // 3
            value = org.hibernate.annotations.GenerationTime.ALWAYS)  // .INSERT, .NEVER
    private Calendar lastUpdate;

    @Column(name = "mark_deleted", insertable = false, updatable = true)
    @org.hibernate.annotations.Generated(
            value = org.hibernate.annotations.GenerationTime.INSERT)
    private boolean markDeleted;

   ...
}
```

Здесь у нас базовый класс сущности, который сам по себе не сохраняется, но содержит общие поля, которые есть у всех сущностей. Среди них - как раз дата последней модификации и отметка об удалении.

* **3:** @Generated - когда используем аннотации хибера, рекомендуют писать полностью вместе с пакетом. Этой аннотацией помечаем автогенерируемое свойство. С помощью параметра *value* задаем, после каких запросов нужно вытянуть значение столбца из БД, чтобы поместить в java-объект. Ведь если значение генерится БД'шкой, наш объект о нем ничего не узнает, если отдельно это значение не вытянуть. При значении ALWAYS хибер будет вытягивать сгенерированное значение при выполнении и insert-операций, и update-операций. При NEVER - очевидно никогда, при INSERT - только при вставках.

  На примере этих полей: почему для lastUpdate стоит ALWAYS, а для markDeleted - INSERT? Потому что lastUpdate всегда генерирует БД, программа это свойство не изменяет. Значит, после любых операций хибер должен вытянуть сгенерированной БД'шкой значение. Для markDeleted ситуация немного иная, потому что программа его может изменять. При первичном сохранении объекта, т.е. по сути при вставке новой строки в таблицу, БД генерирует полю markDeleted значение по умолчанию, значит нужно это значение вытянуть. Но если мы это свойство изменим в программе и сохраним объект (вызвав таким образом уже команду update), то вытягивать значение бессмысленно, потому что оно у нас уже есть.

* **1:** @Column - помимо мапинга на столбец БД, через параметры insertable\updatabe сообщаем о том, нужно ли добавлять в запрос на insert\update поле lastUpdate. Если false - то не нужно (по умолчанию true)

  На примере полей: программа никогда не устанавливает свойство lastUpdate, значит не нужно его добавлять ни в запросы insert, ни в update. Для поля markDeleted первичное значение задает БД, поэтому в запрос insert это поле добавлять не надо. Но когда мы хотим удалить объект, то именно программа устанавливает значение markDeleted = true, поэтому в запросы update его вставлять надо.

* **2:** @Temporal - возможные значения TIMESTAMP, DATE, TIME. Этой аннотацией нужно обязательно помечать автосвойства, у которых тип - разновидность времени, чтобы хибер корректно заполнял их. По значениям и так понятно что для чего. У нас тип LocalDateTime, т.е. дата со временем, значит и в Temporal указываем дату со временем.

## Комбинация @Generated и insertable\updatable

Хотя @Generated и insertable\updateble непосредственно не связаны друг с другом, но если у нас например БД генерит значение поля и при вставке, и при обновлении, то вполне логично будет использовать комбинацию Generated ALWAYS + Column insertable\updatable = false, чтобы хибер все время вытягивал для нас сгенерированное значение, но не добавлял эти столбцы в запросы. Но если оставить insertable\updatable = true, то ошибки вероятно не будет, просто запрос будет обновлять\вставлять лишние значения, которые БД все равно затрет своими значениями. По крайней мере в моих экспериментах было так.