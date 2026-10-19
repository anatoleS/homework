package ru.otus.cachehw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

public class MyCache<K, V> implements HwCache<K, V> {
    private static final Logger log = LoggerFactory.getLogger(MyCache.class);
    private static final String ACTION_PUT = "PUT";
    private static final String ACTION_GET = "GET";
    private static final String ACTION_REMOVE = "REMOVE";

    private final Map<K, V> store = new WeakHashMap<>();
    private final List<HwListener<K,V>> listeners = new ArrayList<>();

    @Override
    public void put(K key, V value) {
        store.put(key, value);
        sendNotify(key, value, ACTION_PUT);
    }

    @Override
    public void remove(K key) {
        V oldValue = store.remove(key);
        sendNotify(key, oldValue, ACTION_REMOVE);
    }

    @Override
    public V get(K key) {
        V result = store.get(key);
        sendNotify(key, result, ACTION_GET);
        return result;
    }

    @Override
    public boolean containsKey(K key) {
        return store.containsKey(key);
    }

    @Override
    public void addListener(HwListener<K, V> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeListener(HwListener<K, V> listener) {
        listeners.remove(listener);
    }

    private void sendNotify(K key, V value, String action) {
        for (var listener : listeners) {
            try {
                listener.notify(key, value, action);
            } catch (Exception e) {
                log.warn("Listener threw exception for action {} key={}", action, key, e);
            }
        }
    }
}
