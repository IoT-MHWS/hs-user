package artgallery.user.exception;

import artgallery.user.dto.Role;

public class RoleAlreadyExistsException extends DatabaseConflictException {
  public RoleAlreadyExistsException(Role role) {
    super(String.format(("role %s already exists"), role.name()));
  }
}
