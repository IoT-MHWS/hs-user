package artgallery.user.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class UserDetailsDTO {
  @NotNull
  public Long id;
  @NotNull
  public String login;
  @NotNull
  public List<String> authorities;
}
