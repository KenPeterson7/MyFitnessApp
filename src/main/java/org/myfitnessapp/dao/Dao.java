package org.myfitnessapp.dao;


import java.util.Map;

public interface Dao<T> {
    T get(long id);

    String create(T object);

    boolean update(long id, T object);

    boolean partialUpdate(long id, Map<String, Object> updates);

    void delete(long id);
}
