package ru.api.common;

import java.util.Objects;
import java.util.regex.Pattern;

public final class Route {
    private final String method;
    private final String pathPattern;
    private final Pattern pattern;

    public Route(String method, String pathPattern) {
        this.method = method;
        this.pathPattern = pathPattern;
        this.pattern = compilePattern(pathPattern);
    }

    private Pattern compilePattern(String pattern) {
        String regex = pattern.replaceAll("\\{.+?\\}", "([^/]+)");
        return Pattern.compile("^" + regex + "$");
    }

    public boolean matches(String method, String path) {
        return this.method.equals(method) && pattern.matcher(path).matches();
    }

    public String getPathPattern() {
        return pathPattern;
    }

    public Pattern getPattern() {
        return pattern;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Route route = (Route) o;
        return Objects.equals(method, route.method) &&
                Objects.equals(pathPattern, route.pathPattern);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, pathPattern);
    }

    public static Route of(String method, String path) {
        return new Route(method, path);
    }
}
