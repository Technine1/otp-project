package otp.dao;

import otp.model.OtpConfig;
import otp.util.DbUtil;

import java.sql.*;
import java.util.Optional;

public class OtpConfigDaoImpl implements OtpConfigDao {
    private final Connection connection = DbUtil.getConnection();

    @Override
    public Optional<OtpConfig> getConfig() {
        String sql = "SELECT * FROM otp_config WHERE id = TRUE";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                OtpConfig config = new OtpConfig();
                config.setCodeLength(rs.getInt("code_length"));
                config.setTtlSeconds(rs.getInt("ttl_seconds"));
                return Optional.of(config);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to read OTP config", e);
        }
        return Optional.empty();
    }

    @Override
    public void updateConfig(OtpConfig config) {
        String sql = """
                INSERT INTO otp_config (id, code_length, ttl_seconds)
                VALUES (TRUE, ?, ?)
                ON CONFLICT (id) DO UPDATE
                SET code_length = EXCLUDED.code_length,
                    ttl_seconds = EXCLUDED.ttl_seconds
                """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, config.getCodeLength());
            ps.setInt(2, config.getTtlSeconds());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update OTP config", e);
        }
    }
}