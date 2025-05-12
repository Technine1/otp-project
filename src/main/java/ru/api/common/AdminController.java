package ru.api.common;

import ru.api.common.annotation.Controller;
import ru.api.common.annotation.RouteMapping;

@Controller("/admin")
public class AdminController extends RequestHandler {

    @RouteMapping(method = "GET", path = "/users")
    public Response<Void> getUsers(Request request) {
        return Response.of(Status.OK);
    }

    @RouteMapping(method = "DELETE", path = "/users/{id}")
    public Response<Void> deleteUser(Request request) {
        return Response.of(Status.OK);
    }

    @RouteMapping(method = "PUT", path = "/otp")
    public Response<Void> updateOtp(Request request) {
        return Response.of(Status.OK);
    }

    @RouteMapping(method = "DELETE", path = "/otp/{id}")
    public Response<Void> deleteOtp(Request request) {
        return Response.of(Status.OK);
    }
}

