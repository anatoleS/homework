package ru.otus.anatoly.atm.decorator;

import lombok.extern.slf4j.Slf4j;
import ru.otus.anatoly.atm.Atm;
import ru.otus.anatoly.atm.model.Nominal;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class LoggingAtm extends AtmDecoratorBase {

    public LoggingAtm(Atm component) {
        super(component);
    }

    @Override
    public void accept(Nominal nominal, int count) {
        log.info("accept nominal={} count={}", nominal, count);
        try {
            super.accept(nominal, count);
            log.info("new balance={}", getBalance());
        } catch (Exception e) {
            log.error("accept failed nominal={} count={}", nominal, count, e);
            throw e;
        }
    }

    @Override
    public Optional<Map<Nominal, Integer>> withdraw(BigDecimal amount) {
        log.info("withdraw amount={} balance before={}", amount, getBalance());
        try {
            Optional<Map<Nominal, Integer>> result = super.withdraw(amount);
            log.info("withdraw result={} balance after={}", result, getBalance());
            return result;
        } catch (Exception e) {
            log.error("withdraw failed amount={}", amount, e);
            throw e;
        }
    }

    @Override
    public BigDecimal getBalance() {
        BigDecimal balance = super.getBalance();
        log.debug("getBalance={}", balance);
        return balance;
    }
}
