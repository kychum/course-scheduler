package common;
import java.util.ArrayList;

public class Instance{
  String name;
  ArrayList<Course> courses;
  ArrayList<Lab> labs;
  ArrayList<Slot> courseSlots;
  ArrayList<Slot> labSlots;

  public Instance( String name ) {
    this.name = name;
  }
}
