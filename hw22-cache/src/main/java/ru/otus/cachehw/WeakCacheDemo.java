package ru.otus.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WeakCacheDemo {
    private static final Logger log = LoggerFactory.getLogger(WeakCacheDemo.class);

    public static void main(String[] args) throws InterruptedException {
        HwCache<Object, String> cache = new MyCache<>();
        cache.addListener((k, v, a) -> log.info("cache {} key={} value={}", a, k, v));

        Object key = new Object();
        cache.put(key, "value1");
        log.info("after put, get = {}", cache.get(key));

        // Drop strong reference to key
        key = null;
        System.gc();
        Thread.sleep(200);

        // Key should be collected, entry removed
        log.info("after gc, cache should be empty");
    }
}
