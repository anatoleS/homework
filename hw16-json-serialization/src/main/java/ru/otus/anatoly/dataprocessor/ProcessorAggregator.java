package ru.otus.anatoly.dataprocessor;

import ru.otus.anatoly.model.Measurement;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProcessorAggregator implements Processor {

    @Override
    public Map<String, Double> process(List<Measurement> data) {
        // группирует выходящий список по name, при этом суммирует поля value
        Map<String, Double> result = new HashMap<>();
        data.forEach((m) -> result.merge(m.name(), m.value(), Double::sum));
        return result;
    }
}
