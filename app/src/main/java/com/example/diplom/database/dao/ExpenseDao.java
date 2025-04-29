package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.diplom.database.entities.Expense;

import java.util.Date;
import java.util.List;

@Dao
public interface ExpenseDao extends BaseDao<Expense> {
    @Query("SELECT * FROM expenses WHERE id = :id")
    LiveData<Expense> getExpenseById(int id);

    @Query("SELECT * FROM expenses ORDER BY expense_date DESC")
    LiveData<List<Expense>> getAllExpenses();

    @Query("SELECT * FROM expenses WHERE category_id = :categoryId ORDER BY expense_date DESC")
    LiveData<List<Expense>> getExpensesByCategory(int categoryId);

    @Query("SELECT * FROM expenses WHERE expense_date BETWEEN :startDate AND :endDate ORDER BY expense_date DESC")
    LiveData<List<Expense>> getExpensesBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE expense_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalExpensesBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE is_material_cost = 1 AND expense_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalMaterialCostsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE is_labor_cost = 1 AND expense_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalLaborCostsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE is_capital_cost = 1 AND expense_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalCapitalCostsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE is_energy_cost = 1 AND expense_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalEnergyCostsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM expenses WHERE is_other_cost = 1 AND expense_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalOtherCostsBetweenDates(Date startDate, Date endDate);

    @Query("SELECT * FROM expenses WHERE " +
            "(title LIKE '%' || :search || '%' OR " +
            "description LIKE '%' || :search || '%') " +
            "ORDER BY expense_date DESC")
    LiveData<List<Expense>> searchExpenses(String search);
}