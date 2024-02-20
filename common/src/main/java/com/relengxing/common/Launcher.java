package com.relengxing.common;

import io.vertx.core.Vertx;

import java.util.function.Consumer;

/**
 * @author chaoli
 * @date 2024-02-15 20:41
 * @Description
 **/
public interface Launcher {

    void start(Consumer<Vertx> startConsumer);

}
