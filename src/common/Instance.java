package common;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;

public class Instance{
  private String name;
  private HashSet<Course> courses;
  private HashSet<Lab> labs;
  private HashSet<Slot> courseSlots;
  private HashSet<Slot> labSlots;
  private Constraints constraints;

  public Instance () {
    courses = new HashSet<Course>();
    labs = new HashSet<Lab>();
    courseSlots = new HashSet<Slot>();
    labSlots = new HashSet<Slot>();
    constraints = new Constraints();
  }

  public void setName( String name ) {
    this.name = name;
  }

  public ArrayList<Slot> getCourseSlots() {
    return new ArrayList<Slot>( courseSlots );
  }

  public ArrayList<Slot> getLabSlots() {
    return new ArrayList<Slot>( labSlots );
  }

  public ArrayList<Course> getCourses() {
    return new ArrayList<Course>( courses );
  }

  public Constraints getConstraints() {
    return constraints;
  }

  public void addCourse( Course course ) {
    this.courses.add( course );
  }

  public void addLab( Lab lab ) {
    this.labs.add( lab );
  }

  public void addCourseSlot( Slot slot ) {
    this.courseSlots.add( slot );
  }

  public void addLabSlot( Slot slot ) {
    this.labSlots.add( slot );
  }

  public boolean addIncomp( Assignable first, Assignable second ) {
    return this.constraints.addIncomp( first, second );
  }

  public boolean addPair( Assignable first, Assignable second ) {
    return this.constraints.addPair( first, second );
  }

  public boolean addUnwanted( Assignable assn, Slot slot ) {
    return this.constraints.addUnwanted( assn, slot );
  }
}

