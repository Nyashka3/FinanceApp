package com.example.diplom.utils;

import androidx.room.TypeConverter;

import java.util.Date;

/**
 * Класс-конвертер для типов данных, которые нужно конвертировать при сохранении в базу данных.
 * Используется для конвертации типа Date в Long и обратно.
 */
public class DateConverter {
    @TypeConverter
    public static Date fromTimestamp(Long value) {
        return value == null ? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date) {
        return date == null ? null : date.getTime();
    }
}
