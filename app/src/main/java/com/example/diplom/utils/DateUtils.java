package com.example.diplom.utils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Locale;

/**
 * Утилитный класс для работы с датами.
 */
public class DateUtils {

    /**
     * Возвращает строковое представление даты в формате "dd.MM.yyyy"
     * @param date дата для форматирования
     * @return отформатированная дата
     */
    public static String formatDate(Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        return localDate.format(formatter);
    }

    /**
     * Возвращает строковое представление даты и времени в формате "dd.MM.yyyy HH:mm"
     * @param date дата для форматирования
     * @return отформатированная дата и время
     */
    public static String formatDateTime(Date date) {
        if (date == null) {
            return "";
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        return localDateTime.format(formatter);
    }

    /**
     * Возвращает строковое представление месяца и года в формате "MMMM yyyy"
     * @param date дата для форматирования
     * @return отформатированный месяц и год
     */
    public static String formatMonthYear(Date date) {
        if (date == null) {
            return "";
        }
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", new Locale("ru"));
        return localDate.format(formatter);
    }

    /**
     * Проверяет, является ли дата сегодняшней
     * @param date дата для проверки
     * @return true, если дата соответствует сегодняшнему дню
     */
    public static boolean isToday(Date date) {
        if (date == null) {
            return false;
        }
        LocalDate today = LocalDate.now();
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return today.equals(localDate);
    }

    /**
     * Возвращает дату начала текущего месяца
     * @return дата начала текущего месяца
     */
    public static Date getStartOfCurrentMonth() {
        LocalDate firstDayOfMonth = LocalDate.now().withDayOfMonth(1);
        LocalDateTime startOfMonth = firstDayOfMonth.atStartOfDay();
        return Date.from(startOfMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Возвращает дату конца текущего месяца
     * @return дата конца текущего месяца
     */
    public static Date getEndOfCurrentMonth() {
        LocalDate lastDayOfMonth = LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth());
        LocalDateTime endOfMonth = lastDayOfMonth.atTime(LocalTime.MAX);
        return Date.from(endOfMonth.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Возвращает разницу между датами в днях
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return количество дней между датами
     */
    public static long getDaysBetween(Date startDate, Date endDate) {
        LocalDate start = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        LocalDate end = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        return ChronoUnit.DAYS.between(start, end);
    }

    /**
     * Преобразует строку с датой в объект Date
     * @param dateStr строка с датой в различных форматах
     * @return объект Date или null, если преобразование не удалось
     */
    public static Date parseDate(String dateStr) {
        if (dateStr == null || dateStr.isEmpty()) {
            return null;
        }

        // Определяем различные форматы даты
        DateTimeFormatter[] dateFormatters = {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"),
                DateTimeFormatter.ofPattern("dd.MM.yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("yyyyMMdd'T'HHmm")
        };

        for (DateTimeFormatter formatter : dateFormatters) {
            try {
                // Пробуем парсить как дату с временем
                if (formatter.toString().contains("HH:mm:ss")) {
                    LocalDateTime localDateTime = LocalDateTime.parse(dateStr, formatter);
                    return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
                } else {
                    // Парсим как просто дату
                    LocalDate localDate = LocalDate.parse(dateStr, formatter);
                    return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                }
            } catch (DateTimeParseException e) {
                // Пропускаем и пробуем следующий формат
            }
        }

        // Если ни один формат не подошел, возвращаем текущую дату
        return new Date();
    }

    // Вспомогательные методы для конвертации между Date и LocalDate/LocalDateTime

    /**
     * Конвертирует Date в LocalDate
     * @param date объект Date
     * @return объект LocalDate
     */
    public static LocalDate toLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    /**
     * Конвертирует LocalDate в Date
     * @param localDate объект LocalDate
     * @return объект Date
     */
    public static Date toDate(LocalDate localDate) {
        if (localDate == null) {
            return null;
        }
        return Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Конвертирует Date в LocalDateTime
     * @param date объект Date
     * @return объект LocalDateTime
     */
    public static LocalDateTime toLocalDateTime(Date date) {
        if (date == null) {
            return null;
        }
        return date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
    }

    /**
     * Конвертирует LocalDateTime в Date
     * @param localDateTime объект LocalDateTime
     * @return объект Date
     */
    public static Date toDate(LocalDateTime localDateTime) {
        if (localDateTime == null) {
            return null;
        }
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }
}