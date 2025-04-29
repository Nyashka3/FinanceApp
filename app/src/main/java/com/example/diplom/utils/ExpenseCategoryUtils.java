package com.example.diplom.utils;

import com.example.diplom.database.entities.Category;

import java.util.Date;
import java.util.ArrayList;
import java.util.List;

/**
 * Утилитный класс для работы с категориями расходов
 */
public class ExpenseCategoryUtils {

    // Названия категорий
    public static final String MATERIALS_CATEGORY = "Материальные затраты";
    public static final String SALARY_CATEGORY = "Заработная плата";
    public static final String DEPRECIATION_CATEGORY = "Амортизация";
    public static final String ENERGY_CATEGORY = "Энергия и топливо";
    public static final String OTHER_CATEGORY = "Прочие расходы";

    /**
     * Получает список экономически верных категорий расходов
     * @return список стандартных категорий расходов
     */
    public static List<Category> getDefaultExpenseCategories() {
        List<Category> categories = new ArrayList<>();
        Date now = new Date();

        // Категория 1: Материальные затраты
        Category materials = new Category();
        materials.setName(MATERIALS_CATEGORY);
        materials.setDescription("Сырье, материалы, комплектующие");
        materials.setExpense(true);
        materials.setIcon("ic_material");
        materials.setColor("#3498db");
        materials.setCreatedAt(now);
        categories.add(materials);

        // Категория 2: Заработная плата
        Category salary = new Category();
        salary.setName(SALARY_CATEGORY);
        salary.setDescription("Основная и дополнительная зарплата сотрудников");
        salary.setExpense(true);
        salary.setIcon("ic_labor");
        salary.setColor("#e74c3c");
        salary.setCreatedAt(now);
        categories.add(salary);

        // Категория 3: Амортизация
        Category depreciation = new Category();
        depreciation.setName(DEPRECIATION_CATEGORY);
        depreciation.setDescription("Износ оборудования и основных средств");
        depreciation.setExpense(true);
        depreciation.setIcon("ic_depreciation");
        depreciation.setColor("#9b59b6");
        depreciation.setCreatedAt(now);
        categories.add(depreciation);

        // Категория 4: Энергия и топливо
        Category energy = new Category();
        energy.setName(ENERGY_CATEGORY);
        energy.setDescription("Затраты на коммунальные услуги, топливо для производства");
        energy.setExpense(true);
        energy.setIcon("ic_energy");
        energy.setColor("#f39c12");
        energy.setCreatedAt(now);
        categories.add(energy);

        // Категория 5: Прочие расходы
        Category other = new Category();
        other.setName(OTHER_CATEGORY);
        other.setDescription("Аренда, реклама, транспорт и т.д.");
        other.setExpense(true);
        other.setIcon("ic_other");
        other.setColor("#27ae60");
        other.setCreatedAt(now);
        categories.add(other);

        return categories;
    }

    /**
     * Определяет, является ли категория материалоемкой
     * @param categoryName название категории
     * @return true, если категория материалоемкая
     */
    public static boolean isMaterialIntensive(String categoryName) {
        return MATERIALS_CATEGORY.equals(categoryName);
    }

    /**
     * Определяет, является ли категория трудоемкой
     * @param categoryName название категории
     * @return true, если категория трудоемкая
     */
    public static boolean isLaborIntensive(String categoryName) {
        return SALARY_CATEGORY.equals(categoryName);
    }

    /**
     * Определяет, является ли категория фондоемкой (амортизация)
     * @param categoryName название категории
     * @return true, если категория фондоемкая
     */
    public static boolean isCapitalIntensive(String categoryName) {
        return DEPRECIATION_CATEGORY.equals(categoryName);
    }

    /**
     * Определяет, является ли категория энергоемкой
     * @param categoryName название категории
     * @return true, если категория энергоемкая
     */
    public static boolean isEnergyIntensive(String categoryName) {
        return ENERGY_CATEGORY.equals(categoryName);
    }
}