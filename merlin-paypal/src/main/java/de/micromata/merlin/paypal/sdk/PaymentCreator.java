package de.micromata.merlin.paypal.sdk;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PaymentCreator {
    private static Logger log = LoggerFactory.getLogger(PaymentCreator.class);


    /**
     * Creates the remote payment (publish to Paypal).
     *
     * @param payment
     * @return Return Paypal's redirect url for the user to do the payment.
     */
 /*   public static String publish(PayPalConfig config, Payment payment) {*/
        /*
        WebProfile webProfile = new WebProfile();
        InputFields inputFields = new InputFields();
        inputFields.setNoShipping(0);
        inputFields.setAddressOverride(1);
        webProfile.setInputFields(inputFields);
        try {
            webProfile.create(config.getApiContext());
        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException occurred while trying to publish web profile: " + e.getDetails() + ". webProfile=" + webProfile);
            return null;
        }*/
        // Create payment
      /*  try {
            Payment createdPayment = payment.create(config.getApiContext());
            if (createdPayment != null) {
                log.info("Created payment by PayPal: " + createdPayment);
            } else {
                log.error("Error while trying to publish payment: " + payment);
                return null;
            }
            Iterator<Links> links = createdPayment.getLinks().iterator();
            while (links.hasNext()) {
                Links link = links.next();
                if (link.getRel().equalsIgnoreCase("approval_url")) {
                    // Redirect the customer to link.getHref()
                    String redirectUserHref = link.getHref();
                    log.info("Redirect user to: " + redirectUserHref);
                    return redirectUserHref;
                }
            }
        } catch (PayPalRESTException e) {
            log.error("PayPalRESTException occurred while trying to publish payment: " + e.getDetails() + ". payment=" + payment);
            return null;
        }
        log.error("Oups, no redirect link found for redirecting the user.");
        return null;
    }

    public static String publish(PayPalConfig config, Transaction... transactions) {
        Payment payment = prepare(config, transactions);
        return publish(config, payment);
    }*/
}
