package common;

/**
 * Abstract class/struct holding together some common parts of courses/labs.
 *
 * @author Kevin
 * @version 1.00
 */
public abstract class Assignable implements Comparable<Assignable> {
  protected String identifier;
  protected int courseNum;
  protected int section;

  public boolean isEveningClass() {
    return String.valueOf( section ).charAt( 0 ) == '9';
  }

  public boolean is500Level() {
    return courseNum >= 500 && courseNum < 600;
  }

  abstract public boolean isLab();

  public String getIdentifier() {
    return identifier;
  }

  public int getCourseNum() {
    return courseNum;
  }

  public int getSection() {
    return section;
  }

  public int compareTo( Assignable a ) {
    if( a instanceof Course ) {
      return compareTo( (Course)a );
    }
    return compareTo( (Lab)a );
  }

  abstract public int compareTo( Course c );
  abstract public int compareTo( Lab l );
}

