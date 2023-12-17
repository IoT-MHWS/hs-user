package artgallery.user.configuration;

import artgallery.user.configuration.jwt.JwtAuthenticationFilter;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

import java.util.Map;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class SecurityConfiguration {
  private final String[] WHITE_LIST_URLS = {
    "/api-docs",
    "/api-docs/**",
    "/swagger-ui",
    "/swagger-ui/**",
    "/webjars/**"
  };

  private final ReactiveAuthenticationManager reactiveAuthenticationManager;
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Bean
  public SecurityWebFilterChain filterChain(ServerHttpSecurity http) {
    http
      .authorizeExchange(exchange -> exchange
        .pathMatchers(WHITE_LIST_URLS).permitAll()
        .pathMatchers("/api/v1/auth/login").permitAll()
        .anyExchange().authenticated()
      )
      .csrf(ServerHttpSecurity.CsrfSpec::disable)
      .cors(ServerHttpSecurity.CorsSpec::disable)
      .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
      .logout(ServerHttpSecurity.LogoutSpec::disable)
      .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION);

    return http.build();
  }

  @Bean
  @Order(-2)
  public CustomWebExceptionHandler customExceptionHandler(WebProperties webProperties, ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
    CustomWebExceptionHandler exceptionHandler = new CustomWebExceptionHandler(
      new DefaultErrorAttributes(), webProperties.getResources(), applicationContext, exceptionToStatusCode(), HttpStatus.INTERNAL_SERVER_ERROR
    );
    exceptionHandler.setMessageWriters(configurer.getWriters());
    exceptionHandler.setMessageReaders(configurer.getReaders());
    return exceptionHandler;
  }

  @Bean
  public Map<Class<? extends Exception>, HttpStatus> exceptionToStatusCode() {
    return Map.of(
      ExpiredJwtException.class, HttpStatus.UNAUTHORIZED,
      BadCredentialsException.class, HttpStatus.UNAUTHORIZED,
      SignatureException.class, HttpStatus.UNAUTHORIZED
    );
  }

}
