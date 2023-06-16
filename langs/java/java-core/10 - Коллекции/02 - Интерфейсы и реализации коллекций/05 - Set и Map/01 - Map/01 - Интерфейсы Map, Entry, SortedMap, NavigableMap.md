# Схема

Map (словарь) - это структура, хранящая пары "ключ - значение". Одному ключу может соответствовать строго одно значение.

![coll_iface_concrete_map.drawio](img/coll_iface_concrete_map.drawio.svg)



# Концепция

Map - это словарь, хранящий элементы в виде пары "ключ + значение". Следовательно, используй Map, если:

* Нужно сохранять данные с возможностью извлечения по какому-то конкретному признаку (ключу).

При этом:

* Порядок элементов не важен? Используй HashMap.
* Хочешь, чтобы при итерации элементы шли в том порядке, в котором добавляешь? Используй LinkedHashMap.
* Хочешь, чтобы элементы были отсортированы и можно было делать всякое вроде "найти элементы больше такого-то"? Используй TreeMap.

# Интерфейс Map

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/Map.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/Map.java |

Обзор некоторых методов:

```java
public interface Map<K, V> {
    // Получение набора ключей, значений и ключей + значений
    Set<Map.Entry<K, V>> entrySet();
    Set<K> keySet();
    Collection<V> values();
    
    // Поиск ключа \ значения
    boolean containsKey(Object key);
    boolean containsValue(Object value);
    
    // Сохранение и извлечение элементов
    V put(K key, V value);
    default V putIfAbsent(K key, V value)
    void putAll(Map<? extends K, ? extends V> m);
    
    V get(Object key);
    default V getOrDefault(Object key, V defaultValue)
    
    // Удаление и замена
    V remove(Object key);
    default boolean remove(Object key, Object value);
        
    default V replace(K key, V value)
    default boolean replace(K key, V oldValue, V newValue)
    
    // Размер
    boolean isEmpty();
    int size();
    
    // Прочее
    default V merge(K key, V value, BiFunction<? super V, ? super V, ? extends V> remappingFunction)   
}
```

## Примеры



# Интерфейс Entry

Интерфейс Entry объявлен внутри интерфейса Map. Entry - это интерфейс, олицетворяющий элемент словаря.

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/Map.Entry.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/Map.java |

```java
public interface Map<K, V> {
    // ...
    interface Entry<K, V> {
        K getKey();
        V getValue();
        V setValue(V value);
        boolean equals(Object o);
        int hashCode();

        public static <K extends Comparable<? super K>, V> Comparator<Map.Entry<K, V>> comparingByKey();
        public static <K, V extends Comparable<? super V>> Comparator<Map.Entry<K, V>> comparingByValue();
        public static <K, V> Comparator<Map.Entry<K, V>> comparingByKey(Comparator<? super K> cmp);
        public static <K, V> Comparator<Map.Entry<K, V>> comparingByValue(Comparator<? super V> cmp);
        public static <K, V> Map.Entry<K, V> copyOf(Map.Entry<? extends K, ? extends V> e);
    }
   // ...
}
```

Реализации этого интерфейса находятся внутри каждой конкретной реализации словаря.

# Интерфейс SortedMap

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/SortedMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/SortedMap.java |

```java
public interface SortedMap<K,V> 
    extends Map<K,V> {
    ...
    +.firstKey();
    +.lastKey();
    
    +.headMap(K toKey);
    +.tailMap(K fromKey);
    
    +subMap(K fromKey, K toKey);
    ...
}
```



# Интерфейс NavigableMap

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/NavigableMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/NavigableMap.java |

```java
public interface NavigableMap<K,V> 
    extends SortedMap<K,V> {
    ...
    +.ceilingEntry(K key);
    
    ...
}
```

