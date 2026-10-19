package net.iozamudioa.fueler.infrastructure.adapters.in.web.exception;

import java.net.URI;

import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import net.iozamudioa.fueler.domain.exception.InvalidFuelRecordException;
import net.iozamudioa.fueler.domain.exception.ResourceAlreadyExistsException;
import net.iozamudioa.fueler.domain.exception.ResourceNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleNotFound(
            ResourceNotFoundException ex) {

        return buildProblem(
                HttpStatus.NOT_FOUND,
                "Resource not found",
                ex.getMessage(),
                "RESOURCE_NOT_FOUND");
    }

    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ProblemDetail> handleAlreadyExists(
            ResourceAlreadyExistsException ex) {

        return buildProblem(
                HttpStatus.CONFLICT,
                "Resource already exists",
                ex.getMessage(),
                "RESOURCE_ALREADY_EXISTS");
    }

    @ExceptionHandler(InvalidFuelRecordException.class)
    public ResponseEntity<ProblemDetail> handleInvalidFuelRecord(
            InvalidFuelRecordException ex) {

        return buildProblem(
                HttpStatus.BAD_REQUEST,
                "Invalid fuel record",
                ex.getMessage(),
                "INVALID_FUEL_RECORD");
    }

    private ResponseEntity<ProblemDetail> buildProblem(
            HttpStatus status,
            String title,
            String detail,
            String code) {

        ProblemDetail problem = ProblemDetail.forStatusAndDetail(
                status, detail);

        problem.setTitle(title);
        problem.setType(URI.create("about:blank"));
        problem.setProperty("code", code);

        return ResponseEntity.status(status).body(problem);
    }
}