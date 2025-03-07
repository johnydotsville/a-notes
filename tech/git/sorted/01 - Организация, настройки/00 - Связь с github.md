# В Ubuntu

Чтобы получить возможность пушить в свои репозитории, нужно настроить авторизовать свой комп на гитхабе. Для линукса делается так:

* Устанавливаем github cli.
  * Переходим [сюда](https://github.com/cli/cli/blob/trunk/docs/install_linux.md).
  * Ищем раздел *Manual installation*.
  * Там переходим по ссылке для скачивания установочника.
  * Ищем подходящий установочник. Для Ubuntu это "GitHub CLI 2.45.0 linux amd64 deb".
  * Устанавливаем.
* В убунтовском терминале вводим `gh auth login`.
  * Нужно ответить на несколько вопросов.
    * What account do you want to log into? GitHub.com
    * What is your preferred protocol for Git operations on this host? HTTPS
    * Authenticate Git with your GitHub credentials? Yes
    * How would you like to authenticate GitHub CLI? Login with a web browser
  * Далее в терминал выведется код из восьми цифр. Его нужно ввести в браузер, когда он откроется и запустит страницу авторизации на гитхабе.
  * Для открытия браузера жмем Enter.
  * Вводим в браузере код.
  * Готово, можно пользоваться.