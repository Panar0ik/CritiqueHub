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
        // Получаем имя операции из аннотации
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        ApplyAsync annotation = signature.getMethod().getAnnotation(ApplyAsync.class);
        String opName = annotation.value();

        // 1. Регистрируем в БД и получаем ID
        String opId = operationService.register(opName);

        // 2. Уводим реальное выполнение метода сервиса в фоновый поток
        operationService.runTask(opId, () -> {
            try {
                joinPoint.proceed(); // Выполняется сам метод сервиса
            } catch (Throwable e) {
                throw new RuntimeException(e);
            }
        });

        // 3. Мгновенно возвращаем ID операции наружу (в контроллер)
        return opId;
    }
}
