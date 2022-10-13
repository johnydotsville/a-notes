# Docker Compose

Docker Compose - это инструмент, который позволяет разом запустить несколько контейнеров, в случаях когда для работы одного нужен другой. Например, если приложению для работы одновременно нужен ngnix и mysql, то вместо того, чтобы поднимать их по-отдельности, можно с помощью docker compose запустить разом и в нужном порядке. Или кафка, которая работает вместе с зукипером.

DC в линуксе нужно устанавливать отдельно, а в винде он ставится автоматически вместе с docker engine при установке gui клиента докера.

Проверить установку и узнать версию можно командой

```
docker-compose -version
```

Настройка dc производится в файле docker-compose.yaml, в котором указываются сервисы, конфиги, сети, секреты, тома. Сервисы - это собственно говоря и есть элементы, которые должны работать вместе. Про остальное пока не знаю.

Пример docker-compose.yaml для запуска кафки и зукипера:

```
version: '2'
services:
  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 22181:2181
  
  kafka:
    image: confluentinc/cp-kafka:latest
    depends_on:
      - zookeeper
    ports:
      - 29092:29092
    environment:
      KAFKA_BROKER_ID: 1
      KAFKA_ZOOKEEPER_CONNECT: zookeeper:2181
      KAFKA_ADVERTISED_LISTENERS: PLAINTEXT://kafka:9092,PLAINTEXT_HOST://localhost:29092
      KAFKA_LISTENER_SECURITY_PROTOCOL_MAP: PLAINTEXT:PLAINTEXT,PLAINTEXT_HOST:PLAINTEXT
      KAFKA_INTER_BROKER_LISTENER_NAME: PLAINTEXT
      KAFKA_OFFSETS_TOPIC_REPLICATION_FACTOR: 1
```

Для запуска этих сервисов пользуемся командой

```
docker-compose -f "e:\tmp\docker-compose.yaml" up -d
```

# Свои программы в dc

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
* Каждый сервис описываем отдельно:
  * Указываем образ, из которого будет развернут контейнер и имя будущего контейнера
  * Переменные окружения, порты
  * Порядок запуска сервисов через раздел depends_on
  * Тома через volumes (подробнее - в мануале про тома)

## Запуск

```
docker-compose -f "e:\tmp\docker-compose.yaml" up -d
```

* -d - detached mode, может быть вреден, т.к. в консоль не будет выводиться информация о процессе запуска

## Остановка

Запущенные контейнеры можно разом остановить из GUI. Чтобы сделать это из терминала, нужно перейти в директорию с файлом docker-compose.yml, в котором описано то, что мы запускали, и из этой директории выполнить

```
docker-compose stop
```



## Порядок запуска сервисов

Через depends_on мы можем указать докеру, в какой последовательности он должен запускать сервисы. Однако докер не способен понять, нормально ли поднялся сервис с точки зрения бизнес-логики, например, произошло ли соединение с БД и все в этом роде. Он ориентируется на свои технические факты - запустилось, значит все нормально, запускаю следующий.

Поэтому нужно писать приложения таким образом, чтобы они в случае чего сами предпринимали попытки переподключиться к БД или опросить другой сервис на предмет корректной работы.