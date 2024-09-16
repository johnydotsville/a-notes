Некоторые вещи, которые я заметил в исходниках опенсорсных программ.

Github Desktop:

* Из одного файла экспортируется не обязательно только одна вещь. Например:

  * Экспортируется интерфейс, переменная этого интерфейса с положенным в нее объектом, и какая-то функция.
  * В файле могут быть не экспортированные вещи, для внутрифайлового пользования.

* Поля в интерфейсах очень любят ставить readonly. Как будто других пока и не встречал.

* Интерфейсам ставят префикс `I`, например `ICompareState`.

* Встречается довольно много файлов, в которых экспортируются функции, а не классы. Т.е. нет стремления всю функциональность запихнуть в классы. Возможно, если функция чувствует себя нормально сама по себе, то не надо делать ее частью класса.

* Публичные члены класса явно помечаются как public, например конструкторы.

* Статические методы имеют тенденцию писать ДО конструктора.

  * Модификатор доступа не влияет на позицию члена в классе, public и private могут идти вперемешку.

* Конструкторы иногда делают приватными, а для создания экземпляра делают статический метод.

  * Метод, кстати, возможно, имеет тенденцию называться from. Например, `fromMenu(menu: IMenu)`

* Текстовое описание классов, методов, наверное помогает в автодокументации. Как минимум, этот текст может вывести VS Code в подсказке. P.S. Каждую новую строку не обязательно делать со звездочкой. Важно только чтобы начиналось с `/**` и заканчивалось `*/`:

  * Классы

    * ```typescript
      /**
       * An immutable, transformable object which represents an application menu
       * and its current state (which menus are open, which items are selected).
       *
       * The primary use case for this is for rendering a custom application menu
       */
      export class AppMenu {
      ```

  * Методы

    * ```typescript
      /**
       * Static constructor for the initial creation of an AppMenu instance
       * from an IMenu instance.
       */
      public static fromMenu(menu: IMenu): AppMenu {
        const map = buildIdMap(menu)
      ```

    * ```typescript
      /**
       * Used by static constructors and transformers.
       *
       * @param menu  The menu that this instance operates on, taken from an
       *              electron Menu instance and converted into an IMenu model
       *              by menuFromElectronMenu.
       * @param openMenus A list of currently open menus with their selected items
       *                  in the application menu.
       *
       *                  The semantics around what constitutes an open menu and how
       *                  selection works is defined within this class class as well as
       *                  in the individual components transforming that state.
       * @param menuItemById A map between menu item ids and their corresponding MenuItem.
       */
      private constructor(
        private readonly menu: IMenu,
      ```

* Используются методы-строители, как обычные, так и статические.

* enum'ы не стесняются делать вот так

  ```typescript
  export enum BannerType {
    SuccessfulMerge = 'SuccessfulMerge',
    MergeConflictsFound = 'MergeConflictsFound',
    SuccessfulRebase = 'SuccessfulRebase',
  ```

* Когда класс называется из двух слов, например, GitAuthor, то файл называется `git-author.ts`, т.е. в два слова через тире.

  * А если это интерфейс, например, ILastThankYou, то `I` в название файла не идет, т.е. `last-thank-you.ts`

* any бывает используется.