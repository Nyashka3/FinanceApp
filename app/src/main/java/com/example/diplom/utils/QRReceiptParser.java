package com.example.diplom.utils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Утилитный класс для парсинга QR-кодов фискальных чеков
 */
public class QRReceiptParser {

    /**
     * Извлекает сумму из QR-кода чека
     * @param qrContent содержимое QR-кода
     * @return сумма чека или null, если не удалось извлечь
     */
    public static Double extractAmount(String qrContent) {
        if (qrContent == null) {
            return null;
        }

        // Паттерн для поиска параметра s=xxx
        Pattern pattern = Pattern.compile("s=([0-9]+(\\.[0-9]+)?)");
        Matcher matcher = pattern.matcher(qrContent);

        if (matcher.find()) {
            try {
                String amountStr = matcher.group(1);
                return Double.parseDouble(amountStr);
            } catch (NumberFormatException e) {
                return null;
            }
        }

        return null;
    }

    /**
     * Извлекает дату и время из QR-кода чека
     * @param qrContent содержимое QR-кода
     * @return дата и время в виде строки или null
     */
    public static Date extractDateTime(String qrContent) {
        if (qrContent == null) {
            return null;
        }

        // Паттерн для поиска параметра t=yyyyMMddTHHmm
        Pattern pattern = Pattern.compile("t=([0-9]{8}T[0-9]{4})");
        Matcher matcher = pattern.matcher(qrContent);

        if (matcher.find()) {
            return DateUtils.parseDate(matcher.group(1));
        }

        return null;
    }
}
