package com.critiquehub.util.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class LoggingAspect {

    @Around("@annotation(com.critiquehub.util.aspect.LogExecutionTime)")
    public Object logExecutionTime(final ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();

        Object proceed = joinPoint.proceed();

        long executionTime = System.currentTimeMillis() - start;

        log.info("Method {} executed in {}ms", joinPoint.getSignature().toShortString(), executionTime);
        return proceed;
    }

    @Around("execution(* com.critiquehub.service.*.*(..))")
    public Object logAllServiceMethods(final ProceedingJoinPoint joinPoint) throws Throwable {
        long start = System.currentTimeMillis();
        Object proceed = joinPoint.proceed();
        long executionTime = System.currentTimeMillis() - start;

        log.debug("Service profiling: {} took {}ms", joinPoint.getSignature().toShortString(), executionTime);
        return proceed;
    }
}
