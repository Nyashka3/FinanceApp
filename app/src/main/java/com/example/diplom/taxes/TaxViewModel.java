package com.example.diplom.taxes;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.diplom.database.AppDatabase;
import com.example.diplom.database.dao.TaxDao;
import com.example.diplom.database.entities.Tax;
import com.example.diplom.models.TaxCalculator;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * ViewModel для работы с налогами
 */
public class TaxViewModel extends AndroidViewModel {

    private final TaxDao taxDao;
    private final TaxCalculator taxCalculator;

    private final MutableLiveData<Date> currentDate;
    private final MutableLiveData<String> filterStatus;
    private final MutableLiveData<String> filterType;

    private final LiveData<List<Tax>> allTaxes;
    private final LiveData<List<Tax>> filteredTaxes;
    private final LiveData<List<Tax>> upcomingTaxes;
    private final LiveData<Double> totalTaxesForCurrentPeriod;

    public TaxViewModel(@NonNull Application application) {
        super(application);

        // Инициализация DAO
        AppDatabase database = AppDatabase.getDatabase(application);
        taxDao = database.taxDao();

        // Инициализация калькулятора налогов
        taxCalculator = new TaxCalculator();

        // Инициализация LiveData
        currentDate = new MutableLiveData<>(new Date());
        filterStatus = new MutableLiveData<>("ALL");
        filterType = new MutableLiveData<>("ALL");

        // Получение всех налогов
        allTaxes = taxDao.getAllTaxes();

        // Применение фильтров к налогам
        filteredTaxes = Transformations.switchMap(filterStatus, status ->
                Transformations.switchMap(filterType, type -> {
                    if ("ALL".equals(status) && "ALL".equals(type)) {
                        return allTaxes;
                    } else if ("ALL".equals(status)) {
                        return taxDao.getTaxesByType(type);
                    } else if ("ALL".equals(type)) {
                        return taxDao.getTaxesByStatus(status);
                    } else {
                        return taxDao.getTaxesByStatusAndType(status, type);
                    }
                }));

        // Получение предстоящих налогов
        upcomingTaxes = Transformations.switchMap(currentDate, date -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.add(Calendar.MONTH, 3); // налоги на ближайшие 3 месяца
            Date endDate = calendar.getTime();
            return taxDao.getUpcomingTaxes(date, endDate);
        });

        // Расчет общей суммы налогов за текущий период
        totalTaxesForCurrentPeriod = Transformations.switchMap(currentDate, date -> {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startDate = calendar.getTime();

            calendar.add(Calendar.MONTH, 1);
            calendar.add(Calendar.MILLISECOND, -1);
            Date endDate = calendar.getTime();

            return taxDao.getTotalTaxesBetweenDates(startDate, endDate);
        });
    }

    /**
     * Установка фильтра по статусу
     * @param status статус налога
     */
    public void setFilterStatus(String status) {
        filterStatus.setValue(status);
    }

    /**
     * Установка фильтра по типу
     * @param type тип налога
     */
    public void setFilterType(String type) {
        filterType.setValue(type);
    }

    /**
     * Установка текущей даты
     * @param date текущая дата
     */
    public void setCurrentDate(Date date) {
        currentDate.setValue(date);
    }

    /**
     * Добавление нового налога
     * @param tax налог для добавления
     */
    public void insert(Tax tax) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taxDao.insert(tax);
        });
    }

    /**
     * Обновление налога
     * @param tax налог для обновления
     */
    public void update(Tax tax) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taxDao.update(tax);
        });
    }

    /**
     * Удаление налога
     * @param tax налог для удаления
     */
    public void delete(Tax tax) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            taxDao.delete(tax);
        });
    }

    /**
     * Расчет налога УСН "Доходы"
     * @param income общий доход
     * @return сумма налога
     */
    public double calculateUsnIncomeTax(double income) {
        return taxCalculator.calculateUsnIncomeTax(income);
    }

    /**
     * Расчет налога УСН "Доходы минус расходы"
     * @param income общий доход
     * @param expenses общие расходы
     * @return сумма налога
     */
    public double calculateUsnIncomeExpenseTax(double income, double expenses) {
        return taxCalculator.calculateUsnIncomeExpenseTax(income, expenses);
    }

    /**
     * Расчет налога по патентной системе
     * @param potentialYearlyIncome потенциально возможный годовой доход
     * @param periodInMonths период действия патента (в месяцах)
     * @return сумма налога
     */
    public double calculatePatentTax(double potentialYearlyIncome, int periodInMonths) {
        return taxCalculator.calculatePatentTax(potentialYearlyIncome, periodInMonths);
    }

    /**
     * Расчет страховых взносов
     * @param salary зарплата
     * @return карта взносов по типам
     */
    public Map<String, Double> calculateInsuranceContributions(double salary) {
        return taxCalculator.calculateInsuranceContributions(salary);
    }

    /**
     * Расчет НДФЛ
     * @param salary зарплата
     * @return сумма НДФЛ
     */
    public double calculateIncomeTax(double salary) {
        return taxCalculator.calculateIncomeTax(salary);
    }

    /**
     * Получение всех налогов
     * @return LiveData список всех налогов
     */
    public LiveData<List<Tax>> getAllTaxes() {
        return allTaxes;
    }

    /**
     * Получение отфильтрованных налогов
     * @return LiveData список отфильтрованных налогов
     */
    public LiveData<List<Tax>> getFilteredTaxes() {
        return filteredTaxes;
    }

    /**
     * Получение предстоящих налогов
     * @return LiveData список предстоящих налогов
     */
    public LiveData<List<Tax>> getUpcomingTaxes() {
        return upcomingTaxes;
    }

    /**
     * Получение общей суммы налогов за текущий период
     * @return LiveData с общей суммой налогов
     */
    public LiveData<Double> getTotalTaxesForCurrentPeriod() {
        return totalTaxesForCurrentPeriod;
    }

    /**
     * Получение конкретного налога по id
     * @param id идентификатор налога
     * @return LiveData с налогом
     */
    public LiveData<Tax> getTaxById(int id) {
        return taxDao.getTaxById(id);
    }
}
