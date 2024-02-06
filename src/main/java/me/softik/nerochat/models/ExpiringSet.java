package me.softik.nerochat.models;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Sets;

import java.util.Set;
import java.util.concurrent.TimeUnit;

public class ExpiringSet<E> {

    private final Cache<E, Object> cache;
    private static final Object PRESENT = new Object();

    public ExpiringSet(long duration, TimeUnit unit) {
        this.cache = Caffeine.newBuilder().expireAfterWrite(duration, unit).build();
    }

    public boolean add(E item) {
        boolean present = contains(item);
        this.cache.put(item, PRESENT);
        return !present;
    }

    public boolean contains(E item) {
        return this.cache.getIfPresent(item) != null;
    }

    public Set<E> getValues() {
        return Sets.newHashSet(this.cache.asMap().keySet());
    }

    public long getSize() {
        return this.cache.estimatedSize();
    }
}
