package ru.technine1.otp.api.common;

public class Response<T> {

    private Status status;
    private T body;

    public Response(Status status) {
        this.status = status;
    }

    public Response(Status status, T body) {
        this.status = status;
        this.body = body;
    }

    public Status getStatus() {
        return status;
    }

    public T getBody() {
        return body;
    }

    public static <T> Response<T> of(Status code) {
        return new Response<>(code);
    }

    public static <T> Response<T> of(Status code, T body) {
        return new Response<>(code, body);
    }
}
