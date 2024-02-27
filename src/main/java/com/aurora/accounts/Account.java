package com.aurora.accounts;

import java.util.concurrent.atomic.AtomicLong;

public final class Account {
    private final long id;
    private AtomicLong balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = new AtomicLong(balance);
    }

    public long getId() {
        return id;
    }

    public long getBalance() {
        return balance.get();
    }

    public void setBalance(long balance) {
        this.balance = new AtomicLong(balance);
    }

    @Override
    public int hashCode() {
        int result = 31 + (int) (id ^ (id >>> 32));
        return 31 * result + (int) (balance.get() ^ (balance.get() >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account)) return false;
        Account other = (Account) obj;
        return id == other.id && balance == other.balance;
    }
}
