package com.se.subscriber;

import com.se.subscriber.model.Message;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;

@EnableBinding(Sink.class)
@SpringBootApplication
public class SubscriberServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(SubscriberServiceApplication.class, args);
    }

    /**
     * прослушиватель, который может обрабатывать входящие сообщения.
     * пытается автоматически конвертировать входящие сообщения в тип Message.
     * @param message
     * @throws Exception
     */
    @StreamListener(target = Sink.INPUT)
    public void handleMessage(Message message) throws Exception {
        if(message.getMessage().contains("fuck")) {
            throw new Exception("Error spam!!!");
        }
        System.out.println("Spam message: " + message);
    }
}
