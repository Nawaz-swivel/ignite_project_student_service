package com.swivel.ignite.student.exception;

import com.swivel.ignite.student.enums.ErrorResponseStatusType;
import com.swivel.ignite.student.enums.ResponseStatusType;
import com.swivel.ignite.student.wrapper.ErrorResponseWrapper;
import com.swivel.ignite.student.wrapper.ResponseWrapper;
import com.swivel.ignite.student.wrapper.RestErrorResponseWrapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomizedExceptionHandling extends ResponseEntityExceptionHandler {

    private static final String ERROR_MESSAGE = "Oops!! Something went wrong. Please try again.";

    @ExceptionHandler(StudentServiceException.class)
    public ResponseEntity<ResponseWrapper> handleStudentServiceException(StudentServiceException exception,
                                                                         WebRequest request) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(StudentNotFoundException.class)
    public ResponseEntity<ResponseWrapper> handleStudentNotFoundException(StudentNotFoundException exception,
                                                                          WebRequest request) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .STUDENT_NOT_FOUND.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType.STUDENT_NOT_FOUND
                .getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(StudentAlreadyExistsException.class)
    public ResponseEntity<ResponseWrapper> handleStudentAlreadyExistsException(StudentAlreadyExistsException exception,
                                                                               WebRequest request) {
        ResponseWrapper responseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR, ErrorResponseStatusType
                .STUDENT_ALREADY_EXISTS.getMessage(), null, ERROR_MESSAGE, ErrorResponseStatusType
                .STUDENT_ALREADY_EXISTS.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(AuthServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handleAuthServiceHttpClientErrorException(
            AuthServiceHttpClientErrorException exception, WebRequest request) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR.getMessage(),
                exception.status, exception.responseBody, ERROR_MESSAGE,
                ErrorResponseStatusType.AUTH_INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(TuitionServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handleTuitionServiceHttpClientErrorException(
            TuitionServiceHttpClientErrorException exception, WebRequest request) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.TUITION_INTERNAL_SERVER_ERROR.getMessage(),
                null, exception.responseBody, ERROR_MESSAGE, ErrorResponseStatusType
                .TUITION_INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(PaymentServiceHttpClientErrorException.class)
    public ResponseEntity<ResponseWrapper> handlePaymentServiceHttpClientErrorException(
            PaymentServiceHttpClientErrorException exception, WebRequest request) {
        ResponseWrapper responseWrapper = new RestErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.PAYMENT_INTERNAL_SERVER_ERROR.getMessage(),
                null, exception.responseBody, ERROR_MESSAGE, ErrorResponseStatusType
                .PAYMENT_INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(responseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
