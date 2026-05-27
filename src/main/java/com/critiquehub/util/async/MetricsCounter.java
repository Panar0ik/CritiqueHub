package com.critiquehub.util.async;

import org.springframework.stereotype.Component;
import java.util.concurrent.atomic.AtomicInteger;

@Component
public class MetricsCounter {

    private final AtomicInteger atomicCounter = new AtomicInteger(0);

    private int synchronizedCounter = 0;

    public int incrementAtomic() {
        return atomicCounter.incrementAndGet();
    }

    public synchronized int incrementSynchronized() {
        return ++synchronizedCounter;
    }

    public int getAtomicValue() {
        return atomicCounter.get();
    }

    public synchronized int getSynchronizedValue() {
        return synchronizedCounter;
    }
}
