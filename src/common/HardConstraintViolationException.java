package common;
import java.lang.RuntimeException;

public class HardConstraintViolationException extends RuntimeException {
  public HardConstraintViolationException() {
    this( "" );
  }

  public HardConstraintViolationException( String message ) {
    super( message );
  }
}
