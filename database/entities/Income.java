package com.example.diplom.database.entities;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.ColumnInfo;
import androidx.room.ForeignKey;
import androidx.room.Index;

import java.util.Date;

/**
 * Сущность дохода
 */
@Entity(
        tableName = "incomes",
        foreignKeys = @ForeignKey(
                entity = Category.class,
                parentColumns = "id",
                childColumns = "category_id",
                onDelete = ForeignKey.SET_NULL
        ),
        indices = @Index("category_id")
)
public class Income {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;

    private double amount;

    private String description;

    @ColumnInfo(name = "income_date")
    private Date incomeDate;

    @ColumnInfo(name = "category_id")
    private Integer categoryId;

    @ColumnInfo(name = "is_product_revenue")
    private boolean isProductRevenue;

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

    public Date getIncomeDate() {
        return incomeDate;
    }

    public void setIncomeDate(Date incomeDate) {
        this.incomeDate = incomeDate;
    }

    public Integer getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Integer categoryId) {
        this.categoryId = categoryId;
    }

    public boolean isProductRevenue() {
        return isProductRevenue;
    }

    public void setProductRevenue(boolean productRevenue) {
        isProductRevenue = productRevenue;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
}
