package com.relengxing.facade;

import io.vertx.core.Vertx;
import io.vertx.kafka.client.producer.KafkaProducer;

import java.util.HashMap;
import java.util.Map;

/**
 * @author chaoli
 * @date 2024-02-20 10:48
 * @Description
 **/
public class KafkaConfig {


    private static KafkaProducer<String, String> producer;

    public static KafkaProducer<String, String> getProducer(){
        return producer;
    }


    public static Map<String,String> getConsumerConfig(){
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        config.put("group.id", "test");
        config.put("auto.offset.reset", "earliest");
        config.put("enable.auto.commit", "false");
        return config;
    }

    public static void init(Vertx vertx){
        if (producer != null) {
            return;
        }
        Map<String, String> config = new HashMap<>();
        config.put("bootstrap.servers", "localhost:9092");
        config.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer");
        config.put("acks", "1");
        producer = KafkaProducer.create(vertx, config);
    }

}
