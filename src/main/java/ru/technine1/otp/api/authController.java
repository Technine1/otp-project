package ru.technine1.otp.api;

import ru.technine1.otp.api.common.Request;
import ru.technine1.otp.api.common.Response;
import ru.technine1.otp.api.common.Status;
import ru.technine1.otp.api.common.annotation.Controller;
import ru.technine1.otp.api.common.annotation.RouteMapping;
import ru.technine1.otp.exception.OTPException;
import ru.model.User;
import ru.service.AuthService;

import java.util.Map;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller("/auth")
class AuthController {

    private AuthService authService;
    private final ObjectMapper mapper = new ObjectMapper();

    public void authController(AuthService authService) {
        this.authService = authService;
    }

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RouteMapping(method = "GET")
    public Response<Void> test(Request request) {
        System.out.println("Test endpoint hit");
        return Response.of(Status.OK);
    }

    @RouteMapping(method = "POST")
    public Response<Map<String, String>> login(Request request) {
        String token = authService.login(
                (String) request.body().get("login"),
                (String) request.body().get("password")
        );
        return Response.<Map<String, String>>of(Status.OK, Map.of("token", token));
    }

    @RouteMapping(method = "POST", path = "/new")
    public Response<Void> create(Request request) {
        User user = mapper.convertValue(request.body(), User.class);
        if (!authService.create(user)) {
            throw new OTPException(Status.INTERNAL_SERVER_ERROR, "Ошибка при создании пользователя");
        }
        return Response.of(Status.NO_CONTENT);
    }
}



