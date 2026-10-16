package ru.otus.crm.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwCache;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.sessionmanager.TransactionManager;
import ru.otus.crm.model.Client;

import java.util.List;
import java.util.Optional;

public class DbServiceClientImpl implements DBServiceClient {
    private static final Logger log = LoggerFactory.getLogger(DbServiceClientImpl.class);

    private final DataTemplate<Client> clientDataTemplate;
    private final TransactionManager transactionManager;
    private final HwCache<CacheKey, Client> cache = new MyCache<>();

    private static final class CacheKey {
        final long id;
        CacheKey(long id) { this.id = id; }
        @Override public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof CacheKey)) return false;
            return id == ((CacheKey) o).id;
        }
        @Override public int hashCode() { return Long.hashCode(id); }
        @Override public String toString() { return String.valueOf(id); }
    }

    public DbServiceClientImpl(TransactionManager transactionManager, DataTemplate<Client> clientDataTemplate) {
        this.transactionManager = transactionManager;
        this.clientDataTemplate = clientDataTemplate;
        cache.addListener((k, v, a) -> log.info("cache {} key={} value={}", a, k, v));
    }

    @Override
    public Client saveClient(Client client) {
        Client saved = transactionManager.doInTransaction(session -> {
            var clientCloned = client.clone();
            if (client.getId() == null) {
                clientDataTemplate.insert(session, clientCloned);
                log.info("created client: {}", clientCloned);
            } else {
                clientDataTemplate.update(session, clientCloned);
                log.info("updated client: {}", clientCloned);
            }
            return clientCloned;
        });
        if (saved.getId() != null) {
            cache.put(new CacheKey(saved.getId()), saved);
        }
        return saved;
    }

    @Override
    public Optional<Client> getClient(long id) {
        CacheKey key = new CacheKey(id);
        if (cache.containsKey(key)) {
            Client cached = cache.get(key);
            log.info("cache hit for id {}", id);
            return Optional.of(cached);
        }
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientOptional = clientDataTemplate.findById(session, id);
            log.info("client: {}", clientOptional);
            clientOptional.ifPresent(c -> cache.put(new CacheKey(id), c));
            return clientOptional;
        });
    }

    @Override
    public List<Client> findAll() {
        return transactionManager.doInReadOnlyTransaction(session -> {
            var clientList = clientDataTemplate.findAll(session);
            log.info("clientList:{}", clientList);
            return clientList;
        });
    }
}
