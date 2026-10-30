package ru.otus.anatoly.ws;

import java.security.Principal;

public record WsPrincipal(String name) implements Principal {
    @Override
    public String getName() {
        return name;
    }
}
