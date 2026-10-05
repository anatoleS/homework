package ru.otus.anatoly.mapper;

import java.lang.reflect.Field;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final EntityClassMetaData<?> metaData;
    private final String tableName;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> metaData) {
        this.metaData = metaData;
        this.tableName = metaData.getName();
    }

    @Override
    public String getSelectAllSql() {
        return "SELECT * FROM " + tableName;
    }

    @Override
    public String getSelectByIdSql() {
        String idColumn = metaData.getIdField().getName();
        return "SELECT * FROM " + tableName + " WHERE " + idColumn + " = ?";
    }

    @Override
    public String getInsertSql() {
        StringBuilder columns = new StringBuilder();
        StringBuilder placeholders = new StringBuilder();

        for (Field field : metaData.getFieldsWithoutId()) {
            if (columns.length() > 0) {
                columns.append(", ");
                placeholders.append(", ");
            }
            columns.append(field.getName());
            placeholders.append("?");
        }

        return "INSERT INTO " + tableName + " (" + columns + ") VALUES (" + placeholders + ")";
    }

    @Override
    public String getUpdateSql() {
        StringBuilder setClause = new StringBuilder();

        for (Field field : metaData.getFieldsWithoutId()) {
            if (setClause.length() > 0) {
                setClause.append(", ");
            }
            setClause.append(field.getName()).append(" = ?");
        }

        String idColumn = metaData.getIdField().getName();
        return "UPDATE " + tableName + " SET " + setClause + " WHERE " + idColumn + " = ?";
    }
}