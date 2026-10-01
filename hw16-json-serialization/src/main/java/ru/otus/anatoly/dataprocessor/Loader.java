package ru.otus.anatoly.dataprocessor;

import ru.otus.anatoly.model.Measurement;

import java.util.List;

public interface Loader {

    List<Measurement> load();
}
