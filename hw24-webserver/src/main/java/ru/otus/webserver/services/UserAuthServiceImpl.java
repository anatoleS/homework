package ru.otus.webserver.services;

public class UserAuthServiceImpl implements UserAuthService {

    private static final String ADMIN_LOGIN = "admin";
    private static final String ADMIN_PASSWORD = "admin";

    @Override
    public boolean authenticate(String login, String password) {
        if (login == null || password == null) {
            return false;
        }
        return ADMIN_LOGIN.equals(login) && ADMIN_PASSWORD.equals(password);
    }
}
