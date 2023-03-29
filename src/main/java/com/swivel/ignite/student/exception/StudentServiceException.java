package com.swivel.ignite.student.exception;

/**
 * StudentServiceException
 */
public class StudentServiceException extends RuntimeException {

    /**
     * StudentServiceException with error message.
     *
     * @param errorMessage error message
     */
    public StudentServiceException(String errorMessage) {
        super(errorMessage);
    }

    /**
     * StudentServiceException with error message and throwable error
     *
     * @param errorMessage error message
     * @param error        error
     */
    public StudentServiceException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }
}
