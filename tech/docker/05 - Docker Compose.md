# Docker Compose

Docker Compose - это инструмент, который позволяет разом запустить несколько контейнеров, которые будут работать как единое целое. Например, если приложению для работы одновременно нужен ngnix и mysql, то вместо того, чтобы поднимать их по-отдельности, можно с помощью docker compose запустить разом. Или кафка, которая работает вместе с зукипером.

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

Для запуска этих сервисом пользуемся командой

```
docker-compose -f "e:\tmp\docker-compose.yaml" up -d
```

