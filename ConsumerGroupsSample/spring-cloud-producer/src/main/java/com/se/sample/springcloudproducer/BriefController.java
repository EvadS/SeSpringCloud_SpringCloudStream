package com.se.sample.springcloudproducer;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@EnableBinding(Source.class)
@RestController
public class BriefController {

    //refer to instance of bean that Stream adds to container
    @Autowired
    Source mysource;


    //take in a message via HTTP, publish to broker
    @RequestMapping(path = "/brief", method = RequestMethod.POST)
    public String publishMessage(@RequestBody String payload) {

        System.out.println(payload);

        //send message to channel
        mysource.output().send(MessageBuilder.withPayload(payload).build());

        return "success";
    }
}