package ru.otus.services.processors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.api.SensorDataProcessor;
import ru.otus.api.model.SensorData;
import ru.otus.lib.SensorDataBufferedWriter;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

// Этот класс нужно реализовать
@SuppressWarnings({"java:S1068", "java:S125"})
public class SensorDataProcessorBuffered implements SensorDataProcessor {
    private static final Logger log = LoggerFactory.getLogger(SensorDataProcessorBuffered.class);
    private final int bufferSize;
    private final SensorDataBufferedWriter writer;
    private final List<SensorData> buffer = new ArrayList<>();

    public SensorDataProcessorBuffered(int bufferSize, SensorDataBufferedWriter writer) {
        this.bufferSize = bufferSize;
        this.writer = writer;
    }

    @Override
    public synchronized void process(SensorData data) {
        buffer.add(data);
        buffer.sort(Comparator.comparing(SensorData::getMeasurementTime));
        if (buffer.size() >= bufferSize) {
            flush();
        }
    }

    public synchronized void flush() {
        if (buffer.isEmpty()) {
            return;
        }
        writer.writeBufferedData(new ArrayList<>(buffer));
        buffer.clear();
    }

    @Override
    public synchronized void onProcessingEnd() {
        flush();
    }
}
