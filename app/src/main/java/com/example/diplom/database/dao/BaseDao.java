package com.example.diplom.database.dao;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Update;

public interface BaseDao<T> {
    @Insert
    long insert(T item);

    @Update
    void update(T item);

    @Delete
    void delete(T item);
}
