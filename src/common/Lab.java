package common;

/**
 * Class that represents both labs and tutorials.
 *
 * @author Kevin
 * @version 1.00
 */
public class Lab extends Assignable {
  private int courseSection;
  private boolean isTutorial;
  private boolean allSections;

  /**
   * Constructs a Lab object that is associated with a single course section.
   *
   * @param identifier the course identifier
   * @param courseSection the section of the lecture associated with this lab
   * @param allSection flag to check whether all information of lab has been supplied
   * @param section the lab/tutorial's section number
   * @param isTutorial determines if the lab should be classified as a tutorial
   */
  public Lab( String identifier, int courseSection, int section, boolean isTutorial ) {
    this.identifier = identifier;
    this.courseSection = courseSection;
    this.allSections = false;
    this.section = section;
    this.isTutorial = isTutorial;
  }

  /**
   * Constructs a Lab object that can be applied to by students of any section.
   *
   * @param identifier the course identifier
   * @param section the lab/tutorial's section number
   * @param isTutorial determines if the lab should be classified as a tutorial
   */
  public Lab( String identifier, int section, boolean isTutorial ) {
    this.identifier = identifier;
    this.courseSection = -1;
    this.allSections = true;
    this.section = section;
    this.isTutorial = isTutorial;
  }

  /**
   * Transforms a lab instance into a string.
   *
   * @return This lab as a string, for example, "CPSC 433 LEC 01 TUT 01".
   */
  public String toString() {
    String course;
    if( allSections ) {
      course = identifier;
    }
    else {
      course = identifier + String.format("LEC %2d", courseSection);
    }
    return String.format( "%s %s %2d", course, (isTutorial ? "TUT" : "LAB"), section );
  }
}

