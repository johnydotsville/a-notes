# Hashtable

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/Hashtable.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/Hashtable.java |



# HashMap

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/HashMap.java |

Характеристики:

* В основе лежит хэш-таблица в виде вот такого массива:

  ```java
  transient Node<K,V>[] table;
  ```

* Для разрешения коллизий используется связный список. Каждый хранимый элемент в HashMap представлен вот таким классом (этот класс объявлен внутри класса HashMap):

  ```java
  static class Node<K,V> implements Map.Entry<K,V> {
      final int hash;
      final K key;
      V value;
      Node<K,V> next;
      ...
  }
  ```

* Разрешен null в ключах и значениях. В значениях очевидно почему, а в ключе за счет того, что передача null расценивается как ключ = 0.

* Для определения позиции в ХТ хэш рассчитывается от ключа.

* Не гарантирует определенный порядок элементов и сохранение этого порядка с течением времени. Вероятно это связано с перехэшированием. Когда хэш-таблица достигает границы дозволенной загруженности, выделяется новый массив, хэш элементов пересчитывается и они занимают новые места.

* Добавление\удаление за константное время

* Не потокобезопасна

## Добавление элемента и про null в ключе

Вот код добавления элемента (видно, что хэш берется *от ключа*):

```java
public V put(K key, V value) {
    return putVal(hash(key), key, value, false, true);
}
```

А вот код функции hash:

```java
static final int hash(Object key) {
    int h;
    return (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
}
```

Т.е. видно, что null в качестве ключа обрабатывается особым образом - хэш будет 0 и данные с ключом null всегда попадают в одно и то же место. Стало быть, можно сколько угодно раз добавлять элементы с ключом null и каждый новый элемент просто перезапишет старый.

> `>>>` беззнаковый сдвиг вправо, `^` xor

## Роль equals

Хэш-код используется для определения ячейки хэш-таблицы, а equals - для определения конкретного элемента внутри этой ячейки, если элементов там несколько. При извлечении по equals определяется, какой именно элемент надо вернуть. А при добавлении - понять, надо ли именно добавить новый элемент, либо перезаписать уже существующий.

# LinkedHashMap

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/LinkedHashMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/LinkedHashMap.java |

Характеристики, в дополнение к характеристикам HashMap:

* При итерации элементы перебираются в порядке вставки. Элемент, вставленный повторно (т.е. когда в словаре уже есть элемент с указанным ключом), при итерации остается на исходной позиции.

Каждый элемент внутри LinkedHashMap представлен вот таким классом:

```java
static class Entry<K,V> extends HashMap.Node<K,V> {
    Entry<K,V> before, after;
    Entry(int hash, K key, V value, Node<K,V> next) {
        super(hash, key, value, next);
    }
}
...
transient LinkedHashMap.Entry<K,V> head;
...
transient LinkedHashMap.Entry<K,V> tail;
```

Т.е. стандартный узел дополняется еще ссылками на следующий и предыдущий элемент. Кроме того, есть ссылки на начало и конец списка. Таким образом, элементы попадают в хэш-таблицу как обычно, но просто еще имеют ссылки друг на друга.

# TreeMap

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/TreeMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/TreeMap.java |

* Использует красно-черное дерево

  ```java
  private transient Entry<K,V> root;
  ```

* Поэтому для операций добавления, удаления, извлечения и contains дает сложность $log_{2} n$

* Порядок элементов - так называемый natural ordering, т.е. элементы или должны реализовывать интерфейс Comparable, или структуре при создании нужно передать Comparator, чтобы она знала, как сравнивать элементы

* null не допускается в качестве ключа. Но когда-то давно, до 7 джавы, допускалось добавлять null первым элементом, после чего уже ничего нельзя было добавить и ничего не работало, но теперь null запрещен

* Не синхронизирован

## null в ключе

TODO: На самом деле, можно добавить null в ключе. Детально сейчас нет времени разбираться, есть вещи поважнее, но я оставлю тут зацепку на будущее. Поведение получается странное, но ошибки нет, можно потом получше поэкспериментировать:

```java
private static void nullAsKeyInTreeMap() {
    Comparator<Integer> personComparator = (Integer i1, Integer i2) -> {
        if (i1 == null || i2 == null) {
            return 0;
        }
        return i1.compareTo(i2);
    };

    Map<Integer, Person> map = new TreeMap<Integer, Person>(personComparator);

    Person tom = new Person("Tom Sawyer", 14);
    Person huck = new Person("Huck Finn", 14);
    Person mary = new Person("Mary Sawyer", 17);

    map.put(5, tom);
    map.put(12, huck);
    map.put(null, mary);

    printMap(map);
}
// Вывод (Том пропал)
// Mary Sawyer 17
// Huck Finn 14

private static void printMap(Map<Integer, Person> map) {
    Set<Map.Entry<Integer, Person>> entries = map.entrySet();
    for (Map.Entry<Integer, Person> entry : entries) {
        System.out.println(entry.getValue());
    }
}
```

