package com.saimon.utils;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.ibm.icu.util.CurrencyAmount;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.Currency;
import java.util.Locale;

/**
 * @Author Muhammad Saimon
 * @since 25/01/2021 15:47
 */

public class NumberToSpelling {

    private final static String COUNTRY_CODE = "BD";
    private final static String LANGUAGE = "en";
    private final static String FRACTION_UNIT_NAME = "Paisa";

    public static String convertToCurrencyFormat(String numberSt) {
        try {
            Double number = Double.parseDouble(numberSt);
            NumberFormat numberFormat = NumberFormat.getInstance();
            numberFormat.setMinimumFractionDigits(2);
//            numberFormat.setMaximumFractionDigits(2);
//            numberFormat.setRoundingMode(RoundingMode.FLOOR);
            return numberFormat.format(number);
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return numberSt;
        }
    }

    public static String generateBalanceInWord(String amountString) {
        return translateNumberToWord(amountString).trim();
    }

    private static String translateNumberToWord(String amount) {

        StringBuffer result = new StringBuffer();

        Locale locale = new Locale(LANGUAGE, COUNTRY_CODE);
        Currency currency = Currency.getInstance(locale);

        String inputArr[] = new BigDecimal(amount).abs().toPlainString().split("\\.+");
        RuleBasedNumberFormat rule = new RuleBasedNumberFormat(locale, RuleBasedNumberFormat.SPELLOUT);

        int i = 0;
        for (String input : inputArr) {
            CurrencyAmount crncyAmt = new CurrencyAmount(new BigDecimal(input), currency);
            if (i++ == 0) {
                result.append(rule.format(crncyAmt)).append(" Taka and "); // currency.getDisplayName()
            } else {
                result.append(rule.format(crncyAmt)).append(" " + FRACTION_UNIT_NAME);
            }
        }

        return capitalizeFirstLetters(result.toString());
    }

    private static String capitalizeFirstLetters(String sentence) {
        String result = "";
        for (String v : sentence.split(" ")) {
            result += v.substring(0, 1).toUpperCase() + v.substring(1) + " ";
        }

        return result;
    }

}
