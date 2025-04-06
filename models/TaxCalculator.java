package com.example.diplom.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Модель для расчета налогов малого бизнеса.
 * Включает методы для расчета различных типов налогов: УСН, ЕНВД, патент и т.д.
 */
public class TaxCalculator {

    // Константы для налоговых ставок
    public static final double USN_INCOME_RATE = 0.06; // УСН "Доходы" - 6%
    public static final double USN_INCOME_EXPENSE_RATE = 0.15; // УСН "Доходы минус расходы" - 15%
    public static final double PATENT_SYSTEM_RATE = 0.06; // Патентная система налогообложения - 6%
    public static final double PENSION_FUND_RATE = 0.22; // Пенсионный фонд - 22%
    public static final double MEDICAL_INSURANCE_RATE = 0.051; // Медицинское страхование - 5.1%
    public static final double SOCIAL_INSURANCE_RATE = 0.029; // Социальное страхование - 2.9%

    /**
     * Расчет налога по УСН "Доходы".
     *
     * @param income общий доход за период (в рублях)
     * @return сумма налога (в рублях)
     */
    public double calculateUsnIncomeTax(double income) {
        return income * USN_INCOME_RATE;
    }

    /**
     * Расчет налога по УСН "Доходы минус расходы".
     *
     * @param income общий доход за период (в рублях)
     * @param expenses общие расходы за период (в рублях)
     * @return сумма налога (в рублях)
     */
    public double calculateUsnIncomeExpenseTax(double income, double expenses) {
        double taxBase = Math.max(0, income - expenses);
        return taxBase * USN_INCOME_EXPENSE_RATE;
    }

    /**
     * Расчет налога по патентной системе налогообложения.
     *
     * @param potentialYearlyIncome потенциально возможный годовой доход (в рублях)
     * @param periodInMonths период действия патента (в месяцах)
     * @return сумма налога (в рублях)
     */
    public double calculatePatentTax(double potentialYearlyIncome, int periodInMonths) {
        if (periodInMonths < 1 || periodInMonths > 12) {
            throw new IllegalArgumentException("Период действия патента должен быть от 1 до 12 месяцев");
        }
        return potentialYearlyIncome * PATENT_SYSTEM_RATE * (periodInMonths / 12.0);
    }

    /**
     * Расчет страховых взносов для работников.
     *
     * @param salary зарплата работника (в рублях)
     * @return карта с суммами взносов по типам (в рублях)
     */
    public Map<String, Double> calculateInsuranceContributions(double salary) {
        Map<String, Double> contributions = new HashMap<>();

        // Расчет взносов в различные фонды
        contributions.put("Пенсионный фонд", salary * PENSION_FUND_RATE);
        contributions.put("Медицинское страхование", salary * MEDICAL_INSURANCE_RATE);
        contributions.put("Социальное страхование", salary * SOCIAL_INSURANCE_RATE);

        // Общая сумма взносов
        double total = contributions.get("Пенсионный фонд") +
                contributions.get("Медицинское страхование") +
                contributions.get("Социальное страхование");
        contributions.put("Всего", total);

        return contributions;
    }

    /**
     * Расчет НДФЛ (Налог на доходы физических лиц).
     *
     * @param salary зарплата работника (в рублях)
     * @return сумма НДФЛ (в рублях)
     */
    public double calculateIncomeTax(double salary) {
        return salary * 0.13; // НДФЛ - 13%
    }

    /**
     * Расчет чистой зарплаты работника после уплаты НДФЛ.
     *
     * @param grossSalary зарплата до вычета налогов (в рублях)
     * @return чистая зарплата (в рублях)
     */
    public double calculateNetSalary(double grossSalary) {
        return grossSalary - calculateIncomeTax(grossSalary);
    }
}
