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
    return section == 9;
  }

  public boolean is500Level() {
    return courseNum >= 500 && courseNum < 600;
  }

  public String getIdentifier() {
    return identifier;
  }

  public int getNumber() {
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

