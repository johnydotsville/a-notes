# Чекбосы, модификация вида

* Typora > Preferences > Appearence > Themes > Open theme folder

* ```css
  .md-task-list-item > input:before          # Выключенный
  .md-task-list-item > input:checked:before  # Включенный
  ```

* ```css
  .md-task-list-item > input:before {
      content: "";
      display: inline-block;
      vertical-align: middle;
      text-align: center;
      background-color: #363B40;
      /* custom: закругление, размер, положение */
      border: 1.25px solid #b8bfc6;
      margin-top: -0.3rem;
      width: 1rem;
      height: 1rem;
      border-radius: 50%;
  }
  ```

  ```css
  .md-task-list-item > input:checked:before,
  .md-task-list-item > input[checked]:before {
      content: '\221A';
      /*◘*/
      font-size: 0.625rem;
      line-height: 0.625rem;
      color: #DEDEDE;
      background-color: #3abe25;  /* Зеленый фон */
  }
  ```

* Для проверки надо переоткрыть файл (на маке не обязательно закрывать программу, достаточно закрыть файл).

- [ ] Проверка: выключенный
- [x] Провека: включенный

