package artgallery.user;

import artgallery.user.dto.*;
import artgallery.user.service.UserService;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class UserControllerTest extends AuthorizedControllerTest {
  @Autowired
  private WebTestClient webTestClient;

  static private UserDTO userDTO;

  @BeforeAll
  static void setup(@Autowired UserService userService) {
    userService.addRole(username, Role.ADMIN);

    userDTO = new UserDTO();
    userDTO.setLogin("username-3");
    userDTO.setPassword("password-3");
    userService.create(userDTO).block();
  }

  @Autowired
  UserService userService;

  @Test
  public void testRegisterUser() throws Exception {
    UserDTO userDTO = new UserDTO();
    userDTO.setLogin("username-4");
    userDTO.setPassword("password-4");

    String request = objectMapper.writeValueAsString(userDTO);

    String result = webTestClient.post()
      .uri("/api/v1/users/create")
      .bodyValue(request)
      .header("Content-Type", "application/json")
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isCreated()
      .returnResult(String.class)
      .getResponseBody()
      .blockFirst();

    UserCreatedDTO resultDTO = objectMapper.readValue(result, UserCreatedDTO.class);

    assertEquals(userDTO.getLogin(), resultDTO.getLogin());
  }

  @Test
  public void testGetCurrentUser() throws Exception {
    String result = webTestClient.get()
      .uri("/api/v1/users/current")
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isOk()
      .returnResult(String.class)
      .getResponseBody()
      .blockFirst();

    UserDetailsDTO resultDTO = objectMapper.readValue(result, UserDetailsDTO.class);

    assertEquals(username, resultDTO.getLogin());
  }

  @Test
  public void testRegisterUserConflict() throws Exception {
    String request = objectMapper.writeValueAsString(userDTO);
    webTestClient.post()
      .uri("/api/v1/users/create")
      .bodyValue(request)
      .header("Content-Type", "application/json")
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isEqualTo(409);
  }

  @Test
  void testRoleAdding() throws Exception {
    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setRole(Role.MODERATOR);
    String request = objectMapper.writeValueAsString(roleDTO);

    webTestClient.post()
      .uri("/api/v1/users/{login}/roles/", userDTO.getLogin())
      .bodyValue(request)
      .header("Content-Type", "application/json")
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isCreated();

    userService.removeRole(userDTO.getLogin(), Role.MODERATOR).block();
  }

  @Test
  void testExistingRoleAdding() throws Exception {
    userService.addRole(userDTO.getLogin(), Role.MODERATOR).block();

    RoleDTO roleDTO = new RoleDTO();
    roleDTO.setRole(Role.MODERATOR);
    String request = objectMapper.writeValueAsString(roleDTO);

    webTestClient.post()
      .uri("/api/v1/users/{login}/roles/", userDTO.getLogin())
      .bodyValue(request)
      .header("Content-Type", "application/json")
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isEqualTo(409);

    userService.removeRole(userDTO.getLogin(), Role.MODERATOR).block();
  }

  @Test
  void testRoleRemoving() throws Exception {
    userService.addRole(userDTO.getLogin(), Role.MODERATOR).block();

    webTestClient.delete()
      .uri("/api/v1/users/{login}/roles/{role}/", userDTO.getLogin(), Role.MODERATOR.toString())
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isNoContent();
  }

  @Test
  void testNonExistantRoleRemoving() throws Exception {
    webTestClient.delete()
      .uri("/api/v1/users/{login}/roles/{role}/", userDTO.getLogin(), Role.MODERATOR.toString())
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isNotFound();
  }

  @Test
  void testRoleGetting() throws Exception {
    String result = webTestClient.get()
      .uri("/api/v1/users/{login}/roles", userDTO.getLogin())
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isOk()
      .returnResult(String.class)
      .getResponseBody()
      .blockFirst();

    RoleDTO[] results = objectMapper.readValue(result, RoleDTO[].class);

    assertAll(
      () -> assertEquals(1, results.length),
      () -> assertEquals(Role.PUBLIC, results[0].getRole())
    );
  }

  @Test
  void testUserDoesNotExistRoleGetting() throws Exception {
    webTestClient.get()
      .uri("/api/v1/users/does-not-exist/roles")
      .header("Authorization", String.format("Bearer %s", tokenDTO.getToken()))
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isNotFound();
  }
}
