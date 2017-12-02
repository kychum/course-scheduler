package common;
import java.util.Set;
import java.util.HashSet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

public class Instance{
  private String name;
  private HashSet<Course> courses;
  private HashSet<Lab> labs;
  private HashSet<Slot> courseSlots;
  private HashSet<Slot> labSlots;
  private Constraints constraints;
  private HashMap<Assignable, HashSet<Slot>> partAssign;
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

  public ArrayList<Slot> getLabSlots() {
    return new ArrayList<Slot>( labSlots );
  }

  public ArrayList<Course> getCourses() {
    return new ArrayList<Course>( courses );
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

  public void addCourseSlot( Slot slot ) {
    this.courseSlots.add( slot );
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

  public boolean addPartAssign( Assignable assn, Slot slot ) {
    if( !this.partAssign.containsKey( assn ) ) {
      this.partAssign.put( assn, new HashSet<Slot>() );
    }
    return this.partAssign.get( assn ).add( slot );
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
        if( c.getNumber() == l.getNumber() ) {
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

    if( courses.stream().anyMatch( c -> c.getNumber() == 313 ) ){
      add813();
    }
    if( courses.stream().anyMatch( c -> c.getNumber() == 413 ) ){
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

    // Check partial assignments
    partAssign.forEach( (course, slot) -> {
      if( courses.contains( course ) && course.getNumber() != 813 && course.getNumber() != 913 ) {
        if( !courseSlots.contains( slot ) ) {
          throw new HardConstraintViolationException( "Course is not assigned to a course slot" );
        }
      }
      else if( labs.contains( course ) ) {
        if( !labSlots.contains( slot ) ) {
          throw new HardConstraintViolationException( "Lab is not assigned to a lab slot" );
        }
      }
      else {
        throw new HardConstraintViolationException( "Assignable does not exist in the list of courses or labs" );
      }
    } );
  }

  private void add813() {
    Course cpsc813 = Course.getCPSC813();
    addPartAssign( cpsc813, Slot.getSpecialSlot() );
    courses.stream()
      .filter( c -> c.getNumber() == 313 )
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
      .filter( c -> c.getNumber() == 413 )
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
          .forEach( s -> out.append( a.toString() + ", " + s.toString() + "\n" ) );
      });
    out.append("\n");

    out.append("Preferences:\n");
    preferences.stream()
      .sorted()
      .forEach( p -> out.append( String.format("%s, %s, %d\n", p.slot.toString(), p.course.toString(), p.value ) ) );
    out.append("\n");

    out.append("Pair:\n");
    HashMap<Assignable, HashSet<Assignable>> pair = constraints.getPair();
    pair.keySet().stream()
      .sorted()
      .forEach( a1 -> {
        pair.get(a1).stream()
          .filter( a2 -> a1.compareTo( a2 ) < 0 )
          .forEach( a2 -> out.append( a1.toString() + ", " + a2.toString() + "\n" ) );
      });
    out.append("\n");

    out.append("Partial assignments:\n");
    partAssign.keySet().stream()
      .sorted()
      .forEach( assn ->
        partAssign.get( assn ).stream()
          .sorted()
          .forEach( s -> out.append( assn.toString() + ", " + s.toString() + "\n" ) )
      );
    out.append("\n");

    return out.toString();
  }

  private class Preference implements Comparable<Preference> {
    Assignable course;
    Slot slot;
    int value;

    public Preference( Assignable a, Slot s, int v ) {
      course = a;
      slot = s;
      value = v;
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
