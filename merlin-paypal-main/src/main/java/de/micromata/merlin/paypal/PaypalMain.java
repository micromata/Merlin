package de.micromata.merlin.paypal;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;

public class PaypalMain {
    private static Logger log = LoggerFactory.getLogger(PaypalMain.class);

    public static void main(String[] args) throws IOException {
        new PaypalMain()._start(args);
    }

    PaypalCredentials credentials;

    private PaypalMain() {
    }

    public void _start(String[] args) throws IOException {
        // create Options object
        Options options = new Options();
        options.addOption("f", "file", true, "The properties file with the property 'paypal.access_token'.");
        options.addOption("a", "access_token", true, "The access token for PayPal calls.");
        options.addOption("g", "get_access_token", true, "Initial get of the access token. Argument is <client ID>:<secret>");

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
            if (line.hasOption("g")) {
                if (!validateNotGiven(line, "a", "f")) {
                    return;
                }
                getAccessToken(line.getOptionValue("g"));
                return;
            }
            if (!line.hasOption('f') && !line.hasOption('a')) {
                System.err.println("Please specify properties file with 'paypal.access_token' with option -f or specify access_token direct with option -a.");
                return;
            }
            credentials = new PaypalCredentials();
            if (line.hasOption("f")) {
                String filename = line.getOptionValue("f");
                File file = new File(filename);
                if (!file.exists()) {
                    System.err.println("Can't read credentials from file '" + filename + "'. It doesn't exist.");
                    return;
                }
                credentials.read(file);
                if (StringUtils.isBlank(credentials.getAccessToken())) {
                    System.err.println("No paypal.access_token=... given in properties file '" + filename + "'.");
                    return;
                }
            } else {
                credentials.setAccessToken(line.getOptionValue("a"));
                if (StringUtils.isBlank(credentials.getAccessToken())) {
                    System.err.println("No access token given via option -a.");
                    return;
                }
            }
            testCall(credentials);
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
    private void getAccessToken(String clientIdSecret) {
        HttpsCall call = new HttpsCall().setAcceptLanguage("en_US").setAccept(HttpsCall.MimeType.JSON);
        call.setUserPasswordAuthorization(clientIdSecret);
        call.post("https://api.sandbox.paypal.com/v1/oauth2/token", "grant_type=client_credentials");
    }

    private void testCall(PaypalCredentials credentials) {
        HttpsCall call = new HttpsCall().setBearerAuthorization(credentials.getAccessToken())
                .setContentType(HttpsCall.MimeType.JSON);
        PaypalPost paypalPost = new PaypalPost();
        paypalPost.setReturnUrl("https://example.com/your_redirect_url.html");
        paypalPost.setCancelUrl("https://example.com/your_cancel_url.html");
        String input = paypalPost.toJson(new BigDecimal("7.42"));
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
