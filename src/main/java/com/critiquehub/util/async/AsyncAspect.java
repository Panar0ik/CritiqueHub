package com.critiquehub.util.async;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

@Aspect
@Component
@RequiredArgsConstructor
public class AsyncAspect {

    private final OperationService operationService;

    @Around("@annotation(com.critiquehub.util.async.ApplyAsync)")
    public Object handleAsync(final ProceedingJoinPoint joinPoint) throws Throwable {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ApplyAsync annotation = signature.getMethod().getAnnotation(ApplyAsync.class);
        String opName = annotation.value();

        String opId = operationService.register(opName);

        operationService.runTask(opId, () -> {
            try {
                joinPoint.proceed();
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        return opId;
    }
}
