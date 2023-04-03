package com.swivel.ignite.student.service;

import com.swivel.ignite.student.dto.request.StudentCreateRequestDto;
import com.swivel.ignite.student.dto.response.UserResponseDto;
import com.swivel.ignite.student.exception.AuthServiceHttpClientErrorException;
import com.swivel.ignite.student.wrapper.UserResponseWrapper;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests {@link AuthService} class
 */
class AuthServiceTest {

    private static final String USER_ID = "uid-123456789";
    private static final String USER_NAME = "Mohamed Nawaz";
    private static final String TOKEN = "Bearer 123456789";
    private static final String BASE_URL = "http://localhost:8080/ignite-auth-service";
    private static final String CREATE_USER_URL = "/api/v1/auth/users/register";
    private static final String DELETE_USER_URL = "/api/v1/auth/users/delete/{username}";
    private AuthService authService;
    @Mock
    private RestTemplate restTemplate;

    @BeforeEach
    void setUp() {
        initMocks(this);
        authService = new AuthService(BASE_URL, CREATE_USER_URL, DELETE_USER_URL, restTemplate);
    }

    /**
     * Start of tests for registerStudent method
     */
    @Test
    void Should_RegisterStudent_When_RegisteringStudentIsSuccessful() throws IOException {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class),
                eq(UserResponseWrapper.class))).thenReturn(getSampleUserResponseEntity());
        assertEquals(USER_ID, authService.registerStudent(getSampleStudentCreateRequestDto(), TOKEN).getUserId());
    }

    @Test
    void Should_ThrowAuthServiceHttpClientErrorException_When_RegisteringStudentIsFailed() {
        StudentCreateRequestDto requestDto = getSampleStudentCreateRequestDto();

        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(UserResponseWrapper.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        AuthServiceHttpClientErrorException exception = assertThrows(AuthServiceHttpClientErrorException.class,
                () -> authService.registerStudent(requestDto, TOKEN));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Failed to register student in auth db",
                exception.getMessage());
    }

    /**
     * Start of tests for deleteStudent method
     */
    @Test
    void Should_DeleteStudent_When_DeletingStudentIsSuccessful() throws IOException {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class),
                anyMap())).thenReturn(getSampleResponseEntity());
        authService.deleteStudent(USER_NAME, TOKEN);
        verify(restTemplate).exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class),
                anyMap());
    }

    @Test
    void Should_ThrowAuthServiceHttpClientErrorException_When_DeletingStudentIsFailed() {
        when(restTemplate.exchange(anyString(), any(HttpMethod.class), any(HttpEntity.class), eq(String.class),
                anyMap())).thenThrow(new HttpClientErrorException(HttpStatus.INTERNAL_SERVER_ERROR));
        AuthServiceHttpClientErrorException exception = assertThrows(AuthServiceHttpClientErrorException.class,
                () -> authService.deleteStudent(USER_NAME, TOKEN));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR.value() + " Failed to delete student from auth db",
                exception.getMessage());
    }

    /**
     * This method returns a sample response entity
     *
     * @return Response Enity
     */
    private ResponseEntity<UserResponseWrapper> getSampleUserResponseEntity() {
        return new ResponseEntity<>(getSampleUserResponseWrapper(), HttpStatus.OK);
    }

    /**
     * This method returns a sample UserResponseWrapper
     *
     * @return UserResponseWrapper
     */
    private UserResponseWrapper getSampleUserResponseWrapper() {
        UserResponseWrapper responseWrapper = new UserResponseWrapper();
        responseWrapper.setData(getSampleUserResponseDto());
        return responseWrapper;
    }

    /**
     * This method returns a sample UserResponseDto
     *
     * @return UserResponseDto
     */
    private UserResponseDto getSampleUserResponseDto() {
        UserResponseDto responseDto = new UserResponseDto();
        responseDto.setUserId(USER_ID);
        return responseDto;
    }

    /**
     * This method returns a sample StudentCreateRequestDto
     *
     * @return StudentCreateRequestDto
     */
    StudentCreateRequestDto getSampleStudentCreateRequestDto() {
        StudentCreateRequestDto requestDto = new StudentCreateRequestDto();
        requestDto.setUsername(USER_NAME);
        return requestDto;
    }

    /**
     * This method returns a sample response entity
     *
     * @return Response Enity
     */
    private ResponseEntity<String> getSampleResponseEntity() {
        return new ResponseEntity<>("Response", HttpStatus.OK);
    }
}
