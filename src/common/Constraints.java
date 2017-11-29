package common;
import java.util.TreeMap;
import java.util.HashSet;

// Initialized once; the constraints class object is meant to store all the various mappings that define the problem constraints.
public class Constraints {
  TreeMap<Assignable, HashSet<Assignable>> pair; // Represents case where two courses should be paired together
  TreeMap<Assignable, HashSet<Assignable>> incomp; // Represents case where two courses cannot both be in same timeslot
  TreeMap<Assignable, HashSet<Slot>> unwanted; // represents case where a course cannot be in a certain timeslot
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
	  unwanted = new TreeMap<>();
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
    if( !incomp.containsKey( a1 ) ) {
      incomp.put( a1, new HashSet<Assignable>() );
    }
    if( !incomp.containsKey( a2 ) ) {
      incomp.put( a2, new HashSet<Assignable>() );
    }
    return incomp.get(a1).add(a2) && incomp.get(a2).add(a1);
  }

  public boolean checkIncomp(Assignable a1, Assignable a2) {
    // Since the mapping is two-way, it should always only check one of these
	  return (incomp.get(a1).contains(a2) || incomp.get(a2).contains(a1));
  }

  public boolean addPair(Assignable a1, Assignable a2) {
    if( !pair.containsKey( a1 ) ) {
      pair.put( a1, new HashSet<Assignable>() );
    }
    if( !pair.containsKey( a2 ) ) {
      pair.put( a2, new HashSet<Assignable>() );
    }
    return (pair.get(a1).add(a2) && pair.get(a2).add(a1));
  }


  public boolean checkPair(Assignable a1, Assignable a2) {
    return (pair.get(a1).contains(a2) || pair.get(a2).contains(a1));
  }

  public boolean addUnwanted( Assignable assign, Slot slot ) {
    if( !unwanted.containsKey( assign ) ) {
      unwanted.put( a1, new HashSet<Slot>() );
    }
    return unwanted.get( a1 ).add( slot );
  }

  public boolean checkUnwanted( Assignable assign, Slot slot ) {
    return unwanted.get( a1 ).contains( slot );
  }

}
