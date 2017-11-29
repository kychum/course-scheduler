package common;
import java.lang.Exception;

public class HardConstraintViolationException extends Exception {
  public HardConstraintViolationException() {
    this( "" );
  }

  public HardConstraintViolationException( String message ) {
    super( message );
  }
}
