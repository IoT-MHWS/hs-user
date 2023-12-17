package artgallery.user.exception;

public class DatabaseConflictException extends RuntimeException {
  public DatabaseConflictException(String msg) {
    super(msg);
  }
}
