package artgallery.user.configuration.jwt;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter implements WebFilter {

  public static final String JWT_PREFIX = "Bearer ";

  private final JwtService jwtService;
  private final ReactiveUserDetailsService userDetailsService;

  @Override
  public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
    String token = resolveToken(exchange.getRequest());

    if (StringUtils.hasText(token) && this.jwtService.isTokenValid(token)) {
      return userDetailsService.findByUsername(jwtService.extractUsername(token))
        .map(userDetails -> new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities()))
        .flatMap(authentication -> chain.filter(exchange)
          .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication)));
    }

    return chain.filter(exchange);
  }

  private String resolveToken(ServerHttpRequest request) {
    String bearerToken = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
    if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(JWT_PREFIX)) {
      return bearerToken.substring(JWT_PREFIX.length());
    }
    return null;
  }

}
