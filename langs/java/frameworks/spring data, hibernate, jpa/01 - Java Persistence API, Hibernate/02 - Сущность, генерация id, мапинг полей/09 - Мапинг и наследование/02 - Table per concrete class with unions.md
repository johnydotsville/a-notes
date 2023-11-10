# Table per concrete class with unions

## Характеристика

У этого подхода особенность в том, что все BillingDetails (т.е. все и кредитные карты, и банковские аккаунты, и т.д.) вытягиваются *одним* запросом, который использует union. Поскольку запрос один, этот подход считается эффективнее чем "table-per-concrete-class с неявным полиморфизмом":

```sql
select
	b1_0.id,
	b1_0.clazz_,
	b1_0.owner,
	b1_0.account,
	b1_0.bankname,
	b1_0.swift,
	b1_0.cardnumber,
	b1_0.expmonth,
	b1_0.expyear 
from 
( 
	select 
		id, owner, cardnumber, expmonth, expyear, null::text as account, 
		null::text as bankname, null::text as swift, 1 as clazz_ 
	from creditcard 
	
	union all 
	
	select 
		id, owner, null::text as cardnumber, null::text as expmonth, 
		null::text as expyear, account, bankname, swift, 2 as clazz_ 
	from bankaccount 
) b1_0
```

## Реализация

Здесь есть технические особенности реализации:

* Базовый класс должен быть абстрактным и отмечен как `@Entity`. Абстрактность нужна, потому что у него все еще нет собственной таблицы в БД.

* Идентификатор должен содержаться теперь в базовом классе, а не в потомках.

* Должна быть указана стратегия наследования `@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)`.

* Общие поля в таблицах, содержащих данные потомков, должны теперь называться одинаково.

  Если в предыдущем подходе с @MappedSuperclass мы могли переопределить название поля (например, owner на cc_owner), то теперь так не получится. Поле должно называться owner в обеих таблицах creditcard, bankaccount и быть замаплено в родительском классе под этим названием.

```java
@Entity
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)  // <-- Явно задаем стратегию наследования.
@Getter @Setter
public abstract class BillingDetails {  // <-- Класс должен быть абстрактным, т.к. у него нет своей таблицы.

    @Id  // <-- Класс должен содержать id.
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "billingdetails_id_gen")
    @SequenceGenerator(name = "billingdetails_id_gen", sequenceName = "billingdetails_id_seq", allocationSize = 1)
    protected int id;

    @Column(name = "owner")  // <-- Общие поля должны называться в таблицах одинаково.
    protected String holder;

}
```

Классы потомков:

* Больше не содержат id. 
* Больше не могут переопределять названия полей.

```java
@Entity
@Table(name = "bankaccount")
@Getter @Setter
public class BankAccount extends BillingDetails {

	// <-- Больше не содержит id.

    @Column(name = "account")
    private String account;

    @Column(name = "bankname")
    private String bankName;

    @Column(name = "swift")
    private String swift;

}
```

```java
@Entity
@Table(name = "creditcard")
// <-- Больше не можем переопределить через @AttributeOverride название поля owner на cc_owner.
@Getter @Setter
public class CreditCard extends BillingDetails {

	// <-- Больше не содержит id.

    @Column(name = "cardnumber")
    private String cardNumber;

    @Column(name = "expmonth")
    private String expMonth;

    @Column(name = "expyear")
    private String expYear;

}
```

