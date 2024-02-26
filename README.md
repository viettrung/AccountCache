# AccountCache

AccountCache is a Java implementation of an in-memory cache for storing and accessing 
instances of the Account class in a concurrent environment. It provides thread-safe operations 
for putting, getting, and subscribing to account updates.

## Features

- Thread-safe implementation suitable for use in multi-threaded environments.
- Supports LRU (Least Recently Used) eviction policy for managing cache size.
- Allows subscribing to account updates to receive notifications when an account is put or updated in the cache.
- Provides methods for getting top accounts by balance and tracking hit counts for account lookups.

## Setup

### Prerequisites

Before getting started, ensure that you have the following tools installed:

- Java Development Kit (JDK) version 8 or higher
- Apache Maven (for building and managing dependencies)

### Build the project
Navigate to the project directory and run the following command to build the project:

```bash
mvn clean install
```

## Usage

### Adding the library to your project

To use AccountCache in your Java project, you can download the source code and include it in 
your project's source directory or include the JAR file in your project's classpath.

### Creating a AccountCache instance

To create a AccountCache instance, you can specify the maximum capacity of the cache:

```java
AccountCache cache = new AccountCacheImpl(100); // Create a cache with a maximum capacity of 100 accounts
```

### Putting an account into the cache
You can put an account into the cache using the putAccount method:
```java
Account account = new Account(1, 1000); // Create an account with ID 1 and balance 1000
cache.putAccount(account); // Put the account into the cache
```

### Getting an account from the cache
You can get an account from the cache by its ID using the getAccountById method:
```java
Account account = cache.getAccountById(1); // Get the account with ID 1 from the cache
if (account != null) {
    // Account found, perform operations
} else {
    // Account not found
}
```

### Subscribing to account updates
You can subscribe to account updates to receive notifications when an account is 
put or updated in the cache:
```java
cache.subscribeForAccountUpdates(account -> {
    // Handle account update
    System.out.println("Account updated: " + account);
});
```

### Other methods
The AccountCache also provides other methods such as getTop3AccountsByBalance to 
get the top 3 accounts by balance, and getAccountByIdHitCount to get the number of hits 
for account lookups.
