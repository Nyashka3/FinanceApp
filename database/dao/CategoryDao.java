package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.diplom.database.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao {
    @Insert
    long insert(Category category);

    @Update
    void update(Category category);

    @Delete
    void delete(Category category);

    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(int id);

    @Query("SELECT * FROM categories WHERE is_expense = :isExpense ORDER BY name ASC")
    LiveData<List<Category>> getCategoriesByType(boolean isExpense);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();
}