package de.micromata.merlin.paypal.purejava;

import de.micromata.merlin.paypal.PaypalConfig;
import org.apache.commons.text.StringEscapeUtils;

import java.math.BigDecimal;

public class CreatePaymentData {
    PaypalConfig config;

    public CreatePaymentData(PaypalConfig config) {
        this.config = config;
    }

    /**
     * @param amount The amount to pay by the customer.
     * @return The json request parameter for creating a payment.
     */
    public String createRequestParameter(BigDecimal amount) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        writeValue(sb, "  ", "intent", "sale");
        sb.append(",\n");

        writeName(sb, "  ", "redirect_urls");
        sb.append("{\n");
        writeValue(sb, "    ", "return_url", config.getReturnUrl());
        sb.append(",\n");
        writeValue(sb, "    ", "cancel_url", config.getCancelUrl());
        sb.append("\n  },");

        writeName(sb, "\n  ", "payer");
        sb.append("{\n");
        writeValue(sb, "    ", "payment_method", "paypal");
        sb.append("\n  },");

        writeName(sb, "\n  ", "transactions");
        sb.append("[{\n");
        writeName(sb, "    ", "amount");
        sb.append("{\n");
        writeValue(sb, "      ", "total", amount.toString());
        sb.append(",\n");
        writeValue(sb, "      ", "currency", "EUR");
        sb.append("\n    }");
        sb.append("\n  }]");
        sb.append("\n}");
        return sb.toString();
    }

    private void writeValue(StringBuilder sb, String indent, String name, String value) {
        sb.append(indent).append('\"').append(name).append("\": \"").append(StringEscapeUtils.escapeJson(value)).append('\"');
    }

    private void writeName(StringBuilder sb, String indent, String name) {
        sb.append(indent).append('\"').append(name).append("\": ");
    }
}