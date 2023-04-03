package com.swivel.ignite.student.service;

import com.swivel.ignite.student.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.student.dto.response.UserResponseDto;
import com.swivel.ignite.student.exception.AuthServiceHttpClientErrorException;
import com.swivel.ignite.student.wrapper.UserResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Auth Microservice
 */
@Slf4j
@Service
public class AuthService {

    private static final String AUTH_HEADER = "Authorization";
    private static final String FAILED_TO_REGISTER_STUDENT_IN_AUTH_DB = "Failed to register student in auth db";
    private static final String FAILED_TO_DELETE_STUDENT_FROM_AUTH_DB = "Failed to delete student from auth db";
    private final RestTemplate restTemplate;
    private final String getCreateUserUrl;
    private final String getDeleteUserUrl;

    public AuthService(@Value("${auth.baseUrl}") String baseUrl,
                       @Value("${auth.createUserUrl}") String createUserUrl,
                       @Value("${auth.deleteUserUrl}") String deleteUserUrl,
                       RestTemplate restTemplate) {
        this.getCreateUserUrl = baseUrl + createUserUrl;
        this.getDeleteUserUrl = baseUrl + deleteUserUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * This method is used to register a student in auth db in auth microservice
     *
     * @param requestDto StudentCreateRequestDto
     * @param token      access token
     * @return UserResponseDto
     * @throws IOException
     */
    public UserResponseDto registerStudent(StudentCreateRequestDto requestDto, String token) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_HEADER, token);
        HttpEntity<StudentCreateRequestDto> entity = new HttpEntity<>(requestDto, headers);
        try {
            log.debug("Calling auth service to register the student in auth db. url: {}", getCreateUserUrl);
            ResponseEntity<UserResponseWrapper> result = restTemplate.exchange(getCreateUserUrl, HttpMethod.POST,
                    entity, UserResponseWrapper.class);
            String responseBody = Objects.requireNonNull(result.getBody()).getData().toLogJson();
            log.debug("Registering student in auth db was successful. statusCode: {}, response: {}", result.getStatusCode(),
                    responseBody);
            return Objects.requireNonNull(result.getBody()).getData();
        } catch (HttpClientErrorException e) {
            log.error(" Error from auth microservice: {}", e.getMessage());
            throw new AuthServiceHttpClientErrorException(e.getStatusCode(), FAILED_TO_REGISTER_STUDENT_IN_AUTH_DB,
                    e.getResponseBodyAsString(), e.getStatusText(), e);
        }
    }

    /**
     * This method is used to delete a student from auth db in auth microservice
     *
     * @param username username
     * @param token    access token
     * @throws IOException
     */
    public void deleteStudent(String username, String token) throws IOException {
        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("username", username);
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(getDeleteUserUrl).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_HEADER, token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            log.debug("Calling auth service to delete the student from auth db. url: {}", getDeleteUserUrl);
            ResponseEntity<String> result = restTemplate.exchange(builder.toUriString(), HttpMethod.DELETE,
                    entity, String.class, uriParam);
            log.debug("Deleting student from auth db was successful. statusCode: {}", result.getStatusCode());
        } catch (HttpClientErrorException e) {
            log.error(" Error from auth microservice: {}", e.getMessage());
            throw new AuthServiceHttpClientErrorException(e.getStatusCode(), FAILED_TO_DELETE_STUDENT_FROM_AUTH_DB,
                    e.getResponseBodyAsString(), e.getStatusText(), e);
        }
    }
}
