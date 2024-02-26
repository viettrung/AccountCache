package com.aurora.accounts;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * An implementation of the {@link AccountCache} interface using a concurrent
 * map for caching accounts and a concurrent linked deque for tracking access
 * order.
 */
public class AccountCacheImpl implements AccountCache {
    private final int capacity;
    private final ConcurrentMap<Long, Account> cache;
    private final ConcurrentLinkedDeque<Long> accessQueue;
    private final Set<Consumer<Account>> listeners;
    private final AtomicInteger hitCount;

    /**
     * Constructs an {@code AccountCacheImpl} with the specified capacity.
     *
     * @param capacity the maximum number of accounts the cache can hold
     */
    public AccountCacheImpl(int capacity) {
        this.capacity = capacity;
        this.cache = new ConcurrentHashMap<>();
        this.accessQueue = new ConcurrentLinkedDeque<>();
        this.listeners = new CopyOnWriteArraySet<>();
        this.hitCount = new AtomicInteger(0);
    }

    @Override
    public Account getAccountById(long id) {
        Account account = cache.get(id);
        if (account != null) {
            updateAccessQueue(id);
            hitCount.incrementAndGet();
        }
        return account;
    }

    @Override
    public void subscribeForAccountUpdates(Consumer<Account> listener) {
        listeners.add(listener);
    }

    @Override
    public List<Account> getTop3AccountsByBalance() {
        List<Account> sortedAccounts = new ArrayList<>(cache.values());
        sortedAccounts.sort(Comparator.comparingLong(Account::getBalance).reversed());
        return sortedAccounts.subList(0, Math.min(3, sortedAccounts.size()));
    }

    @Override
    public int getAccountByIdHitCount() {
        return hitCount.get();
    }

    @Override
    public void putAccount(Account account) {
        listeners.forEach(listener -> listener.accept(account));

        if (cache.size() >= capacity) {
            evictLeastRecentlyUsed();
        }

        cache.put(account.getId(), account);
        updateAccessQueue(account.getId());
    }

    /**
     * Updates the access queue with the given account ID.
     *
     * @param id the ID of the account to update in the access queue
     */
    private void updateAccessQueue(long id) {
        accessQueue.remove(id);
        accessQueue.offer(id);
    }

    /**
     * Evicts the least recently used account from the cache.
     */
    private void evictLeastRecentlyUsed() {
        Long idToRemove = accessQueue.poll();
        if (idToRemove != null) {
            cache.remove(idToRemove);
        }
    }
}
