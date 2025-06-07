package otp.model;

public class OtpConfig {
    private boolean id = true; // заглушка для ограничения одной строки
    private int codeLength;
    private int ttlSeconds;

    public int getCodeLength() {
        return codeLength;
    }

    public void setCodeLength(int codeLength) {
        this.codeLength = codeLength;
    }

    public int getTtlSeconds() {
        return ttlSeconds;
    }

    public void setTtlSeconds(int ttlSeconds) {
        this.ttlSeconds = ttlSeconds;
    }

    public boolean getId() {
        return id;
    }

    public void setId(boolean id) {
        this.id = id;
    }
}
