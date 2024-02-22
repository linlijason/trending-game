package com.bistro.aop;

import com.bistro.common.exception.api.ApiException;
import com.bistro.common.exception.api.ApiExceptionMsgEnum;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.expression.MethodBasedEvaluationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.annotation.Order;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

@Aspect
@Order(1)
@Component
public class RedissonLockAspect {

    private final static Logger log = LoggerFactory.getLogger(RedissonLockAspect.class);

    @Autowired
    private RedissonClient redissonClient;


    @Around("@annotation(redissonLock)")
    public Object around(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws Throwable {
        Object obj;
        String key = redissonLock.keyPrefix() + parseExpression(joinPoint, redissonLock);
//        log.info("key:{}", key);
        RLock lock = redissonClient.getLock(key);
        boolean lockResult = lock.tryLock(redissonLock.waitTime(), redissonLock.leaseTime(), redissonLock.timeUnit());
        if (lockResult) {
            log.info("acquire the lock:{}", key);
            try {
                obj = joinPoint.proceed();
            } finally {
                if (lock.isLocked() && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    log.info("releases the lock:{}", key);
                }
            }

        } else {
            log.info("not acquire the lock:{}", key);
            throw new ApiException(ApiExceptionMsgEnum.LOCK_GET_ERROR.getCode(), "frequent operation");
        }
        return obj;
    }

    private Method getTargetMethod(ProceedingJoinPoint pjp) throws NoSuchMethodException {
        Signature signature = pjp.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method agentMethod = methodSignature.getMethod();
        return pjp.getTarget().getClass().getMethod(agentMethod.getName(), agentMethod.getParameterTypes());
    }

    private String parseExpression(ProceedingJoinPoint joinPoint, RedissonLock redissonLock) throws NoSuchMethodException {
        String lockParam = redissonLock.keyParam();
        if (!lockParam.matches("^#.*.$")) {
            return lockParam;
        }
        Method targetMethod = getTargetMethod(joinPoint);
        ExpressionParser parser = new SpelExpressionParser();
        EvaluationContext context = new MethodBasedEvaluationContext(new Object(), targetMethod, joinPoint.getArgs(),
                new DefaultParameterNameDiscoverer());
        Expression expression = parser.parseExpression(lockParam);
        return expression.getValue(context, String.class);
    }
}
