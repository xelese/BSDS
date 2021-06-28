package Model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Response {

    public final String message;

    public Response(@JsonProperty("message") String message, String message1) {
        this.message = message1;
    }

}
