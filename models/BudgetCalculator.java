package com.example.diplom.models;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Модель для расчета и оптимизации бюджета.
 * Включает методы для планирования бюджета, расчета экономических показателей
 * и оптимизации расходов.
 */
public class BudgetCalculator {

    /**
     * Расчет чистой прибыли.
     *
     * @param income общий доход (в рублях)
     * @param expenses общие расходы (в рублях)
     * @param taxes налоги (в рублях)
     * @return чистая прибыль (в рублях)
     */
    public double calculateNetProfit(double income, double expenses, double taxes) {
        return income - expenses - taxes;
    }

    /**
     * Расчет коэффициента текущей ликвидности.
     * Коэффициент текущей ликвидности = Оборотные активы / Краткосрочные обязательства
     *
     * @param currentAssets оборотные активы (в рублях)
     * @param currentLiabilities краткосрочные обязательства (в рублях)
     * @return коэффициент текущей ликвидности
     */
    public double calculateCurrentRatio(double currentAssets, double currentLiabilities) {
        if (currentLiabilities <= 0) {
            throw new IllegalArgumentException("Краткосрочные обязательства должны быть положительными");
        }
        return currentAssets / currentLiabilities;
    }

    /**
     * Расчет коэффициента финансовой независимости.
     * Коэффициент финансовой независимости = Собственный капитал / Общие активы
     *
     * @param equity собственный капитал (в рублях)
     * @param totalAssets общие активы (в рублях)
     * @return коэффициент финансовой независимости
     */
    public double calculateFinancialIndependenceRatio(double equity, double totalAssets) {
        if (totalAssets <= 0) {
            throw new IllegalArgumentException("Общие активы должны быть положительными");
        }
        return equity / totalAssets;
    }

    /**
     * Расчет ROI (Return on Investment).
     * ROI = (Прибыль от инвестиций - Стоимость инвестиций) / Стоимость инвестиций * 100%
     *
     * @param investmentProfit прибыль от инвестиций (в рублях)
     * @param investmentCost стоимость инвестиций (в рублях)
     * @return ROI в процентах
     */
    public double calculateROI(double investmentProfit, double investmentCost) {
        if (investmentCost <= 0) {
            throw new IllegalArgumentException("Стоимость инвестиций должна быть положительной");
        }
        return ((investmentProfit - investmentCost) / investmentCost) * 100;
    }

    /**
     * Расчет периода окупаемости инвестиций.
     * Период окупаемости = Первоначальные инвестиции / Ежегодный денежный поток
     *
     * @param initialInvestment первоначальные инвестиции (в рублях)
     * @param annualCashFlow ежегодный денежный поток (в рублях)
     * @return период окупаемости в годах
     */
    public double calculatePaybackPeriod(double initialInvestment, double annualCashFlow) {
        if (annualCashFlow <= 0) {
            throw new IllegalArgumentException("Ежегодный денежный поток должен быть положительным");
        }
        return initialInvestment / annualCashFlow;
    }

    /**
     * Расчет ABC-анализа расходов.
     * Группа A - 80% расходов (наиболее значимые)
     * Группа B - 15% расходов (средней значимости)
     * Группа C - 5% расходов (наименее значимые)
     *
     * @param expensesMap карта расходов (категория -> сумма)
     * @return карта с распределением категорий по группам ABC
     */
    public Map<String, List<String>> abcAnalysis(Map<String, Double> expensesMap) {
        // Реализация ABC-анализа
        // ...

        // Заглушка для примера
        return new HashMap<>();
    }

    /**
     * Оптимизация бюджета на основе приоритетов расходов.
     *
     * @param currentBudget текущий бюджет (в рублях)
     * @param targetBudget целевой бюджет (в рублях)
     * @param expensesWithPriority карта расходов с приоритетами (категория -> [сумма, приоритет])
     * @return оптимизированная карта расходов
     */
    public Map<String, Double> optimizeBudget(double currentBudget, double targetBudget,
                                              Map<String, Object[]> expensesWithPriority) {
        // Реализация оптимизации бюджета
        // ...

        // Заглушка для примера
        return new HashMap<>();
    }

    /**
     * Расчет маржинальной прибыли.
     * Маржинальная прибыль = Выручка - Переменные затраты
     *
     * @param revenue выручка (в рублях)
     * @param variableCosts переменные затраты (в рублях)
     * @return маржинальная прибыль (в рублях)
     */
    public double calculateMarginProfit(double revenue, double variableCosts) {
        return revenue - variableCosts;
    }

    /**
     * Расчет коэффициента маржинальной прибыли.
     * Коэффициент маржинальной прибыли = Маржинальная прибыль / Выручка
     *
     * @param marginProfit маржинальная прибыль (в рублях)
     * @param revenue выручка (в рублях)
     * @return коэффициент маржинальной прибыли
     */
    public double calculateMarginProfitRatio(double marginProfit, double revenue) {
        if (revenue <= 0) {
            throw new IllegalArgumentException("Выручка должна быть положительной");
        }
        return marginProfit / revenue;
    }
}
