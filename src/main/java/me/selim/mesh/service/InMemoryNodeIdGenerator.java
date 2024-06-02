package me.selim.mesh.service;

import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
public class InMemoryNodeIdGenerator implements IdGenerator<Long> {
    private static final AtomicLong counter = new AtomicLong();

    @Override
    public Long next() {
        return counter.incrementAndGet();
    }
}
