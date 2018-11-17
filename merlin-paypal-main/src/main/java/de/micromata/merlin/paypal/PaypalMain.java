package de.micromata.merlin.paypal;

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

    PaypalCredentials credentials;

    private PaypalMain() {
    }

    public void _start(String[] args) throws IOException {
        // create Options object
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
                System.err.println("Please specify properties file with paypal credentials or create this: " + file.getAbsolutePath());
                return;
            }
            credentials = new PaypalCredentials();
            credentials.read(file);
            if (StringUtils.isBlank(credentials.getClientId()) ||
                    StringUtils.isBlank(credentials.getSecret())) {
                System.err.println("Please define properties in file '" + file.getAbsolutePath() + "':");
                System.err.println(PaypalCredentials.KEY_CLIENT_ID + "=<client id>");
                System.err.println(PaypalCredentials.KEY_SECRET + "=<secret>");
            }

            String accessToken = getAccessToken(credentials);
            testCall(accessToken);
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
    private String getAccessToken(PaypalCredentials credentials) {
        HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
        call.setUserPasswordAuthorization(credentials.getClientId() + ":" + credentials.getSecret());
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

    private void testCall(String accessToken) {
        HttpsCall call = new HttpsCall().setBearerAuthorization(accessToken)
                .setContentType(HttpsCall.MimeType.JSON);
        CreatePaymentData paypalPost = new CreatePaymentData();
        paypalPost.setReturnUrl("https://example.com/your_redirect_url.html");
        paypalPost.setCancelUrl("https://example.com/your_cancel_url.html");
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
