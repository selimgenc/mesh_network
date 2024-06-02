package me.selim.mesh.web.rest;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import me.selim.mesh.error.ResourceDoesNotExistException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.io.IOException;

@ControllerAdvice
public class RestGlobalExceptionHandler extends ResponseEntityExceptionHandler {
    protected static final Logger log = LoggerFactory.getLogger(RestGlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        log.error("An internal server error occurred", ex);
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("There is a HTTP request method not supported", ex);
        return super.handleHttpRequestMethodNotSupported(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        log.error("There is a Method argument not valid", ex);
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

    @ExceptionHandler(ResourceDoesNotExistException.class)
    void handleResourceDoesNotExistException(ResourceDoesNotExistException ex,
                                             HttpServletRequest request, HttpServletResponse response) throws IOException {
        log.error("There is a resource does not exist", ex);
        response.sendError(HttpStatus.NOT_FOUND.value(),
                "The resource '" + request.getRequestURI() + "' does not exist");
    }


    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArguments(IllegalArgumentException ex) {
        log.error("There is an illegal argument", ex);
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, ex.getMessage());
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }
}