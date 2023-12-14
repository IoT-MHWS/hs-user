package artgallery.user.service;

import artgallery.user.dto.Role;
import artgallery.user.dto.RoleDTO;
import artgallery.user.dto.UserCreatedDTO;
import artgallery.user.dto.UserDTO;
import artgallery.user.dto.UserDetailsDTO;
import artgallery.user.entity.RoleEntity;
import artgallery.user.entity.UserEntity;
import artgallery.user.exception.RoleAlreadyExistsException;
import artgallery.user.exception.RoleDoesNotExistException;
import artgallery.user.exception.UserDoesNotExistException;
import artgallery.user.repository.RoleRepository;
import artgallery.user.repository.UserRepository;
import artgallery.user.security.ServerUserDetails;
import lombok.RequiredArgsConstructor;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RoleRepository roleRepository;
  private final PasswordEncoder passwordEncoder;

  public Mono<UserCreatedDTO> create(UserDTO userDTO) {
    return Mono.just(userDTO)
      .subscribeOn(Schedulers.boundedElastic())
      .<UserEntity>handle((dto, sink) -> {
        if (userRepository.existsByLogin(dto.getLogin())) {
          sink.error(new UserDoesNotExistException(dto.getLogin()));
          return;
        }

        var userEntity = UserEntity.builder()
          .login(dto.getLogin())
          .password(passwordEncoder.encode(dto.getPassword()))
          .roles(new ArrayList<>())
          .build();

        var roleEntity = roleRepository.findByName(Role.PUBLIC.name());

        if (roleEntity.isEmpty()) {
          sink.error(new RoleDoesNotExistException(Role.PUBLIC));
          return;
        }

        userEntity.getRoles().add(roleEntity.get());
        userRepository.save(userEntity);
        sink.next(userEntity);

      }).map(entity -> new UserCreatedDTO(entity.getId(), entity.getLogin()));
  }

  public Mono<UserDetailsDTO> getUserDetails(ServerUserDetails userDetails) {
    return Mono.just(new UserDetailsDTO(
      userDetails.getId(),
      userDetails.getUsername(),
      userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toList()))
    );
  }

  public Mono<Void> addRole(String login, Role role) {
    return Mono.just(login)
      .subscribeOn(Schedulers.boundedElastic())
      .<UserEntity>handle((val, sink) -> {
        var userEntityOpt = userRepository.findByLogin(login);
        if (userEntityOpt.isEmpty()) {
          sink.error(new UserDoesNotExistException(login));
          return;
        }
        var roleEntityOpt = roleRepository.findByName(role.name());
        if (roleEntityOpt.isEmpty()) {
          sink.error(new RoleDoesNotExistException(role));
          return;
        }

        var userEntity = userEntityOpt.get();
        var roleEntity = roleEntityOpt.get();

        if (userEntity.getRoles().contains(roleEntity)) {
          sink.error(new RoleAlreadyExistsException(role));
          return;
        }

        userEntity.getRoles().add(roleEntity);
        userRepository.save(userEntity);
        sink.next(userEntity);
      }).then();
  }

  public Mono<Void> removeRole(String login, Role role) {
    return Mono.just(login)
      .subscribeOn(Schedulers.boundedElastic())
      .<UserEntity>handle((val, sink) -> {
        var userEntityOpt = userRepository.findByLogin(login);
        if (userEntityOpt.isEmpty()) {
          sink.error(new UserDoesNotExistException(login));
          return;
        }
        var roleEntityOpt = roleRepository.findByName(role.name());
        if (roleEntityOpt.isEmpty()) {
          sink.error(new RoleDoesNotExistException(role));
          return;
        }

        var userEntity = userEntityOpt.get();
        var roleEntity = roleEntityOpt.get();

        if (!userEntity.getRoles().contains(roleEntity)) {
          sink.error(new RoleDoesNotExistException(role));
          return;
        }

        userEntity.getRoles().remove(roleEntity);
        userRepository.save(userEntity);
        sink.next(userEntity);
      }).then();
  }

  public Flux<RoleDTO> getRoles(String login) {
    return Mono.just(login)
      .subscribeOn(Schedulers.boundedElastic())
      .<List<RoleEntity>>handle((val, sink) -> {
        var userEntityOpt = userRepository.findByLogin(login);
        if (userEntityOpt.isEmpty()) {
          sink.error(new UserDoesNotExistException(login));
          return;
        }
        sink.next(userEntityOpt.get().getRoles());
      })
      .flatMapMany(Flux::fromIterable)
      .map(entity -> new RoleDTO(Role.valueOf(entity.getName())));
  }
}
