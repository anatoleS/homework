package ru.otus.anatoly.atm.decorator;

import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

public class LoggingAtm extends AtmDecoratorBase {

    public LoggingAtm(Atm component) {
        super(component);
    }

    @Override
    public void accept(Nominal nominal, int count) {
        // Для демо, логирование в консоль, здесь и далее
        System.out.println("[LoggingAtm] accept nominal=" + nominal + " count=" + count);
        super.accept(nominal, count);
        System.out.println("[LoggingAtm] new balance=" + getBalance());
    }

    @Override
    public Optional<Map<Nominal, Integer>> withdraw(BigDecimal amount) {
        System.out.println("[LoggingAtm] withdraw amount=" + amount + " balance before=" + getBalance());
        Optional<Map<Nominal, Integer>> result = super.withdraw(amount);
        System.out.println("[LoggingAtm] withdraw result=" + result + " balance after=" + getBalance());
        return result;
    }

    @Override
    public BigDecimal getBalance() {
        BigDecimal balance = super.getBalance();
        System.out.println("[LoggingAtm] getBalance=" + balance);
        return balance;
    }
}
