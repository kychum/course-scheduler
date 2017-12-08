package common;
import java.util.Objects;

/**
 * Class that represents both labs and tutorials.
 *
 * @author Kevin
 * @version 1.00
 */
public class Lab extends Assignable {
	private int labSection;
	private boolean isTutorial;
	private boolean allSections;

	/**
	 * Constructs a Lab object that is associated with a single course section.
	 *
	 * @param identifier the course identifier
	 * @param section the section of the lecture associated with this lab
	 * @param allSection flag to check whether the lab is for an entire course, rather than a specific course section,
	 * @param labSection the lab/tutorial's labSection number
	 * @param isTutorial determines if the lab should be classified as a tutorial
	 */
	public Lab( String identifier, int courseNum, int section, int labSection, boolean isTutorial ) {
		this.identifier = identifier;
		this.courseNum = courseNum;
		this.section = section;
		this.allSections = false;
		this.labSection = labSection;
		this.isTutorial = isTutorial;
	}

	/**
	 * Constructs a Lab object that can be applied to by students of any section.
	 *
	 * @param identifier the course identifier
	 * @param labSection the lab/tutorial's section number
	 * @param isTutorial determines if the lab should be classified as a tutorial
	 */
	public Lab( String identifier, int courseNum, int labSection, boolean isTutorial ) {
		this.identifier = identifier;
		this.courseNum = courseNum;
		this.section = -1;
		this.allSections = true;
		this.labSection = labSection;
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
			course = identifier + String.format(" %d LEC %02d", courseNum, section);
		}
		return String.format( "%s %s %02d", course, (isTutorial ? "TUT" : "LAB"), labSection );
	}

	public boolean isForAllSections() {
		return allSections;
	}

	public int getLabSection() {
		return labSection;
	}

	public boolean isLab() {
		return true;
	}

	public boolean equals( Lab l ){
		return this.identifier.equals( l.identifier ) &&
			this.courseNum == l.courseNum &&
			this.section == l.section &&
			this.allSections == l.allSections &&
			this.labSection == l.labSection &&
			this.isTutorial == l.isTutorial;
	}

	@Override
	public boolean equals( Object a ) {
		if( a instanceof Lab )
			return equals( (Lab)a );
		return false;
	}

	@Override
	public int hashCode() {
		return Objects.hash( this.identifier, this.courseNum, this.section, this.allSections, this.labSection );
	}

	public int compareTo( Course c ) {
		int ci = this.identifier.compareTo( c.identifier );
		if( ci < 0 ) {
			return -1;
		}
		else if( ci == 0 ) {
			if( this.courseNum < c.courseNum ) {
				return -1;
			}
			else if( this.courseNum == c.courseNum ) {
				if( this.section < c.section & !allSections) {
					return -1;
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
				if( this.labSection < l.labSection ) {
					return -1;
				}
				else if( this.labSection == l.labSection ) {
					if( this.allSections ) {
						if( !l.allSections ) {
							return -1;
						}
						return 0;
					}
					else{
						if( this.section < l.section ) {
							return -1;
						}
						else if( this.section == l.section ) {
							return 0;
						}
					}
				}
			}
		}
		return 1;
	}
}

