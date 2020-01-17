#Spring Cloud Stream

## RabbitMQ 

### Docker 
Проверяем статус текущих контейнеров 
```
 docker ps -a
```
если еще нету контейнера - создаем 

 ```
 docker run -d --hostname my-test-rabbit --name test-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management 

```
если контейнер есть, но остановлен 
```
docker start test-rabbit
``` 
проверяем 
http://localhost:15672/

guest/guest 

----------------------------------------------

запускаем producer 
```
mvn -f spring-cloud-producer/ clean package

java -jar spring-cloud-producer/target/producer.jar

```

запускаем consumer (3шт)
``` 
mvn -f spring-cloud-stream-consumer clean package

java -jar spring-cloud-stream-consumer/target/appl.jar
java -jar spring-cloud-stream-consumer/target/appl.jar
java -jar spring-cloud-stream-consumer/target/appl.jar
```

проверяем:

 [queues](http://localhost:15672/#/queues)
проверить что создана точка обмена (Exchange). Она названа «legalbriefs» 

[channels](http://localhost:15672/#/channels)
 Должно быть три очереди, которые отображаются в каждом из трех экземпляров приложений, которые мы запускали.


------------

## проверяем отправку 
``` 
curl -X POST \
  http://localhost:8020/brief \
  -H 'Content-Type: application/json' \
  -d '{
  "casenumber": "test",
  "attorney": "123456"
}'
```
каждый абонент получает копию сообщения, потому что по умолчанию все происходит в простом режиме публикация/подписка

## Конфигурация групп подписчиков

В приложении-подписчике мы добавляем одну строку в наш файл конфигурации
``` json
#adds consumer group processing
spring.cloud.stream.bindings.input.group = briefProcessingGroup
```
После перегенерации jar файла подписчика и запуска каждого файла мы видим другую настройку в RabbitMQ. То, что вы видите, — это одна именованная очередь, но три «потребителя» очереди. (Вкладка очередь)

Отправьте два разных сообщения и убедитесь, что каждое обрабатывается только одним экземпляром подписчика. Это простой способ использовать брокер сообщений для масштабирования обработки.

##Выполнение stateful обработки с использованием разбиения на разделы
. Разделы (Partitions) в Kafka вводят уровень параллельной обработки путем записи данных в разные разделы. Затем каждый подписчик тянет сообщение из заданного раздела (partition), чтобы выполнить работу.

#### publisher 
а стороне издателя (producer) все, что необходимо указать: (a) количество разделов и (b) выражение, описывающее разделение данных. Никаких изменений кода.
```json
#adding configuration for partition processing
spring.cloud.stream.bindings.output.producer.partitionKeyExpression=payload.attorney
spring.cloud.stream.bindings.output.producer.partitionCount=3
```

#### consumer
 вы устанавливаете количество разделов и устанавливаете свойство «partitioned» равным «true». Что также интересно, но логично, так это то, что по мере запуска каждого абонента вам нужно дать ему «индекс», чтобы Spring Cloud Streams знал, из какого раздела он должен читать сообщения.
 ```json 
 #add partition processing
 spring.cloud.stream.bindings.input.consumer.partitioned=true
 #spring.cloud.stream.instanceIndex=0
 spring.cloud.stream.instanceCount=3
```

A consumer group is required for a partitioned subscription

можно задать индек каждому инстансу в *.properties
```json spring.cloud.stream.instanceIndex=0 ```

либо указать при запуске для каждого
```
java -jar spring-cloud-stream-consumer/target/appl.jar --spring.cloud.stream.instanceIndex=0
``` 
```
java -jar spring-cloud-stream-consumer/target/appl.jar --spring.cloud.stream.instanceIndex=1
```
```
java -jar spring-cloud-stream-consumer/target/appl.jar --spring.cloud.stream.instanceIndex=2
```
теперь есть три очереди, каждая с другим «ключом маршрутизации» («routing key»), который соответствует его разделу.

теперь все сообщения с одним и тем же именем индекса переходят к одному экземпляру.

![Alt text](routing_key?raw=true "Title")

### тест для разделов 



``` 
curl -X POST \
  http://localhost:8020/brief \
  -H 'Content-Type: application/json' \
  -d '{
  "casenumber": "test",
  "attorney": "123456"
}'
```

legalbriefs-1

{
  "casenumber": "legalbriefs-1",
  "attorney": "1"
}

legalbriefs-2

{
  "casenumber": "some text ",
  "attorney": "2"
}

legalbriefs-3

{
  "casenumber": "some text ",
  "attorney": "3"
}