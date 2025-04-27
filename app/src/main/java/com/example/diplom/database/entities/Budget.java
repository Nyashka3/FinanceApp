package com.example.diplom.database.entities;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

/**
 * Сущность бюджета
 */
@Entity(tableName = "budgets")
public class Budget {
    @PrimaryKey(autoGenerate = true)
    private int id = 0;

    private String name;

    @ColumnInfo(name = "start_date")
    private Date startDate;

    @ColumnInfo(name = "end_date")
    private Date endDate;

    @ColumnInfo(name = "total_planned_income")
    private double totalPlannedIncome;

    @ColumnInfo(name = "total_planned_expense")
    private double totalPlannedExpense;

    private String status;

    private String notes;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public double getTotalPlannedIncome() {
        return totalPlannedIncome;
    }

    public void setTotalPlannedIncome(double totalPlannedIncome) {
        this.totalPlannedIncome = totalPlannedIncome;
    }

    public double getTotalPlannedExpense() {
        return totalPlannedExpense;
    }

    public void setTotalPlannedExpense(double totalPlannedExpense) {
        this.totalPlannedExpense = totalPlannedExpense;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
