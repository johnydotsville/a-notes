# ConcurrentHashMap

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentHashMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/concurrent/ConcurrentHashMap.java |

Характеристики:

* Потокобезопасный словарь.

* НЕ допускает null ни как ключ, ни как значение.

* Операции извлечения не используют замки, поэтому могут пересекаться с операциями обновления (включая put и remove).

* Операция извлечения по ключу показывает значение наиболее свежей *завершенной* операции обновления.

* При групповых операциях, вроде putAll и clear, нет гарантий что операции извлечения увидят все изменения. Они вполне могут увидеть их только частично.

* Итераторы отражают состояние словаря на момент своего создания и не выбрасывают ConcurrentModificationException.

  По этой же причине операции вроде size, isEmpty, containsValue носят исключительно приблизительный характер.



# ConcurrentSkipListMap

| Описание     | Ссылка                                                       |
| ------------ | ------------------------------------------------------------ |
| Документация | https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ConcurrentSkipListMap.html |
| Исходный код | https://github.com/openjdk/jdk/blob/master/src/java.base/share/classes/java/util/concurrent/ConcurrentSkipListMap.java |

Характеристики: