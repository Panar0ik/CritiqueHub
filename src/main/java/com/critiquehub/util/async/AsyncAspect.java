package com.critiquehub.util.async;

import lombok.RequiredArgsConstructor;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.concurrent.CompletionException;

@Aspect
@Component
@RequiredArgsConstructor
public class AsyncAspect {

    private final OperationService operationService;
    private final OperationRepository operationRepository;

    @Around("@annotation(com.critiquehub.util.async.ApplyAsync)")
    public Object handleAsync(final ProceedingJoinPoint joinPoint) throws Throwable {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final ApplyAsync annotation = signature.getMethod().getAnnotation(ApplyAsync.class);
        final String opName = annotation.value();

        final String opId = operationService.register(opName);

        operationService.runTask(opId, () -> {
            try {
                updateOperationState(opId, "PROCESSING");

                joinPoint.proceed();

                updateOperationState(opId, "COMPLETED");
            } catch (Throwable e) {
                updateOperationState(opId, "FAILED");
                throw new CompletionException("Async task failed: " + opId, e);
            }
        });

        return opId;
    }

    private void updateOperationState(final String opId, final String state) {
        operationRepository.findById(opId).ifPresent(operation -> {
            operation.setState(state);
            operation.setUpdatedAt(LocalDateTime.now());
            operationRepository.save(operation);
        });
    }
}
