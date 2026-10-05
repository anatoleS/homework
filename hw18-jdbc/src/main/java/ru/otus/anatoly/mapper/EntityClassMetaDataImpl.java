package ru.otus.anatoly.mapper;

import lombok.Getter;
import ru.otus.anatoly.annotation.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Getter
public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    private final Class<T> clazz;
    private final String name;
    private final Constructor<T> constructor;
    private final Field idField;
    private final List<Field> allFields;
    private final List<Field> fieldsWithoutId;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
        this.name = clazz.getSimpleName().toLowerCase();
        try {
            this.constructor = clazz.getConstructor();
            this.constructor.setAccessible(true);
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No default constructor", e);
        }

        this.allFields = new ArrayList<>();
        Field id = null;
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            allFields.add(field);
            if (field.isAnnotationPresent(Id.class)) {
                id = field;
            }
        }

        if (id == null) {
            throw new RuntimeException("No @Id field found");
        }
        this.idField = id;

        this.fieldsWithoutId = new ArrayList<>(allFields);
        fieldsWithoutId.remove(idField);
    }

}