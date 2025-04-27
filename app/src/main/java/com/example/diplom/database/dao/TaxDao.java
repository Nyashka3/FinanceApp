package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.diplom.database.entities.Tax;

import java.util.Date;
import java.util.List;

@Dao
public interface TaxDao extends BaseDao<Tax> {
    @Query("SELECT * FROM taxes WHERE id = :id")
    LiveData<Tax> getTaxById(int id);

    @Query("SELECT * FROM taxes ORDER BY due_date DESC")
    LiveData<List<Tax>> getAllTaxes();

    @Query("SELECT * FROM taxes WHERE status = :status ORDER BY due_date DESC")
    LiveData<List<Tax>> getTaxesByStatus(String status);

    @Query("SELECT * FROM taxes WHERE type = :type ORDER BY due_date DESC")
    LiveData<List<Tax>> getTaxesByType(String type);

    @Query("SELECT * FROM taxes WHERE status = :status AND type = :type ORDER BY due_date DESC")
    LiveData<List<Tax>> getTaxesByStatusAndType(String status, String type);

    @Query("SELECT * FROM taxes WHERE due_date BETWEEN :startDate AND :endDate ORDER BY due_date DESC")
    LiveData<List<Tax>> getTaxesBetweenDates(Date startDate, Date endDate);

    @Query("SELECT SUM(amount) FROM taxes WHERE due_date BETWEEN :startDate AND :endDate")
    LiveData<Double> getTotalTaxesBetweenDates(Date startDate, Date endDate);

    // EndDate в параметрах был
    @Query("SELECT * FROM taxes WHERE due_date >= :date AND (payment_date IS NULL OR payment_date > :date) ORDER BY due_date ASC")
    LiveData<List<Tax>> getUpcomingTaxes(Date date);
}