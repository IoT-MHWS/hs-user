package artgallery.user.configuration;

import artgallery.user.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import java.util.Optional;

@Configuration
public class ServerConfiguration {

  @Bean
  public ReactiveUserDetailsService userDetailsService(UserRepository userRepository) {
    return login -> Mono.fromCallable(() -> getUserDetails(login, userRepository))
      .flatMap(optional -> optional.map(Mono::just).orElseGet(Mono::empty));
  }

  private Optional<UserDetails> getUserDetails(String login, UserRepository userRepository) {
    return userRepository.findByLogin(login)
      .map(ServerUserDetails::new);
  }

  @Bean
  public ReactiveAuthenticationManager reactiveAuthenticationManager(ReactiveUserDetailsService userDetailsService,
                                                                     PasswordEncoder passwordEncoder) {
    var authenticationManager = new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
    authenticationManager.setPasswordEncoder(passwordEncoder);
    return authenticationManager;
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

}
