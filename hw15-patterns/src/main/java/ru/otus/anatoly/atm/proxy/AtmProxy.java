package ru.otus.anatoly.atm.proxy;

import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class AtmProxy implements Atm {
    private final Atm realAtm;
    private final AccessControl accessControl;
    private final String userId;

    public AtmProxy(Atm realAtm, AccessControl accessControl, String userId) {
        this.realAtm = realAtm;
        this.accessControl = accessControl;
        this.userId = userId;
    }

    @Override
    public void accept(Nominal nominal, int count) {
        checkPermission("accept");
        realAtm.accept(nominal, count);
    }

    @Override
    public Optional<Map<Nominal, Integer>> withdraw(BigDecimal amount) {
        checkPermission("withdraw");
        return realAtm.withdraw(amount);
    }

    @Override
    public BigDecimal getBalance() {
        return realAtm.getBalance();
    }

    @Override
    public Map<Nominal, Integer> getState() {
        return realAtm.getState();
    }

    private void checkPermission(String operation) {
        if (!accessControl.hasPermission(userId, operation)) {
            throw new SecurityException("Access denied for user " + userId + " operation " + operation);
        }
    }
}
