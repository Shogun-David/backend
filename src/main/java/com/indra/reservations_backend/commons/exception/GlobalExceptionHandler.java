package com.indra.reservations_backend.commons.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.indra.reservations_backend.commons.dto.ExceptionDto;

import org.springframework.web.bind.annotation.ResponseStatus;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

  private HttpStatus getStatus(Throwable ex) {
    ResponseStatus status = ex.getClass().getAnnotation(ResponseStatus.class);
    if (status != null) {
      return status.value();
    }
    return HttpStatus.INTERNAL_SERVER_ERROR;
  }

  @ExceptionHandler({
      BadRequestException.class,
      ResourceNotFoundException.class,
      BussinessException.class
  })
  public ResponseEntity<ExceptionDto> handleCustomExceptions(RuntimeException ex, HttpServletRequest request) {
    HttpStatus status = getStatus(ex);

    ExceptionDto dto = ExceptionDto.builder()
        .hora(LocalDateTime.now().format(FORMATTER))
        .mensaje(ex.getMessage())
        .url(request.getRequestURI())
        .codeStatus(status.value())
        .build();

    if (status.is5xxServerError()) {
        log.error("Error interno", ex);
    } else {
        log.warn("Error funcional: {}", ex.getMessage());
    }


    return ResponseEntity.status(status).body(dto);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ExceptionDto> handleValidationException(MethodArgumentNotValidException ex,
      HttpServletRequest request) {
    HttpStatus status = HttpStatus.BAD_REQUEST;

    String mensaje = ex.getBindingResult().getAllErrors().stream()
        .map(error -> {
          if (error instanceof FieldError fe) {
            return fe.getField() + ": " + fe.getDefaultMessage();
          } else {
            return error.getDefaultMessage();
          }
        })
        .collect(Collectors.joining(", "));

    ExceptionDto dto = ExceptionDto.builder()
        .hora(LocalDateTime.now().format(FORMATTER))
        .mensaje(mensaje)
        .url(request.getRequestURI())
        .codeStatus(status.value())
        .build();

    log.error("ValidationException: {}", mensaje);

    return ResponseEntity.status(status).body(dto);
  }


  // @ExceptionHandler(JWTVerificationException.class)
  // public ResponseEntity<?> handleJwtException(JWTVerificationException ex
    
  // ) {

  //     String mensaje = ex.getMessage();
  //     HttpStatus status = HttpStatus.BAD_REQUEST;
  //     ExceptionDto dto = ExceptionDto.builder()
  //         .hora(LocalDateTime.now().format(FORMATTER))
  //         .mensaje(mensaje)
  //         //.url(request.getRequestURI())
  //         .url("")
  //         .codeStatus(status.value())
  //         .build();;
  //     return ResponseEntity.status(status).body(dto);
  // }  
}
