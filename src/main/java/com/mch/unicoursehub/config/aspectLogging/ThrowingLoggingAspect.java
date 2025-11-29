package com.mch.unicoursehub.config.aspectLogging;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

/**
 * Aspect for logging exceptions thrown in service methods.
 * This aspect logs the method signature and the thrown exception.
 */
@Aspect
@Component
@Slf4j
public class ThrowingLoggingAspect {

    /**
     * Logs the exception thrown in any service method.
     *
     * @param joinPoint The join point representing the method execution.
     * @param error The exception thrown by the method.
     */
    @AfterThrowing(pointcut = "execution(* com.mch.unicoursehub.service.*.*(..))", throwing = "error")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable error) {
        log.error("Exception in method: " + joinPoint.getSignature());
        log.error("Exception: " + error);
    }

}
