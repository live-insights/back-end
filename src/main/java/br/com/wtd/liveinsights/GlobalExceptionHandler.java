package br.com.wtd.liveinsights;

import br.com.wtd.liveinsights.dto.ErrorResponse;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return buildErrorResponse(ex, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildErrorResponse(ex, HttpStatus.NOT_FOUND);
    }

    private ResponseEntity<ErrorResponse> buildErrorResponse(Exception ex, HttpStatus status) {
        String message = logException(ex);
        return ResponseEntity.status(status).body(new ErrorResponse(message, status));
    }

    @SuppressWarnings("CallToPrintStackTrace")
    private String logException(Exception ex) {
        ex.printStackTrace();
        return ex.getMessage();
    }
}
