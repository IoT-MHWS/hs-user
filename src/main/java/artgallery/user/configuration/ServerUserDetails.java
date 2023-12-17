package artgallery.user.configuration;

import artgallery.user.entity.UserEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.stream.Collectors;

@AllArgsConstructor
public class ServerUserDetails implements UserDetails {

  @Getter
  private Long id;
  private String username;
  private String password;
  private Collection<? extends GrantedAuthority> authorities;

  public ServerUserDetails(UserEntity userEntity) {
    this.id = userEntity.getId();
    this.username = userEntity.getLogin();
    this.password = userEntity.getPassword();
    this.authorities = userEntity.getRoles().stream()
      .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName()))
      .collect(Collectors.toList());
  }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return this.authorities;
  }

  @Override
  public String getPassword() {
    return this.password;
  }

  @Override
  public String getUsername() {
    return this.username;
  }

  @Override
  public boolean isAccountNonExpired() {
    return true;
  }

  @Override
  public boolean isAccountNonLocked() {
    return true;
  }

  @Override
  public boolean isCredentialsNonExpired() {
    return true;
  }

  @Override
  public boolean isEnabled() {
    return true;
  }
}
