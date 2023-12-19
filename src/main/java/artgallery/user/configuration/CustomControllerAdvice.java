package artgallery.user.configuration;

import artgallery.user.exception.DatabaseConflictException;
import artgallery.user.exception.DoesNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.result.method.annotation.ResponseEntityExceptionHandler;

@ControllerAdvice
public class CustomControllerAdvice extends ResponseEntityExceptionHandler {

  @ResponseBody
  @ExceptionHandler({DoesNotExistException.class})
  public ResponseEntity<?> handleDoesNotExist(DoesNotExistException ex) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
  }

  @ResponseBody
  @ExceptionHandler({DatabaseConflictException.class})
  public ResponseEntity<?> handleDatabaseConflict(DatabaseConflictException ex) {
    return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
  }
}
