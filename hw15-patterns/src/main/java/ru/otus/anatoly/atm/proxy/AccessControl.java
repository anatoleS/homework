package ru.otus.anatoly.atm.proxy;

public interface AccessControl {
    boolean hasPermission(String userId, String operation);
}
