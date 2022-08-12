# Вопросы

- [ ] Что такое Zookeeper?
- [ ] Как создать темы в кафке, запущенной в докере?
- [ ] Как в джаве записать\прочитать что-то в\из темы?
- [ ] Message, record, topic, partition, offset, controller, broker, cluster



# Терминология

* **Сообщения\записи (message\record)** - это одно и то же, это данные, которые продюсер отправляет в топик
* т.е. **Топик** - это что-то вроде набора сообщений, которые логически относятся к одной "теме"

# Кафка и зукипер

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

Для запуска этих сервисом пользуемся командой (дб запущен Docker Desktop)

```
docker-compose -f "e:\tmp\docker-compose.yaml" up -d
```



# Offset Explorer

Визуальный инструмент для просмотра брокеров, потребителей и производителей кафки https://kafkatool.com/download.html

Мануал по настройке чего-то простого https://www.baeldung.com/ops/kafka-docker-setup





Не указываем номер версии для спринговой кафки в зависимостях





docker-compose -f "e:\tmp\docker-compose.yaml" up -d