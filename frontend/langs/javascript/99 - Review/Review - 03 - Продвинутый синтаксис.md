# Продвинутый синтаксис

## Деструктурирующее присваивание

* Синтаксис деструктуризации массива, мапы и сета.
  * Пропуск элементов.
  * Пример, как использовать деструктуризацию для удобного обхода свойств объекта.
  * Значение для переменной при деструктуризации, если ей не хватило значения в массиве.
  * Задание значения по умолчанию для переменной.
* Синтаксис деструктуризации объекта.
  * Маппинг между переменными и свойствами объекта.
    * Автоматический.
    * Явный.
  * Пропуск свойств при деструктуризации.
  * Значение переменной, если в объекте при деструктуризации не нашлось указанного свойства.
  * Задание значения по умолчанию для переменной.
  * Использование заранее объявленных переменных при деструктуризации.
    * Массивов, мап, сетов.
    * Объектов.
* Вложенная деструктуризация.
* Трюк со свапом переменных.



## Остаточные параметры ...

* Синтаксис остаточных параметров.
* Остаточные параметры и деструктуризация.
  * Остаток массива.
  * Остаток объекта.
* Остаточные параметры и параметры функции.
  * Куда собираются аргументы, для которых не хватило параметров.
  * Остаточный параметр как единственный параметр функции.



## Оператор разбиения ...

* Оператор разбиения и итерируемые объекты.
* Концептуальное отличие оператора разбиения и деструктуризации.
  * Места применения того и другого.



## Нулевое слияние ?? и нулевое присваивание ??=

* Как работают эти операторы.
* Отличия в работе между ?? и || на примере с `height: 0`



## Присваивающее И &&= и ИЛИ ||=

* Как работают эти операторы.



## Опциональная цепочка ?.

* Является синтаксическим сахаром, а не оператором.
* Используется со свойствами, которые могут быть null или undefined.
  * Как реагирует на null и undefined.
* Как сочетается с обращением к свойствам через `[ ]` и с вызовом функций через `( )`
* Как правильно располагать эту конструкцию при обращении к свойствам и методам.