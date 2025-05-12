
package ru.technine1.otp.api;

import com.sun.net.httpserver.HttpExchange;
import ru.technine1.otp.api.common.*;

import java.util.Map;

public class OtpController extends RequestHandler {

    public OtpController(String path) {
        super.controllerPath = path;
        super.routes = Map.of(
                Route.of("GET", ""), this::generate,
                Route.of("POST", ""), this::validate
        );
    }

    private Response<?> validate(Route route, HttpExchange httpExchange) {
        return null;
    }

    private Response<?> generate(Route route, HttpExchange httpExchange) {
        return null;
    }

    private Response<Void> generate(Request request) {
        return Response.of(Status.OK);
    }

    private Response<Void> validate(Request request) {
        return Response.of(Status.OK);
    }
}
