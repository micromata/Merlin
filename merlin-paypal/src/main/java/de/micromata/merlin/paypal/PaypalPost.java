package de.micromata.merlin.paypal;

import org.apache.commons.text.StringEscapeUtils;

import java.math.BigDecimal;

public class PaypalPost {
    private String returnUrl, cancelUrl;

    public String toJson(BigDecimal amount) {
        StringBuilder sb = new StringBuilder();
        sb.append("{\n");
        writeValue(sb, "  ", "intent", "sale");
        sb.append(",\n");

        writeName(sb, "  ", "redirect_urls");
        sb.append("{\n");
        writeValue(sb, "    ", "return_url", returnUrl);
        sb.append(",\n");
        writeValue(sb, "    ", "cancel_url", cancelUrl);
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

    public void setReturnUrl(String returnUrl) {
        this.returnUrl = returnUrl;
    }

    public void setCancelUrl(String cancelUrl) {
        this.cancelUrl = cancelUrl;
    }
}