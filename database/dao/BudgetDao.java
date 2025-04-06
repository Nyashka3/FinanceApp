package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.diplom.database.entities.Budget;

import java.util.Date;
import java.util.List;

@Dao
public interface BudgetDao {
    @Insert
    long insert(Budget budget);

    @Update
    void update(Budget budget);

    @Delete
    void delete(Budget budget);

    @Query("SELECT * FROM budgets WHERE id = :id")
    LiveData<Budget> getBudgetById(int id);

    @Query("SELECT * FROM budgets ORDER BY start_date DESC")
    LiveData<List<Budget>> getAllBudgets();

    @Query("SELECT * FROM budgets WHERE status = :status ORDER BY start_date DESC")
    LiveData<List<Budget>> getBudgetsByStatus(String status);

    @Query("SELECT * FROM budgets WHERE start_date <= :date AND end_date >= :date LIMIT 1")
    LiveData<Budget> getCurrentBudget(Date date);

    @Query("SELECT * FROM budgets WHERE end_date < :date ORDER BY end_date DESC LIMIT 1")
    LiveData<Budget> getLastBudget(Date date);
}
