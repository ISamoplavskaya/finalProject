package ua.com.finalproject.exeption.exeptionHandler;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import ua.com.finalproject.exeption.AlreadyExistsException;
import ua.com.finalproject.exeption.EmailSendingException;
import ua.com.finalproject.exeption.NotFoundException;
import ua.com.finalproject.exeption.RelatedResourceAccessException;

@ControllerAdvice
public class ServiceExceptionHandler {
    @ExceptionHandler(AlreadyExistsException.class)
    public ResponseEntity<String> handleUsernameAlreadyExistsException(AlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleFoundException(NotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Search exception: " + ex.getMessage());
    }

    @ExceptionHandler(RelatedResourceAccessException.class)
    public ResponseEntity<String> handleRelatedResourceAccessException(RelatedResourceAccessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Related resource access exception: " + ex.getMessage());
    }

    @ExceptionHandler(EmailSendingException.class)
    public ResponseEntity<String> handleEmailSendingException(EmailSendingException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Email exception: " + ex.getMessage());
    }

}
