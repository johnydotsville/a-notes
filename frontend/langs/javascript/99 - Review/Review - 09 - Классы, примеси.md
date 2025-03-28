# Классы

* Классы в JS - это синтаксический сахар над созданием объекта и установкой ему прототипа.
* Ключевое слово `class`-  это синтаксический сахар над функцией-конструктором.
  * У класса и функции один и тот же прототип.
* Конструктор класса.
  * Тело конструктора класса - это тело функции-конструктора.
  * В JS у класса мб только один конструктор.
  * Конструктор писать не обязательно, если он не нужен.
* Свойства `[[Prototype]]` и `.prototype` у класса.
* Расположение членов класса.
  * Статические свойства и методы.
  * Обычные свойства.
    * Объявление обычных свойст непосредственно в теле класса со значениями по умолчанию.
  * Обычные методы.
  * Для статических членов используется ключевое слово `static`.
* this в классах.
  * В статических методах.
  * В обычных методах.
    * Обязательность this в обычных методах для обращения к полям объекта.
* Добавление методов в существующий класс.
* Имя класса - это имя переменной, в которой лежит функция.
  * Передача класса как обычной функции.
* Объект, созданный из класса, называется экземпляр (или инстанс).
* Абстрактных классы в JS.
* Класс и strict mode.
* Класс и всплытие.
* Объявление класса
  * statement-стиль.
  * expression-стиль.
* Модификаторы видимости.
  * public.
  * protected.
    * Условная договоренность для имен protected-полей.
  * private.
    * Имя private-поля.
    * Обязательность объявления private-полей в теле класса перед использованием.
    * Для чтения и записи private-полей нужны геттеры и сеттеры.

# Наследование

* Ключевое слово для наследования.
* Собственный и родительский конструктор.
  * Обязательный вызов родительского конструктора в конструкторе потомка.
    * Ключевое слово для вызова родительского конструктора.
    * Автоматический вызов родительского конструктора без параметров.
    * Ограничение на вызов родительского конструктора и this.
* Порядок поиска методов.
* Синтаксис переопределения метода в потомке.
  * Вызов родительской реализации метода в потомке.
* Все наследование под капотом работает на основе прототипов.
  * Прототип класса-родителя и прототип класса-потомка.
  * Свойство .prototype у классов.
  * Прототип экземпляра.
  * Место хранения
    * static-методов и свойств класса.
    * Обычных методов.
    * Обычных свойств.
    * Собственность на эти вещи.

# Принадлежность к классу

* Оператор instanceof для проверки принадлежности объекта к классу.
  * Принадлежность по цепочке, а не только непосредственная.
  * Что служит признаком того, что объект принадлежит какому-то классу?
* Проверка того, что один объект является прототипом другого методом `a.isPrototypeOf(b)`
  * Проверка по цепочке, а не только непосредственная.