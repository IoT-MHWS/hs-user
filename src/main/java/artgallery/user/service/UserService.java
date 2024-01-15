package artgallery.user.service;

import artgallery.user.configuration.ServerUserDetails;
import artgallery.user.dto.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface UserService {
  Mono<UserCreatedDTO> create(UserRegisterDTO userDTO);

  Mono<UserDetailsDTO> getUserDetails(ServerUserDetails userDetails);

  Mono<Void> addRole(String login, Role role);

  Mono<Void> removeRole(String login, Role role);

  Flux<RoleDTO> getRoles(String login);
}
