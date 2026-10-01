package ru.otus.anatoly.atm.decorator;

import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public abstract class AtmDecoratorBase implements AtmDecorator {
    protected final Atm component;

    protected AtmDecoratorBase(Atm component) {
        this.component = component;
    }

    @Override
    public Atm getComponent() {
        return component;
    }

    @Override
    public void accept(Nominal nominal, int count) {
        component.accept(nominal, count);
    }

    @Override
    public Optional<Map<Nominal, Integer>> withdraw(BigDecimal amount) {
        return component.withdraw(amount);
    }

    @Override
    public BigDecimal getBalance() {
        return component.getBalance();
    }

    @Override
    public Map<Nominal, Integer> getState() {
        return component.getState();
    }
}
