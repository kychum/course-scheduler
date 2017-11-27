package common;
import java.util.TreeMap;

public class Constraints {
  TreeMap<Assignable, Assignable> pair; // Represents case where two courses cannot both be in same timeslot
  TreeMap<Assignable, Slot> timeslot; // represents case where a course cannot be in a certain timeslot
  public Constraints() {
	  // initialize
	  pair = new TreeMap<>();
  }
  public boolean addConstraint(Assignable a1, Assignable a2) {
	  // Returns true if adding constraint succeeded, false if failed
	  if (checkConstraint(a1, a2)) {
		  return false;
	  }
	  else {
		  pair.put(a1, a2);
		  return true;
	  }
  }
  
  public boolean checkConstraint(Assignable a1, Assignable a2) {
	  if (pair.get(a1).equals(a2) || pair.get(a2).equals(a1))
	  {
		  return true;
	  }
	  else {
		  return false;
	  }
  }
}
