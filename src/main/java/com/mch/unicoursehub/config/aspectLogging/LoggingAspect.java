package com.mch.unicoursehub.config.aspectLogging;

import brave.Span;
import brave.Tracer;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


/**
 * Aspect for logging and tracing method executions in the application.
 */
@Aspect
@Component
@Slf4j
public class LoggingAspect {

    /**
     * The Tracer used for distributed tracing.
     */

    private Tracer tracer;

    /**
     * Logs the execution of methods in the service and security layers.
     * Captures the method name, arguments, execution time, and exceptions if any.
     *
     * @param joinPoint the join point providing method details.
     * @return the result of the method execution.
     * @throws Throwable if the underlying method throws an exception.
     */
    @Around("execution(* com.mch.unicoursehub.service.*.*.*(..)) " +
            "|| execution(* com.mch.unicoursehub.security.*.*(..)) ")
    public Object logMethodExecution(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = joinPoint.getSignature().toShortString();
        long startTime = System.currentTimeMillis();

        log.info("Method {} started ", methodName);
//        log.info("Method {} started with arguments: {}", methodName, joinPoint.getArgs());

        Object result;
        try {
            result = joinPoint.proceed();  // Proceed with the original method execution
        } catch (Exception e) {
            log.error("Method {} threw an exception: {}", methodName, e.getMessage());
            throw e;
        }

        long timeTaken = System.currentTimeMillis() - startTime;
        log.info("Method {} completed in {} ms", methodName, timeTaken);

        return result;
    }


    /**
     * Adds tracing information to methods in the service layer.
     * Captures details such as class name, method name, arguments, and results, and tags them into the trace.
     *
     * @param joinPoint the join point providing method details.
     * @return the result of the method execution.
     * @throws Throwable if the underlying method throws an exception.
     */

    public Object traceMethods(ProceedingJoinPoint joinPoint) throws Throwable {
        Span span = tracer.nextSpan().name(joinPoint.getSignature().getName()).start();
        span.tag("class", joinPoint.getTarget().getClass().getSimpleName());
        span.tag("method", joinPoint.getSignature().getName());
        Object[] args = joinPoint.getArgs();
        for (int i = 0; i < args.length; i++) {
            span.tag("arg" + i, String.valueOf(args[i]));
        }
        try (Tracer.SpanInScope scope = tracer.withSpanInScope(span)) {
            Object result = joinPoint.proceed();
            span.tag("result", String.valueOf(result)); // Optional, based on your performance needs
            return result;
        } catch (Throwable t) {
            span.error(t); // Log the error into the span
            throw t;
        } finally {
            span.finish();
        }
    }

}
