package de.micromata.merlin.paypal.sdk;

import com.paypal.api.payments.Amount;
import com.paypal.api.payments.Details;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Helper class for creating amounts with scale 2
 */
public class PaymentAmount {
    public enum Currency {EUR}

    private BigDecimal shipping;
    private BigDecimal tax;
    private BigDecimal subtotal;
    private String currency;

    public PaymentAmount(String currency) {
        this.currency = currency;
    }

    public PaymentAmount(Currency currency) {
        setCurrency(currency);
    }

    public BigDecimal getShipping() {
        return shipping;
    }

    public Amount asAmount() {
        Amount amount = new Amount();
        amount.setCurrency(currency);
        Details details = new Details();
        if (shipping != null) {
            details.setShipping(asString(shipping));
        }
        if (subtotal != null) {
            details.setSubtotal(asString(subtotal));
        }
        if (tax != null) {
            details.setTax(asString(tax));
        }
        amount.setDetails(details);
        amount.setTotal(getTotalString());
        return amount;
    }

    /**
     * Ensures scale 2.
     *
     * @param shipping
     * @return this for chaining.
     */
    public PaymentAmount setShipping(BigDecimal shipping) {
        this.shipping = roundAmount(shipping);
        return this;
    }

    public BigDecimal getTax() {
        return tax;
    }

    /**
     * Ensures scale 2.
     *
     * @param tax
     * @return this for chaining.
     */
    public PaymentAmount setTax(BigDecimal tax) {
        this.tax = roundAmount(tax);
        return this;
    }

    public PaymentAmount setTax(double tax) {
        return setTax(new BigDecimal(tax));
    }

    public BigDecimal getSubtotal() {
        return subtotal;
    }

    public String getSubtotalString() {
        return asString(getSubtotal());
    }

    /**
     * Ensures scale 2.
     *
     * @param subtotal
     * @return this for chaining.
     */
    public PaymentAmount setSubtotal(BigDecimal subtotal) {
        this.subtotal = roundAmount(subtotal);
        return this;
    }

    public PaymentAmount setSubtotal(double subtotal) {
        return setSubtotal(new BigDecimal(subtotal));
    }

    public BigDecimal getTotal() {
        BigDecimal total = BigDecimal.ZERO;
        if (shipping != null) {
            total = total.add(shipping);
        }
        if (tax != null) {
            total = total.add(tax);
        }
        if (subtotal != null) {
            total = total.add(subtotal);
        }
        return roundAmount(total);
    }

    public String getTotalString() {
        return asString(getTotal());
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency.name();
    }

    private BigDecimal roundAmount(BigDecimal amount) {
        if (amount == null) {
            return amount;
        }
        return amount.setScale(2, RoundingMode.HALF_UP);
    }

    private String asString(BigDecimal amount) {
        if (amount == null) {
            return "0.00";
        }
        return amount.toString();
    }
}
