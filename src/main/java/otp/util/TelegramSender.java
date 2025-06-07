package otp.util;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Properties;

public class TelegramSender {
    private static final Logger log = LoggerFactory.getLogger(TelegramSender.class);

    private final String botToken;
    private final String chatId;

    public TelegramSender() {
        Properties props = PropertiesLoader.load("telegram.properties");
        this.botToken = props.getProperty("telegram.bot_token");
        this.chatId = props.getProperty("telegram.chat_id");
    }

    public boolean send(String code) {
        String message = "Your OTP code is: " + code;
        String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
        String url = String.format(
                "https://api.telegram.org/bot%s/sendMessage?chat_id=%s&text=%s",
                botToken, chatId, encodedMessage
        );

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(url);
            try (CloseableHttpResponse response = httpClient.execute(request)) {
                int status = response.getCode();
                String responseBody = EntityUtils.toString(response.getEntity());

                if (status == 200) {
                    log.info("OTP code sent via Telegram to chat_id={}", chatId);
                    return true;
                } else {
                    log.error("Telegram API error (status={}): {}", status, responseBody);
                    return false;
                }
            }
        } catch (Exception e) {
            log.error("Failed to send OTP via Telegram: {}", e.getMessage(), e);
            return false;
        }
    }
}
