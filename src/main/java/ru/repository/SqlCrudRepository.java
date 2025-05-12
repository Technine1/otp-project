package ru.repository;

import ru.model.Identity;

import java.io.Closeable;
import java.io.IOException;
import java.util.List;

public class SqlCrudRepository implements CrudRepository, Closeable {

    @Override
    public <ID, T extends Identity<ID>> boolean save(T entity) {
        return false;
    }

    @Override
    public <ID, T extends Identity<ID>> T getById(ID id, Class<T> type) {
        return null;
    }

    @Override
    public <ID, T extends Identity<ID>> List<T> getByField(String field, Object value, Class<T> type) {
        return null;
    }


    @Override
    public <ID, T extends Identity<ID>> List<T> getAll(Class<T> type) {
        return null;
    }

    @Override
    public <ID, T extends Identity<ID>> void deleteById(ID id, Class<T> type) {

    }

    @Override
    public void close() throws IOException {

    }
}
