package com.swivel.ignite.student.exception;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;

import java.io.IOException;

/**
 * Auth Microservice client error exception
 */
public class AuthServiceHttpClientErrorException extends HttpClientErrorException {

    public final String status;
    public final transient JsonNode responseBody;
    public final Exception exception;

    public AuthServiceHttpClientErrorException(HttpStatus statusCode, String statusText, String responseBody,
                                               String status, Exception e) throws IOException {
        super(statusCode, statusText);
        this.status = status;
        this.responseBody = getJsonObject(responseBody);
        this.exception = e;
    }

    public AuthServiceHttpClientErrorException(HttpStatus statusCode, String message) throws IOException {
        super(statusCode, message);
        this.status = null;
        this.responseBody = null;
        this.exception = null;
    }

    private JsonNode getJsonObject(String jsonString) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.readTree(jsonString);
    }
}
