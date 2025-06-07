package otp.util;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EmailSender {
    private static final Logger log = LoggerFactory.getLogger(EmailSender.class);

    private final String username;
    private final String password;
    private final String from;
    private final Session session;

    public EmailSender() {
        Properties props = PropertiesLoader.load("email.properties");

        this.username = props.getProperty("email.username");
        this.password = props.getProperty("email.password");
        this.from = props.getProperty("email.from");

        this.session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });
    }

    public boolean send(String to, String code) {
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(from));
            message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
            message.setSubject("Your OTP Code");
            message.setText("Your OTP code is: " + code);

            Transport.send(message);
            log.info("OTP code sent via email to {}", to);
            return true;

        } catch (MessagingException e) {
            // Без stack trace:
            log.error("Failed to send email to {}: {}", to, e.getMessage());
            return false;
        }
    }
}
