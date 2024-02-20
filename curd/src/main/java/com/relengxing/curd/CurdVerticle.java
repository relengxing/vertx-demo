package com.relengxing.curd;

import com.relengxing.common.EventConstant;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.mysqlclient.MySQLClient;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

import java.security.PublicKey;

/**
 * @author chaoli
 * @date 2024-02-15 20:47
 * @Description
 **/
public class CurdVerticle extends AbstractVerticle {

    @Override
    public void start() {
//        System.out.println(Thread.currentThread().getName() +
//            ": CurdVerticle Hello Verticle : " +
//            Thread.currentThread().getId());

        // 创建用户
        vertx.eventBus().<String>consumer(EventConstant.CREATE_USER, reply -> {
            final String username = reply.body();
            createUser(username)
                .onSuccess(id -> reply.replyAndRequest("created user,id: " + id))
                .onFailure(th -> reply.fail(500, th.getMessage()));
        });
        // 查询用户
        vertx.eventBus().<String>consumer(EventConstant.QUERY_USER, reply -> {
            final String username = reply.body();
            queryUser(username)
                .onSuccess(count -> reply.replyAndRequest("query user, email: " + count))
                .onFailure(th -> reply.fail(500, th.getMessage()));
        });
    }


    /**
     * 创建用户
     * @param username
     * @return
     */
    public Future<Long> createUser(String username) {
        if (username.equals(EventConstant.UNKNOWN)) {
            throw new RuntimeException("name Verification failed");
        }
        Promise<Long> promise = Promise.promise();
        MySQLPool mySQLPool = MysqlConfig.getMysqlPool();
        mySQLPool
            .preparedQuery("INSERT INTO user (name,age,email) VALUES (?, ?, ?)")
            .execute(Tuple.of(username, 100, username + "@qq.com"), ar -> {
                if (ar.succeeded()) {
                    RowSet<Row> rows = ar.result();
                    long lastInsertId = rows.property(MySQLClient.LAST_INSERTED_ID);
                    promise.complete(lastInsertId);
                } else {
                    promise.fail(ar.cause());
                }
            });
        return promise.future();
    }

    /**
     * 查询用户
     * @param username
     * @return
     */
    public Future<String> queryUser(String username){
        if (username.equals(EventConstant.UNKNOWN)) {
            throw new RuntimeException("name Verification failed");
        }

        Promise<String> promise = Promise.promise();
        MySQLPool mySQLPool = MysqlConfig.getMysqlPool();
        mySQLPool
            .preparedQuery("select email from user where name = ? limit 1")
            .execute(Tuple.of(username), ar -> {
                if (ar.succeeded()) {
                    RowSet<Row> rows = ar.result();
                    for (Row row : rows) {
                        promise.complete(row.getString(0));
                        return;
                    }
                } else {
                    promise.fail(ar.cause());
                }
            });
        return promise.future();
    }


}
