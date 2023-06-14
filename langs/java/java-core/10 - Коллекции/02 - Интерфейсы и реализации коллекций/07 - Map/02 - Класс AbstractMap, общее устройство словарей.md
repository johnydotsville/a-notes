# Класс AbstractMap

Абстрактный класс, от которого наследуются все конкретные реализации словарей (в т.ч. и конкурентных).

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/AbstractMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/AbstractMap.java |

```java
public abstract class AbstractMap<K,V>
    extends Object
    implements Map<K,V>
```

В этом классе написаны реализации для большинства методов интерфейса Map (хотя в потомках они зачастую переопределены).

# Устройство словарей

## Общие концептуальные компоненты

В общих чертах все словари устроены похожим образом - они имеют некоторые концептуально одинаковые компоненты, хотя технически эти компоненты реализованы по-разному. Вот эти общие компоненты:

* Класс, представляющий элемент словаря

* Поле под хранение элементов

* Три поля под хранение представлений (и методы их получения) для:

  * Элементов в целом (ключ + значение)
  * Отдельно ключей
  * Отдельно значений

* Классы *представлений* содержимого словаря и классы *итераторов* по этим представлениям. Представления предназначены для выдачи содержимого внешнему коду в понятном виде:

  * Представление + итератор для элементов в целом (ключ + значение)
  * Представление + итератор отдельно для ключей
  * Представление + итератор отдельно для значений

  "В понятном виде" означает, что если мы хотим, например, перебрать элементы словаря (или отдельно ключи \ данные), то словарь должен вернуть нам объект, который даст нам такую возможность, чтобы при этом мы не были привязаны к внутреннему устройству словаря.

## Примеры общих компонентов из реальных классов

Рассмотрим эти общие вещи на примере класса HashMap ([исходный код](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/HashMap.java)). Все они находятся *внутри* класса HashMap. Я разнес их по отдельным блокам кода исключительно для лучшей читаемости. Скопировано как есть, так что можно найти в исходниках полные реализации.

```javascript
public class HashMap<K,V> extends AbstractMap<K,V>
    implements Map<K,V>, Cloneable, Serializable {
    // Все последующие вещи находятся вот тут, описаны прямо внутри класса
}
```

### Класс элемента словаря

У каждой реализации этот класс может называться по-своему и иметь свою уникальную организацию. Например, у HashMap она такая:

```java
static class Node<K,V> implements Map.Entry<K,V> { 
    ...
    final int hash;
    final K key;
    V value;
    Node<K,V> next;
    ...
    public final K getKey()        { return key; }
    public final V getValue()      { return value; }
    ...
}
```

А у TreeMap вот такая - и название другое, и организация связи между элементами сделана по-другому, в стиле деревьев (родитель, левый и правый узлы):

```java
static final class Entry<K,V> implements Map.Entry<K,V> {
    ...
    K key;
    V value;
    Entry<K,V> left;
    Entry<K,V> right;
    Entry<K,V> parent;
    boolean color = BLACK;
    ...
}
```

### Поле под хранение элементов

Это поле, в котором собственно говоря и хранится содержимое словаря. Именно через это поле идет вся работа с элементами внутри словаря - добавление, удаление, поиск и т.д. У HashMap вот такое:

```java
transient Node<K,V>[] table;
```

У TreeMap - вот такое:

```java
private transient Entry<K,V> root;
```

Т.е. видно, что у реализации, использующей хэш-таблицу, содержимое представлено в виде массива. А у той, которая использует дерево, содержимое представлено корневым элементом, из которого идут ссылки на остальные элементы.

### Поля под хранение представлений и методы их получения

Представления нужны для того, чтобы можно было вернуть внешнему коду данные в таком виде, чтобы он с ними мог работать, ничего не зная о внутреннем устройстве словаря.

```java
transient Set<Map.Entry<K,V>> entrySet;
transient Set<K>        keySet;  // Наследие от AbstractMap
transient Collection<V> values;  // Наследие от AbstractMap
```

Эти поля изначально не заполнены. Они единожды заполняются при первом обращении к ним. Единожды, потому что в них после заполнения лежат живые данные, а не копии. Обращение к ним предусмотрено с помощью методов:

```java
public Set<Map.Entry<K,V>> entrySet() {
    Set<Map.Entry<K,V>> es;
    return (es = entrySet) == null ? (entrySet = new EntrySet()) : es;
}

public Set<K> keySet() {
    Set<K> ks = keySet;
    if (ks == null) {
        ks = new KeySet();
        keySet = ks;
    }
    return ks;
}

public Collection<V> values() {
    Collection<V> vs = values;
    if (vs == null) {
        vs = new Values();
        values = vs;
    }
    return vs;
}
```

### Классы представлений и итераторов

Базовый абстрактный класс итератора:

```java
abstract class HashIterator { 
    Node<K,V> next;  //
    Node<K,V> current;
    ...
    final Node<K,V> nextNode() { ... }
}
```

Представление и итератор для целого элемента:

```java
final class EntrySet extends AbstractSet<Map.Entry<K,V>> {   // Работа с целыми элементами
    ...
    public final Iterator<Map.Entry<K,V>> iterator() {
        return new EntryIterator();
    }
    ...
}

final class EntryIterator extends HashIterator implements Iterator<Map.Entry<K,V>> {
    ...
    public final Map.Entry<K,V> next() { 
        return nextNode(); // nextNode() - это метод самого класса словаря
    }
    ...
}
```

Представление и итератор для ключей:

```java
final class KeySet extends AbstractSet<K> {  // Работа с ключами
    ...
    public final Iterator<K> iterator() { 
        return new KeyIterator(); 
    }
    ...
}

final class KeyIterator extends HashIterator implements Iterator<K> {
    ...
    public final K next() { 
        return nextNode().key; 
    }
    ...
}
```

Представление и итератор для значений:

```java
final class Values extends AbstractCollection<V> {  // Работа со значениями
    ...
    public final Iterator<V> iterator() { 
        return new ValueIterator(); 
    }
    ...
}

final class ValueIterator extends HashIterator implements Iterator<V> {
    public final V next() { return nextNode().value; }
}
```

У остальных словарей тоже есть классы представлений и итераторов, только называются по-другому. При желании можно полистать исходники и найти. Вот несколько примеров заголовков из TreeMap ([исходный код](https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/TreeMap.java)):

```java
final class AscendingEntrySetView extends EntrySetView { ... }
final class DescendingEntrySetView extends EntrySetView { ... }
abstract class EntrySetView extends AbstractSet<Map.Entry<K,V>> { ... }

abstract class PrivateEntryIterator<T> implements Iterator<T> { ... }
final class EntryIterator extends PrivateEntryIterator<Map.Entry<K,V>> { ... }
final class ValueIterator extends PrivateEntryIterator<V> { ... }
final class KeyIterator extends PrivateEntryIterator<K> { ... }
final class DescendingKeyIterator extends PrivateEntryIterator<K> { ... }
// и т.д.
```

## Операции над представлениями

Получив любое представление (элементов в целом, отдельно ключей или отдельно значений), у нас через это представление:

* Есть возможность удалять элементы, искать. Внутри этих методов представления используются методы самого словаря.
* НЕТ возможности добавлять элементы. Во-первых, нам не доступны реальные типы элементов, вроде Node, потому что они скрыты внутри класса словаря. Во-вторых, даже если мы например сделаем собственные реализации, подходящие под нужные интерфейсы, то вызов метода добавление на представлении (а он есть), выбросит UnsupportedOperationException.

> В целом не понятно, почему через представления можно удалять, но нельзя добавлять. Технически возможность добавления есть, потому что раз уж удаление и прочее пользуется методами класса словаря, стало быть и добавление могло бы ими пользоваться. Так что не понятно. С логической точки зрения, имхо, тогда уж вообще стоило бы сделать только итерацию по представлению, а модификацию полностью исключить.

Допустим у нас есть вот такой словарь:

```java
Map<Integer, String> map = new HashMap<>();
map.put(1, "Tom Sawyer");
map.put(2, "Sid Sawyer");
map.put(3, "Huck Finn");
map.put(4, "Becky Thatcher");
```

И пара вспомогательных методов для распечатки словаря и множества из элементов словаря:

```java
public static void printMap(Map<Integer, String> map) {
    var entries = map.entrySet();
    for (Map.Entry<Integer, String> e : entries) {
        System.out.println(String.format("%d %s", e.getKey(), e.getValue()));
    }
}

public static void printSet(Set<Map.Entry<Integer, String>> set) {
    for (Map.Entry<Integer, String> e : set) {
        System.out.println(String.format("%d %s", e.getKey(), e.getValue()));
    }
}
```

Проверим тот факт, что через представление можно делать все, кроме добавления:

```java
Set<Map.Entry<Integer, String>> set = map.entrySet();  // Получили представление всех элементов
printSet(set);  // Убедились, что все персонажи на месте
Map.Entry<Integer, String> tom = set.stream().findFirst().get();  // Выбрали первый элемент из представления
set.remove(tom);  // Удалили найденный элемент, используя представление, а не словарь

printMap(map);  // Убедились, что из словаря пропал Том Сойер
printSet(set);  // Ну и из представления тоже
```

С представлениями ключей аналогично:

```java
Set<Integer> keys = map.keySet();
keys.remove(2);
printMap(map);  // Сид Сойер пропал из словаря
```

Попробуем добавить:

```java
Set<Map.Entry<Integer, String>> set = map.entrySet();
Map.Entry<Integer, String> tom = set.stream().findFirst().get();  // Получим готовый объект
set.remove(tom);  // Сначала удалим объект, через представление
set.add(tom);  // Попробуем добавить - нельзя, UnsupportedOperationException

map.put(tom.getKey(), tom.getValue());  // А через сам словарь - пожалуйста, добавляется
```

