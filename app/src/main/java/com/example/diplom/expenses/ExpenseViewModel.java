package com.example.diplom.expenses;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;

import com.example.diplom.database.AppDatabase;
import com.example.diplom.database.dao.CategoryDao;
import com.example.diplom.database.dao.ExpenseDao;
import com.example.diplom.database.entities.Category;
import com.example.diplom.database.entities.Expense;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

/**
 * ViewModel для управления расходами
 */
public class ExpenseViewModel extends AndroidViewModel {

    private final ExpenseDao expenseDao;
    private final CategoryDao categoryDao;

    private final LiveData<List<Expense>> allExpenses;
    private final LiveData<List<Category>> allCategories;

    private final MutableLiveData<String> filterCategory = new MutableLiveData<>();
    private final MutableLiveData<Date> filterStartDate = new MutableLiveData<>();
    private final MutableLiveData<Date> filterEndDate = new MutableLiveData<>();
    private final MutableLiveData<String> filterSearch = new MutableLiveData<>();
    private final MutableLiveData<Integer> sortOrder = new MutableLiveData<>();
    private final MutableLiveData<Boolean> dataRefresh = new MutableLiveData<>(false);

    private final MediatorLiveData<List<Expense>> filteredExpenses = new MediatorLiveData<>();
    private final MediatorLiveData<Double> totalFilteredExpenses = new MediatorLiveData<>();

    // Константы для типов сортировки
    public static final int SORT_DATE_DESC = 0; // По дате (по убыванию)
    public static final int SORT_DATE_ASC = 1;  // По дате (по возрастанию)
    public static final int SORT_AMOUNT_DESC = 2; // По сумме (по убыванию)
    public static final int SORT_AMOUNT_ASC = 3;  // По сумме (по возрастанию)
    public static final int SORT_TITLE_ASC = 4;  // По названию (A-Z)
    public static final int SORT_TITLE_DESC = 5;  // По названию (Z-A)

    public ExpenseViewModel(@NonNull Application application) {
        super(application);

        // Инициализация DAO
        AppDatabase database = AppDatabase.getDatabase(application);
        expenseDao = database.expenseDao();
        categoryDao = database.categoryDao();

        // Получение всех расходов и категорий
        allExpenses = expenseDao.getAllExpenses();
        allCategories = categoryDao.getCategoriesByType(true); // true = расходные категории

        // Установка значений по умолчанию
        sortOrder.setValue(SORT_DATE_DESC);
        filterSearch.setValue("");

        // Настройка MediatorLiveData для обработки фильтров и сортировки
        filteredExpenses.addSource(allExpenses, expenses -> applyFiltersAndSort());
        filteredExpenses.addSource(filterCategory, category -> applyFiltersAndSort());
        filteredExpenses.addSource(filterStartDate, date -> applyFiltersAndSort());
        filteredExpenses.addSource(filterEndDate, date -> applyFiltersAndSort());
        filteredExpenses.addSource(filterSearch, search -> applyFiltersAndSort());
        filteredExpenses.addSource(sortOrder, order -> applyFiltersAndSort());
        filteredExpenses.addSource(dataRefresh, refresh -> applyFiltersAndSort());

        // Расчет общей суммы отфильтрованных расходов
        totalFilteredExpenses.addSource(filteredExpenses, expenses -> {
            if (expenses != null) {
                double total = 0;
                for (Expense expense : expenses) {
                    total += expense.getAmount();
                }
                totalFilteredExpenses.setValue(total);
            } else {
                totalFilteredExpenses.setValue(0.0);
            }
        });
    }

    /**
     * Обновляет данные (используется при возвращении на экран расходов)
     */
    public void refreshData() {
        dataRefresh.setValue(!dataRefresh.getValue());
    }

    /**
     * Применение фильтров и сортировки к списку расходов
     */
    private void applyFiltersAndSort() {
        List<Expense> expenses = allExpenses.getValue();
        if (expenses == null) {
            filteredExpenses.setValue(new ArrayList<>());
            return;
        }

        List<Expense> result = new ArrayList<>(expenses);

        // Применение фильтра по категории
        String category = filterCategory.getValue();
        if (category != null && !category.isEmpty()) {
            // Получаем ID категории по имени
            Integer categoryId = getCategoryIdByName(category);
            if (categoryId != -1) {
                result.removeIf(expense -> expense.getCategoryId() == null || !expense.getCategoryId().equals(categoryId));
            }
        }

        // Применение фильтра по диапазону дат
        Date startDate = filterStartDate.getValue();
        Date endDate = filterEndDate.getValue();
        if (startDate != null && endDate != null) {
            result.removeIf(expense -> {
                Date expenseDate = expense.getExpenseDate();
                return expenseDate == null || expenseDate.before(startDate) || expenseDate.after(endDate);
            });
        }

        // Применение фильтра по поиску
        String search = filterSearch.getValue();
        if (search != null && !search.isEmpty()) {
            String searchLower = search.toLowerCase();
            result.removeIf(expense ->
                    (expense.getTitle() == null || !expense.getTitle().toLowerCase().contains(searchLower)) &&
                            (expense.getDescription() == null || !expense.getDescription().toLowerCase().contains(searchLower))
            );
        }

        // Применение сортировки
        Integer currentSortOrder = sortOrder.getValue();
        if (currentSortOrder != null) {
            switch (currentSortOrder) {
                case SORT_DATE_DESC:
                    Collections.sort(result, (e1, e2) -> {
                        if (e1.getExpenseDate() == null || e2.getExpenseDate() == null) {
                            return 0;
                        }
                        return e2.getExpenseDate().compareTo(e1.getExpenseDate());
                    });
                    break;
                case SORT_DATE_ASC:
                    Collections.sort(result, (e1, e2) -> {
                        if (e1.getExpenseDate() == null || e2.getExpenseDate() == null) {
                            return 0;
                        }
                        return e1.getExpenseDate().compareTo(e2.getExpenseDate());
                    });
                    break;
                case SORT_AMOUNT_DESC:
                    Collections.sort(result, (e1, e2) -> Double.compare(e2.getAmount(), e1.getAmount()));
                    break;
                case SORT_AMOUNT_ASC:
                    Collections.sort(result, (e1, e2) -> Double.compare(e1.getAmount(), e2.getAmount()));
                    break;
                case SORT_TITLE_ASC:
                    Collections.sort(result, (e1, e2) -> {
                        if (e1.getTitle() == null || e2.getTitle() == null) {
                            return 0;
                        }
                        return e1.getTitle().compareToIgnoreCase(e2.getTitle());
                    });
                    break;
                case SORT_TITLE_DESC:
                    Collections.sort(result, (e1, e2) -> {
                        if (e1.getTitle() == null || e2.getTitle() == null) {
                            return 0;
                        }
                        return e2.getTitle().compareToIgnoreCase(e1.getTitle());
                    });
                    break;
            }
        }

        filteredExpenses.setValue(result);
    }

    /**
     * Получение ID категории по имени
     * @param categoryName имя категории
     * @return ID категории или null, если не найдена
     */
    private Integer getCategoryIdByName(String categoryName) {
        List<Category> categories = allCategories.getValue();
        if (categories != null) {
            for (Category category : categories) {
                if (category.getName().equals(categoryName)) {
                    return category.getId();
                }
            }
        }
        return -1;
    }

    /**
     * Получение категории по ID
     * @param categoryId ID категории
     * @return категория или null, если не найдена
     */
    private Category getCategoryById(Integer categoryId) {
        if (categoryId == null) {
            return null;
        }

        List<Category> categories = allCategories.getValue();
        if (categories == null) {
            return null;
        }

        for (Category category : categories) {
            if (category.getId() == categoryId) {
                return category;
            }
        }

        return null;
    }

    /**
     * Устанавливает фильтр по категории
     * @param category название категории или null для сброса фильтра
     */
    public void setFilterCategory(String category) {
        filterCategory.setValue(category);
    }

    /**
     * Устанавливает фильтр по диапазону дат
     * @param startDate начальная дата
     * @param endDate конечная дата
     */
    public void setFilterDateRange(Date startDate, Date endDate) {
        filterStartDate.setValue(startDate);
        filterEndDate.setValue(endDate);
    }

    /**
     * Устанавливает фильтр по поисковому запросу
     * @param search поисковый запрос
     */
    public void setFilterSearch(String search) {
        filterSearch.setValue(search);
    }

    /**
     * Устанавливает порядок сортировки
     * @param order порядок сортировки
     */
    public void setSortOrder(int order) {
        sortOrder.setValue(order);
    }

    /**
     * Сбрасывает все фильтры
     */
    public void resetFilters() {
        filterCategory.setValue(null);
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        filterSearch.setValue("");
        sortOrder.setValue(SORT_DATE_DESC);
    }

    /**
     * Добавляет новый расход
     * @param expense расход для добавления
     */
    public void insert(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.insert(expense);
            // Обновляем данные после вставки
//            refreshData();
        });
    }

    /**
     * Обновляет существующий расход
     * @param expense расход для обновления
     */
    public void update(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.update(expense);
            // Обновляем данные после обновления
//            refreshData();
        });
    }

    /**
     * Удаляет расход
     * @param expense расход для удаления
     */
    public void delete(Expense expense) {
        AppDatabase.databaseWriteExecutor.execute(() -> {
            expenseDao.delete(expense);
            // Обновляем данные после удаления
//            refreshData();
        });
    }

    /**
     * Получает расход по ID
     * @param id ID расхода
     * @return LiveData с расходом
     */
    public LiveData<Expense> getExpenseById(int id) {
        return expenseDao.getExpenseById(id);
    }

    /**
     * Получает все расходы
     * @return LiveData со списком всех расходов
     */
    public LiveData<List<Expense>> getAllExpenses() {
        return allExpenses;
    }

    /**
     * Получает отфильтрованные расходы
     * @return LiveData со списком отфильтрованных расходов
     */
    public LiveData<List<Expense>> getFilteredExpenses() {
        return filteredExpenses;
    }

    /**
     * Получает общую сумму отфильтрованных расходов
     * @return LiveData с общей суммой
     */
    public LiveData<Double> getTotalFilteredExpenses() {
        return totalFilteredExpenses;
    }

    /**
     * Получает все категории расходов
     * @return LiveData со списком категорий
     */
    public LiveData<List<Category>> getAllCategories() {
        return allCategories;
    }
}