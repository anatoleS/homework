package ru.otus.anatoly.mapper;

/** Создает SQL - запросы */
public interface EntitySQLMetaData {

    String getSelectAllSql();

    String getSelectByIdSql();

    String getInsertSql();

    String getUpdateSql();
}
