package ru.otus.anatoly.dataprocessor;

import ru.otus.anatoly.model.Measurement;

import java.util.List;
import java.util.Map;

public interface Processor {

    Map<String, Double> process(List<Measurement> data);
}
