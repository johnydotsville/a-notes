# Резюме

Упорядоченная коллекция, для которой характерно следующее:

* Соблюдается порядок элементов. Т.е. в каком порядке добавили, в таком и обошли

* Произвольный доступ, т.е. можно получить элемент по индексу

  Нужно учитывать, что производительность может быть плохой в зависимости от реализации. LinkedList тоже поддерживает доступ по индексу, но пользоваться этой возможностью лучше не надо

* Дублирующиеся элементы не запрещены

Список умеет все то же, что и коллекция, плюс:

* Сортировать свои элементы
* Возвращать\устанавливать элемент по индексу
* Все, что может быть связано с индексацией: удалять промежуток, возвращать промежуток

![coll_iface_concrete_list.drawio](img/coll_iface_concrete_list.drawio.svg)

Зачем существуют классы AbstractCollection и AbstractList (не показаны на схеме, чтобы не загромождать ее), если есть интерфейсы Collection и List? Если концептуально, то в абстрактных классах находится дефолтная реализация некоторых методов. Но почему бы ее не поместить тогда в интерфейсы, в дефолтные методы? Как минимум потому, что такая возможность появилась только в Java 8, а фреймворк коллекций был и раньше.

# RandomAccess

Это абсолютно пустой интерфейс, нужный по сути только для пометки, что коллекция "нативно" поддерживает доступ по индексу. Этим интерфейсом помечены классы ArrayList и Vector < Stack, но не помечен LinkedList.

# Краткий обзор методов

```java
public interface List<E> 
    extends Collection<E> {
         .add(item);
        +.add(index, item);
         .addAll(Collection);
        +.addAll(index, Collection);
    
        +.get(index);
        +.set(index, item);
    
        +.sort();
    
        +.indexOf(Object);
        +.lastIndexOf(Object);
        
        +.replaceAll(lambda);
        
        +.subList(from, to);
    
         .remove(Object);
        +.remove(index);
         .removeAll(Collection);
         .removeIf(Predicate);
    
         .retainAll(Collection);    
    
        +.listIterator();
        +.listIterator(index);
    
         .contains(Object);
         .containsAll(Collection);
    
         .size();
         .isEmpty();
         .clear();
    ...
         .equals(Object);
    
         .stream();    
         .parallelStream();
    
         .spliterator();
        
         .toArray();
         .toArray(T[] arr);
}
```

## listIterator

Возвращает "списочный" итератор, который в отличие от обычного итератора, умеет двигаться не только вперед, но и назад.