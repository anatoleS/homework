package ru.otus.anatoly.atm.decorator;

import ru.otus.anatoly.atm.Atm;

public interface AtmDecorator extends Atm {
    Atm getComponent();
}
