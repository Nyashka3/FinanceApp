package com.example.diplom.utils;

import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        return dateFormat.format(date);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault());
        return dateFormat.format(date);
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM yyyy", new Locale("ru"));
        return dateFormat.format(date);
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
        Calendar today = Calendar.getInstance();
        Calendar calendarDate = Calendar.getInstance();
        calendarDate.setTime(date);
        return today.get(Calendar.YEAR) == calendarDate.get(Calendar.YEAR) &&
                today.get(Calendar.DAY_OF_YEAR) == calendarDate.get(Calendar.DAY_OF_YEAR);
    }

    /**
     * Возвращает дату начала текущего месяца
     * @return дата начала текущего месяца
     */
    public static Date getStartOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, 1);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    /**
     * Возвращает дату конца текущего месяца
     * @return дата конца текущего месяца
     */
    public static Date getEndOfCurrentMonth() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    /**
     * Возвращает разницу между датами в днях
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return количество дней между датами
     */
    public static long getDaysBetween(Date startDate, Date endDate) {
        long diffInMillies = Math.abs(endDate.getTime() - startDate.getTime());
        return TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }
}
