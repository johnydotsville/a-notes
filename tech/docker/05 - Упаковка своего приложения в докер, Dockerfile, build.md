# Упаковка приложения в докер

Запускаем мавен clean + package и в директории target появляется .jar файл, который мы и будем упаковывать.

## Подготовка Dockerfile

Чтобы докер понимал, как это делать, создаем в корне приложения (на одном уровне с .pom) файл Dockerfile (без расширения) с таким содержимым:

```
FROM openjdk:11

WORKDIR /app/
COPY ./target/webclient-demo.jar .
EXPOSE 49080
CMD java -jar webclient-demo.jar
```

* FROM задает образ-основу для нашего собственного образа
* WORKDIR задает "рабочую" директорию внутри образа, относительно которой будут ориентироваться команды COPY, CMD (и другие, вроде RUN, ADD, ENTRYPOINT). Если директории не существует, она создается автоматически
* COPY копирует *откуда* - *куда*. Откуда в данном случае это папка с программой на нашем компьютере, а . - это директория /app/ внутри будущего образа, которую мы задали через WORKDIR
* EXPOSE указывает, какой порт слушает работающее внутри контейнера приложение. Можно указывать EXPOSE несколько раз, если портов больше одного. Эта команда служит для информационных целей, чтобы  пользователь образа через команду `docker inspect SomeImageName` мог узнать о портах
* CMD задает команду, которая будет выполняться при запуске контейнера. Здесь мы указываем просто имя нашей программы, без полного пути, опять же за счет того, что задали WORKDIR. CMD фактически может быть только одна, потому что если указать их несколько, то выполнится только последняя

## build

Для сборки образа переходим в директорию с этим Dockerfile и набираем

```
docker build -t currency-generator .
```

Здесь через -t задается человекочитаемое имя образа, а `.` - это директория с файлом Dockerfile (в данном случае текущая). После этого в docker desktop появляется этот образ и его можно запустить как обычно.

# Запуск через docker-compose

Конфигурацию описываем в файле *docker-compose.yml*. Положить его можно куда угодно:

```yaml
version: "3.9"

services:
  
  service-a:
    image: service-a-img
    container_name: service-a-cont
    ports:
      - "49080:49080"
    networks:
      servicesubnet:
        ipv4_address: "172.16.238.10"

  service-b:
    image: service-b-img
    container_name: service-b-cont
    ports:
      - "49081:49081"
    depends_on:
      - db
      - service-a
    networks:
      servicesubnet:
        ipv4_address: "172.16.238.11"
        
  service-c:
    image: service-c-img
    container_name: service-c-cont
    ports:
      - "49082:49082"
    depends_on:
      - db
      - service-a
      - service-b
    networks:
      servicesubnet:
        ipv4_address: "172.16.238.12"

  db:
    image: postgres
    container_name: postgres-cont
    environment:
      TZ: "Europe/Moscow"
      PGTZ: "Europe/Moscow"
      POSTGRES_DB: "postgres"
      POSTGRES_USER: "postgres"
      POSTGRES_PASSWORD: "0451"
    ports:
      - "5432:5432"
    volumes:
      - pgs_data:/var/lib/postgresql/data
    networks:
      servicesubnet:
        ipv4_address: "172.16.238.13"

volumes:
  pgs_data:

networks:
  servicesubnet:
    driver: bridge
    ipam:
      driver: default
      config:
        - subnet: "172.16.238.0/24"
```

Критично важно соблюдать отступы. Если например перед именем какого-нибудь сервиса не подставить два пробела, тогда он выпадет из секции сервисов и будет ошибка при запуске композа. Что здесь написано:

* Секция services описывает сервисы, которые мы хотим запустить вместе

* Чтобы они видели друг друга, нужно запустить их в одной виртуальной сети. Ее мы описываем в секции networks и указываем явно каждому сервису IP-адрес в этой сети.

  Если сеть явно не писать и не объявлять, то docker-compose сам создаст сеть и разместит в ней все сервисы.

  Общее правило такое: если связь сервисов целиком осуществляется здесь же в yml-файле, то если одному сервису требуется ip другого, можно просто указать имя сервиса вместо ip и докер сам разберется. Но если (как в примере выше) мы запускаем три самописных сервиса, которые в своем коде общаются друг с другом, то для них имена "service-a-b-c" ничего не значат. И в этом случае как раз потребуется явно указать ip.

* Каждый сервис описываем отдельно:

  * Указываем образ, из которого будет развернут контейнер и имя будущего контейнера
  * Переменные окружения, порты
  * Порядок запуска сервисов через раздел depends_on
  * Тома через volumes (подробнее - в мануале про тома)

# Остальные возможности Dockerfile

```java
FROM python:3.6  // Образ, на основе которого мы создаем свой образ
RUN mkdir -p /usr/src/app/  // Создать внутри образа директорию
WORKDIR /usr/src/app/  // Сделать эту директорию рабочей
COPY . .  // Скопировать из текущей директории НАШЕГО компа все содержимое в директорию внутри ОБРАЗА, причем вторая . указывает на /usr/src/app/, т.к. она задана в WORKDIR
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

## Команды

* RUN выполняет указанную команду *на этапе билда* образа (в отличие от CMD, которая срабатывает уже при запуске контейнера)
* ENV устанавливает переменные окружения
* ENTRYPOINT пока не сталкивался