package com.relengxing.common;

import io.vertx.core.Vertx;
import io.vertx.core.VertxOptions;

import java.util.function.Consumer;

/**
 * @author chaoli
 * @date 2024-02-15 20:42
 * @Description
 **/
public class SingleLauncher implements Launcher{
    @Override
    public void start(Consumer<Vertx> consumer) {
        final VertxOptions options = new VertxOptions();
        final Vertx vertx = Vertx.vertx(options);
        if (null != vertx) {
            consumer.accept(vertx);
        }
    }
}
