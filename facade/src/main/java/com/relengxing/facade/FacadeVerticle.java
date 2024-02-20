package com.relengxing.facade;

import com.relengxing.common.EventConstant;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Handler;
import io.vertx.core.MultiMap;
import io.vertx.core.Promise;
import io.vertx.core.eventbus.Message;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.kafka.client.consumer.KafkaConsumer;
import io.vertx.kafka.client.producer.KafkaProducerRecord;
import io.vertx.redis.client.RedisAPI;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author chaoli
 * @date 2024-02-15 20:47
 * @Description
 **/
public class FacadeVerticle extends AbstractVerticle {

    @Override
    public void start() {
        HttpServer server = vertx.createHttpServer();
        Router router = Router.router(vertx);
        // mysql:创建用户
        router.route().method(HttpMethod.POST).path("/user").handler(createUser);
        // mysql:查询用户
        router.route().method(HttpMethod.GET).path("/user").handler(queryUser);
        // redis: 写入
        router.route().method(HttpMethod.POST).path("/redis").handler(writeRedis);
        // redis: 读取
        router.route().method(HttpMethod.GET).path("/redis").handler(getRedis);
        // kafka: 写入
        router.route().method(HttpMethod.POST).path("/kafka").handler(writeKafka);
        // kafka 消费
        Map<String, String> consumerConfig = KafkaConfig.getConsumerConfig();
        KafkaConsumer<String, String> consumer = KafkaConsumer.create(vertx, consumerConfig);
        consumer.handler(record -> {
            System.out.println("Processing key=" + record.key() + ",value=" + record.value() +
                ",partition=" + record.partition() + ",offset=" + record.offset());
        });
        Set<String> topics = new HashSet<>();
        topics.add("topic_1");
        consumer.subscribe(topics);


        server.requestHandler(router).listen(8888);
    }


    public Handler<RoutingContext> createUser = ctx -> {
        MultiMap queryParams = ctx.queryParams();
        String name = queryParams.contains("name") ? queryParams.get("name") : EventConstant.UNKNOWN;

        Promise<Message<String>> promise = Promise.promise();
        vertx.eventBus().request(EventConstant.CREATE_USER, name, promise);

        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "text/plain");
        promise.future().onSuccess(msg -> {
            String body = msg.body();
            response.end("create User Success... " + body);
        }).onFailure(msg -> {
            String body = msg.getMessage();
            response.end("create User Failed... " + body);
        });
    };

    public Handler<RoutingContext> queryUser = ctx -> {
        MultiMap queryParams = ctx.queryParams();
        String name = queryParams.contains("name") ? queryParams.get("name") : EventConstant.UNKNOWN;

        Promise<Message<String>> promise = Promise.promise();
        vertx.eventBus().request(EventConstant.QUERY_USER, name, promise);

        HttpServerResponse response = ctx.response();
        response.putHeader("content-type", "text/plain");
        promise.future().onSuccess(msg -> {
            String body = msg.body();
            response.end("query User Success... " + body);
        }).onFailure(msg -> {
            String body = msg.getMessage();
            response.end("query User Failed... " + body);
        });
    };


    public Handler<RoutingContext> writeRedis = ctx -> {
        MultiMap queryParams = ctx.queryParams();
        String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";

        RedisAPI.api(RedisConfig.getClient()).set(List.of(name, name + "@redis.com")).onSuccess(result -> {
                ctx.end("write redis success");
            })
            .onFailure(err -> {
                ctx.end("write redis failed");
            });
    };

    public Handler<RoutingContext> getRedis = ctx -> {
        //  查询
        MultiMap queryParams = ctx.queryParams();
        String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";

        RedisAPI.api(RedisConfig.getClient()).get(name).onSuccess(result -> {
                ctx.end("get redis success, " + result);
            })
            .onFailure(err -> {
                ctx.end("get redis failed");
            });
    };


    public Handler<RoutingContext> writeKafka = ctx -> {
        MultiMap queryParams = ctx.queryParams();
        String name = queryParams.contains("name") ? queryParams.get("name") : "unknown";

        KafkaProducerRecord<String, String> record =
            KafkaProducerRecord.create("topic_1", "message_" + name);

        KafkaConfig.getProducer().write(record);
    };
}
