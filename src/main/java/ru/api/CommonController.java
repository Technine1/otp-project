package ru.api;

import ru.api.common.Route;

import java.util.Map;

public class CommonController {
    protected String controllerPath;
    private Map<Route, Object> routes;


    public Map<Route, Object> getRoutes() {
        return routes;
    }

    public void setRoutes(Map<Route, Object> routes) {
        this.routes = routes;
    }
}
