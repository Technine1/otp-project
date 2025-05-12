package ru.api.common;

import java.util.Map;

public record Request(Map<String, Object> body,
                      Map<String, String> queryParams,
                      Map<String, String> pathVariables) {
}
