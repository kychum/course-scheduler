package common;
import java.util.ArrayList;

public class Instance{
  String name;
  private ArrayList<Course> courses;
  ArrayList<Lab> labs;
  private ArrayList<Slot> courseSlots;
  ArrayList<Slot> labSlots;

  public Instance( String name ) {
    this.name = name;
  }

public ArrayList<Slot> getCourseSlots() {
	return courseSlots;
}

public void setCourseSlots(ArrayList<Slot> courseSlots) {
	this.courseSlots = courseSlots;
}

public ArrayList<Course> getCourses() {
	return courses;
}

public void setCourses(ArrayList<Course> courses) {
	this.courses = courses;
}
}
