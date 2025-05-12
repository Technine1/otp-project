package ru.technine1.otp.api.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import ru.technine1.otp.api.common.annotation.*;
import ru.technine1.otp.exception.OTPException;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class RequestHandler implements HttpHandler {
    protected static final ObjectMapper mapper = new ObjectMapper();
    protected String controllerPath;
    private String restPath;
    protected Map<Route, BiFunction<Route, HttpExchange, Response<?>>> routes = new HashMap<>();

    public RequestHandler() {
        Controller controller = this.getClass().getAnnotation(Controller.class);
        if (controller != null) {
            this.restPath = controller.value();
        }
        for (Method method : this.getClass().getDeclaredMethods()) {
            RouteMapping route = method.getAnnotation(RouteMapping.class);
            if (route != null) {
                routes.put(Route.of(route.method(), route.path()), process(method));
            }
        }
    }

    private BiFunction<Route, HttpExchange, Response<?>> process(Method method) {
        return (route, exchange) -> {
            try {
                Map<String, Object> body = Collections.emptyMap();
                try {
                    String rawBody = new String(exchange.getRequestBody().readAllBytes());
                    if (!rawBody.isBlank()) {
                        body = mapper.readValue(rawBody, new TypeReference<>() {
                        });
                    }
                } catch (IOException e) {
                    throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
                }
                Map<String, String> pathVariables = extractPathVariables(route, exchange.getRequestURI().getPath().substring(restPath.length()));
                Map<String, String> queryParams = extractQueryParams(exchange.getRequestURI().getQuery());

                Parameter[] parameters = method.getParameters();
                Object[] args = new Object[parameters.length];

                for (int i = 0; i < parameters.length; i++) {
                    Parameter parameter = parameters[i];
                    if (parameter.isAnnotationPresent(Body.class)) {
                        args[i] = mapper.convertValue(body, parameter.getType());
                    }
                    else if (parameter.isAnnotationPresent(QueryParam.class)) {
                        QueryParam annotation = parameter.getAnnotation(QueryParam.class);
                        String value = queryParams.get(annotation.value());
                        if (value == null && annotation.required()) {
                            throw new OTPException(Status.BAD_REQUEST,
                                    "Missing required query parameter: " + annotation.value());
                        }
                        args[i] = mapper.convertValue(value, parameter.getType());
                    }
                    else if (parameter.isAnnotationPresent(PathVariable.class)) {
                        PathVariable annotation = parameter.getAnnotation(PathVariable.class);
                        String value = pathVariables.get(annotation.value());
                        if (value != null) {
                            args[i] = mapper.convertValue(value, parameter.getType());
                        }
                    }
                }
                return (Response<?>) method.invoke(this, args);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                if (e.getCause() instanceof OTPException) {
                    throw ((OTPException) e.getCause());
                }
                throw new RuntimeException(e);
            }
        };
    }

    private Map<String, String> extractPathVariables(Route route, String path) {
        Map<String, String> vars = new HashMap<>();
        Matcher matcher = route.getPattern().matcher(path);
        if (matcher.matches()) {
            Matcher patternMatcher = Pattern.compile("\\{(.+?)\\}").matcher(route.getPathPattern());
            int groupIndex = 1;
            while (patternMatcher.find()) {
                String variableName = patternMatcher.group(1);
                String variableValue = matcher.group(groupIndex++);
                vars.put(variableName, variableValue);
            }
        }
        return vars;
    }

    private static Map<String, String> extractQueryParams(String query) {
        if (query == null) {
            return Collections.emptyMap();
        }
        Map<String, String> rsl = new HashMap<>();
        String[] params = query.split("&");
        for (String param : params) {
            String[] p = param.split("=");
            if (p.length != 2) {
                throw new OTPException(Status.BAD_REQUEST, "Invalid query params " + param);
            }
            rsl.put(p[0], p[1]);
        }
        return rsl;
    }

    public String getRestPath() {
        return restPath;
    }

    @Override
    public void handle(com.sun.net.httpserver.HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath().substring(restPath.length());
        String method = exchange.getRequestMethod();
        Response response = routes.keySet().stream()
                .filter(r -> r.matches(method, path))
                .findFirst()
                .map(route -> routes.get(route).apply(route, exchange))
                .orElseThrow(() -> new OTPException(Status.METHOD_NOT_ALLOWED, "Route " + method + " " + restPath + path + " not found"));
//                .orElse(new Response(Status.METHOD_NOT_ALLOWED, new ErrorDTO("Route " + method + " " + path + " not found")));
//        .map(r -> {
//            Map<String, String> variables = r.extractPathVariables(path);
//            Map<String, Object> body = Collections.emptyMap();
//            try {
//                String rawBody = new String(exchange.getRequestBody().readAllBytes());
//                if (!rawBody.isBlank()) {
//                    body = mapper.readValue(rawBody, new TypeReference<>() {});
//                }
//            } catch (IOException e) {
//                throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
//            }
//            Map<String, String> params = extractQueryParams(exchange.getRequestURI().getQuery());
//            Principal principal = (Principal) exchange.getPrincipal();
//            return routes.get(r).apply(new Request(body, params, variables, principal));
//        })//todo разделить

//                .orElseThrow(() -> Response.of(ResponseCode.METHOD_NOT_ALLOWED, new ErrorDTO("Route " + method + " " + path + " not found")));
        sendResponse(exchange, response);
    }

    //todo
    private static void sendResponse(com.sun.net.httpserver.HttpExchange exchange, Response<?> response) {
        exchange.getResponseHeaders().add("Content-Type", "application/json");
        String body = null;
        try {
            body = response.getBody() != null ? mapper.writeValueAsString(response.getBody()) : "";
        } catch (JsonProcessingException e) {
            throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
        }
        try {
            exchange.sendResponseHeaders(response.getStatus().getCode(), response.getStatus().equals(Status.NO_CONTENT) ? -1 : body.length());
        } catch (IOException e) {
            throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
        }
        if (!response.getStatus().equals(Status.NO_CONTENT)) {
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body.getBytes(StandardCharsets.UTF_8));
            } catch (IOException e) {
                throw new OTPException(Status.INTERNAL_SERVER_ERROR, e);
            }
        }
    }
}
