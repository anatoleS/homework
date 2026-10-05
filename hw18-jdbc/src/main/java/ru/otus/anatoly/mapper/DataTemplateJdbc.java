package ru.otus.anatoly.mapper;

import ru.otus.anatoly.core.repository.DataTemplate;
import ru.otus.anatoly.core.repository.executor.DbExecutor;

import java.lang.reflect.Field;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Сохраняет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutor dbExecutor,
                            EntityClassMetaData<T> entityClassMetaData,
                            EntitySQLMetaData entitySQLMetaData) {
        this.dbExecutor = dbExecutor;
        this.entityClassMetaData = entityClassMetaData;
        this.entitySQLMetaData = entitySQLMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection,
                entitySQLMetaData.getSelectByIdSql(),
                List.of(id),
                rs -> {
                    try {
                        if (rs.next()) {
                            return mapRowToObject(rs);
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                });
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection,
                entitySQLMetaData.getSelectAllSql(),
                List.of(),
                rs -> {
                    List<T> result = new ArrayList<>();
                    try {
                        while (rs.next()) {
                            result.add(mapRowToObject(rs));
                        }
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return result;
                }).orElse(List.of());
    }

    @Override
    public long insert(Connection connection, T object) {
        return setIdToObject(object, dbExecutor.executeStatement(connection,
                entitySQLMetaData.getInsertSql(),
                getParams(object)));
    }

    @Override
    public void update(Connection connection, T object) {
        dbExecutor.executeStatement(connection,
                entitySQLMetaData.getUpdateSql(),
                getParamsWithLastId(object));
    }

    private T mapRowToObject(java.sql.ResultSet rs) throws Exception {
        T obj = entityClassMetaData.getConstructor().newInstance();
        for (Field field : entityClassMetaData.getAllFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            Object value = rs.getObject(fieldName);
            field.set(obj, value);
        }
        return obj;
    }

    private List<Object> getParams(T obj) {
        List<Object> params = new ArrayList<>();
        for (Field field : entityClassMetaData.getFieldsWithoutId()) {
            try {
                field.setAccessible(true);
                params.add(field.get(obj));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return params;
    }

    private List<Object> getParamsWithLastId(T obj) {
        List<Object> params = new ArrayList<>(getParams(obj));
        try {
            Field idField = entityClassMetaData.getIdField();
            params.add(idField.get(obj));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return params;
    }


    private long setIdToObject(T object, long id) {
        // Установить ID в объект
        try {
            Field idField = entityClassMetaData.getIdField();
            idField.setAccessible(true);
            idField.set(object, id);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return id;
    }
}
