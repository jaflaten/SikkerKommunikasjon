package no.hvl.dat251.v22.SikkerKommunikasjon.service;

import com.mailjet.client.ClientOptions;
import com.mailjet.client.MailjetClient;
import com.mailjet.client.MailjetRequest;
import com.mailjet.client.MailjetResponse;
import com.mailjet.client.errors.MailjetException;
import com.mailjet.client.errors.MailjetSocketTimeoutException;
import com.mailjet.client.resource.Emailv31;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class EmailService {

    /**
     * The email sikkerkommunikasjon sends it's emails from
     */
    public static final String SIKKERKOMMUNIKASJON_EMAIL = "sikkerkomm_test@protonmail.com";

    /**
     * If the email service is operational, e.g. correct api-keys are set
     */
    private boolean isEnabled;

    private MailjetClient client;

    @Autowired
    public EmailService() {
        String apiKeyPublic = System.getenv("MJ_APIKEY_PUBLIC");
        String apiKeyPrivate = System.getenv("MJ_APIKEY_PRIVATE");

        isEnabled = false;
        if (apiKeyPublic != null && apiKeyPrivate != null)
            isEnabled = true;  // Found both environment variables
        if (apiKeyPublic == null)
            log.error("Could not find environment variable 'MJ_APIKEY_PUBLIC' used for email service.");
        if (apiKeyPrivate == null)
            log.error("Could not find environment variable 'MJ_APIKEY_PRIVATE' used for email service.");

        if (isEnabled) {
            client = new MailjetClient(
                    apiKeyPublic,
                    apiKeyPrivate,
                    new ClientOptions("v3.1")
            );
        }
    }

    /**
     * Sends a simple email through MailJet.
     *
     * @param recipient The email address to send email to
     * @param subject   The header of the email
     * @param message   The message that the email should contain
     */
    public void sendSimpleEmail(String recipient, String subject, String message) {
        if (!isEnabled) {
            log.error("Email capabilities is disabled, not sending email with subject: '" + subject + "'.");
        }

        MailjetRequest request = new MailjetRequest(Emailv31.resource)
                .property(Emailv31.MESSAGES, new JSONArray()
                        .put(new JSONObject()
                                .put(Emailv31.Message.FROM, new JSONObject()
                                        .put("Email", SIKKERKOMMUNIKASJON_EMAIL)
                                        .put("Name", "Sikkerkommunikasjon"))
                                .put(Emailv31.Message.TO, new JSONArray()
                                        .put(new JSONObject()
                                                .put("Email", recipient)))
                                .put(Emailv31.Message.SUBJECT, subject)
                                .put(Emailv31.Message.TEXTPART, message)
                        ));

        try {
            MailjetResponse response = client.post(request);
            log.info(String.valueOf(response.getStatus()));
            log.info(String.valueOf(response.getData()));
        } catch (MailjetException | MailjetSocketTimeoutException e) {
            log.error("Error when sending email:");
            e.printStackTrace();
        }
    }
}
