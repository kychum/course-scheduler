package common;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;

public class Instance{
  private static Logger log = Logger.getLogger( "Instance" );
  private String name;
  private HashSet<Course> courses;
  private HashSet<Lab> labs;
  private HashSet<Slot> courseSlots;
  private HashSet<Slot> labSlots;
  private Constraints constraints;
  private HashMap<Assignable, Slot> partAssign;
  private HashSet<Preference> preferences;

  public Instance () {
    courses = new HashSet<Course>();
    labs = new HashSet<Lab>();
    courseSlots = new HashSet<Slot>();
    labSlots = new HashSet<Slot>();
    constraints = new Constraints();
    partAssign = new HashMap<>();
    preferences = new HashSet<Preference>();
  }

  public void setName( String name ) {
    this.name = name;
  }

  public ArrayList<Slot> getCourseSlots() {
    return new ArrayList<Slot>( courseSlots );
  }
  
  public HashSet<Slot> getCourseSlotsHash() {
	  return courseSlots;
  }
  
  public HashSet<Slot> getLabSlotsHash() {
	  return labSlots;
  }

  public ArrayList<Slot> getLabSlots() {
    return new ArrayList<Slot>( labSlots );
  }

  public ArrayList<Course> getCourses() {
    return new ArrayList<Course>( courses );
  }

  public ArrayList<Lab> getLabs(){
	  return new ArrayList<Lab>( labs );
  }

  public HashSet<Course> getCoursesHash() {
    return courses;
  }

  public HashSet<Lab> getLabsHash(){
    return labs;
  }
  
  public boolean hasCourse( Assignable c ) {
    return courses.contains( c );
  }

  public boolean hasLab( Assignable l ) {
    return labs.contains( l );
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

  private static Slot invalidSlot = new Slot( "TU", "11:00", 0, 0, false );
  public void addCourseSlot( Slot slot ) {
    if( slot.equals( invalidSlot ) ) {
      log.warning( "No courses can be scheduled at TU, 11:00. Ignoring this entry." );
    }
    else {
      this.courseSlots.add( slot );
    }
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

  public void addPartAssign( Assignable assn, Slot slot ) throws HardConstraintViolationException {
    if( partAssign.containsKey( assn ) && !partAssign.get( assn ).equals( slot ) ) {
      throw new HardConstraintViolationException( String.format("Class [%s] has two different partial assignments.", assn.toString()) );
    }
    this.partAssign.put( assn, slot );
  }

  HashMap<Assignable, Slot> getPartAssign() {
    return partAssign;
  }

  public boolean addPreference( Assignable assn, Slot slot, int value ) {
    return ((this.courseSlots.stream().anyMatch( s -> s.equals(slot) ) || this.labSlots.stream().anyMatch( s -> s.equals(slot) )) &&
        this.preferences.add( new Preference( assn, slot, value ) ));
  }

  /**
   * Adds any applicable special cases to the instance and verifies its validity.
   *
   * @throws HardConstraintViolationException if impossible to solve from given constraints
   */
  public void finalizeInstance() throws HardConstraintViolationException{
    for( Course c : courses ) {
      // forbid courses from being scheduled at the same time as their labs
      for( Lab l : labs ) {
        if( c.getCourseNum() == l.getCourseNum() ) {
          if( l.isForAllSections() || (l.getSection() == c.getSection()) ) {
            constraints.addIncomp( c, l );
          }
        }
      }

      // courses on the 500-level must be scheduled at different times.
      if( c.is500Level() ) {
        courses.stream()
          .filter( c2 -> !c.equals( c2 ) && c2.is500Level() )
          .forEach( c2 -> constraints.addIncomp( c, c2 ) );
      }
    }

    // Check if not exact match and output an error
    for( Preference p : preferences ) {
      Assignable a =  courses.stream().filter( a1 -> a1.hashCode() == p.course.hashCode() ).findFirst().orElse(null);
      if ( a == null ){
        a = labs.stream().filter( a1 -> a1.hashCode() == p.course.hashCode() ).findFirst().orElse(null);
      }
      if( a != null && !p.course.equals(a) ){
        log.warning(String.format("Course definition [%s] in preferences is not exactly the same as expected [%s], but will treat them as the same", p.course, a));
      }
    }

    if( courses.stream().anyMatch( c -> c.getCourseNum() == 313 ) ) {
      add813();
    }
    if( courses.stream().anyMatch( c -> c.getCourseNum() == 413 ) ) {
      add913();
    }

    verifyInstance();
  }

  /**
   * Checks that the instance is valid
   *
   * @throws HardConstraintViolationException if the instance is impossible to satisfy
   */
  public void verifyInstance() throws HardConstraintViolationException {
    // Ensure we can schedule everything
    int maxCourse = courseSlots.stream().mapToInt( s -> s.getMaxAssign() ).sum();
    int maxLab = labSlots.stream().mapToInt( s -> s.getMaxAssign() ).sum();
    if( courses.size() > maxCourse ) {
      throw new HardConstraintViolationException( "There are not enough slots for the number of courses given." );
    }
    if( labs.size() > maxLab ) {
      throw new HardConstraintViolationException( "There are not enough slots for the number of labs given." );
    }

    // Check evening courses
    int maxEveningCourse = courseSlots.stream().filter( Slot::isEveningSlot ).mapToInt( s -> s.getMaxAssign() ).sum();
    int maxEveningLab = labSlots.stream().filter( Slot::isEveningSlot ).mapToInt( s -> s.getMaxAssign() ).sum();
    long eveningCourses = courses.stream().filter( Course::isEveningClass ).count();
    long eveningLabs = labs.stream().filter( Lab::isEveningClass ).count();
    if( maxEveningCourse < eveningCourses ) {
      throw new HardConstraintViolationException( "There are not enough evening slots for the number of courses given." );
    }
    if( maxEveningLab < eveningLabs ) {
      throw new HardConstraintViolationException( "There are not enough evening slots for the number of labs given." );
    }

    // Check 500-level courses
    long level500Count = courses.stream().filter( Course::is500Level ).count();
    int numCourseSlots = courseSlots.size();
    if( level500Count > numCourseSlots ) {
      throw new HardConstraintViolationException( "There are not enough slots to schedule all 500-level courses." );
    }

    // Check partial assignments
    partAssign.forEach( (course, slot) -> {
      if( courses.contains( course ) && course.getCourseNum() != 813 && course.getCourseNum() != 913 ) {
        if( !courseSlots.contains( slot ) ) {
          throw new HardConstraintViolationException( "Course is not assigned to a course slot" );
        }
      }
      else if( labs.contains( course ) ) {
        if( !labSlots.contains( slot ) ) {
          throw new HardConstraintViolationException( "Lab is not assigned to a lab slot" );
        }
      }
      else if( course.getCourseNum() != 813 && course.getCourseNum() != 913 ) {
        throw new HardConstraintViolationException( "Assignable does not exist in the list of courses or labs" );
      }
    } );

    // Check preferences
    Preference[] ignoredPreferences = preferences.stream()
      .filter( p -> constraints.checkUnwanted( p.course, p.slot ) )
      .toArray( Preference[]::new ); 
    for( Preference p : ignoredPreferences ) {
      log.warning( "Ignoring preference for [%s] to slot [%s] due to the assignment being unwanted" );
      preferences.remove( p );
    }
  }

  public HashSet<Preference> getPrefs() {
    return preferences;
  }

  private void add813() {
    Course cpsc813 = Course.getCPSC813();
    addPartAssign( cpsc813, Slot.getSpecialSlot() );
    courses.stream()
      .filter( c -> c.getCourseNum() == 313 )
      .forEach( c -> {
        constraints.addIncomp( cpsc813, c );
        for( Assignable c2 : constraints.getIncomp( c ) ) {
          constraints.addIncomp( cpsc813, c2 );
        }
      } );
  }

  private void add913() {
    Course cpsc913 = Course.getCPSC913();
    addPartAssign( cpsc913, Slot.getSpecialSlot() );
    courses.stream()
      .filter( c -> c.getCourseNum() == 413 )
      .forEach( c -> {
        constraints.addIncomp( cpsc913, c );
        for( Assignable c2 : constraints.getIncomp( c ) ) {
          constraints.addIncomp( cpsc913, c2 );
        }
      } );

  }

  public String toString() {
    StringBuilder out = new StringBuilder();
    out.append("Name:\n");
    out.append(name + "\n\n");

    out.append("Course slots:\n");
    courseSlots.stream().sorted().forEach( s -> out.append( s.toString() + "\n" ) );
    out.append("\n");

    out.append("Lab slots:\n");
    labSlots.stream().sorted().forEach( s -> out.append( s.toString() + "\n" ) );
    out.append("\n");

    out.append("Courses:\n");
    courses.stream().map( Course::toString ).sorted().forEach( s -> out.append( s + "\n" ) );
    out.append("\n");

    out.append("Labs:\n");
    labs.stream().map( Lab::toString ).sorted().forEach( s -> out.append( s + "\n" ) );
    out.append("\n");

    out.append("Not compatible:\n");
    HashMap<Assignable, HashSet<Assignable>> incomp = constraints.getIncomp();
    incomp.keySet().stream()
      .sorted()
      .map( a1 -> incomp.get( a1 ).stream()
          .filter( a2 -> a1.compareTo( a2 ) < 0 )
          .sorted()
          .map( a2 -> a1.toString() + ", " + a2.toString() + "\n" )
          .reduce( "", (s1, s2) -> s1 + s2 ) )
      .forEach( s -> out.append( s ) );

    out.append("\n");

    out.append("Unwanted:\n");
    HashMap<Assignable, HashSet<Slot>> unwanted = constraints.getUnwanted();
    unwanted.keySet().stream()
      .sorted()
      .forEach( a -> {
        unwanted.get(a).stream()
          .sorted()
          .forEach( s -> out.append( a.toString() + ", " + s.toString( true ) + "\n" ) );
      });
    out.append("\n");

    out.append("Preferences:\n");
    preferences.stream()
      .sorted()
      .forEach( p -> out.append( String.format("%s, %s, %d\n", p.slot.toString( true ), p.course.toString(), p.value ) ) );
    out.append("\n");

    out.append("Pair:\n");
    HashMap<Assignable, HashSet<Assignable>> pair = constraints.getPair();
    pair.keySet().stream()
      .sorted()
      .forEach( a1 -> {
        pair.get(a1).stream()
          .filter( a2 -> a1.compareTo( a2 ) < 0 )
          .sorted()
          .forEach( a2 -> out.append( a1.toString() + ", " + a2.toString() + "\n" ) );
      });
    out.append("\n");

    out.append("Partial assignments:\n");
    partAssign.keySet().stream()
      .sorted()
      .forEach( assn -> out.append( assn.toString() + ", " + partAssign.get( assn ).toString( true ) + "\n" ) );
    out.append("\n");

    return out.toString();
  }

  public class Preference implements Comparable<Preference> {
    Assignable course;
    Slot slot;
    int value;

    public Preference( Assignable a, Slot s, int v ) {
      course = a;
      slot = s;
      value = v;
    }

    public Assignable getCourse() {
      return course;
    }

    public Slot getSlot() {
      return slot;
    }

    public int getValue() {
      return value;
    }

    public boolean equals( Preference other ){
      return course.equals( other.course ) &&
        slot.equals( other.slot ) &&
        value == other.value;
    }

    public int hashCode() {
      return Objects.hash( course, slot, value );
    }

    public int compareTo( Preference other ) {
      int c = course.compareTo( other.course );
      int s = slot.compareTo( other.slot );
      int v = value - other.value;
      if( s < 0 ){
        return -1;
      }
      if( s == 0 ) {
        if( c < 0 ) {
          return -1;
        }
        if( c == 0 ) {
          if( v < 0 ) {
            return -1;
          }
          if( v == 0 ) {
            return 0;
          }
        }
      }
      return 1;
    }
  }
}
