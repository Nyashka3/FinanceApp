package com.example.diplom.analytics;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.diplom.database.AppDatabase;
import com.example.diplom.database.dao.ExpenseDao;
import com.example.diplom.database.dao.IncomeDao;
import com.example.diplom.models.EfficiencyModel;
import com.example.diplom.utils.DateUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * ViewModel для аналитики, содержит бизнес-логику и данные для UI.
 */
public class AnalyticsViewModel extends AndroidViewModel {

    private final ExpenseDao expenseDao;
    private final IncomeDao incomeDao;

    private final MutableLiveData<Date> startDate;
    private final MutableLiveData<Date> endDate;

    private final LiveData<Double> totalExpenses;
    private final LiveData<Double> totalIncome;
    private final MediatorLiveData<Double> balance;

    private final LiveData<Double> materialCosts;
    private final LiveData<Double> laborCosts;
    private final LiveData<Double> productRevenue;

    private final MediatorLiveData<Double> materialIntensity;
    private final MediatorLiveData<Double> laborIntensity;
    private final MediatorLiveData<String> productionType;

    private final EfficiencyModel efficiencyModel;

    public AnalyticsViewModel(@NonNull Application application) {
        super(application);

        // Получение DAO
        AppDatabase database = AppDatabase.getDatabase(application);
        expenseDao = database.expenseDao();
        incomeDao = database.incomeDao();

        // Инициализация модели для расчетов эффективности
        efficiencyModel = new EfficiencyModel();

        // Настройка периода анализа (по умолчанию текущий месяц)
        startDate = new MutableLiveData<>(DateUtils.getStartOfCurrentMonth());
        endDate = new MutableLiveData<>(DateUtils.getEndOfCurrentMonth());

        // Наблюдение за расходами и доходами в выбранном периоде
        totalExpenses = Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        expenseDao.getTotalExpensesBetweenDates(start, end)));

        totalIncome = Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        incomeDao.getTotalIncomesBetweenDates(start, end)));

        // Настройка MediatorLiveData для баланса
        balance = new MediatorLiveData<>();
        balance.addSource(totalIncome, income -> calculateBalance());
        balance.addSource(totalExpenses, expenses -> calculateBalance());

        // Наблюдение за материальными и трудовыми затратами
        materialCosts = Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        expenseDao.getTotalMaterialCostsBetweenDates(start, end)));

        laborCosts = Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        expenseDao.getTotalLaborCostsBetweenDates(start, end)));

        // Наблюдение за выручкой от продукции
        productRevenue = Transformations.switchMap(startDate, start ->
                Transformations.switchMap(endDate, end ->
                        incomeDao.getTotalProductRevenueBetweenDates(start, end)));

        // Настройка MediatorLiveData для материалоемкости
        materialIntensity = new MediatorLiveData<>();
        materialIntensity.addSource(materialCosts, cost -> calculateMaterialIntensity());
        materialIntensity.addSource(productRevenue, revenue -> calculateMaterialIntensity());

        // Настройка MediatorLiveData для трудоемкости
        laborIntensity = new MediatorLiveData<>();
        laborIntensity.addSource(laborCosts, cost -> calculateLaborIntensity());
        laborIntensity.addSource(productRevenue, revenue -> calculateLaborIntensity());

        // Настройка MediatorLiveData для типа производства
        productionType = new MediatorLiveData<>();
        productionType.addSource(materialIntensity, intensity -> determineProductionType());
        productionType.addSource(laborIntensity, intensity -> determineProductionType());
    }

    /**
     * Устанавливает период анализа
     * @param start начальная дата
     * @param end конечная дата
     */
    public void setAnalysisPeriod(Date start, Date end) {
        startDate.setValue(start);
        endDate.setValue(end);
    }

    /**
     * Вычисляет баланс на основе доходов и расходов
     */
    private void calculateBalance() {
        Double expenses = totalExpenses.getValue();
        Double income = totalIncome.getValue();

        if (expenses == null) {
            expenses = 0.0;
        }

        if (income == null) {
            income = 0.0;
        }

        balance.setValue(income - expenses);
    }

    /**
     * Вычисляет материалоемкость производства
     */
    private void calculateMaterialIntensity() {
        Double costs = materialCosts.getValue();
        Double revenue = productRevenue.getValue();

        if (costs == null) {
            costs = 0.0;
        }

        if (revenue == null || revenue == 0.0) {
            materialIntensity.setValue(0.0);
            return;
        }

        try {
            double intensity = efficiencyModel.calculateMaterialIntensity(costs, revenue);
            materialIntensity.setValue(intensity);
        } catch (IllegalArgumentException e) {
            materialIntensity.setValue(0.0);
        }
    }

    /**
     * Вычисляет трудоемкость производства
     */
    private void calculateLaborIntensity() {
        Double costs = laborCosts.getValue();
        Double revenue = productRevenue.getValue();

        if (costs == null) {
            costs = 0.0;
        }

        if (revenue == null || revenue == 0.0) {
            laborIntensity.setValue(0.0);
            return;
        }

        try {
            double intensity = efficiencyModel.calculateLaborIntensity(costs, revenue);
            laborIntensity.setValue(intensity);
        } catch (IllegalArgumentException e) {
            laborIntensity.setValue(0.0);
        }
    }

    /**
     * Определяет тип производства на основе материалоемкости и трудоемкости
     */
    private void determineProductionType() {
        Double material = materialIntensity.getValue();
        Double labor = laborIntensity.getValue();

        if (material == null || labor == null) {
            productionType.setValue("Недостаточно данных");
            return;
        }

        String type = efficiencyModel.determineProductionType(material, labor);
        productionType.setValue(type);
    }

    /**
     * Возвращает данные для диаграммы расходов по категориям
     * @return карта с данными для диаграммы (категория -> сумма)
     */
    public LiveData<Map<String, Double>> getExpensesByCategory() {
        // Заглушка для примера. В реальном приложении здесь должна быть
        // логика получения данных из базы данных
        MutableLiveData<Map<String, Double>> result = new MutableLiveData<>();
        Map<String, Double> data = new HashMap<>();
        data.put("Материалы", 45000.0);
        data.put("Оплата труда", 35000.0);
        data.put("Аренда", 15000.0);
        data.put("Коммунальные услуги", 8000.0);
        data.put("Налоги", 12000.0);
        data.put("Прочее", 5000.0);
        result.setValue(data);
        return result;
    }

    /**
     * Получает прогноз расходов на следующий период
     * @return прогнозируемая сумма расходов
     */
    public LiveData<Double> getExpensesForecast() {
        // Заглушка для примера
        MutableLiveData<Double> forecast = new MutableLiveData<>();
        forecast.setValue(125000.0);
        return forecast;
    }

    // Геттеры для LiveData
    public LiveData<Double> getTotalExpenses() {
        return totalExpenses;
    }

    public LiveData<Double> getTotalIncome() {
        return totalIncome;
    }

    public LiveData<Double> getBalance() {
        return balance;
    }

    public LiveData<Double> getMaterialCosts() {
        return materialCosts;
    }

    public LiveData<Double> getLaborCosts() {
        return laborCosts;
    }

    public LiveData<Double> getProductRevenue() {
        return productRevenue;
    }

    public LiveData<Double> getMaterialIntensity() {
        return materialIntensity;
    }

    public LiveData<Double> getLaborIntensity() {
        return laborIntensity;
    }

    public LiveData<String> getProductionType() {
        return productionType;
    }
}
