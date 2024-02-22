package com.bistro.framework.manager;

import com.bistro.common.utils.spring.SpringUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * 异步任务管理器
 *
 * @author ruoyi
 */
public class AsyncManager {
    /**
     * 操作延迟10毫秒
     */
    private final int OPERATE_DELAY_TIME = 10;

    /**
     * 异步操作任务调度线程池
     */
    private ThreadPoolTaskExecutor executor;

    /**
     * 单例模式
     */
    private AsyncManager() {
        executor = SpringUtils.getBean("threadPoolTaskExecutor");
    }

    private static AsyncManager me = new AsyncManager();

    public static AsyncManager me() {
        return me;
    }

    /**
     * 执行任务
     *
     * @param task 任务
     */
    public void execute(Runnable task) {
        executor.execute(task);
    }

    /**
     * 停止任务线程池
     */
    public void shutdown() {
        executor.shutdown();
    }

}
