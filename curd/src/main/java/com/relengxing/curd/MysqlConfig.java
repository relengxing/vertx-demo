package com.relengxing.curd;

import io.vertx.core.Vertx;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;

/**
 * @author chaoli
 * @date 2024-02-09 13:51
 * @Description
 **/
public class MysqlConfig {


    private static MySQLPool mySQLPool;

    public static MySQLPool getMysqlPool() {
        return mySQLPool;
    }


    public static void init(Vertx vertx) {
        if (mySQLPool != null) {
            return;
        }
        MySQLConnectOptions connectOptions = new MySQLConnectOptions()
            .setPort(3306)
            .setHost("127.0.0.1")
            .setDatabase("test")
            .setUser("root")
            .setPassword("zip94303");
        // 连接池选项
        PoolOptions poolOptions = new PoolOptions()
            .setMaxSize(5);
        // 创建带连接池的客户端
        mySQLPool = MySQLPool.pool(vertx, connectOptions, poolOptions);
    }

}
