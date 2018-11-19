package de.micromata.merlin.paypal.utils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class PayPalUtils {

    /**
     * Ensures scale 2.
     *
     * @param amount
     */
    public static BigDecimal roundAmount(BigDecimal amount) {
        if (amount == null) {
            return amount;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * Doesn't round.
     *
     * @param value
     * @param valuesToAdd If null, a will be returned.
     * @return
     */
    public static BigDecimal add(BigDecimal value, BigDecimal... valuesToAdd) {
        if (valuesToAdd == null) {
            return value;
        }
        for (BigDecimal valueToAdd : valuesToAdd) {
            if (valueToAdd != null)
                value = value.add(valueToAdd);
        }
        return value;
    }

    public static String asString(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.toString();
    }
}
