package com.swivel.ignite.student.service;

import com.swivel.ignite.student.dto.response.StudentResponseDto;
import com.swivel.ignite.student.exception.TuitionServiceHttpClientErrorException;
import com.swivel.ignite.student.wrapper.StudentResponseWrapper;
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
 * Tuition Microservice
 */
@Slf4j
@Service
public class TuitionService {

    private static final String AUTH_HEADER = "Authorization";
    private static final String FAILED_TO_REMOVE_STUDENT_FROM_TUITION = "Failed to remove student from tuition";
    private final RestTemplate restTemplate;
    private final String getRemoveStudentUrl;

    public TuitionService(@Value("${tuition.baseUrl}") String baseUrl,
                          @Value("${tuition.removeStudentUrl}") String removeStudentUrl,
                          RestTemplate restTemplate) {
        this.getRemoveStudentUrl = baseUrl + removeStudentUrl;
        this.restTemplate = restTemplate;
    }

    /**
     * This method removes a student from tuition in tuition microservice
     *
     * @param studentId student id
     * @param tuitionId tuition id
     * @throws IOException
     */
    public StudentResponseDto removeStudent(String studentId, String tuitionId, String token) throws IOException {
        Map<String, String> uriParam = new HashMap<>();
        uriParam.put("studentId", studentId);
        uriParam.put("tuitionId", tuitionId);
        UriComponents builder = UriComponentsBuilder.fromHttpUrl(getRemoveStudentUrl).build();
        HttpHeaders headers = new HttpHeaders();
        headers.set(AUTH_HEADER, token);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            log.debug("Calling tuition service to remove a student from tuition. url: {}", getRemoveStudentUrl);
            ResponseEntity<StudentResponseWrapper> result = restTemplate.exchange(builder.toUriString(), HttpMethod.POST,
                    entity, StudentResponseWrapper.class, uriParam);
            String responseBody = Objects.requireNonNull(result.getBody()).getData().toLogJson();
            log.debug("Removing student from tuition was successful. statusCode: {}, response: {}", result.getStatusCode(),
                    responseBody);
            return Objects.requireNonNull(result.getBody()).getData();
        } catch (HttpClientErrorException e) {
            log.error(" Error from reg: {}", e.getMessage());
            throw new TuitionServiceHttpClientErrorException(e.getStatusCode(), FAILED_TO_REMOVE_STUDENT_FROM_TUITION,
                    e.getResponseBodyAsString(), e);
        }
    }
}
