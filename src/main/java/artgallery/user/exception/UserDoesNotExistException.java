package artgallery.user.exception;

public class UserDoesNotExistException extends DoesNotExistException {
  public UserDoesNotExistException(String login) {
    super(String.format(("user %s does not exist"), login));
  }
}
