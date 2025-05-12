package ru.api;

import ru.api.common.Request;
import ru.api.common.RequestHandler;
import ru.api.common.Response;
import ru.api.common.Status;
import ru.api.common.annotation.Controller;
import ru.api.common.annotation.RouteMapping;
import ru.service.AuthService;

import java.util.Map;

@Controller("/auth")
public class AuthController extends RequestHandler {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @RouteMapping(method = "GET")
    public Response<Map<String, String>> test(Request request) {
        System.out.println();
        return Response.of(Status.OK);
    }
//    @RouteMapping(method = "POST")
//        String token = authService.login((String) request.body().get("login"), (String) request.body().get("password"));
//        return Response.of(Status.OK, Map.of("token", token));
//    }
//
//    @RouteMapping(method = "POST", path = "/new")
//            throw new OTPException(Status.INTERNAL_SERVER_ERROR, "Ошибка при создании пользователя");
//        }
//        return Response.of(Status.NO_CONTENT);
//    }
    //todo JWT+,
    // reg+,
    // auth+,
    // role+,
    // exceptions,
    // crudService
    //todo hikari, migration, integrations, шедуллер,
    //todo logging-inerceprins, logging, mvn-build
}