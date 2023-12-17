package artgallery.user.exception;

public class DoesNotExistException extends RuntimeException {
  public DoesNotExistException(String msg) {
    super(msg);
  }
}
