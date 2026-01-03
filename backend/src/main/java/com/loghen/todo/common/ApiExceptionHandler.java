package com.loghen.todo.common;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestControllerAdvice
public class ApiExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorBody> badRequest(IllegalArgumentException ex, HttpServletRequest req) {
    log.warn("400 {} - {}", req.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
        new ErrorBody(Instant.now().toString(), 400, "Bad Request", ex.getMessage(), req.getRequestURI())
    );
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorBody> notFound(NotFoundException ex, HttpServletRequest req) {
    log.warn("404 {} - {}", req.getRequestURI(), ex.getMessage());
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
        new ErrorBody(Instant.now().toString(), 404, "Not Found", ex.getMessage(), req.getRequestURI())
    );
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorBody> serverError(Exception ex, HttpServletRequest req) {
    log.error("500 {} - {}", req.getRequestURI(), ex.getMessage(), ex);
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
        new ErrorBody(Instant.now().toString(), 500, "Internal Server Error", "Unexpected error", req.getRequestURI())
    );
  }

  public record ErrorBody(String timestamp, int status, String error, String message, String path) {}
}
