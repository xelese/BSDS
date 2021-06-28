package model;

public class Response<T> {
    T message;

    public Response(T message) {
        this.message = message;
    }

    public T getMessage() {
        return message;
    }
}
