package com.pastry;

/**
 * Represents a dependency that would normally read user data from a database.
 */
public interface UserRepository {
    String findNameById(long userId);
}
