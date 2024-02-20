package com.relengxing.curd;

import com.relengxing.common.ClusterLauncher;
import com.relengxing.common.Launcher;
import com.relengxing.common.SingleLauncher;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.ThreadingModel;

/**
 * @author chaoli
 * @date 2024-02-15 20:44
 * @Description
 **/
public class CurdMainLauncher {

    public static void main(String[] args) {
        // 哪种模式？
        final boolean isClustered = true;
        final Launcher launcher = isClustered ? new ClusterLauncher() : new SingleLauncher();

        // 设置Options
        launcher.start(vertx -> {
            MysqlConfig.init(vertx);
            // 执行Vertx相关后续逻辑
            vertx.deployVerticle(CurdVerticle.class, new DeploymentOptions().setThreadingModel(ThreadingModel.WORKER).setWorkerPoolSize(5));
        });
    }
}
