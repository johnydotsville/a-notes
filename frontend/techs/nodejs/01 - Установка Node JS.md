# Установка Node JS

* Идем [сюда](https://nodejs.org/en/download), качаем LTS версию для Windows.

* Запускаем установщик, все опции по умолчанию.

  * Главное не ставить галочку "Установить дополнительные инструменты", потому что там ставится питон, какая-то шняга для Visual Studio, обновления винды и велика вероятность, что все сломается.

* Проверим, что Node установился нормально. Для этого открываем консоль от администратора и набираем `node -v`. Если вывелась версия, значит все в порядке.

* Можно даже создать где-нибудь файл `script.js`

  ```javascript
  let hello = "Hello, Node js!";
  console.log(hello);
  ```

  Перейти в консоли к этому файлу, скомпилировать и выполнить его командой `node script.js`.