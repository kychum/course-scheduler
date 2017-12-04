package optimizer;
import common.Instance.Preference;
import common.Assignment;
import common.Assignable;
import common.Instance;
import common.Course;
import common.Slot;
import common.Lab;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public class Optimizer{
  private Assignment assignment;
  private Instance instance;
  private Map<Assignable, Slot> aByCourse;
  private Map<Slot, HashSet<Assignable>> aBySlot;

  private int w_minfilled = 10;
  private int w_pref = 10;
  private int w_pair = 10;
  private int w_secdiff = 10;

  public Optimizer( Assignment assignment ) {
    this.assignment = assignment;
    this.instance = assignment.getInstance();
    aByCourse = assignment.getAssignmentsByCourse();
    aBySlot = assignment.getAssignmentsBySlot();
  }

  public Assignment optimize(Assignment original) {
    HashSet<Slot> min = getMinViolations();
    HashSet<Tuple<Assignable, Assignable>> pair = getPairViolations();
    return original;
  }

  private HashSet<Slot> getMinViolations() {
    Map<Slot, HashSet<Assignable>> assigns = assignment.getAssignmentsBySlot();
    HashSet<Slot> out = assigns.keySet().stream()
      .filter( slot -> slot.getMinAssign() < assigns.get(slot).size() )
      .collect( Collectors.toCollection( HashSet::new ) );
    return out;
  }

  private HashSet<Tuple<Assignable, Assignable>> getPairViolations() {
    HashSet<Tuple<Assignable, Assignable>> out = new HashSet<>();
    HashMap<Assignable, HashSet<Assignable>> pair = instance.getConstraints().getPair();
    for( Assignable assign : pair.keySet() ) {
      HashSet<Assignable> pairedCourses = aBySlot.get( aByCourse.get( assign ) );
      pair.get( assign ).stream().filter( a2 -> !pairedCourses.contains( a2 ) )
        .forEach( a2 -> {
          out.add( new Tuple<>( assign, a2 ) );
        });
    }
    return out;
  }

  private HashMap<Tuple<Assignable, Slot>, Integer> getPrefViolations() {
    HashMap<Tuple<Assignable, Slot>, Integer> out = new HashMap<>();
    HashSet<Preference> prefs = instance.getPrefs();
    for( Preference pref : prefs ) {
      Assignable assign = pref.getCourse();
      Slot slot = pref.getSlot();
      int value = pref.getValue();
      if( !aByCourse.get( assign ).equals( slot ) ) {
        Tuple<Assignable, Slot> tup = new Tuple<>(assign, slot);
        out.put( tup, value );
      }
    }
    return out;
  }

  private HashSet<Tuple<Assignable, Assignable>> getSectionViolations() {
    HashSet<Tuple<Assignable, Assignable>> out = new HashSet<>();
    for( Course c : instance.getCourses()) {
      aBySlot.get( aByCourse.get( c ) ).stream()
        .filter( c2 -> c2.getCourseNum() == c.getCourseNum() && 
            c2.getSection() != c.getSection() )
        .forEach( c2 -> out.add( new Tuple<>(c, c2) ) );
    }
    return out;
  }

  // Unordered tuple
  private class Tuple<F, S> {
    F first;
    S second;

    public Tuple(F f, S s) {
      first = f;
      second = s;
    }

    @Override
    public boolean equals( Object o ) {
      if( o instanceof Tuple ) {
        Tuple t = ((Tuple) o);
        return (first.equals( t.first ) && second.equals( t.second )) ||
          (first.equals(t.second) && second.equals( t.first ));
      }

      return false;
    }

    @Override
    public int hashCode() {
      return first.hashCode() ^ second.hashCode();
    }
  }
}
