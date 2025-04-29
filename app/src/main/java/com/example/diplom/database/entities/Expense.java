package com.example.diplom.database.entities;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

/**
 * Сущность расхода
 */
@Entity(
        tableName = "expenses",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = @Index("category_id")
)
public class Expense {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private double amount;

    private String description;

    @ColumnInfo(name = "expense_date")
    private Date expenseDate;

    @ColumnInfo(name = "category_id")
    private Integer categoryId;

    @ColumnInfo(name = "is_material_cost")
    private boolean isMaterialCost;

    @ColumnInfo(name = "is_labor_cost")
    private boolean isLaborCost;

    @ColumnInfo(name = "is_capital_cost")
    private boolean isCapitalCost;

    @ColumnInfo(name = "is_energy_cost")
    private boolean isEnergyCost;

    @ColumnInfo(name = "is_other_cost")
    private boolean isOtherCost;

    @ColumnInfo(name = "created_at")
    private Date createdAt;

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(Date expenseDate) {
        this.expenseDate = expenseDate;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isMaterialCost() {
        return isMaterialCost;
    }

    public void setMaterialCost(boolean materialCost) {
        isMaterialCost = materialCost;
    }

    public boolean isLaborCost() {
        return isLaborCost;
    }

    public void setLaborCost(boolean laborCost) {
        isLaborCost = laborCost;
    }

    public boolean isCapitalCost() {
        return isCapitalCost;
    }

    public void setCapitalCost(boolean capitalCost) {
        isCapitalCost = capitalCost;
    }

    public boolean isEnergyCost() {
        return isEnergyCost;
    }

    public void setEnergyCost(boolean energyCost) {
        isEnergyCost = energyCost;
    }

    public boolean isOtherCost() {
        return isOtherCost;
    }

    public void setOtherCost(boolean otherCost) {
        isOtherCost = otherCost;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}