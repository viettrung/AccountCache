package com.aurora.accounts;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Random;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class AccountCacheImplTest {

    @Test
    public void testGetAccountById() {
        AccountCache cache = new AccountCacheImpl(3);
        Account account1 = new Account(1, 100);
        Account account2 = new Account(2, 200);

        cache.putAccount(account1);
        cache.putAccount(account2);

        assertEquals(account1, cache.getAccountById(1));
        assertEquals(account2, cache.getAccountById(2));
        assertNull(cache.getAccountById(3)); // Non-existent account
    }

    @Test
    public void testSubscribeForAccountUpdates() throws InterruptedException {
        AccountCache cache = new AccountCacheImpl(10);
        Account account = new Account(1, 100);

        CountDownLatch latch = new CountDownLatch(1);
        cache.subscribeForAccountUpdates(updatedAccount -> {
            assertEquals(account, updatedAccount);
            latch.countDown();
        });

        cache.putAccount(account);
        assertTrue(latch.await(1, TimeUnit.SECONDS));
    }

    @Test
    public void testGetTop3AccountsByBalance() {
        AccountCache cache = new AccountCacheImpl(10);
        Account account1 = new Account(1, 100);
        Account account2 = new Account(2, 200);
        Account account3 = new Account(3, 300);
        Account account4 = new Account(4, 400);
        Account account5 = new Account(5, 500);

        cache.putAccount(account1);
        cache.putAccount(account5);
        cache.putAccount(account3);
        cache.putAccount(account2);
        cache.putAccount(account4);

        List<Account> top3Accounts = cache.getTop3AccountsByBalance();
        assertEquals(3, top3Accounts.size());

        assertEquals(account5, top3Accounts.get(0)); // Highest balance
        assertEquals(account4, top3Accounts.get(1));
        assertEquals(account3, top3Accounts.get(2)); // Lowest balance among top 3
    }

    @Test
    public void testGetTop3AccountsByBalanceWhenCapacityLessThan3() {
        AccountCache cache = new AccountCacheImpl(2);
        Account account1 = new Account(1, 100);
        Account account2 = new Account(2, 200);
        Account account3 = new Account(3, 300);

        cache.putAccount(account1);
        cache.putAccount(account2);
        cache.putAccount(account3);

        List<Account> top3Accounts = cache.getTop3AccountsByBalance();
        assertEquals(2, top3Accounts.size());

        assertEquals(account3, top3Accounts.get(0)); // Highest balance
        assertEquals(account2, top3Accounts.get(1)); // Lowest balance among top 3
    }

    @Test
    public void testPutAccountEviction() {
        AccountCache cache = new AccountCacheImpl(3);

        Account account1 = new Account(1, 100);
        Account account2 = new Account(2, 200);
        Account account3 = new Account(3, 300);
        Account account4 = new Account(4, 100);

        cache.putAccount(account1);
        cache.putAccount(account2);
        cache.putAccount(account3);

        // updating account1
        cache.putAccount(new Account(1, 150));

        // 'Used' account2
        cache.getAccountById(2);

        cache.putAccount(account4); // Should evict account3

        assertNull(cache.getAccountById(3));
    }

    @Test
    public void testGetAccountByIdHitCount() {
        AccountCache cache = new AccountCacheImpl(3);

        cache.putAccount(new Account(1, 100));
        cache.putAccount(new Account(2, 200));
        cache.putAccount(new Account(3, 300));
        cache.putAccount(new Account(4, 400));

        cache.getAccountById(1); // has been evicted
        cache.getAccountById(2);
        cache.getAccountById(10); // not found
        cache.getAccountById(3);

        // expect 2 hits only
        assertEquals(2, cache.getAccountByIdHitCount());
    }

    @Test
    void testConcurrentAccess() throws InterruptedException {
        final int numThreads = 100;
        final int numAccounts = 1000;
        final AccountCache cache = new AccountCacheImpl(numAccounts);

        ExecutorService executorService = Executors.newFixedThreadPool(numThreads);
        CountDownLatch latch = new CountDownLatch(numThreads);

        for (int i = 0; i < numThreads; i++) {
            executorService.execute(() -> {
                for (int j = 0; j < numAccounts; j++) {
                    long accountId = new Random().nextInt(100) + 1;
                    Account account = new Account(accountId, j * 100);
                    cache.putAccount(account);
                    assertNotNull(cache.getAccountById(accountId));
                }
                latch.countDown();
            });
        }

        latch.await();
        assertEquals(numThreads * numAccounts, cache.getAccountByIdHitCount());
    }

}
