# Документация

| Источник    | Ссылка                                                       |
| ----------- | ------------------------------------------------------------ |
| MDN         | https://developer.mozilla.org/en-US/docs/Web/API/IndexedDB_API |
| Илья Кантор | https://learn.javascript.ru/indexeddb                        |

# Что такое IndexedDB

IndexedDB - это:

* Объектно-ориентированная БД (Object-oriented database). Это значит, что данные хранятся не в виде таблиц с фиксированным количеством колонок, как в реляционных СУБД, а в виде объектов.
* У хранимого объекта должен быть ключ, который его уникально идентифицирует.
* idb работает на основе транзакций.
* Поддерживает индексы.
* Предназначена для хранения больших объемов данных (включая файлы и BLOB).

# Асинхронность операций с БД

Все операции с IndexedDb - асинхронные и работа с ними опирается на события. Например, команда "открыть БД" возвращает не саму БД, а объект "запроса на открытие". У этого запроса есть события, например "успешно", "неуспешно", и другие. Мы на эти события должны повесить обработчик и когда запрос выполнится, то он вызовет наш обработчик и передаст ему объект события, из которого мы сможем достать, например, уже объект самой БД.

Этот принцип применим абсолютно ко всем операциям над БД. Поэтому практически любую работу с idb (например, открытие \ создание) удобно оформлять через промисы за счет того, что внутри обработчиков событий мы можем вызывать resolve \ reject, а потом авейтить промис и т.о. получать ощущение синхронной работы. Поскольку все методы idb асинхронные, то без промисов нам бы пришлось вкладывать колбеки друг в друга, а с промисами у нас ощущение линейного кода.