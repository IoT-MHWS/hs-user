package artgallery.user;

import artgallery.user.dto.TokenDTO;
import artgallery.user.dto.UserDTO;
import artgallery.user.repository.UserRepository;
import artgallery.user.service.AuthService;
import artgallery.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public class AuthControllerTest {

  @Autowired
  private WebTestClient webTestClient;

  @Autowired
  AuthService authService;

  @Autowired
  UserService userService;

  static private final ObjectMapper objectMapper = new ObjectMapper();

  static private UserDTO userDTO;

  @BeforeAll
  static void createUserDTO(@Autowired UserRepository userRepository) {
    userRepository.deleteAll();
    userDTO = new UserDTO();
    userDTO.setLogin("username-2");
    userDTO.setPassword("password-2");
  }

  @Test
  public void testLoginUser() throws Exception {
    userService.create(userDTO).block();

    String request = objectMapper.writeValueAsString(userDTO);

    String result = webTestClient.post()
      .uri("/api/v1/auth/login")
      .bodyValue(request)
      .header("Content-Type", "application/json")
      .accept(MediaType.APPLICATION_JSON)
      .exchange()
      .expectStatus()
      .isOk()
      .returnResult(String.class)
      .getResponseBody()
      .blockFirst();

    TokenDTO resultDTO = objectMapper.readValue(result, TokenDTO.class);

    assertAll(
      () -> assertNotNull(resultDTO.getToken()),
      () -> assertNotNull(resultDTO.getExpiration())
    );
  }

  @Autowired
  UserRepository userRepository;

  @AfterEach
  public void cleanup() {
    userRepository.deleteAll();
  }
}
