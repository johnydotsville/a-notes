# Связи

Гипервизор, аппаратная виртуализация, докер образ, докер котейнер

# Разбираемся

Концептуально, есть идея о параллельном запуске нескольких операционных систем на одном компьютере одновременно. То есть получается что каждый пользователь работает в собственном экземпляре ОС. Эти экземпляры полностью изолированы.

На другом конце - концепция разделения времени, т.е. когда несколько пользователей одновременно работают в одной ОС.

Виртуализация - это полная имитация компьютера, т.е. физических ресурсов, вроде ядер процессора, оперативной памяти и т.д.

Контейнеризация - это "виртуализация" только программного уровня, т.е. операционной системы и установленный в ней программ\настроек. 

Т.о. виртуальная машина - это полноценная имитация компьютера с установленной операционной системой. Работает ВМ под управлением гипервизора. Например, VirtualBox, VMWare - это программы-гипервизоры.



# Докер

Образ - это неизменяемая сущность в виде гномика, из которой "разворачивается" сущность, которую уже можно изменить - контейнер. Образ состоит из нескольких read-only слоев, в которых авторы образа, грубо говоря, подготовили "базу" системы. Когда образ разворачивается, превращаясь в контейнер, то в него добавляется еще один слой, который можно изменять. Через этот слой производится "доводка" базовой системы для своих нужд. Контейнер можно "закоммитить" и он превратится в образ с еще одним read-only слоем. И так можно повторять много раз.

Контейнер можно запускать повторно и между запусками он сохраняет свое состояние. Например, можно добавить в БД данные, остановить контейнер, запустить снова, и данные с прошлой работы там будут.



# Проблемы

В виртуальных машинах докер работать не должен. Ситуативно может, но не обязан.

После установки Docker Desktop на Win 10 была ошибка:

> docker failed to initialize

Как исправил:

* В EFI должна быть включена виртуализация. У меня это была опция Intel Virtualization Technology
* В Windows Features должны быть включены пункты:
  * Платформа виртуальной машины (Virtual Machine Platform)
  * Подсистема Windows для Linux (Windows Subsystem for Linux)
  * Hyper-V

В Windows Features (Компоненты Windows) можно зайти из *"Панели управления > Включение или отключение компонентов Windows"*

WSL, Windows Subsystem for Linux, докер похоже ставит автоматически.

В *Диспетчер задач > Производительность* пункт *Виртуализация* должен показывать *Включено*.

После перезагрузки докер заработал. Не сразу. Сначала висел, пришлось его выключить и включить заново.

# Команды

```
docker images
```

Список всех скачанных\созданных образов

```
docker ps
```

Список работающих сейчас контейнеров.

* -a - показать список всех имеющихся контейнеров, а не только работающих.
* -q - выводить только id контейнеров

---

```
docker info
```

Какая-то информация. Много чего, но суть этой информации пока не понятна.

---

```
docker build -t myImageName <dir>
```

Собрать свой образ.

* -t - имя образа. При создании нескольких образов с одинаковым тэгом, старые образы получают имя none, а тэг указывает только на последний созданный образ.
* dir - путь до Dockerfile

Dockerfile - файл без расширения, именно с таким именем. Содержит инструкции, по которым докер будет создавать образ. Пример:

```java
FROM python:3.6  // Образ, на основе которого мы создаем свой образ
RUN mkdir -p /usr/src/app/  // Создать внутри образа директорию
WORKDIR /usr/src/app/  // Сделать эту директорию рабочей
COPY . /usr/src/app/  // Скопировать из текущей директории НАШЕГО компа все содержимое в директорию внутри ОБРАЗА
RUN pip install --no-cache-dir -r reqs.txt  // Установить зависимости программы, печисленные в файле reqs.txt
EXPOSE 8080  // Сообщаем о порте, который слушает приложение, работающее в контейнере
ENV TZ Europe/Moscow  // Устанавливаем переменную окружения (при запуске можно будет поменять, вроде)
CMD ["python", "hello-docker.py"]  // Выполнить команду "python hello-docker.py", запустив ее в shell
```

reqs.txt выглядит так:

```
flask==1.1.1
```

Дополнение: насчет COPY, директории запуска docker build и положения Dockerfile. В команде COPY отсчет ведется очевидно от директории, в которой лежит Dockerfile. Если положить программу не рядом с Dockerfile, а например в ./prog, тогда нужно будет указать COPY ./prog Если этого не сделать, то в /usr/src/app/ скопируется папка prog, и тогда надо было бы указать в CMD ["python", "**prog**/hello-docker.py"]. В общем, за этими путями надо следить.

---

````
docker run --name MyPostgres -e POSTGRES_PASSWORD=mysecretpassword -p 8090:8080 -d postgres
````

Создает и запускает из образа *postgres* контейнер по имени *MyPostgres*.

* -d - если есть, запускается в detached-режиме (background). Если нету, в attached (foreground). В attached-режиме консоль, в которой запущена команда run, "связывается" с процессом контейнера, это значит, что вся информация от него в виде логов будет выводиться в эту консоль. Еще наверное можно отдавать контейнеру команды через эту же консоль. В detached-режиме, соответственно, консоль "освобождается" после запуска и ее можно использовать для других целей.

* -e - флаг для установки переменных окружения. В контейнерах уже могут быть какие-то переменные окружения. Этим флагом можно их переназначить, или создать новые. В данном примере переменной POSTGRES_PASSWORD присвоится значение mysecretpassword.

  TZ=Europe/Moscow - пример установки временной зоны.

* -p - проброс портов, порт хоста: порт приложения в контейнере. Например, в контейнере работает веб-приложение, которое слушает порт 8080. Поскольку контейнер изолирован, хост ничего не знает об этом. Поэтому нужно явно при запуске соответствие портов. Можно замапить любой порт хоста на порт приложения.

* -v - монтирование папки хоста в контейнер *absolute/host/path/folder/:/usr/src/app/folder/*  В качестве основы для пути контейнера указываем папку, в которую копировали приложение при сборке образа. Можно ли указать какую-то другую, пока не знаю. Теперь можно менять на хосте, например, содержимое файла, из которого программа в контейнере что-то читает, и программа новые данные увидит.

  Вместо пути хоста можно указывать volume (как создать volume - ниже). *volumeName:/usr/src/app/resources*

* --name - имя, которое присвоится разворачиваемому контейнеру. Если запускать с разными именами, получаются разные контейнеры.

* --rm - после остановки контейнера (либо если он сам завершит работу) он автоматически удалится

---

```
docker start -a containterName
```

Запустить контейнер, который ранее уже запускался через docker run. Остановленный контейнер сохраняет свое состояние, так что можно например создать в БД таблицу, остановить контейнер, запустить его через start и таблица будет на месте.

* -a - attach, присоединиться к контейнеру, чтобы в консоль выводилась информация о его работе. То же самое, когда при run не используем -d

---

```java
docker volume ls  // Список томов
docker volume create MyVolume
```

volume'ы это способ сохранить данные после удаления контейнера. Допустим, в образе есть директория /usr/src/app/ как в примере выше. Если программа, работающая в контейнере, будет туда что-то писать, то при уничтожении контейнера данные уничтожатся вместе с ним, потому что они - часть контейнера. Поэтому, чтобы как-то их сохранить, используется либо монитрование папки контейнера к папке хоста, чтобы данные сохранялись в нее и оставались на хосте, либо создаются volume'ы. Volume - это самостоятельная сущность, с контейнером не связанная. Примонтировать ее к папке контейнера можно точно так же как и папку хоста.

---

```
docker rm 46286a4fff79
```

Удалить контейнер. Да, именно контейнер, а не образ. Цифра - id контейнера. Можно использовать имя вместо id. Главное не перепутать его с именем образа, а то не удалит.

```
docker rmi 23id228322
```

Удалить образ.

---

```
docker stop 46286a4fff79
```

Остановить контейнер

---





# Важные заметки

* При работе с веб приложениями важно помнить про проброс портов. При старте контейнера надо указать *портХоста:портПриложенияВКонтейнере* чтобы при обращении на хосте по этому порту сообщение доходило до приложения

# Вопросы

> Где докер хранит файлы образов?

Очень много ответов и мест, где лежит не понятно что. Одна из версий: *C:\Users\username\AppData\Local\Docker\wsl\data*. По размеру файла примерно похоже. Вероятно, все образы находятся внутри этого виртуального диска. Кажется еще есть два стиля хранения образом - lin и win.

> Как докер работает в плане адресов, например, при запуске контейнера в лог выводится "*listening on IPv4 address "0.0.0.0", port 5432*". Что это значит?





# Видео

https://www.youtube.com/watch?v=QF4ZF857m44

Докер - это средство упаковки, доставки и запуска приложений.

Контейнер работает до тех пор, пока работает приложение.