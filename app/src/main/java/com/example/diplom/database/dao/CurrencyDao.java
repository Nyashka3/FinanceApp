package com.example.diplom.database.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Query;

import com.example.diplom.database.entities.Currency;

import java.util.List;

@Dao
public interface CurrencyDao extends BaseDao<Currency> {

    @Query("SELECT * FROM currencies WHERE id = :id")
    LiveData<Currency> getCurrencyById(int id);

    @Query("SELECT * FROM currencies WHERE code = :code AND base_currency = :base")
    LiveData<Currency> getCurrencyByCode(String code, String base);

    @Query("SELECT * FROM currencies WHERE code = :code AND base_currency = :base")
    Currency getCurrencyByCodeSync(String code, String base);

    @Query("SELECT * FROM currencies ORDER BY name ASC")
    LiveData<List<Currency>> getAllCurrencies();

    @Query("SELECT * FROM currencies ORDER BY name ASC")
    List<Currency> getAllCurrenciesSync();

    @Query("SELECT * FROM currencies WHERE base_currency = :base ORDER BY name ASC")
    LiveData<List<Currency>> getAllCurrenciesByBase(String base);

    @Query("SELECT * FROM currencies WHERE base_currency = :base ORDER BY name ASC")
    List<Currency> getAllCurrenciesByBaseSync(String base);

    @Query("SELECT * FROM currencies WHERE code IN (:codes) ORDER BY name ASC")
    LiveData<List<Currency>> getCurrenciesByCodes(List<String> codes);

    @Query("SELECT * FROM currencies WHERE name LIKE '%' || :search || '%' OR code LIKE '%' || :search || '%' ORDER BY name ASC")
    LiveData<List<Currency>> searchCurrencies(String search);
}
