package common;
import java.util.Objects;

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
   * @param allSection flag to check whether the lab is for an entire course, rather than a specific course section,
   * @param section the lab/tutorial's section number
   * @param isTutorial determines if the lab should be classified as a tutorial
   */
  public Lab( String identifier, int courseNum, int courseSection, int section, boolean isTutorial ) {
    this.identifier = identifier;
    this.courseNum = courseNum;
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
  public Lab( String identifier, int courseNum, int section, boolean isTutorial ) {
    this.identifier = identifier;
    this.courseNum = courseNum;
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
      course = identifier + " " + courseNum;
    }
    else {
      course = identifier + String.format(" %d LEC %02d", courseNum, courseSection);
    }
    return String.format( "%s %s %02d", course, (isTutorial ? "TUT" : "LAB"), section );
  }

  public boolean isForAllSections() {
    return allSections;
  }

  public int hashCode() {
    return Objects.hash( this.identifier, this.courseNum, this.courseSection, this.allSections, this.section, this.isTutorial);
  }

  public int compareTo( Course c ) {
    return (-1 * c.compareTo( this ));
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
        if( this.courseSection < l.courseSection ) {
          return -1;
        }
        else if( this.courseSection == l.courseSection ) {
          if( this.allSections ) {
            if( !l.allSections ) {
              return -1;
            }
            if( this.isTutorial ) {
              if( !l.isTutorial ) {
                return -1;
              }
              return 0;
            }
          }
          else{
            if( this.section < l.section ) {
              return -1;
            }
            else if( this.section == l.section ) {
              if( this.isTutorial ) {
                if( !l.isTutorial ) {
                  return -1;
                }
                return 0;
              }
              if( !l.isTutorial ){
                return 0;
              }
            }
          }
        }
      }
    }
    return 1;
  }
}

