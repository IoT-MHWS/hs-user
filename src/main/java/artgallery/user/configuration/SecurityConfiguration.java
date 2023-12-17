package artgallery.user.configuration;

import artgallery.user.configuration.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.HttpStatusServerEntryPoint;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

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

}
