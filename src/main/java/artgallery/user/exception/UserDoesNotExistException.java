package artgallery.user.exception;

public class UserDoesNotExistException extends Exception {
  public UserDoesNotExistException(String login) {
    super(String.format(("user %s does not exist"), login));
  }
}
