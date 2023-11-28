package artgallery.user.service;

import artgallery.user.dto.TokenDTO;
import artgallery.user.dto.UserDTO;
import artgallery.user.dto.UserDetailsDTO;
import artgallery.user.entity.UserEntity;
import artgallery.user.exception.UserDoesNotExistException;
import artgallery.user.repository.UserRepository;
import artgallery.user.security.ServerUserDetails;
import artgallery.user.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
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

  public Mono<TokenDTO> login(UserDTO req) {
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

  public Mono<UserDetailsDTO> getUserDetails(ServerUserDetails userDetails) {
    return Mono.just(new UserDetailsDTO(
      userDetails.getId(),
      userDetails.getUsername(),
      userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
    );
  }

}
