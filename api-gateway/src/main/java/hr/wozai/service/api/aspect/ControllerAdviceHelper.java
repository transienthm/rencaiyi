package hr.wozai.service.api.aspect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * @author lepujiu
 * @version 1.0
 * @created 16/9/21
 */
@ControllerAdvice
public class ControllerAdviceHelper {
  private static final Logger LOGGER = LoggerFactory.getLogger(ControllerAdviceHelper.class);

  @ExceptionHandler(MissingServletRequestParameterException.class)
  public void handleMissingParams(MissingServletRequestParameterException ex) {
    LOGGER.error(ex.toString());
  }
}
