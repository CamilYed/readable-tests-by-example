package tech.allegro.blog.vinyl.shop.common.api;

import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import tech.allegro.blog.vinyl.shop.common.json.ErrorsJson;

import java.util.LinkedList;
import java.util.List;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Order(Ordered.HIGHEST_PRECEDENCE)
@ControllerAdvice
class MethodArgumentNotValidExceptionHandler {

  @ResponseStatus(BAD_REQUEST)
  @ResponseBody
  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ErrorsJson methodArgumentNotValidException(MethodArgumentNotValidException ex) {
    BindingResult result = ex.getBindingResult();
    List<org.springframework.validation.FieldError> fieldErrors = result.getFieldErrors();
    return processFieldErrors(fieldErrors);
  }

  private ErrorsJson processFieldErrors(List<org.springframework.validation.FieldError> fieldErrors) {
    List<ErrorsJson.Error> errors = new LinkedList<>();
    for (org.springframework.validation.FieldError fieldError : fieldErrors) {
      final var errorJson = ErrorsJson.Error.builder()
        .withCode(BAD_REQUEST.toString())
        .withPath(fieldError.getField())
        .withMessage(fieldError.getDefaultMessage())
        .withUserMessage("Validation error.")
        .build();
      errors.add(errorJson);
    }
    return new ErrorsJson(errors);
  }
}
