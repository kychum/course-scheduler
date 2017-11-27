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

  public Instance () {
    courses = new HashSet<Course>();
    labs = new HashSet<Lab>();
    courseSlots = new HashSet<Slot>();
    labSlots = new HashSet<Slot>();
  }

  public void setName( String name ) {
    this.name = name;
  }

  public ArrayList<Slot> getCourseSlots() {
    return new ArrayList<Slot>( courseSlots );
  }

  public ArrayList<Course> getCourses() {
    return new ArrayList<Course>( courses );
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
}

