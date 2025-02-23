# Flexbox

## Флекс-контейнер

* Тип контейнера
  * Блочный `display: flex`
    * Контейнер займет всю ширину родителя, т.о. на строке только один контейнер.
  * Строчный `display: inline-flex`
    * Контейнер займет места, сколько ему надо и на одной строке мб несколько контейнеров.
* Оси контейнера.
  * Виды осей
    * Основная.
    * Поперечная.
    * Мы задаем направление основной оси, тогда поперечной становится перпендикулярная ей ось.
  * Задание направления основной оси
    * `flex-direction: row* | column | row-reverse | column-reverse`
* Перенос элементов
  * `flex-wrap: nowrap* | wrap | wrap-reverse`
  * Без переноса
    * Когда контейнер сужается, элементы начинают сжиматься, чтобы влезть в доступную ширину контейнера. Если они уже максимально сжались, а ширины контейнера им все равно не хватило, они вылезут за его границы.
      * Максимальное сжатие элемента - это так, чтобы элемент был минимального размера, но контент не вылез за его границы. Например, для текстового контента это ширина самого длинного слова.
  * С переносом
    * Когда элементы не помещаются в доступную ширину \ высоту контейнера, они переносятся в новую строку \ столбец.
    * В горизонтальный контейнер будут добавляться строки.
      * Столбцов в горизонтальном контейнере нет.
    * В вертикальный контейнер будут добавляться столбцы.
      * Строк в вертикальном контейнере нет.
* Направление оси + перенос элементов.
  * `flex-flow: row wrap`, т.е. формат `ось + алгоритм-переноса`
* Расстояние между элементами
  * `row-gap: 60px`
  * `column-gap: 40px`
  * `gap: 60px 40px`, формат `row + column`
  * Оба свойства работают и для горизонтальных, и для вертикальных контейнеров. Хотя в горизонтальных контейнерах нет столбцов, а в вертикальных - нет строк, в данном случае можно ориентироваться чисто визуально.

## Выравнивание.

* Выравнивание элементов.
  * После переносов в строках \ столбцах остается свободное место, что позволяет нам выравнивать элементы разными способами.
    * По основной оси.
      * `justify-content: flex-start* | flex-end | center | space-between | space-evenly | space-around`
    * По поперечной оси.
      * `align-items: flex-start | flex-end | center | stretch* | baseline`
* Можно выравнивать сами строки и столбцы в пространстве контейнера, а не только элементы внутри них.
  * `align-content: normal* | flex-start | flex-end | center | space-between | space-evenly | space-around`

## Флекс-элемент

* Растягивание и сужение.
  * Растягивание - определяет, будет ли элемент пытаться растянуться на все свободное место в контейнере по основной оси.
    * `flex-grow: 0* `
    * 0 - элемент не пытается растянуться и занимает нужный его содержимому размер.
    * 1 и больше - коэффициенты. Если у нескольких элементов одинаковый коэффициент, то они растягиваются пропорционально. Если у кого-то коэффициент больше, он получит больше места.
  * Сужение - определяет, будет ли элемент пытаться сузиться, если в контейнере не хватает места по основной оси.
    * `flex-shrink: 1*`
    * 0 - элемент не пытается сузиться и занимает полный необходимый его содержимому размер.
    * 1 и больше - коэффициенты. Если у нескольких элементов одинаковый коэффициент, то они сужаются пропорционально. Если у кого-то коэффициент больше, он сузится сильнее.
    * Элементы не сужаются больше, чем их минимальный размер. Например, в случае текстового содержимого, минимальный размер - это длина самого длинного слова.
* Базовый размер.
  * Это "стартовый" размер для элемента перед тем, как контейнер начнет распределять место между элементами.
  * `flex-basis: auto* | 0 | 100px`
  * auto - элемент "стартует" со своим естественным размером.
  * 0 - элемент "стартует" с минимального размера. Например, в случае текстового содержимого, минимальный размер - это длина самого длинного слова.
    * Это не то же самое, что установка width: 0
  * 100px - когда указано конкретное значение, то элемент "стартует" с него. Причем оно перекрывает width \ height элемента, даже если они заданы явно.
* Растягивание + сужение + базовый размер.
  * `flex: 0 1 auto` - дефолтный паттерн, формат grow + shrink + basis.
  * Стандартные паттерны на почти все случаи жизни.
    * `initial` : 0 1 auto - элемент не растягивается, а при нехватке места сужается.
    * `auto` : 1 1 auto - если свободное место есть, элемент растягивается. Когда места не хватает - сужается.
    * `none` : 0 0 auto - элемент не растягивается и не сужается независимо от наличия или отсутствия свободного места.
    * `1` - или другое число. Все равно что "это число" 1 0.
      * В этом случае место распределится равномерно между всеми элементами, у которых коэффициеты одинаковые, либо пропорционально, если разные.
        * Базис 0 сделает их равными с точки зрения "стартового" размера, ну а коэффициент растягивания забалансит пропорционально.
* Порядок.
  * Позволяет явно манипулировать положением элемента относительно других. Злоупотреблять не стоит.
  * `order: 0*`
  * Числа по сути тоже коэффициенты.
    * Паттерны
      * `-1` - поместить в начало.
      * `1` - поместить в конец.

# Grid

## Структура грида

* Грид-контейнер это элемент со свойством `display: grid | inline-grid`
* Состав грида.
  * Элемент и ячейка.
    * Элемент - это элемент, вложенный непосредственно в грид контейнер.
    * Ячейка - это слот под элемент.
  * Линия.
    * Это линии, которые делят контейнер на ячейки.
    * У линий есть номера.
    * Линиям можно задавать имена.
    * С помощью линий можно сделать так, чтобы элемент, лежащий в гриде, занимал сразу несколько ячеек.
  * Полоса.
    * Это пространство между двумя соседними линиями, горизонтальными или вертикальными.
  * Область.
    * Это прямоугольная область, охватывающая несколько ячеек.

## Строки и столбцы

* Направление добавления элементов.
  * Элементы могут добавляться в строку, а могут в столбец.
  * `grid-auto-flow: row* | column`
  
* Количество строк и столбцов.
  * Явно задать нельзя, из-за того что грид это все-таки динамический контейнер и его размер зависит от количества добавленных элементов.
  * На это количество можно влиять с помощью шаблонов строк и столбцов.
  
* Шаблон строк и шаблон столбцов.
  * Шаблон строк.
    * `grid-template-rows: знач1 знач2 ... значN`
    * Задает *высоту* строк.
    * В случае заполнения по столбцу определяет количество строк.
  * Шаблон столбцов.
    * `grid-template-columns: знач1 знач2 ... значN`
    * Задает *ширину* столбцов.
    * В случае заполнения по строке определяет количество столбцов.
  * Шаблон строк и шаблон столбцов не взаимоисключающие.
    * Их можно использовать одновременно.
      * Но шаблон для "авто-оси" повлияет на размер только перечисленного количества строк \ столбцов. Остальные получат значения по умолчанию.
    * Одновременное использование обоих шаблонов больше актуально, когда грид используется для построения сетки страницы и по макету заранее известно, сколько будет строк и столбцов.
  * "Динамические" строки \ столбцы.
    * Это строки \ столбцы, которые образуются когда при добавлении элементов строка \ столбец переполняются и добавляется новая строка \ столбец.
    * Шаблоны `grid-auto-rows` и `grid-auto-columns`
      * Задают размер для динамических строк \ столбцов.
      * Если задано одно значение, оно используется для всех авто- строк \ столбцов.
      * Если задано несколько значений, например, два, то они используются "циклически".
      * Если эти шаблоны используются совместо с обычным шаблоном, тогда для первых строк \ столбцов размер возьмется из обычного шаблона, а для остальных - из "авто"-шаблона.
  * Размер для шаблонов (и для обычных, и для авто-шаблонов).
    * `auto` - ширина \ высота элемента подберется так, чтобы заполнить все свободное пространство по соответствующей оси.
      * Если у нескольких строк \ столбцов стоит auto, то свободное пространство делится пропорционально их исходным размерам.
    * `fr` - например, 1fr. Это коэффициенты распределения. Если у двух элементов одинаковый коэффициент, то свободное пространство делится так, что оба элемента становятся одинакового размера.
    * Положительное число - например, 100px, 5rem. Элементы будут ровно указанного размера. Если контент не поместится, то он вылезет за границы элемента.
    * Функция `minmax(a, b)`.
      * Строка \ столбец будут такого размера, какой им нужен, но не меньше `a` и не больше `b`.
      * Значения для a, b
        * 100px - конкретное число в пикселях или других единицах.
        * 20% - процент берется от размера соответствующей оси контейнера.
        * 2fr - размер в fr пока разрешается только для max-значения. Например, `grid-template-column: minmax(200px, 1fr) 1fr 1fr` означает, что если места по горизонтали много, то все три колонки будут одинаковой ширины. Если места не хватает, то 2 и 3 колонки будут одинаковые, а первая - поменьше, но не меньше 200px.
        * max-content - ключевое слово, означает такой размер, при котором контент элемента помещается полностью без переноса.
        * min-content - ключевое слово, означает размер, при котором элемент занимает минимум места с учетом переноса своего контента. Например, ширина по самому длинному слову в элементе.
        * auto
          * Если используется как max-значение, то это размер элемента без переноса (как max-content)
          * Если используется как min-значение, то берется значение min-width \ min-height.
    * Функция `repeat()`
      * Позволяет определить несколько столбцов \ строк с одинаковыми параметрами.
        * Формат `grid-template-columns: repeat(3, 150px)` количество + размер.
      * Может использоваться вперемешку с явными объявлениями `grid-template-columns: repeat(3, 150px) 100px auto`
      * Первый параметр, количество
        * Может быть задано не конкретным числом, а ключевыми словами `auto-fill` или `auto-fit`.
          * Для столбцов и строк работают по одинаковому принципу.
          * Начало работы алгоритма одинаковое:
            * Браузер на основе размера из второго параметра функции repeat создает "виртуальные" ячейки в строке \ столбце. Создает их столько, сколько может влезть в соответствующую ось контейнера исходя из размера контейнера по этой оси.
            * Начинает заполнять виртуальные ячейки реальными элементами.
            * Если ячеек не хватило и произошло переполнение, то создается новая строка \ столбец и остальные элементы продолжают добавляться В этом случае auto-fill и auto-fit идентичны.
            * Если ячеек хватило и в контейнере осталось свободное место
              * `auto-fill` - пустые виртуальные ячейки продолжают занимать место.
              * `auto-fit` - пустые виртуальные ячейки удаляются и освободившееся место делится поровну между реальными элементами.
      * Второй параметр, размер.
        * Если первый параметр использует auto-fill или auto-fit, то есть ограничения на задание размера.
        * Размер - число или процент.
          * Ничего не обычного, сколько укажем, столько и будет.
          * minmax(a, b)
            * `min: число | проценты`, `max: число | проценты | fr | min-content | max-content | auto`
            * `min: число | проценты | fr | min-content | max-content | auto`, `max: число | проценты`
            * Оба варианта являются инверсией друг друга.
            * Хотя бы одно из значений в minmax дб числом или процентом.
              * На основе этого числа грид создаст виртуальные элементы.
              * Потом работает алгоритм auto-fill или auto-fit.
              * В конце получившийся размер проверяется на второе значение, чтобы не был больше \ меньше его. Если ок, тогда размер остается какой получился. Если не ок, тогда ставится максимальный \ минимальный.
  
* Расстояние между строками \ столбцами.

  ```css
  row-gap: 20px
  column-gap: 40px;
  gap: 20px 40px;  /* row + column */
  gap: 40px;  /* row + column, одно значение и для того, и для другого */
  ```

* Порядок элементов.

  * `order: -1;`
  * `-1` элемент встает в самое начало.
  * `1` элемент встает в конец.


## Выравнивание

* В гридах, в отличие от флексов, всегда одновременно есть и строки, и столбцы.
  * Причем на их природу не влияет направление заполнения грида. Т.е. строка всегда остается строкой, а столбец - столбцом.
* Выравнивание столбцов по горизонтали.
  * `justify-content: stretch* | start | end | center | space-between | space-evenly | space-around`
* Выравнивание строк по вертикали.
  * `align-content: stretch* | start | end | center | space-between | space-evenly | space-around`
* Выравнивание элементов по горизонтали (внутри колонок).
  * `justify-items: stretch* | start | end | center`
* Выравнивание элементов по вертикали (внутри строк):
  * `align-items: stretch* | start | end | center | baseline`
* `place-items` - комбинация `align-items` + `justify-content`

## Шаблоны областей

* Позволяет задать структуру грида, используя шаблон с именами будущих областей.

  * ```css
    grid-template-areas:
        "ga-menu ga-catalog ga-ad"
        "ga-menu ga-catalog ga-ad"
        "ga-menu ga-catalog ."
        "ga-menu ga-order   ga-order";
    ```

  * Область - это прямоугольная область, охватывающая несколько ячеек.

  * Элементу, добавленному в грид, через css-свойство `grid-area` указывается, к какой области он относится.

    * ```css
      .menu {
        grid-area: ga-menu;
      }
      ```

* Обычно указывается шаблон для столбцов.

  * Обычно в нем задается фиксированное количество столбцов.
  * Количество строк обычно определяется уже в шаблоне областей.
  * В каждой строке дб полный набор колонок.
    * Если в строке какая-то колонка не будет использоваться, на ее место ставится `.` вместо имени области.

## Растягивание элемента на несколько ячеек

* Можно сделать так, чтобы элемент занимал несколько ячеек.
  * Через явное указание линий.
    * `grid-column-start: число | имя-линии`, `grid-column-end: число | имя-линии`
    * `grid-row-start: число | имя-линии`, `grid-row-end: число | имя-линии`
    * Альтернативный синтаксис через `/`
      * `grid-column: 2 / 4` - "от" / "до", от 2 вертикальной линии до 4
      * `grid-row: 1 / 3` - "от" / "до", от 1 горизонтальной линии до 3
  * Автоматическое определение линий.
    * В этом случае мы просто указываем через `span`, на сколько колонок \ строк надо растянуть элемент, а грид сам решает, между каких линий его расположить.
      * `grid-column: span 3`
      * `grid-row: span 2`
  * Отрицательные номера линий.
    * Отрицательные номера линий означают нумерацию с конца.
    * Это позволяет легко тянуть элемент на всю ширину.
      * `grid-column: 1 / -1` - элемент растянется на все доступные столбцы.
* Имена для линий.
  * Задаются при определении шаблона.
    * `grid-template-columns: [foo] auto [bar] auto [zxc]`
    * `grid-template-rows: [foo] auto [bar] auto [zxc]`
    * Имена даются линиям, а не колонкам. Поэтому в примерах выше по три имени, хотя колонок и строк две.

## Grid и переполнение overflow

* Трюк с версткой как на ютубе.
  * Высоту грида сделать во весь экран.
  * Областям, где планируется сделать независимую прокрутку, сделать `overflow-y: auto`

