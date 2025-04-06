package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.diplom.database.entities.Currency;

import java.util.List;

@Dao
public interface CurrencyDao {
    @Insert
    long insert(Currency currency);

    @Update
    void update(Currency currency);

    @Delete
    void delete(Currency currency);

    @Query("SELECT * FROM currencies WHERE id = :id")
    LiveData<Currency> getCurrencyById(int id);

    @Query("SELECT * FROM currencies WHERE code = :code")
    LiveData<Currency> getCurrencyByCode(String code);

    @Query("SELECT * FROM currencies WHERE code = :code")
    Currency getCurrencyByCodeSync(String code);

    @Query("SELECT * FROM currencies ORDER BY name ASC")
    LiveData<List<Currency>> getAllCurrencies();

    @Query("SELECT * FROM currencies ORDER BY name ASC")
    List<Currency> getAllCurrenciesSync();

    @Query("SELECT * FROM currencies WHERE code IN (:codes) ORDER BY name ASC")
    LiveData<List<Currency>> getCurrenciesByCodes(List<String> codes);

    @Query("SELECT * FROM currencies WHERE name LIKE '%' || :search || '%' OR code LIKE '%' || :search || '%' ORDER BY name ASC")
    LiveData<List<Currency>> searchCurrencies(String search);
}
