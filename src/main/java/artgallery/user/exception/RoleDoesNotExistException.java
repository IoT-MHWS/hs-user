package artgallery.user.exception;

import artgallery.user.dto.Role;

public class RoleDoesNotExistException extends DoesNotExistException {
  public RoleDoesNotExistException(Role role) {
    super(String.format(("role %s does not exist"), role.name()));
  }
}
