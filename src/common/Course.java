package common;
import java.util.Objects;

/**
 * Class that represents a course section.
 *
 * @author Kevin
 * @version 1.00
 */
public class Course extends Assignable {
  /**
   * Constructs a course with the given parameters.
   *
   * @param identifier the course identifier, e.g. "CPSC 123"
   * @param section the course section
   */
  public Course( String identifier, int courseNum, int section ) {
    this.identifier = identifier;
    this.courseNum = courseNum;
    this.section = section;
  }

  /**
   * Produces the string representation of a course object.
   *
   * @return This course as a string, e.g. "CPSC 433 LEC 01"
   */
  public String toString() {
    return String.format("%s %d LEC %2d", identifier, courseNum, section);
  }

  public int hashCode() {
    return Objects.hash( this.identifier, this.courseNum, this.section );
  }
}

