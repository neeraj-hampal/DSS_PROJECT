package liftride.Server;



import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionControllerAdvice {

 @ExceptionHandler(CustomException.class)
 public ResponseEntity<String> handleCustomException(CustomException e) {
     return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
 }
}
