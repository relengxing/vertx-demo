package com.relengxing.facade;

import com.relengxing.common.ClusterLauncher;
import com.relengxing.common.Launcher;
import com.relengxing.common.SingleLauncher;
import io.vertx.core.DeploymentOptions;

/**
 * @author chaoli
 * @date 2024-02-15 20:44
 * @Description
 **/
public class FacadeMainLauncher {

    public static void main(String[] args) {
        // 哪种模式？
        final boolean isClustered = true;
        final Launcher launcher = isClustered ? new ClusterLauncher() : new SingleLauncher();
        // 设置Options
        launcher.start(vertx -> {
            RedisConfig.init(vertx);
            KafkaConfig.init(vertx);

            // 执行Vertx相关后续逻辑
            vertx.deployVerticle(FacadeVerticle.class, new DeploymentOptions());
        });
    }
}
