package ru.otus.anatoly.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.otus.anatoly.model.Measurement;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public class ResourcesFileLoader implements Loader {

    private final String fileName;
    private static final ObjectMapper mapper = new ObjectMapper();

    public ResourcesFileLoader(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Measurement> load() {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(fileName)) {
            if (is == null) {
                throw new FileProcessException("File not found: " + fileName);
            }
            return List.of(mapper.readValue(is, Measurement[].class));
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
