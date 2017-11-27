package common;
import java.util.TreeMap;

// Initialized once; the constraints class object is meant to store all the various mappings that define the problem constraints.
public class Constraints {
  TreeMap<Assignable, Assignable> pair; // Represents case where two courses should be paired together
  TreeMap<Assignable, Assignable> incomp; // Represents case where two courses cannot both be in same timeslot
  TreeMap<Assignable, Slot> timeslot; // represents case where a course cannot be in a certain timeslot
  // penalty values for various penalties
  private int pen_coursemin; // timeslot not filled to minimal courses
  private int pen_labsmin; // timeslot not filled to minimal labs
  private int pen_notpaired; // courses that aren't paired together
  private int pen_section; // sections mapped to the same section
  public Constraints() {
	  // initialize
	  defaultPenalties(); //
	  initMaps();
  }
  
  public void initMaps() {
	  pair = new TreeMap<>();
	  incomp = new TreeMap<>();
	  timeslot = new TreeMap<>();
  }
  
  // Initialize constraints with penalty values.
  public Constraints(int courseMin, int labsMin, int notPaired, int section) {
	  setPenalties(courseMin, labsMin, notPaired, section);
	  initMaps();
  }
  
  // This is called when constraints not defined. This *SHOULDN'T* happen, but this is here just in case.
  public void defaultPenalties() {
	  pen_coursemin = 10;
	  pen_labsmin = 10;
	  pen_notpaired = 10;
	  pen_section = 10;
  }
  
  public void setPenalties(int courseMin, int labsMin, int notPaired, int section) {
	  pen_coursemin = courseMin;
	  pen_labsmin = labsMin;
	  pen_notpaired = notPaired;
	  pen_section = section;
  }
  
  public boolean addIncomp(Assignable a1, Assignable a2) {
	  // Returns true if adding constraint succeeded, false if failed
	  if (checkIncomp(a1, a2)) {
		  return false;
	  }
	  else {
		  incomp.put(a1, a2);
		  return true;
	  }
  }
  
  public boolean checkIncomp(Assignable a1, Assignable a2) {
	  if (incomp.get(a1).equals(a2) || incomp.get(a2).equals(a1))
	  {
		  return true;
	  }
	  else {
		  return false;
	  }
  }
  
  public boolean addPair(Assignable a1, Assignable a2) {
	  if (checkPair(a1, a2)) {
		  return false;
	  }
	  else {
		  pair.put(a1, a2);
		  return true;
	  }
  }

  
  public boolean checkPair(Assignable a1, Assignable a2) {
	  if (pair.get(a1).equals(a2) || pair.get(a2).equals(a1))
	  {
		  return true;
	  }
	  else {
		  return false;
	  }
  }
  
  
}
