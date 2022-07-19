Парсер с интерактивной оболочкой



# Запуск

Для запуска оболочки набираем команду *scrapy shell* с указанием сайта:

```
scrapy shell "https://quotes.toscrape.com/page/1/"
```

Скрапи разберет страницу, построит дерево и мы сможем обращаться к нему, используя синтаксис xpath. Для обращения используется объект `response` и его метод `.xpath()`

# Важное

Когда парсинг не идет, обычно в консоли выводится причина

## Сайт не отдает данные

Скрапи ставит себя в user agent, поэтому многие сайты отдают 403 Forbidden. Нужно заменить user agent на нормальный браузер - при запуске shell'а указываем его:

```
scrapy shell -s USER_AGENT="Mozilla/5.0 (X11; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.0" "https://www.regard.ru/catalog/1001"
```

В питоновском коде это выглядит так:

```python
headers = {'User-Agent': 'Mozilla/5.0 (X11; Linux x86_64; rv:48.0) Gecko/20100101 Firefox/48.0'}
scrapy.Request(url=url, headers=headers, callback=self.parse4)
```

## Forbidden by robots.txt

Ситуация: сайт не отдавал данные при указании пагинации. Ошибка "Forbidden by robots.txt". В файле settings.py (настройки шаблонной программы) меняем настройку на ROBOTSTXT_OBEY = False

# Примеры

## Общее

* `//` означает "искать в любом месте". Т.е. //a - найти все теги a внутри страницы.
* `/` означает точный путь, например, `//div/a` уже означало бы "искать везде в документе все div, в которые непосредственно вложен a". Т.е. div span a он не найдет, потому что а вложен не в сам div, а в span.

## Примеры

* На странице есть несколько таблиц, но список сотрудников лежит в той, у которой class равен users. Внутри этой таблицы есть строки, столбцы, и где-то там ссылка с именем сотрудника, причем ссылка выглядит вот так `<a href="/ru/users/113.htm">Testing Account</a>`. Выберем имена сотрудников, т.е. фактически это текст внутри таких ссылок:

```
response.xpath('//table[@class="users"]//a[contains(@href, "users")]/text()').getall()
```



# Черновик

```
cd C:\mine\progs\mine\python\parser
```

```
venv\Scripts\activate
```

```
cd C:\mine\progs\mine\python\parser\tutorial>
```

```
scrapy crawl employee
```

