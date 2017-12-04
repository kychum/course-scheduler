package common;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.HashSet;

public class Assignment {
  // Represent as a two-way map for easy lookup
  private TreeMap<Slot, ArrayList<Assignable>> assignments;
  private TreeMap<Assignable, Slot> courseAssignments;
  private Instance instance;
  
  public Assignment(Instance instance) {
    this.instance = instance;
    assignments = new TreeMap<Slot, ArrayList<Assignable>>();
    for( Slot s : instance.getCourseSlots() ) {
      assignments.put( s, new ArrayList<Assignable>() );
    }
    for( Slot s : instance.getLabSlots() ) {
      assignments.put( s, new ArrayList<Assignable>() );
    }
    courseAssignments = new TreeMap<Assignable, Slot>();

    instance.getPartAssign().forEach( (assign, slot) -> {
      add( slot, assign );
    });
  }

  public TreeMap<Slot, ArrayList<Assignable>> getAssignments() {
    return assignments;
  }

  public void setAssignments(TreeMap<Slot, ArrayList<Assignable>> assignments) {
    this.assignments = assignments;
  }

  private void verifyMax( Slot slot ) throws HardConstraintViolationException {
    if( this.assignments.get( slot ).size() == slot.getMaxAssign() ) {
      throw new HardConstraintViolationException( "Assignment would cause slot to be over capacity." );
    }
  }

  private void verifyUnwanted( Assignable course, Slot slot ) throws HardConstraintViolationException {
    if( this.instance.getConstraints().checkUnwanted( course, slot ) ) {
      throw new HardConstraintViolationException( "Assignment is of a course to an unwanted slot." );
    }
  }

  private void verifyIncomp( Assignable course, ArrayList<Assignable> assigned ) throws HardConstraintViolationException {
    for( Assignable a : assigned ) {
      if( this.instance.getConstraints().checkIncomp( a, course ) ) {
        throw new HardConstraintViolationException( "Assignment would result in incompatibility violation." );
      }
    }
  }

  private void verifyIncomp( Assignable course, ArrayList<Assignable> assigned, Assignable ignore ) throws HardConstraintViolationException {
    for( Assignable a : assigned ) {
      if( a.equals( ignore ) ) {
        continue;
      }
      if( this.instance.getConstraints().checkIncomp( a, course ) ) {
        throw new HardConstraintViolationException( "Assignment would result in incompatibility violation." );
      }
    }
  }
  
  public void add(Slot slot, Assignable assignment) throws HardConstraintViolationException {
    ArrayList<Assignable> currentList = this.assignments.get(slot);
    // hash the 'dummy' slot to find the real slot so we can check the max assign value
    int val = slot.hashCode();

    Slot instanceSlot;
    if( slot.isLabSlot() ) {
      instanceSlot = instance.getLabSlotsHash().stream().filter(s -> s.hashCode() == val).findFirst().orElse(null);
      if( !instance.hasLab( assignment ) ){
        throw new HardConstraintViolationException( "Attempting to assign a lab that does not exist" );
      }
    }
    else{
      instanceSlot = instance.getCourseSlotsHash().stream().filter(s -> s.hashCode() == val).findFirst().orElse(null);
      if( !instance.hasCourse( assignment ) && !assignment.equals( Course.getCPSC813() ) &&
          !assignment.equals( Course.getCPSC913() ) ) {
        throw new HardConstraintViolationException( "Attempting to assign a course that does not exist" );
      }
    }

    if( instanceSlot == null ) {
      if( slot.equals( Slot.getSpecialSlot() ) ) {
        instanceSlot = Slot.getSpecialSlot();
      }
      else {
        throw new HardConstraintViolationException( "Attempting to assign class to a slot that doesn't exist." );
      }
    }

    // Check constraints
    verifyIncomp( assignment, currentList );
    verifyUnwanted( assignment, instanceSlot );
    verifyMax( instanceSlot );

	  currentList.add(assignment);
    this.courseAssignments.put(assignment, instanceSlot);
  }
  
  public void remove(Slot slot, Assignable assignment) {
	  ArrayList<Assignable> currentList = this.assignments.get(slot);
	  currentList.remove(assignment);
	  this.assignments.put(slot, currentList);
  }
  
  public void swap(Assignable a1, Assignable a2) throws HardConstraintViolationException {
    Slot s1 = this.courseAssignments.get( a1 );
    Slot s2 = this.courseAssignments.get( a2 );
	  ArrayList<Assignable> s1List, s2List;
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
}
