package com.relengxing.facade;

import io.vertx.core.Vertx;
import io.vertx.redis.client.Redis;

/**
 * @author chaoli
 * @date 2024-02-14 11:38
 * @Description
 **/
public class RedisConfig {

    private static Redis client;

    public static Redis getClient(){
        return client;
    }

    public static void init(Vertx vertx){
        if (client != null) {
            return;
        }
        client = Redis.createClient(vertx);
    }

}
