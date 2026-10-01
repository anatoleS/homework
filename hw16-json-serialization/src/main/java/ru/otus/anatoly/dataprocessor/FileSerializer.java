package ru.otus.anatoly.dataprocessor;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

public class FileSerializer implements Serializer {

    private final String fileName;
    private static final ObjectMapper mapper = new ObjectMapper();

    public FileSerializer(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public void serialize(Map<String, Double> data) {
        try {
            // Формируем JSON без пробелов, с сохранением порядка ключей
            TreeMap<String, Double> sortedData = new TreeMap<>(data);
            // Записываем отсортированный список в JSON файл
            // Jackson по умолчанию пишет без пробелов, если не использовать pretty printer
            String json = mapper.writeValueAsString(sortedData);
            java.nio.file.Files.writeString(java.nio.file.Path.of(fileName), json);
        } catch (IOException e) {
            throw new FileProcessException(e);
        }
    }
}
