package otp.api;

import com.sun.net.httpserver.HttpServer;
import otp.api.filters.AuthFilter;
import otp.api.handlers.Public.LoginHandler;
import otp.api.handlers.Public.RegisterHandler;
import otp.api.handlers.User.GenerateOtpHandler;
import otp.api.handlers.User.ValidateOtpHandler;
import otp.api.handlers.Admin.*;

import java.net.InetSocketAddress;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import otp.service.scheduler.SchedulerLauncher;

public class MainServer {
    private static final Logger log = LoggerFactory.getLogger(MainServer.class);

    public static void main(String[] args) throws Exception {

        // Обновляем OTP-конфигурацию при старте
        new otp.service.OtpConfigRefresher().refreshFromFile();

        // Запускаем механизм выполнения задач по расписанию (OtpExpirationTask)
        SchedulerLauncher.start();

        HttpServer server = HttpServer.create(new InetSocketAddress(8080), 0);

        // Публичные
        server.createContext("/register", new RegisterHandler());
        server.createContext("/login", new LoginHandler());

        // Пользовательские (авторизация по токену)
        server.createContext("/otp/generate", new GenerateOtpHandler()).getFilters().add(new AuthFilter("USER"));
        server.createContext("/otp/validate", new ValidateOtpHandler()).getFilters().add(new AuthFilter("USER"));

        // Админские
        server.createContext("/admin/config", new UpdateOtpConfigHandler()).getFilters().add(new AuthFilter("ADMIN"));
        server.createContext("/admin/users", new ListUsersHandler()).getFilters().add(new AuthFilter("ADMIN"));
        server.createContext("/admin/delete", new DeleteUserHandler()).getFilters().add(new AuthFilter("ADMIN"));

        server.start();
        log.info("Starting OTP Service on port 8080");
    }
}
