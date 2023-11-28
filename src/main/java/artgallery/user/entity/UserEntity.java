package artgallery.user.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

@Builder
@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Entity
@EqualsAndHashCode
@Table(name = "`user`")
public class UserEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @Column(name = "login", nullable = false, unique = true, length = 32)
  @NotBlank(message = "must be not null")
  private String login;

  @Column(name = "password", nullable = false, length = 255)
  @NotBlank(message = "must be not null")
  private String password;

  @ManyToMany(cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
  @EqualsAndHashCode.Exclude
  @JoinTable(
    name = "user_role",
    joinColumns = {@JoinColumn(name = "user_id")},
    inverseJoinColumns = {@JoinColumn(name = "role_id")}
  )
  private List<RoleEntity> roles;

}
