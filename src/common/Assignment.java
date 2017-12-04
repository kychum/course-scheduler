package common;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.logging.Logger;

public class Assignment {
  // Represent as a two-way map for easy lookup
  private TreeMap<Slot, HashSet<Assignable>> assignments;
  private TreeMap<Assignable, Slot> courseAssignments;
  private Instance instance;
  private static Logger log = Logger.getLogger("Assignment");

  public Assignment(Instance instance) {
    this.instance = instance;
    assignments = new TreeMap<Slot, HashSet<Assignable>>();
    for( Slot s : instance.getCourseSlots() ) {
      assignments.put( s, new HashSet<Assignable>() );
    }
    for( Slot s : instance.getLabSlots() ) {
      assignments.put( s, new HashSet<Assignable>() );
    }
    courseAssignments = new TreeMap<Assignable, Slot>();

    instance.getPartAssign().forEach( (assign, slot) -> {
      add( slot, assign );
    });
  }

  public Instance getInstance() {
    return instance;
  }

  public TreeMap<Slot, HashSet<Assignable>> getAssignmentsBySlot() {
    return assignments;
  }

  public TreeMap<Assignable, Slot> getAssignmentsByCourse() {
    return courseAssignments;
  }

  public void setAssignments(TreeMap<Slot, HashSet<Assignable>> assignments) {
    this.assignments = assignments;
  }

  private void verifyMax( Slot slot ) throws HardConstraintViolationException {
    if( slot != null ) {
      long count = 0;
      if( slot.isLabSlot() ) {
        count = this.assignments.get( slot ).stream().filter( a -> a.isLab() ).count();
      }
      else {
        count = this.assignments.get( slot ).stream().filter( a -> !a.isLab() ).count();
      }
      if( count == slot.getMaxAssign() ) {
        throw new HardConstraintViolationException( "Assignment would cause slot to be over capacity." );
      }
    }
  }

  private void verifyUnwanted( Assignable course, Slot slot ) throws HardConstraintViolationException {
    if( this.instance.getConstraints().checkUnwanted( course, slot ) ) {
      throw new HardConstraintViolationException( "Assignment is of a course to an unwanted slot." );
    }
  }

  private void verifyIncomp( Assignable course, HashSet<Assignable> assigned ) throws HardConstraintViolationException {
    for( Assignable a : assigned ) {
      if( this.instance.getConstraints().checkIncomp( a, course ) ) {
        throw new HardConstraintViolationException( "Assignment would result in incompatibility violation." );
      }
    }
  }

  private void verifyIncomp( Assignable course, HashSet<Assignable> assigned, Assignable ignore ) throws HardConstraintViolationException {
    for( Assignable a : assigned ) {
      if( a.equals( ignore ) ) {
        continue;
      }
      if( this.instance.getConstraints().checkIncomp( a, course ) ) {
        throw new HardConstraintViolationException( "Assignment would result in incompatibility violation." );
      }
    }
  }

  //Convenience function
  public void add(Assignable a, Slot s) throws HardConstraintViolationException {
    add(s, a);
  }

  public void add(Slot slot, Assignable assignment) throws HardConstraintViolationException {
    HashSet<Assignable> currentList = this.assignments.get(slot);
    // hash the 'dummy' slot to find the real slot so we can check the max assign value
    int val = slot.hashCode();

    Slot instanceSlot;
    if( assignment.isLab() ) {
      instanceSlot = instance.getLabSlotsHash().stream().filter(s -> s.hashCode() == val).findFirst().orElse(null);
      if( !instance.hasLab( assignment ) ){
        throw new HardConstraintViolationException( "Attempting to assign a lab that does not exist, or assigning a course into a lab slot" );
      }
    }
    else{
      instanceSlot = instance.getCourseSlotsHash().stream().filter(s -> s.hashCode() == val).findFirst().orElse(null);
      if( !instance.hasCourse( assignment ) && !assignment.equals( Course.getCPSC813() ) &&
          !assignment.equals( Course.getCPSC913() ) ) {
        throw new HardConstraintViolationException( "Attempting to assign a course that does not exist, or assigning a lab into a course slot" );
      }
    }

    if( instanceSlot == null ) {
      if( slot.equals( Slot.getSpecialSlot() ) ) {
        instanceSlot = Slot.getSpecialSlot();
      }
      else {
        throw new HardConstraintViolationException( "Attempting to assign class to a slot that doesn't exist, or course/lab to wrong type of slot." );
      }
    }

    // Check constraints
    verifyIncomp( assignment, currentList );
    verifyUnwanted( assignment, instanceSlot );
    verifyMax( instanceSlot );

	  currentList.add(assignment);
    this.courseAssignments.put(assignment, instanceSlot);
  }

  private void remove(Assignable assignment) {
    Slot slot = courseAssignments.remove( assignment );
    if( slot == null ) {
      log.warning( "Attempting to unassign a class that hasn't been assigned" );
    }
    else {
      assignments.get( slot ).remove( assignment );
    }
  }

  public void move( Assignable a, Slot slot ) {
    // Add first so that if the addition throws, we won't accidentally remove it
    add( slot, a );
    remove( a );
  }

  public void swap(Assignable a1, Assignable a2) throws HardConstraintViolationException {
    Slot s1 = this.courseAssignments.get( a1 );
    Slot s2 = this.courseAssignments.get( a2 );

    //Swapping a course with a lab; need to ensure corresponding slots exist
    if( a1.isLab() != a2.isLab() ) {
      int s1Hash = s1.hashCode();
      int s2Hash = s2.hashCode();
      if( a1.isLab() ) {
        //a1 is a lab, so a2 is a course
        s1 = instance.getCourseSlotsHash().stream().filter( s -> s.hashCode() == s1Hash ).findFirst().orElse( null );
        s2 = instance.getLabSlotsHash().stream().filter( s -> s.hashCode() == s2Hash ).findFirst().orElse( null );
        verifyMax(s1);
        verifyMax(s2);
      }
      else {
        //a1 is a course, so a2 is a lab
        s1 = instance.getLabSlotsHash().stream().filter( s -> s.hashCode() == s1Hash ).findFirst().orElse( null );
        s2 = instance.getCourseSlotsHash().stream().filter( s -> s.hashCode() == s2Hash ).findFirst().orElse( null );
        verifyMax(s1);
        verifyMax(s2);
      }
    }

    if( s1 == null || s2 == null ) {
      throw new HardConstraintViolationException( "Could not swap lab and course due to a corresponding slot not existing" );
    }

	  HashSet<Assignable> s1List, s2List;
	  s1List = this.assignments.get(s1);
	  s2List = this.assignments.get(s2);

    verifyIncomp( a1, s2List, a2 );
    verifyIncomp( a2, s1List, a1 );
    verifyUnwanted( a1, s2 );
    verifyUnwanted( a2, s1 );

	  s1List.remove(a1);
	  s1List.add(a2);
	  s2List.remove(a2);
	  s2List.add(a1);
    this.courseAssignments.put( a1, s2 );
    this.courseAssignments.put( a2, s1 );
  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append( "Eval-value: " + "TO BE IMPLEMENTED" );
    int longest = courseAssignments.keySet().stream()
      .mapToInt( a -> a.toString().length() )
      .max().orElse( 0 );
    courseAssignments.forEach( (assn, slot) -> {
      out.append( String.format( "%" + longest + "s : %s", assn.toString(), slot.toString() ) );
    } );
    return out.toString();
  }
}
