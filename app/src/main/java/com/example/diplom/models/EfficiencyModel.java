package com.example.diplom.models;

/**
 * Модель для расчета эффективности производства.
 * Включает методы для анализа материалоемкости и трудоемкости производства.
 */
public class EfficiencyModel {

    /**
     * Расчет материалоемкости производства.
     * Материалоемкость = (Затраты на материалы / Стоимость произведенной продукции) * 100%
     *
     * @param materialCosts затраты на материалы (в рублях)
     * @param productionValue стоимость произведенной продукции (в рублях)
     * @return материалоемкость в процентах
     */
    public double calculateMaterialIntensity(double materialCosts, double productionValue) {
        if (productionValue <= 0) {
            throw new IllegalArgumentException("Стоимость произведенной продукции должна быть положительной");
        }
        return (materialCosts / productionValue) * 100;
    }

    /**
     * Расчет трудоемкости производства.
     * Трудоемкость = (Затраты на оплату труда / Стоимость произведенной продукции) * 100%
     *
     * @param laborCosts затраты на оплату труда (в рублях)
     * @param productionValue стоимость произведенной продукции (в рублях)
     * @return трудоемкость в процентах
     */
    public double calculateLaborIntensity(double laborCosts, double productionValue) {
        if (productionValue <= 0) {
            throw new IllegalArgumentException("Стоимость произведенной продукции должна быть положительной");
        }
        return (laborCosts / productionValue) * 100;
    }

    /**
     * Определяет тип производства на основе материалоемкости и трудоемкости.
     *
     * @param materialIntensity материалоемкость в процентах
     * @param laborIntensity трудоемкость в процентах
     * @return тип производства (материалоемкое, трудоемкое или смешанное)
     */
    public String determineProductionType(double materialIntensity, double laborIntensity) {
        if (materialIntensity > laborIntensity * 1.5) {
            return "Материалоемкое производство";
        } else if (laborIntensity > materialIntensity * 1.5) {
            return "Трудоемкое производство";
        } else {
            return "Смешанное производство";
        }
    }

    /**
     * Расчет рентабельности производства.
     * Рентабельность = (Прибыль / Себестоимость) * 100%
     *
     * @param profit прибыль (в рублях)
     * @param cost себестоимость (в рублях)
     * @return рентабельность в процентах
     */
    public double calculateProfitability(double profit, double cost) {
        if (cost <= 0) {
            throw new IllegalArgumentException("Себестоимость должна быть положительной");
        }
        return (profit / cost) * 100;
    }

    /**
     * Расчет точки безубыточности в единицах продукции.
     * Точка безубыточности = Постоянные затраты / (Цена единицы продукции - Переменные затраты на единицу)
     *
     * @param fixedCosts постоянные затраты (в рублях)
     * @param pricePerUnit цена единицы продукции (в рублях)
     * @param variableCostPerUnit переменные затраты на единицу продукции (в рублях)
     * @return точка безубыточности в единицах продукции
     */
    public double calculateBreakEvenPoint(double fixedCosts, double pricePerUnit, double variableCostPerUnit) {
        if (pricePerUnit <= variableCostPerUnit) {
            throw new IllegalArgumentException("Цена единицы продукции должна превышать переменные затраты на единицу");
        }
        return fixedCosts / (pricePerUnit - variableCostPerUnit);
    }
}
