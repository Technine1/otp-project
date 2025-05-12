package ru.repository;

import ru.model.Identity;

import java.util.List;

public interface CrudRepository {
    <ID, T extends Identity<ID>> boolean save(T entity);

    <ID, T extends Identity<ID>> T getById(ID id, Class<T> type);

    <ID, T extends Identity<ID>> List<T> getByField(String field, Object value, Class<T> type);

    <ID, T extends Identity<ID>> List<T> getAll(Class<T> type);

    <ID, T extends Identity<ID>> void deleteById(ID id, Class<T> type);
}
