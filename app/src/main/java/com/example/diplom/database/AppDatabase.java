package com.example.diplom.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.example.diplom.database.dao.BudgetDao;
import com.example.diplom.database.dao.CategoryDao;
import com.example.diplom.database.dao.CurrencyDao;
import com.example.diplom.database.dao.ExpenseDao;
import com.example.diplom.database.dao.IncomeDao;
import com.example.diplom.database.dao.TaxDao;
import com.example.diplom.database.entities.Budget;
import com.example.diplom.database.entities.Category;
import com.example.diplom.database.entities.Currency;
import com.example.diplom.database.entities.Expense;
import com.example.diplom.database.entities.Income;
import com.example.diplom.database.entities.Tax;
import com.example.diplom.utils.DateConverter;

import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {Budget.class, Category.class, Expense.class, Income.class, Tax.class, Currency.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({DateConverter.class})
public abstract class AppDatabase extends RoomDatabase {

    private static final String DATABASE_NAME = "budget_optimizer_db";
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    private static volatile AppDatabase INSTANCE;

    public abstract BudgetDao budgetDao();
    public abstract CategoryDao categoryDao();
    public abstract ExpenseDao expenseDao();
    public abstract IncomeDao incomeDao();
    public abstract TaxDao taxDao();
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

        // Создание категорий расходов
        Category materialsCat = new Category();
        materialsCat.setName("Материалы");
        materialsCat.setExpense(true);
        materialsCat.setIcon("ic_material");
        materialsCat.setColor("#3498db");
        materialsCat.setCreatedAt(new Date());
        categoryDao.insert(materialsCat);

        Category laborCat = new Category();
        laborCat.setName("Оплата труда");
        laborCat.setExpense(true);
        laborCat.setIcon("ic_labor");
        laborCat.setColor("#e74c3c");
        laborCat.setCreatedAt(new Date());
        categoryDao.insert(laborCat);

        Category utilitiesCat = new Category();
        utilitiesCat.setName("Коммунальные услуги");
        utilitiesCat.setExpense(true);
        utilitiesCat.setIcon("ic_utilities");
        utilitiesCat.setColor("#9b59b6");
        utilitiesCat.setCreatedAt(new Date());
        categoryDao.insert(utilitiesCat);

        Category rentCat = new Category();
        rentCat.setName("Аренда");
        rentCat.setExpense(true);
        rentCat.setIcon("ic_rent");
        rentCat.setColor("#f39c12");
        rentCat.setCreatedAt(new Date());
        categoryDao.insert(rentCat);

        Category taxesCat = new Category();
        taxesCat.setName("Налоги");
        taxesCat.setExpense(true);
        taxesCat.setIcon("ic_taxes");
        taxesCat.setColor("#27ae60");
        taxesCat.setCreatedAt(new Date());
        categoryDao.insert(taxesCat);

        // Создание категорий доходов
        Category productSalesCat = new Category();
        productSalesCat.setName("Продажи продукции");
        productSalesCat.setExpense(false);
        productSalesCat.setIcon("ic_sales");
        productSalesCat.setColor("#2ecc71");
        productSalesCat.setCreatedAt(new Date());
        categoryDao.insert(productSalesCat);

        Category servicesCat = new Category();
        servicesCat.setName("Услуги");
        servicesCat.setExpense(false);
        servicesCat.setIcon("ic_services");
        servicesCat.setColor("#1abc9c");
        servicesCat.setCreatedAt(new Date());
        categoryDao.insert(servicesCat);

        Category investmentsCat = new Category();
        investmentsCat.setName("Инвестиции");
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

        // 2. Доллар США (USD)
        Currency usd = new Currency();
        usd.setCode("USD");
        usd.setName("Доллар США");
        usd.setRate(0.0120613518); // Примерный курс
        usd.setBaseCurrency("RUB");
        usd.setTrend("stable");
        usd.setChange(0.0);
        usd.setChangePercentage(0.0);
        usd.setIconUrl("ic_currency_usd");
        usd.setUpdatedAt(currentDate);
        currencyDao.insert(usd);

        // 3. Евро (EUR)
        Currency eur = new Currency();
        eur.setCode("EUR");
        eur.setName("Евро");
        eur.setRate(0.0106083224); // Примерный курс
        eur.setBaseCurrency("RUB");
        eur.setTrend("stable");
        eur.setChange(0.0);
        eur.setChangePercentage(0.0);
        eur.setIconUrl("ic_currency_eur");
        eur.setUpdatedAt(currentDate);
        currencyDao.insert(eur);

        // 4. Японская йена (JPY)
        Currency jpy = new Currency();
        jpy.setCode("JPY");
        jpy.setName("Японская йена");
        jpy.setRate(1.7327729226); // Примерный курс
        jpy.setBaseCurrency("RUB");
        jpy.setTrend("stable");
        jpy.setChange(0.0);
        jpy.setChangePercentage(0.0);
        jpy.setIconUrl("ic_currency_jpy");
        jpy.setUpdatedAt(currentDate);
        currencyDao.insert(jpy);

        // 5. Белорусский рубль (BYN)
        Currency byn = new Currency();
        byn.setCode("BYN");
        byn.setName("Белорусский рубль");
        byn.setRate(0.033); // Примерный курс
        byn.setBaseCurrency("RUB");
        byn.setTrend("stable");
        byn.setChange(0.0);
        byn.setChangePercentage(0.0);
        byn.setIconUrl("ic_currency_byn");
        byn.setUpdatedAt(currentDate);
        currencyDao.insert(byn);

        // 6. Китайский юань (CNY)
        Currency cny = new Currency();
        cny.setCode("CNY");
        cny.setName("Китайский юань");
        cny.setRate(0.085); // Примерный курс
        cny.setBaseCurrency("RUB");
        cny.setTrend("stable");
        cny.setChange(0.0);
        cny.setChangePercentage(0.0);
        cny.setIconUrl("ic_currency_cny");
        cny.setUpdatedAt(currentDate);
        currencyDao.insert(cny);
    }
}
