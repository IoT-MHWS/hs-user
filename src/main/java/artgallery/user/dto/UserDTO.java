package artgallery.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class UserDTO {
  @NotNull
  @Size(max = 32, message = "The login '${validatedValue}' must less than {max} characters long")
  private String login;

  @NotNull
  @Size(max = 255, message = "The password must less than {max} characters long")
  private String password;
}
