package artgallery.user;

import artgallery.user.dto.Role;
import artgallery.user.dto.TokenDTO;
import artgallery.user.dto.UserLoginDTO;
import artgallery.user.dto.UserRegisterDTO;
import artgallery.user.repository.UserRepository;
import artgallery.user.service.AuthService;
import artgallery.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@AutoConfigureMockMvc
@SpringBootTest
@Transactional
public abstract class AuthorizedControllerTest {
  protected static String username = "user-1";
  protected static String password = "password";
  protected static String email = "email";

  static protected final ObjectMapper objectMapper = new ObjectMapper();

  protected static TokenDTO tokenDTO;

  @BeforeAll
  static public void authorizeUser(@Autowired UserRepository userRepository, @Autowired UserService userService, @Autowired AuthService authService) throws Exception {
    userRepository.deleteAll();

    UserRegisterDTO userDTO = new UserRegisterDTO();
    userDTO.setLogin(username);
    userDTO.setPassword(password);
    userDTO.setEmail(email);
    userService.create(userDTO).block();
    userService.addRole(username, Role.SUPERVISOR).block();

    UserLoginDTO userLoginDTO = new UserLoginDTO();
    userLoginDTO.setLogin(userDTO.getLogin());
    userLoginDTO.setPassword(userDTO.getPassword());
    tokenDTO = authService.login(userLoginDTO).block();
    assertNotNull(tokenDTO);
  }

  @AfterAll
  static public void cleanup(@Autowired UserRepository userRepository) {
    userRepository.deleteAll();
  }
}
