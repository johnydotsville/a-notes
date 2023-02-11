# Docker

## docker-compose.yaml

Сохраняем куда-нибудь конфигурацию:

```json
version: '2'

services:

  zookeeper:
    image: confluentinc/cp-zookeeper:latest
    container_name: my-zookeeper
    environment:
      ZOOKEEPER_CLIENT_PORT: 2181
      ZOOKEEPER_TICK_TIME: 2000
    ports:
      - 22181:2181

  kafka:
    image: confluentinc/cp-kafka:latest
    container_name: my-kafka
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

Запускаем ее:

```
docker-compose -f "путь\до\конфигурации" up
```

Можно добавить ключ `-d`, чтобы освободить консоль.

# Kafka Tool

В программе Offset Explorer добавляем новый кластер и вводим данные, которые указывали в настройках докера:

* На вкладке Properties:
  * cluster name - любой
  * Zookeeper Host - localhost
  * Zookeeper Port - 22181, как в конфиге проброшенный порт
* На вкладке Advanced:
  * Bootstrap Servers - localhost:29092 (TODO: разобраться что к чему тут)

Затем тут же создаем новый топик.

# Spring

## Зависимости

```xml
<dependency>
    <groupId>org.springframework.kafka</groupId>
    <artifactId>spring-kafka</artifactId>
</dependency>
```

Важно не указывать версию, иначе могут быть проблемы с совместимостью. Без указания возьмется гарантированно рабочая из подборки самого спринга.

## Настройки

Добавим в src/main/resources файл application.properties и укажем в нем адрес сервера кафки (тот, который настраивали в докере):

```json
spring.kafka.bootstrap-servers=localhost:29092
```

## Конфигурируем продюсера и консюмера

Конфигурация продюсера:

```java
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")  // Адрес сервера кафки берем из настроек
    private String bootstrapAddress;

    @Bean
    public ProducerFactory<String, String> producerFactory() {
        Map<String, Object> configProps = new HashMap<>();

        configProps.put(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        configProps.put(
                ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);
        configProps.put(
                ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                StringSerializer.class);

        return new DefaultKafkaProducerFactory<>(configProps);
    }

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }
}
```

Конфигурация консюмера:

```java
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")  // Адрес сервера кафки берем из настроек
    private String bootstrapAddress;

    @Bean
    public ConsumerFactory<String, String> consumerFactory() {
        Map<String, Object> props = new HashMap<>();

        props.put(

                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG,
                bootstrapAddress);
        props.put(
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);
        props.put(
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG,
                StringDeserializer.class);

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, String> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());

        return factory;
    }
}
```

## Пишем продюсера и консюмера

### Продюсер

```java
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    private KafkaTemplate<String, String> kafka;

    @Autowired
    public Producer(KafkaTemplate<String, String> kafkaTemplate) {
        this.kafka = kafkaTemplate;
    }

    public void send(String topic, String message) {
        kafka.send(topic, message);
    }
}
```

Здесь продюсер по сути - простая обертка над объектом KafkaTemplate.

### Консюмер

```java
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class Consumer {
    @KafkaListener(topics = "mytopic", id = "listener1")
    public void read(String message) {
        System.out.println("Событие прочитано: " + message);
    }
}
```

Здесь может смутить наличие параметра message у метода чтения. Но консюмер устроен таким образом, что *автоматически* читает сообщения по мере их поступления. Т.е. метод read нам не придется нигде явно вызывать и этот параметр, соответственно, инфраструктурный, мы с ним имеем дело исключительно внутри этого метода.

## Проверяем как работает

```java
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        ApplicationContext context = SpringApplication.run(App.class, args);

        Producer helper = (Producer) context.getBean("producer");
        String topic = "mytopic";
        String message = "Hello, kafka!";
        helper.send(topic, message);
    }
}
```

Если все успешно, то в Offset Explorer в топике появится событие, а в консоли idea мы увидим отправленное событие с сообщением "Hello, kafka!". Оно прочитается автоматически, т.е. не нужно явно вызывать никаких методов чтения.