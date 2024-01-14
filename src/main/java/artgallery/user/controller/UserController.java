package artgallery.user.controller;

import artgallery.user.dto.*;
import artgallery.user.configuration.ServerUserDetails;
import artgallery.user.service.UserService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;


import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

  private final UserService userService;

  @PostMapping("/create")
  @PreAuthorize("hasRole('SUPERVISOR')")
  public Mono<ResponseEntity<UserCreatedDTO>> create(@RequestBody @Valid UserRegisterDTO userDTO) {
    return userService.create(userDTO)
      .map(jwt -> new ResponseEntity<>(jwt, HttpStatus.CREATED));
  }

  @GetMapping(value="/current", produces = MediaType.APPLICATION_JSON_VALUE)
  public Mono<ResponseEntity<UserDetailsDTO>> getDetails(@AuthenticationPrincipal UserDetails userDetails) {
    ServerUserDetails serverUserDetails = (ServerUserDetails) userDetails;
    return userService.getUserDetails(serverUserDetails)
      .map(details -> new ResponseEntity<>(details, HttpStatus.OK));
  }


  @PostMapping("/{login}/roles/")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
  public Mono<ResponseEntity<?>> addRole(@PathVariable("login") @NotNull String login,
                                         @RequestBody @NotNull RoleDTO role) {
    return userService.addRole(login, role.getRole())
      .then(Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.CREATED)));
  }

  @DeleteMapping("/{login}/roles/{role}/")
  @PreAuthorize("hasRole('ADMIN') or hasRole('SUPERVISOR')")
  public Mono<ResponseEntity<?>> removeRole(@PathVariable("login") @NotNull String login,
                                            @PathVariable("role") @NotNull String roleName) {
    return userService.removeRole(login, Role.valueOf(roleName))
      .then(Mono.fromCallable(() -> new ResponseEntity<>(HttpStatus.NO_CONTENT)));
  }

  @GetMapping("/{login}/roles")
  public Mono<ResponseEntity<?>> getRoles(@PathVariable("login") @NotNull String login) {
    return userService.getRoles(login).collectList()
      .map(list -> new ResponseEntity<>(list, HttpStatus.OK));
  }
}
