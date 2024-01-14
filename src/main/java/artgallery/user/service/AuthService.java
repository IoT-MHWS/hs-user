package artgallery.user.service;

import artgallery.user.dto.TokenDTO;
import artgallery.user.dto.UserLoginDTO;
import artgallery.user.dto.UserRegisterDTO;
import artgallery.user.entity.UserEntity;
import artgallery.user.exception.UserDoesNotExistException;
import artgallery.user.repository.UserRepository;
import artgallery.user.configuration.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserRepository userRepository;
  private final ReactiveAuthenticationManager authenticationManager;
  private final JwtService jwtService;

  public Mono<TokenDTO> login(UserLoginDTO req) {
    return authenticationManager
      .authenticate(new UsernamePasswordAuthenticationToken(req.getLogin(), req.getPassword()))
      .publishOn(Schedulers.boundedElastic())
      .map(auth -> userRepository.findByLogin(req.getLogin()))
      .flatMap(optional -> optional.map(this::generateTokenFromEntity)
        .orElseGet(() -> Mono.error(new UserDoesNotExistException(req.getLogin()))));
  }

  private Mono<TokenDTO> generateTokenFromEntity(UserEntity user) {
    return Mono.just(jwtService.generateToken(User
      .withUsername(user.getLogin())
      .password(user.getPassword())
      .authorities(user.getRoles().stream()
        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
        .collect(Collectors.toList()))
      .accountLocked(false)
      .accountExpired(false)
      .credentialsExpired(false)
      .disabled(false)
      .build()));
  }


}
