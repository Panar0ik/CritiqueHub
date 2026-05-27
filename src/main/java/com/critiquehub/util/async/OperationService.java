package com.critiquehub.util.async;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityNotFoundException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@Service
@Slf4j
public class OperationService {

    private final OperationRepository repository;
    private final Executor taskExecutor;

    public OperationService(
            final OperationRepository repositoryP,
            final @Qualifier("taskExecutor") Executor ptaskExecutorP
    ) {
        this.repository = repositoryP;
        this.taskExecutor = ptaskExecutorP;
    }

    @Transactional
    public String register(final String opName) {
        String id = UUID.randomUUID().toString();

        log.info("[ASYNC-INIT] Registering new operation: '{}' with ID: {} (Thread: {})",
                opName, id, Thread.currentThread().getName());

        Operation op = Operation.builder()
                .id(id)
                .name(opName)
                .state("START")
                .updatedAt(LocalDateTime.now())
                .build();
        repository.save(op);
        return id;
    }

    public void runTask(final String id, final Runnable task) {
        log.info("[ASYNC-SUBMIT] Submitting task {} to pool for operation ID: {}",
                task.getClass().getSimpleName(), id);

        CompletableFuture.runAsync(() -> {
            String currentThreadName = Thread.currentThread().getName();
            try {
                log.info("[ASYNC-START] Task ID: {} started in background thread: {}", id, currentThreadName);

                update(id, "IN_PROGRESS", "Task started");

                task.run();

                log.info("[ASYNC-SUCCESS] Task ID: {} completed successfully in thread: {}", id, currentThreadName);
                update(id, "COMPLETED", "Success");

            } catch (Exception e) {
                log.error("[ASYNC-ERROR] Task ID: {} failed in thread: {}. Reason: {}",
                        id, currentThreadName, e.getMessage(), e);
                update(id, "ERROR", e.getMessage());
            }
        }, taskExecutor);
    }

    @Transactional
    public void update(final String id, final String state, final String payload) {
        log.debug("[ASYNC-UPDATE] Updating task {}: {} -> '{}'", id, state, payload);

        repository.findById(id).ifPresentOrElse(
                op -> {
                    op.setState(state);
                    op.setPayload(payload);
                    op.setUpdatedAt(LocalDateTime.now());
                    repository.save(op);
                },
                () -> log.warn("[ASYNC-WARN] Attempted to update non-existent operation with ID: {}", id)
        );
    }

    @Transactional(readOnly = true)
    public Operation getStatus(final String id) {
        log.debug("[ASYNC-STATUS] Fetching status for task ID: {}", id);

        return repository.findById(id)
                .orElseThrow(() -> {
                    log.error("[ASYNC-NOT-FOUND] Operation with ID: {} not found in database", id);
                    return new EntityNotFoundException("Operation not found: " + id);
                });
    }
}
