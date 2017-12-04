package common;
import common.Instance.Preference;
import java.util.TreeMap;
import java.util.HashSet;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.util.logging.Logger;

public class Assignment {
  // Represent as a two-way map for easy lookup
  private TreeMap<Slot, HashSet<Assignable>> assignments;
  private TreeMap<Assignable, Slot> courseAssignments;
  private Instance instance;
  private static Logger log = Logger.getLogger("Assignment");
  private int[] weights;

  public Assignment(Assignment other) {
    log.info( "copy constructor" );
    if(other.courseAssignments == null);
    this.instance = other.instance;
    log.info( "copied instance" );
    courseAssignments = new TreeMap<>();
    if( other.courseAssignments != null ) {
      other.courseAssignments.forEach( (a, s) -> this.courseAssignments.put( a, s ) );
    }
    log.info( "copied courseassign" );
    assignments = new TreeMap<>();
    other.assignments.forEach( (s, hs) -> {
      HashSet<Assignable> cloned = new HashSet<>();
      hs.forEach( a -> cloned.add(a));
      assignments.put( s, cloned );
    });
    log.info( "copied assigns" );
    this.weights = other.weights;
    log.info( "copied weights" );
  }

  public void setWeights(int min, int pref, int pair, int sec){
    weights = new int[]{min, pref, pair, sec};
  }

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
  
  public TreeMap<Assignable, Slot> getCourseAssignments(){
	  return courseAssignments;
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
    out.append( "Eval-value: " + eval() + "\n" );
    int longest = courseAssignments.keySet().stream()
      .mapToInt( a -> a.toString().length() )
      .max().orElse( 0 );
    courseAssignments.forEach( (assn, slot) -> {
      out.append( String.format( "%-" + longest + "s : %s\n", assn.toString(), slot.toString() ) );
    } );
    return out.toString();
  }

  public HashSet<Slot> getMinViolations() {
    HashSet<Slot> out = assignments.keySet().stream()
      .filter( slot -> slot.getMinAssign() > assignments.get(slot).size() )
      .collect( Collectors.toCollection( HashSet::new ) );
    return out;
  }

  public HashSet<Tuple<Assignable, Assignable>> getPairViolations() {
    HashSet<Tuple<Assignable, Assignable>> out = new HashSet<>();
    HashMap<Assignable, HashSet<Assignable>> pair = instance.getConstraints().getPair();
    for( Assignable assign : pair.keySet() ) {
      HashSet<Assignable> pairedCourses = assignments.get( courseAssignments.get( assign ) );
      pair.get( assign ).stream().filter( a2 -> !pairedCourses.contains( a2 ) )
        .forEach( a2 -> {
          out.add( new Tuple<>( assign, a2 ) );
        });
    }
    return out;
  }

  public HashMap<Tuple<Assignable, Slot>, Integer> getPrefViolations() {
    HashMap<Tuple<Assignable, Slot>, Integer> out = new HashMap<>();
    HashSet<Preference> prefs = instance.getPrefs();
    for( Preference pref : prefs ) {
      Assignable assign = pref.getCourse();
      Slot slot = pref.getSlot();
      int value = pref.getValue();
      if( !courseAssignments.get( assign ).equals( slot ) ) {
        Tuple<Assignable, Slot> tup = new Tuple<>(assign, slot);
        out.put( tup, value );
      }
    }
    return out;
  }

  public HashSet<Tuple<Assignable, Assignable>> getSectionViolations() {
    HashSet<Tuple<Assignable, Assignable>> out = new HashSet<>();
    for( Course c : instance.getCourses()) {
      log.info("testing section violation on " + c.toString());
      log.info("note, slot is " + courseAssignments.get( c ) );
      assignments.get( courseAssignments.get( c ) ).stream()
        .filter( c2 -> c2.getCourseNum() == c.getCourseNum() &&
            c2.getSection() != c.getSection() )
        .forEach( c2 -> out.add( new Tuple<>(c, c2) ) );
    }
    return out;
  }

  public Assignment clone() {
    return new Assignment(this);
  }

  // Get the difference in eval for a swap
  // Remark: this will probably be slow.
  public int stageAction( Assignable a1, Assignable a2 ) {
    log.info( "Entering stage action" );
    Assignment clone = clone();
    log.info( "Clone performed." );
    try{
      clone.swap(a1, a2);
      log.info( "swapped clone" );
      return eval() - clone.eval();
    }
    catch( HardConstraintViolationException e ) {
      // Do nothing;
    }
    return 0;
  }

  // Get the difference in eval for a move
  // Remark: this will probably be slow.
  public int stageAction( Assignable assign, Slot slot ) {
    log.info( "Entering stage action" );
    Assignment a2 = clone();
    log.info( "Clone performed." );
    try{
      a2.move( assign, slot );
      log.info( "swapped clone" );
      return eval() - a2.eval();
    }
    catch( HardConstraintViolationException e ) {
      // Do nothing;
    }
    return 0;
  }

  public int eval() {
    return eval( weights[0], weights[1], weights[2], weights[3] );
  }

  public int eval( int w_minfilled, int w_pref, int w_pair, int w_secdiff ) {
    log.info("in eval (w/weights)");
    int minfilled = evalMinFilled();
    log.info("got min");
    int pref = evalPref();
    log.info("got pref");
    int pair = evalPair();
    log.info("got pair");
    int secdiff = evalSecDiff();
    log.info("got all vals");

    return (minfilled * w_minfilled) + (pref * w_pref) + (pair * w_pair) + (secdiff * w_secdiff);
  }

  public int evalMinFilled() {
    if( getMinViolations() != null )
      return getMinViolations().size();
    return 0;
  }

  public int evalPref() {
    if( getPrefViolations() != null )
      return getPrefViolations().values().stream().mapToInt( p -> p ).sum();
    return 0;
  }

  public int evalPair() {
    if( getPairViolations() != null )
      return getPairViolations().size();
    return 0;
  }

  public int evalSecDiff() {
    if( getSectionViolations() != null )
      return getSectionViolations().size();
    return 0;
  }

}
