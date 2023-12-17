package artgallery.user.configuration;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.*;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
public class CustomWebExceptionHandler extends AbstractErrorWebExceptionHandler {
  private final Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode;
  private final HttpStatus defaultStatus;

  public CustomWebExceptionHandler(ErrorAttributes errorAttributes, WebProperties.Resources resources,
                                   ApplicationContext applicationContext, Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode,
                                   HttpStatus defaultStatus) {
    super(errorAttributes, resources, applicationContext);
    this.exceptionToStatusCode = exceptionToStatusCode;
    this.defaultStatus = defaultStatus;
  }

  @Override
  protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
    return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
  }

  private Mono<ServerResponse> renderErrorResponse(ServerRequest request) {
    Throwable error = this.getError(request);

    HttpStatus httpStatus = exceptionToStatusCode.getOrDefault(error.getClass(), defaultStatus);

    return ServerResponse
      .status(httpStatus)
      .contentType(MediaType.TEXT_PLAIN)
      .body(BodyInserters.fromValue(error.getMessage()));
  }
}
