package com.aurora.accounts;

public final class Account {
    private final long id;
    private final long balance;

    public Account(long id, long balance) {
        this.id = id;
        this.balance = balance;
    }

    public long getId() {
        return id;
    }

    public long getBalance() {
        return balance;
    }

    @Override
    public int hashCode() {
        int result = 31 + (int) (id ^ (id >>> 32));
        return 31 * result + (int) (balance ^ (balance >>> 32));
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Account)) return false;
        Account other = (Account) obj;
        return id == other.id && balance == other.balance;
    }
}
