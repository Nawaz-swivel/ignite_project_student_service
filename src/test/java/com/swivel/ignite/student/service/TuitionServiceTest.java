package com.swivel.ignite.student.service;

import com.swivel.ignite.student.dto.response.StudentResponseDto;
import com.swivel.ignite.student.exception.TuitionServiceHttpClientErrorException;
import com.swivel.ignite.student.wrapper.StudentResponseWrapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests {@link TuitionService} class
 */
class TuitionServiceTest {

    private static final String TOKEN = "Bearer 123456789";
    private static final String STUDENT_ID = "sid-123456789";
    private static final String TUITION_ID = "tid-123456789";
    private static final String STUDENT_NAME = "Mohamed Nawaz";
    private static final String BASE_URL = "http://localhost:8080/ignite-tuition-service";
    private static final String REMOVE_STUDENT_URL = "/api/v1/tuition/remove/student/{studentId}/tuition/{tuitionId}";
    private TuitionService tuitionService;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        initMocks(this);
        tuitionService = new TuitionService(BASE_URL, REMOVE_STUDENT_URL, restTemplate);
    }

    /**
     * Start of tests for deleteByStudentId method
     */
    @Test
    void Should_ReturnStudentResponseDto_When_RemovingStudentIsSuccessful() throws IOException {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class),
                anyMap())).thenReturn(getSampleResponseEntity());
        assertEquals(STUDENT_NAME, tuitionService.removeStudent(STUDENT_ID, TUITION_ID, TOKEN).getUsername());
    }

    @Test
    void Should_ThrowTuitionServiceHttpClientErrorException_When_RemovingStudentIsFailed() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), any(Class.class),
                anyMap())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        TuitionServiceHttpClientErrorException exception = assertThrows(TuitionServiceHttpClientErrorException.class,
                () -> tuitionService.removeStudent(STUDENT_ID, TUITION_ID, TOKEN));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Failed to remove student from tuition",
                exception.getMessage());
    }

    /**
     * This method returns a sample response entity
     *
     * @return Response Entity
     */
    private ResponseEntity<StudentResponseWrapper> getSampleResponseEntity() {
        return new ResponseEntity<>(getSampleStudentResponseWrapper(), HttpStatus.OK);
    }

    /**
     * This method returns a sample StudentResponseWrapper
     *
     * @return StudentResponseWrapper
     */
    private StudentResponseWrapper getSampleStudentResponseWrapper() {
        StudentResponseWrapper responseWrapper = new StudentResponseWrapper();
        responseWrapper.setData(getSampleStudentResponseDto());
        return responseWrapper;
    }

    /**
     * This method returns a sample StudentResponseDto
     *
     * @return StudentResponseDto
     */
    private StudentResponseDto getSampleStudentResponseDto() {
        StudentResponseDto responseDto = new StudentResponseDto();
        responseDto.setUsername(STUDENT_NAME);
        return responseDto;
    }
}
