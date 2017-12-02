package common;

/**
 * Abstract class/struct holding together some common parts of courses/labs.
 *
 * @author Kevin
 * @version 1.00
 */
public abstract class Assignable {
  protected String identifier;
  protected int courseNum;
  protected int section;

  public boolean isEveningClass() {
    return section == 9;
  }

  public boolean is500Level() {
    return courseNum >= 500 && courseNum < 600;
  }
}

