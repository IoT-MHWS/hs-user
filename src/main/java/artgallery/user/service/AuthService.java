package artgallery.user.service;

import artgallery.user.dto.TokenDTO;
import artgallery.user.dto.UserLoginDTO;
import reactor.core.publisher.Mono;

public interface AuthService {
  Mono<TokenDTO> login(UserLoginDTO req);
}
