package de.micromata.merlin.paypal;

import com.paypal.api.payments.Payment;
import com.paypal.api.payments.Transaction;
import de.micromata.merlin.paypal.purejava.CreatePaymentData;
import de.micromata.merlin.paypal.purejava.HttpsCall;
import de.micromata.merlin.paypal.sdk.PaymentAmount;
import de.micromata.merlin.paypal.sdk.PaymentCreator;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PaypalMain {
    private static Logger log = LoggerFactory.getLogger(PaypalMain.class);

    // "access_token":"<access token>"
    Pattern PATTERN_ACCESS_TOKEN = Pattern.compile("\"access_token\":\"([^\"]*)\"");

    public static void main(String[] args) throws IOException {
        new PaypalMain()._start(args);
    }

    PaypalConfig paypalConfig;

    private PaypalMain() {
    }

    public void _start(String[] args) throws IOException {
        //create Options object
        Options options = new Options();
        options.addOption("f", "file", true, "The properties file with the properties 'paypal.client_id' and 'paypal.secret'.");

        //options.addOption("q", "quiet", false, "Don't open browser automatically.");
        options.addOption("h", "help", false, "Print this help screen.");
        CommandLineParser parser = new DefaultParser();
        try {
            // parse the command line arguments
            CommandLine line = parser.parse(options, args);
            if (line.hasOption('h')) {
                printHelp(options);
                return;
            }
            File file;
            if (line.hasOption('f')) {
                file = new File(line.getOptionValue('f'));
            } else {
                file = new File(System.getProperty("user.home", ".merlin-paypal"));
            }
            if (!file.exists()) {
                System.err.println("Please specify properties file with paypal paypalConfig or create this: " + file.getAbsolutePath());
                return;
            }
            paypalConfig = new PaypalConfig();
            paypalConfig.read(file);
            if (StringUtils.isBlank(paypalConfig.getClientId()) ||
                    StringUtils.isBlank(paypalConfig.getClientSecret())) {
                System.err.println("Please define properties in file '" + file.getAbsolutePath() + "':");
                System.err.println(PaypalConfig.KEY_CLIENT_ID + "=<YOUR APPLICATION CLIENT ID>");
                System.err.println(PaypalConfig.KEY_SECRET + "=<YOUR APPLICATION CLIENT SECRET>");
                System.err.println(PaypalConfig.KEY_RETURN_URL
                        + "=<return url called by Paypal after successful payment, e. g. "
                        + PaypalConfig.DEMO_RETURN_URL + ".>");
                System.err.println(PaypalConfig.KEY_CANCEL_URL
                        + "=<cancel url called by Paypal after cancelled payment, e. g. "
                        + PaypalConfig.DEMO_CANCEL_URL + ".>");
            }

            String accessToken = getAccessToken(paypalConfig);
            pureTestCall(accessToken);
            PaymentAmount amount = new PaymentAmount(PaymentAmount.Currency.EUR);
            amount.setSubtotal(29.99);
            Transaction transaction = PaymentCreator.createTransaction(amount, "Micromata T-Shirt Contest 2019");
            Payment payment = PaymentCreator.prepare(paypalConfig, transaction);
            PaymentCreator.publish(paypalConfig, payment);
        } catch (
                ParseException ex) {
            // oops, something went wrong
            System.err.println("Parsing failed.  Reason: " + ex.getMessage());
            printHelp(options);
        }

    }

    /**
     * curl - v https:api.sandbox.paypal.com/v1/oauth2/token -H "Accept: application/json" -H "Accept-Language: en_US"
     * -u "<client_id>:<secret>" -d "grant_type=client_credentials"
     */
    private String getAccessToken(PaypalConfig credentials) {
        HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
        call.setUserPasswordAuthorization(credentials.getClientId() + ":" + credentials.getClientSecret());
        String response = call.post("https://api.sandbox.paypal.com/v1/oauth2/token", "grant_type=client_credentials");
        // "access_token":"<access token>"
        Matcher matcher = PATTERN_ACCESS_TOKEN.matcher(response);
        if (!matcher.find()) {
            System.err.println("Didn't get access token from server: " + response);
            return null;
        }
        String accessToken = matcher.group(1);
        if (log.isDebugEnabled()) log.debug("Access token: " + accessToken);
        return accessToken;
    }

    private void pureTestCall(String accessToken) {
        HttpsCall call = new HttpsCall().setBearerAuthorization(accessToken)
                .setContentType(HttpsCall.MimeType.JSON);
        CreatePaymentData paypalPost = new CreatePaymentData(paypalConfig);
        String input = paypalPost.createRequestParameter(new BigDecimal("7.42"));
        String result = call.post("https://api.sandbox.paypal.com/v1/payments/payment ", input);
        log.info(result);
    }

    private boolean validateGiven(CommandLine commandLine, String... options) {
        for (String option : options) {
            if (!commandLine.hasOption(option)) {
                System.err.println("Please specify -" + option + ".");
                return false;
            }
        }
        return true;
    }

    private boolean validateNotGiven(CommandLine commandLine, String... options) {
        for (String option : options) {
            if (commandLine.hasOption(option)) {
                System.err.println("Please remove option -" + option + ".");
                return false;
            }
        }
        return true;
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("merlin-paypal-main", options);
    }
}
