package com.critiquehub.util.async;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OperationService {

    private final OperationRepository repository;

    public String register(final String opName) {
        String id = UUID.randomUUID().toString();

        Operation op = Operation.builder()
                .id(id)
                .name(opName)
                .state("START")
                .updatedAt(LocalDateTime.now())
                .build();

        repository.save(op);
        return id;
    }

    @Async("taskExecutor")
    public void runTask(final String id, final Runnable task) {
        try {
            update(id, "IN_PROGRESS", "Task started");

            task.run();

            update(id, "COMPLETED", "Success");
        } catch (Exception e) {
            log.error("Async operation {} failed", id, e);
            update(id, "ERROR", e.getMessage());
        }
    }

    public void update(final String id, final String state, final String payload) {
        repository.findById(id).ifPresent(op -> {
            op.setState(state);
            op.setPayload(payload);
            op.setUpdatedAt(LocalDateTime.now());
            repository.save(op);
        });
    }
}
