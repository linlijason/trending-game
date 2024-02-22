package com.bistro.aop;

import com.bistro.constants.RedisConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonLock {

    /**
     * 锁定参数表达式
     */
    String keyParam();

    /**
     * 锁自动释放时间(毫秒)
     */
    int leaseTime() default 30000;

    int waitTime() default 0;

    String keyPrefix() default RedisConstants.LOCK_UID_GAME_CODE;

    TimeUnit timeUnit() default TimeUnit.MILLISECONDS;

}
