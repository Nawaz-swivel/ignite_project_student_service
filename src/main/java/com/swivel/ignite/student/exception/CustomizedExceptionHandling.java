package com.swivel.ignite.student.exception;

import com.swivel.ignite.student.enums.ErrorResponseStatusType;
import com.swivel.ignite.student.enums.ResponseStatusType;
import com.swivel.ignite.student.wrapper.ErrorResponseWrapper;
import com.swivel.ignite.student.wrapper.ResponseWrapper;
import com.swivel.ignite.student.wrapper.RestErrorResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
@Slf4j
public class CustomizedExceptionHandling extends ResponseEntityExceptionHandler {

    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";

    /**
     * This method returns an error response for student service exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(StudentServiceException.class)
    public ResponseEntity<ResponseWrapper> handleStudentServiceException(StudentServiceException exception) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method returns an error response for student not found exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ResponseWrapper> handleStudentNotFoundException(StudentNotFoundException exception) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .STUDENT_NOT_FOUND.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType.STUDENT_NOT_FOUND
                .getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method returns an error response for student already exists exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ResponseWrapper> handleStudentAlreadyExistsException(StudentAlreadyExistsException exception) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .STUDENT_ALREADY_EXISTS.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .STUDENT_ALREADY_EXISTS.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    /**
     * This method returns an error response for auth microservice call exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(AuthServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handleAuthServiceHttpClientErrorException(
            AuthServiceHttpClientErrorException exception) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR.getMessage(),
                exception.status, exception.responseBody, ERROR_MESSAGE,
                ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method returns an error response for tuition microservice call exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(TuitionServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handleTuitionServiceHttpClientErrorException(
            TuitionServiceHttpClientErrorException exception) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.TUITION_INTERNAL_SERVER_ERROR.getMessage(),
                null, exception.responseBody, ERROR_MESSAGE, ErrorResponseStatusType
                .TUITION_INTERNAL_SERVER_ERROR.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method returns an error response for payment microservice call exceptions
     *
     * @param exception exception
     * @return error response
     */
    @ExceptionHandler(PaymentServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentServiceHttpClientErrorException(
            PaymentServiceHttpClientErrorException exception) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.PAYMENT_INTERNAL_SERVER_ERROR.getMessage(),
                null, exception.responseBody, ERROR_MESSAGE, ErrorResponseStatusType
                .PAYMENT_INTERNAL_SERVER_ERROR.getCode());
        log.error(exception.getMessage());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
