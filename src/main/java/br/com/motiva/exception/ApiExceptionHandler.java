package br.com.motiva.exception;
import java.time.LocalDateTime;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
@ControllerAdvice
public class ApiExceptionHandler {
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Map<String,Object>> notFound(ResourceNotFoundException e) { return response(HttpStatus.NOT_FOUND, e.getMessage()); }
    @ExceptionHandler({IllegalArgumentException.class, MethodArgumentNotValidException.class, HttpMessageNotReadableException.class, MethodArgumentTypeMismatchException.class})
    public ResponseEntity<Map<String,Object>> badRequest(Exception e) { return response(HttpStatus.BAD_REQUEST, e instanceof MethodArgumentNotValidException ? "Dados invalidos na requisicao." : e.getMessage()); }
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> conflict(DataIntegrityViolationException e) { return response(HttpStatus.CONFLICT, "Registro viola uma restricao do banco de dados."); }
    private ResponseEntity<Map<String,Object>> response(HttpStatus status, String message) { return ResponseEntity.status(status).body(Map.of("status", status.value(), "message", message, "timestamp", LocalDateTime.now().toString())); }
}
