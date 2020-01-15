# подписчик

### Reference Documentation
Мы в качестве примера попробуем RabbitMQ


 # RabbitMQ 
 ``` docker run -d --hostname my-test-rabbit --name test-rabbit -p 15672:15672 -p 5672:5672 rabbitmq:3-management ```
 
 Web interface (console)
 
 localhost:15672
 
 Логин и пароль по умолчанию guest/guest.
 
  Sink (подписывающаяся сторона)
  Source (публикующая сторона)
  
  
 ### Идем на
  http://localhost:8020/send
   
  затем смотрим  в консоль 
  * publichser-service   
  * subscriber-service 
 