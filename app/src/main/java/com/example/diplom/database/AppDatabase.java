package com.example.diplom.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.diplom.database.dao.CategoryDao;
import com.example.diplom.database.dao.CurrencyDao;
import com.example.diplom.database.dao.ExpenseDao;
import com.example.diplom.database.entities.Category;
import com.example.diplom.database.entities.Currency;
import com.example.diplom.database.entities.Expense;
import com.example.diplom.utils.DateConverter;
import com.example.diplom.utils.ExpenseCategoryUtils;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Category.class, Expense.class, Currency.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "budget_optimizer_db";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile AppDatabase INSTANCE;

    public abstract CategoryDao categoryDao();
    public abstract ExpenseDao expenseDao();
    public abstract CurrencyDao currencyDao();

    public static AppDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                                    context.getApplicationContext(),
                                    AppDatabase.class,
                                    DATABASE_NAME)
                            .addCallback(sRoomDatabaseCallback)
                            .build();
                }
            }
        }
        return INSTANCE;
    }

    private static final RoomDatabase.Callback sRoomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);

            databaseWriteExecutor.execute(() -> {
                // Заполнение начальными данными
                AppDatabase database = INSTANCE;
                if (database != null) {
                    populateInitialData(database);
                }
            });
        }
    };

    private static void populateInitialData(AppDatabase database) {
        // Создаем начальные категории
        populateCategories(database);

        // Создаем начальные валюты
        populateCurrencies(database);
    }

    private static void populateCategories(AppDatabase database) {
        CategoryDao categoryDao = database.categoryDao();

        // Создание экономически верных категорий расходов
        for (Category category : ExpenseCategoryUtils.getDefaultExpenseCategories()) {
            categoryDao.insert(category);
        }

        // Создание категорий доходов
        Category productSalesCat = new Category();
        productSalesCat.setName("Покупки");
        productSalesCat.setDescription("Затраты от покупки");
        productSalesCat.setExpense(false);
        productSalesCat.setIcon("ic_sales");
        productSalesCat.setColor("#2ecc71");
        productSalesCat.setCreatedAt(new Date());
        categoryDao.insert(productSalesCat);

        Category servicesCat = new Category();
        servicesCat.setName("Услуги");
        servicesCat.setDescription("Затраты от использования услуг");
        servicesCat.setExpense(false);
        servicesCat.setIcon("ic_services");
        servicesCat.setColor("#1abc9c");
        servicesCat.setCreatedAt(new Date());
        categoryDao.insert(servicesCat);

        Category investmentsCat = new Category();
        investmentsCat.setName("Инвестиции");
        investmentsCat.setDescription("Затраты от инвестиционной деятельности");
        investmentsCat.setExpense(false);
        investmentsCat.setIcon("ic_investments");
        investmentsCat.setColor("#3498db");
        investmentsCat.setCreatedAt(new Date());
        categoryDao.insert(investmentsCat);
    }

    private static void populateCurrencies(AppDatabase database) {
        CurrencyDao currencyDao = database.currencyDao();
        Date currentDate = new Date();

        // Создание начальных валют

        // 1. Российский рубль (RUB)
        Currency rub = new Currency();
        rub.setCode("RUB");
        rub.setName("Российский рубль");
        rub.setRate(1.0); // Базовая валюта
        rub.setBaseCurrency("RUB");
        rub.setTrend("stable");
        rub.setChange(0.0);
        rub.setChangePercentage(0.0);
        rub.setIconUrl("ic_currency_rub");
        rub.setUpdatedAt(currentDate);
        currencyDao.insert(rub);

        // 2. Евро (EUR)
        Currency eur = new Currency();
        eur.setCode("EUR");
        eur.setName("Евро");
        eur.setRate(1.0); // Базовая валюта
        eur.setBaseCurrency("EUR");
        eur.setTrend("stable");
        eur.setChange(0.0);
        eur.setChangePercentage(0.0);
        eur.setIconUrl("ic_currency_eur");
        eur.setUpdatedAt(currentDate);
        currencyDao.insert(eur);

        // 3. Доллар США (USD)
        Currency usd = new Currency();
        usd.setCode("USD");
        usd.setName("Доллар США");
        usd.setRate(1.0); // Базовая валюта
        usd.setBaseCurrency("USD");
        usd.setTrend("stable");
        usd.setChange(0.0);
        usd.setChangePercentage(0.0);
        usd.setIconUrl("ic_currency_usd");
        usd.setUpdatedAt(currentDate);
        currencyDao.insert(usd);
    }
}