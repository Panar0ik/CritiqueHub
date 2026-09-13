package com.critiquehub.util.async;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsCounter {

    private final AtomicInteger atomicCounter = new AtomicInteger(0);

    public int incrementAtomic() {
        return atomicCounter.incrementAndGet();
    }

    public int getAtomicValue() {
        return atomicCounter.get();
    }
}
