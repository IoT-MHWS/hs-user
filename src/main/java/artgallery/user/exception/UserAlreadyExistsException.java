package artgallery.user.exception;

public class UserAlreadyExistsException extends Exception {
  public UserAlreadyExistsException(String login) {
    super(String.format(("user %s already exists"), login));
  }
}
