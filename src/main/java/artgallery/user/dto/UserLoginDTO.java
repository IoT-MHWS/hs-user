package artgallery.user.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserLoginDTO {
  @NotNull
  @Size(max = 32, message = "The login '${validatedValue}' must less than {max} characters long")
  private String login;

  @NotNull
  @Size(max = 255, message = "The password must less than {max} characters long")
  private String password;
}
