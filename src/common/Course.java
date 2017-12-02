package common;
import java.util.Objects;

/**
 * Class that represents a course section.
 *
 * @author Kevin
 * @version 1.00
 */
public class Course extends Assignable {
  private static final Course cpsc813 = new Course( "CPSC", 813, 1 );
  private static final Course cpsc913 = new Course( "CPSC", 913, 1 );

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

  public static Course getCPSC813() {
    return cpsc813;
  }

  public static Course getCPSC913() {
    return cpsc913;
  }

  /**
   * Produces the string representation of a course object.
   *
   * @return This course as a string, e.g. "CPSC 433 LEC 01"
   */
  public String toString() {
    return String.format("%s %02d LEC %02d", identifier, courseNum, section);
  }

  public boolean equals( Course other ) {
    return this.identifier.equals( other.identifier ) &&
      this.courseNum == other.courseNum &&
      this.section == other.section;
  }

  public int hashCode() {
    return Objects.hash( this.identifier, this.courseNum, this.section );
  }

  public int compareTo( Course c ) {
    if( this.identifier.compareTo( c.identifier ) < 0 ) {
      return -1;
    }
    else if( this.identifier.equals( c.identifier ) ) {
      if( this.courseNum < c.courseNum ) {
        return -1;
      }
      else if( this.courseNum == c.courseNum ) {
        if( this.section < c.section ) {
          return -1;
        }
        else if( this.section == c.section ) {
          return 0;
        }
      }
    }
    return 1;
  }

  public int compareTo( Lab l ) {
    if( this.identifier.compareTo( l.identifier ) < 0 ) {
      return -1;
    }
    else if( this.identifier.equals( l.identifier ) ) {
      if( this.courseNum < l.courseNum ) {
        return -1;
      }
      else if( this.courseNum == l.courseNum ) {
        if( this.section <= l.section ) {
          return -1;
        }
      }
    }
    return 1;
  }
}

