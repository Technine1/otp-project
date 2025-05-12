package ru.api.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.api.common.annotation.Body;
import ru.api.common.annotation.Controller;
import ru.api.common.annotation.PathVariable;
import ru.api.common.annotation.QueryParam;
import ru.api.common.annotation.RouteMapping;
import ru.exception.OTPException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RequestHandler implements HttpHandler {

    protected static final ObjectMapper mapper = new ObjectMapper();
    private String restPath;
    private final Map<Route, BiFunction<Route, HttpExchange, Response<?>>> routes = new HashMap<>();

    public RequestHandler() {
        Controller controller = this.getClass().getAnnotation(Controller.class);
        if (controller != null) {
            this.restPath = controller.value();
        }
        for (Method method : this.getClass().getDeclaredMethods()) {
            RouteMapping route = method.getAnnotation(RouteMapping.class);
            if (route != null) {
                routes.put(Route.of(route.method(), route.path()), buildHandler(method));
            }
        }
    }

    private BiFunction<Route, HttpExchange, Response<?>> buildHandler(Method method) {
        return (route, exchange) -> {
            try {
                Map<String, Object> body = Collections.emptyMap();
                try {
                    String rawBody = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    if (!rawBody.isBlank()) {
                        body = mapper.readValue(rawBody, new TypeReference<>() {});
                    }
                } catch (IOException e) {
                    throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
                }

                Map<String, String> pathVars = extractPathVariables(route, exchange.getRequestURI().getPath().substring(restPath.length()));
                Map<String, String> queryParams = extractQueryParams(exchange.getRequestURI().getQuery());

                Parameter[] parameters = method.getParameters();
                Object[] args = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter p = parameters[i];
                    if (p.isAnnotationPresent(Body.class)) {
                        args[i] = mapper.convertValue(body, p.getType());
                    } else if (p.isAnnotationPresent(QueryParam.class)) {
                        QueryParam q = p.getAnnotation(QueryParam.class);
                        String val = queryParams.get(q.value());
                        if (val == null && q.required()) {
                            throw new OTPException(Status.BAD_REQUEST, "Missing query param: " + q.value());
                        }
                        args[i] = mapper.convertValue(val, p.getType());
                    } else if (p.isAnnotationPresent(PathVariable.class)) {
                        PathVariable v = p.getAnnotation(PathVariable.class);
                        String val = pathVars.get(v.value());
                        if (val == null && !v.optional()) {
                            throw new OTPException(Status.BAD_REQUEST, "Missing path variable: " + v.value());
                        }
                        args[i] = mapper.convertValue(val, p.getType());
                    }
                }

                return (Response<?>) method.invoke(this, args);

            } catch (InvocationTargetException e) {
                Throwable cause = e.getCause();
                if (cause instanceof OTPException) throw (OTPException) cause;
                throw new OTPException(Status.INTERNAL_SERVER_ERROR, cause);
            } catch (Exception e) {
                throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
            }
        };
    }

    private Map<String, String> extractQueryParams(String query) {
        if (query == null || query.isBlank()) return Collections.emptyMap();
        Map<String, String> params = new HashMap<>();
        for (String pair : query.split("&")) {
            String[] p = pair.split("=");
            if (p.length != 2) {
                throw new OTPException(Status.BAD_REQUEST, "Invalid query param: " + pair);
            }
            params.put(p[0], p[1]);
        }
        return params;
    }

    private Map<String, String> extractPathVariables(Route route, String path) {
        Map<String, String> result = new HashMap<>();
        Matcher matcher = route.getPattern().matcher(path);
        if (matcher.matches()) {
            Matcher names = Pattern.compile("\\{(.+?)\\}").matcher(route.getPathPattern());
            int index = 1;
            while (names.find()) {
                result.put(names.group(1), matcher.group(index++));
            }
        }
        return result;
    }

    public String getRestPath() {
        return restPath;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        String path = exchange.getRequestURI().getPath().substring(restPath.length());
        String method = exchange.getRequestMethod();

        Response<?> response = routes.keySet().stream()
                .filter(r -> r.matches(method, path))
                .findFirst()
                .map(route -> routes.get(route).apply(route, exchange))
                .orElseThrow(() -> new OTPException(Status.METHOD_NOT_ALLOWED,
                        "Route not found: " + method + " " + restPath + path));

        sendResponse(exchange, response);
    }

    private void sendResponse(HttpExchange exchange, Response<?> response) {
        exchange.getResponseHeaders().add("Content-Type", "application/json");

        String responseBody = "";
        try {
            if (response.getBody() != null) {
                responseBody = mapper.writeValueAsString(response.getBody());
            }
        } catch (JsonProcessingException e) {
            throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
        }

        try {
            if (response.getStatus() == Status.NO_CONTENT) {
                exchange.sendResponseHeaders(response.getStatus().getCode(), -1);
            } else {
                byte[] bytes = responseBody.getBytes(StandardCharsets.UTF_8);
                exchange.sendResponseHeaders(response.getStatus().getCode(), bytes.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(bytes);
                }
            }
        } catch (IOException e) {
            throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
        }
    }
}