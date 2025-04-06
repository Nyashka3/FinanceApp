package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import androidx.room.Transaction;

import com.example.diplom.database.entities.Income;

import java.util.Date;
import java.util.List;

@Dao
public interface IncomeDao {
    @Insert
    long insert(Income income);

    @Update
    void update(Income income);

    @Delete
    void delete(Income income);

    @Query("SELECT * FROM incomes WHERE id = :id")
    LiveData<Income> getIncomeById(int id);

    @Query("SELECT * FROM incomes ORDER BY income_date DESC")
    LiveData<List<Income>> getAllIncomes();

    @Query("SELECT * FROM incomes WHERE category_id = :categoryId ORDER BY income_date DESC")
    LiveData<List<Income>> getIncomesByCategory(int categoryId);

    @Query("SELECT * FROM incomes WHERE income_date BETWEEN :startDate AND :endDate ORDER BY income_date DESC")
    LiveData<List<Income>> getIncomesBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM incomes WHERE income_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalIncomesBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM incomes WHERE is_product_revenue = 1 AND income_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalProductRevenueBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM incomes WHERE title LIKE '%' || :search || '%' OR description LIKE '%' || :search || '%' ORDER BY income_date DESC")
    LiveData<List<Income>> searchIncomes(String search);
}