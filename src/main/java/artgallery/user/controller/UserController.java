package artgallery.user.controller;

import artgallery.user.dto.RoleDTO;
import artgallery.user.dto.UserCreatedDTO;
import artgallery.user.dto.UserDTO;
import artgallery.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/create")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public Mono<ResponseEntity<UserCreatedDTO>> create(@RequestBody @Valid UserDTO userDTO) {
    return userService.create(userDTO)
      .map(jwt -> new ResponseEntity<>(jwt, HttpStatus.OK));
  }

  @PostMapping("/{login}/roles/add")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<ResponseEntity<?>> addRole(@PathVariable("login") @NotNull String login,
                                         @RequestBody @NotNull RoleDTO role) {
    return userService.addRole(login, role.getRole())
      .then(Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.OK)));
  }

  @PostMapping("/{login}/roles/remove")
  @PreAuthorize("hasRole('ADMIN')")
  public Mono<ResponseEntity<?>> removeRole(@PathVariable("login") @NotNull String login,
                                            @RequestBody @NotNull RoleDTO role) {
    return userService.removeRole(login, role.getRole())
      .then(Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.OK)));
  }

  @GetMapping("/{login}/roles")
  public Mono<ResponseEntity<?>> getRoles(@PathVariable("login") @NotNull String login) {
    return userService.getRoles(login).collectList()
      .map(list -> new ResponseEntity<>(list, HttpStatus.OK));
  }

}
