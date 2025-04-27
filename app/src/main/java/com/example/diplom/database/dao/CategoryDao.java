package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.diplom.database.entities.Category;

import java.util.List;

@Dao
public interface CategoryDao extends BaseDao<Category> {
    @Query("SELECT * FROM categories WHERE id = :id")
    LiveData<Category> getCategoryById(int id);

    @Query("SELECT * FROM categories WHERE is_expense = :isExpense ORDER BY name ASC")
    LiveData<List<Category>> getCategoriesByType(boolean isExpense);

    @Query("SELECT * FROM categories ORDER BY name ASC")
    LiveData<List<Category>> getAllCategories();
}