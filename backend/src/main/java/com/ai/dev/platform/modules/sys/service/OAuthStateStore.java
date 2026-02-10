package com.ai.dev.platform.modules.sys.service;

import org.springframework.stereotype.Component;

import org.springframework.beans.factory.DisposableBean;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.*;

/**
 * Simple in-memory state store for OAuth "state" values with TTL and cleanup.
 * This avoids relying on client-side sessions and provides a short-lived verification.
 */
@Component
public class OAuthStateStore implements DisposableBean {
    private final ConcurrentMap<String, Long> store = new ConcurrentHashMap<>();
    private final long ttlMs = TimeUnit.MINUTES.toMillis(5);
    private final ScheduledExecutorService cleaner = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "oauth-state-cleaner");
        t.setDaemon(true);
        return t;
    });

    public OAuthStateStore() {
        cleaner.scheduleAtFixedRate(this::cleanup, ttlMs, ttlMs, TimeUnit.MILLISECONDS);
    }

    public String createState() {
        String s = UUID.randomUUID().toString();
        store.put(s, System.currentTimeMillis());
        return s;
    }

    public boolean validateAndRemoveState(String state) {
        if (state == null) return false;
        Long ts = store.remove(state);
        if (ts == null) return false;
        return (System.currentTimeMillis() - ts) <= ttlMs;
    }

    private void cleanup() {
        long now = System.currentTimeMillis();
        for (Map.Entry<String, Long> e : store.entrySet()) {
            if (now - e.getValue() > ttlMs) store.remove(e.getKey());
        }
    }

    @Override
    public void destroy() {
        cleaner.shutdownNow();
    }
}
