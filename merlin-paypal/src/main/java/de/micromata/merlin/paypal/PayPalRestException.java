package de.micromata.merlin.paypal;

public class PayPalRestException extends Exception {
    public PayPalRestException(String message) {
        super(message);
    }

    public PayPalRestException(String message, Throwable cause) {
        super(message, cause);
    }
}
