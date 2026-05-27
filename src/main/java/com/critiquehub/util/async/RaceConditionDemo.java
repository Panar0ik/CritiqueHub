package com.critiquehub.util.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class RaceConditionDemo implements CommandLineRunner {

    private static final int DEFAULT_COUNT = 60;
    private static final int DEFAULT_THREAD = 1000;

    private int unsafeCounter = 0;
    private final AtomicInteger safeCounter = new AtomicInteger(0);

    @Override
    public void run(final String... args) throws Exception {
        final int threadsCount = DEFAULT_COUNT;
        final int iterationsPerThread = DEFAULT_THREAD;
        final int expectedTotal = threadsCount * iterationsPerThread;

        log.info("[Race Demo] Запуск теста конкурентности на {} потоках...", threadsCount);

        final CountDownLatch latch = new CountDownLatch(threadsCount);

        try (ExecutorService executorService = Executors.newFixedThreadPool(threadsCount)) {

            for (int i = 0; i < threadsCount; i++) {
                executorService.submit(() -> {
                    try {
                        for (int j = 0; j < iterationsPerThread; j++) {
                            unsafeCounter++;
                            safeCounter.incrementAndGet();
                        }
                    } finally {
                        latch.countDown();
                    }
                });
            }

            latch.await();
        }

        log.info("--- РЕЗУЛЬТАТЫ ТЕСТА КОНКУРЕНТНОСТИ ---");
        log.info("Ожидаемое значение счетчиков: {}", expectedTotal);
        log.info("Фактическое НЕБЕЗОПАСНОЕ значение (unsafeCounter): {}", unsafeCounter);
        log.info("Фактическое БЕЗОПАСНОЕ значение (safeCounter): {}", safeCounter.get());

        if (unsafeCounter < expectedTotal) {
            log.warn(
                    "[RACE CONDITION ПОДТВЕРЖДЕН] Из-за коллизий потоков потеряно {} операций!",
                    expectedTotal - unsafeCounter
            );
        }
    }
}
