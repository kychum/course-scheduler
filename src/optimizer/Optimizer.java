package optimizer;
import common.Instance.Preference;
import common.Assignment;
import common.Assignment.Tuple;
import common.Assignable;
import common.Instance;
import common.Course;
import common.Slot;
import common.Lab;
import java.util.stream.Collectors;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.HashSet;
import java.util.Map;

public class Optimizer{
  private Assignment assignment;
  private Instance instance;

  private int w_minfilled;
  private int w_pref;
  private int w_pair;
  private int w_secdiff;

  public Optimizer( Assignment assignment ) {
    this( assignment, 10, 10, 10, 10 );
  }

  public Optimizer( Assignment assignment, int min, int pref, int pair, int sec ) {
    this.assignment = assignment;
    this.instance = assignment.getInstance();
    w_minfilled = min;
    w_pref = pref;
    w_pair = pair;
    w_secdiff = sec;
  }

  enum ConstraintType {
    MIN,
    PAIR,
    PREF,
    SECTION
  }
  public Assignment optimize(Assignment original) {
    HashSet<Slot> min = assignment.getMinViolations();
    HashSet<Tuple<Assignable, Assignable>> pair = assignment.getPairViolations();
    HashMap<Tuple<Assignable, Slot>, Integer> pref = assignment.getPrefViolations();
    HashSet<Tuple<Assignable, Assignable>> secdiff = assignment.getSectionViolations();

    // Sorted by the values, descending order
    List<Tuple<Assignable, Slot>> sortedPref = pref.entrySet().stream().sorted( Map.Entry.comparingByValue( Comparator.reverseOrder() ) )
      .map( e -> e.getKey() )
      .collect( Collectors.toList() );

    // Determine which course(s) would give the greatest decrease in eval
    // In theory trying to resolve the two largest evals at once would provide
    // the best gain (for a greedy algorithm), but that seems to be more complex
    // than initially thought, so we'll go with the biggest for now.
    ConstraintType biggest;
    if( sortedPref.size() > 0 ) {
      int prefVal = pref.get( sortedPref.get( 0 ) ) * w_pref;
      int[] vals = { w_minfilled, prefVal, w_pair, w_secdiff };
      int maxVal = Arrays.stream( vals ).max().getAsInt();
      if( prefVal == maxVal )
        biggest = PREF;
      if( w_minfilled == maxVal && min.size() > 0 )
        biggest = MIN;
      if( w_pair == maxVal && pair.size() > 0 )
        biggest = PAIR;
      if( w_secdiff == maxVal && secdiff.size() > 0 )
        biggest = SECTION;
    } else {
      int[] vals = { w_minfilled, w_pair, w_secdiff };
      int maxVal = Arrays.stream( vals ).max().getAsInt();
      if( w_minfilled == maxVal && min.size() > 0 )
        biggest = MIN;
      if( w_pair == maxVal && pair.size() > 0 )
        biggest = PAIR;
      if( w_secdiff == maxVal && secdiff.size() > 0 )
        biggest = SECTION;
    }

    // Try to fix the violation
    switch( biggest ) {
      case PAIR:
        //resolvePair( pair.stream().findAny().orElse( null ) );
        break;
      case SECTION:
        //resolveSection( secdiff.stream().findAny().orElse( null ) );
        break;
      case PREF:
        //resolvePreference( sortedPref.get( 0 ) );
        break;
      case MIN:
        //resolveMin( min.stream().findAny().orElse( null );
        break;
      default:
        break;
    }

    return original;
  }
}
