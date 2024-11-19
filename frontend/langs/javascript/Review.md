# Без структуры

```
* Понятие объекта как типа данных.
* Объект как основа для других сложных типов данных вроде массивов, мап, сетов.
* Понятие прототипа.
* Object.prototype, Array.prototype, Map.prototype, Date.prototype
* Функция-конструктор Object, Array, Map, Date и т.д.
* Создание объекта через функцию-конструктор и оператор new.
* Создание объекта через объектный литерал.
* Создание объекта через метод Object.create()
* Указание прототипа для объекта через Object.create()
* Добавление собственных свойств объекту через Object.create()
* Создание объекта без прототипа через Object.create()
* Собственность на свойства и методы у объекта и его прототипа.
* Попытка прочитать или записать \ удалить отсутствующее у объекта свойство и прототип.
* Изменение прототипа после создания объекта.

* Служебное свойство [[Prototype]] у объектов.
* Геттер \ сеттер __proto__ у объектов.
* Методы Object.setPrototypeOf() и Object.getPrototypeOf()
* Количество прототипов у объекта, цепочка прототипов.
* Вызов на объекте метода прототипа, в котором есть this.

* Функция-конструктор и оператор new, принцип работы.
* Установка прототипа для объекта через ФК.
* Дефолтный объект в свойстве .prototype у ФК.
* Добавление свойств и методов в прототип объекта через ФК.
* Добавление свойств и методов в сам объект через ФК.
* Ссылка на ФК в прототипе объекта и ее восстановление вручную.
* Конструкция new.target внутри ФК.
* Одноразовая ФК, создание одного объекта со сложной логикой создания.
```



# Объекты

## Объекты и прототипы

* Понятие объекта как типа данных.
  * Объект как основа для других сложных типов данных вроде массивов, мап, сетов.
* Понятие прототипа.
* Объекты Object.prototype, Array.prototype, Map.prototype, Date.prototype
  * Функция-конструктор Object, Array, Map, Date и т.д.
* Создание объекта.
  * Через функцию-конструктор и оператор new.
  * Через объектный литерал.
  * Через метод Object.create()
    * Указание прототипа для объекта через Object.create()
    * Создание объекта без прототипа через Object.create()
    * Добавление собственных свойств объекту через Object.create()
* Собственность на свойства и методы у объекта и его прототипа.
  * Попытка прочитать или записать \ удалить отсутствующее у объекта свойство и прототип.

## Прототипы

* Хранение прототипа объекта.
  * Служебное свойство [[Prototype]] у объектов.
  * Геттер \ сеттер `__proto__` у объектов.
* Изменение прототипа после создания объекта.
  * Методы Object.setPrototypeOf() и Object.getPrototypeOf()
* Количество прототипов у объекта, цепочка прототипов.
* Вызов на объекте метода прототипа, в котором есть this.

 ## Функция-конструктор

* Функция-конструктор и оператор new, принцип работы.
  * Конструкция new.target внутри ФК.
* Одноразовая ФК, создание одного объекта со сложной логикой создания.
* Свойство .prototype у функций.
  * Установка прототипа для объекта через ФК.
  * Дефолтный объект в свойстве .prototype у ФК, его свойство .constructor
    * Ссылка на ФК в прототипе объекта и ее восстановление вручную.
* Добавление свойств и методов в прототип объекта через ФК.
* Добавление свойств и методов в сам объект через ФК.