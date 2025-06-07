package otp.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.smpp.Session;
import org.smpp.TCPIPConnection;
import org.smpp.pdu.*;

import java.net.ConnectException;
import java.util.Properties;

public class SmsSender {
    private static final Logger log = LoggerFactory.getLogger(SmsSender.class);

    private final String host;
    private final int port;
    private final String systemId;
    private final String password;
    private final String systemType;
    private final String sourceAddr;

    public SmsSender() {
        Properties props = PropertiesLoader.load("sms.properties");
        this.host = props.getProperty("smpp.host", "localhost");
        this.port = Integer.parseInt(props.getProperty("smpp.port", "2775"));
        this.systemId = props.getProperty("smpp.system_id", "smppclient1");
        this.password = props.getProperty("smpp.password", "password");
        this.systemType = props.getProperty("smpp.system_type", "OTP");
        this.sourceAddr = props.getProperty("smpp.source_addr", "OTPService");
    }

    public boolean send(String destination, String code) {
        Session session = null;
        try {
            log.info("Connecting to SMPP server at {}:{}", host, port);
            TCPIPConnection connection = new TCPIPConnection(host, port);
            session = new Session(connection);

            // Привязка
            BindTransmitter bindRequest = new BindTransmitter();
            bindRequest.setSystemId(systemId);
            bindRequest.setPassword(password);
            bindRequest.setSystemType(systemType);
            bindRequest.setInterfaceVersion((byte) 0x34);

            AddressRange addressRange = new AddressRange();
            addressRange.setTon((byte) 0);
            addressRange.setNpi((byte) 0);
            addressRange.setAddressRange(sourceAddr);
            bindRequest.setAddressRange(addressRange);

            BindResponse bindResponse = session.bind(bindRequest);
            if (bindResponse.getCommandStatus() != 0) {
                log.error("SMPP bind failed: status={}", bindResponse.getCommandStatus());
                return false;
            }

            log.info("SMPP bind successful");

            // Отправка сообщения
            SubmitSM message = new SubmitSM();
            message.setSourceAddr(sourceAddr);
            message.setDestAddr(destination);
            message.setShortMessage("Your OTP code: " + code);

            session.submit(message);
            log.info("OTP code sent via SMS to {}", destination);
            return true;

        } catch (ConnectException ce) {
            log.error("SMPP server unavailable at {}:{} — {}", host, port, ce.getMessage());
            return false;
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", destination, e.getMessage(), e);
            return false;
        } finally {
            try {
                if (session != null) {
                    session.unbind();
                    log.info("SMPP session unbound");
                }
            } catch (Exception ignored) {
                log.warn("Failed to unbind SMPP session");
            }
        }
    }
}
