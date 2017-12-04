package optimizer;
import common.Instance.Preference;
import common.Assignment;
import common.Tuple;
import common.Assignable;
import common.Instance;
import common.Course;
import common.Slot;
import common.Lab;
import java.util.stream.Collectors;
import java.util.Arrays;
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
    this.assignment.setWeights( min, pref, pair, sec );
    this.instance = assignment.getInstance();
    w_minfilled = min;
    w_pref = pref;
    w_pair = pair;
    w_secdiff = sec;
  }

  enum ConstraintType {
    NONE,
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
    ConstraintType biggest = ConstraintType.NONE;
    if( sortedPref.size() > 0 ) {
      int prefVal = pref.get( sortedPref.get( 0 ) ) * w_pref;
      int[] vals = { w_minfilled, prefVal, w_pair, w_secdiff };
      int maxVal = Arrays.stream( vals ).max().getAsInt();
      if( prefVal == maxVal )
        biggest = ConstraintType.PREF;
      if( w_minfilled == maxVal && min.size() > 0 )
        biggest = ConstraintType.MIN;
      if( w_pair == maxVal && pair.size() > 0 )
        biggest = ConstraintType.PAIR;
      if( w_secdiff == maxVal && secdiff.size() > 0 )
        biggest = ConstraintType.SECTION;
    } else {
      int[] vals = { w_minfilled, w_pair, w_secdiff };
      int maxVal = Arrays.stream( vals ).max().getAsInt();
      if( w_minfilled == maxVal && min.size() > 0 )
        biggest = ConstraintType.MIN;
      if( w_pair == maxVal && pair.size() > 0 )
        biggest = ConstraintType.PAIR;
      if( w_secdiff == maxVal && secdiff.size() > 0 )
        biggest = ConstraintType.SECTION;
    }

    // Try to fix the violation
    switch( biggest ) {
      case PAIR:
        resolvePair( pair.stream().findAny().orElse( null ) );
        break;
      case SECTION:
        resolveSection( secdiff.stream().findAny().orElse( null ) );
        break;
      case PREF:
        resolvePref( sortedPref.get( 0 ) );
        break;
      case MIN:
        resolveMin( min.stream().findAny().orElse( null ) );
        break;
      default:
        break;
    }

    return original;
  }

  //TODO: Handle null
  enum Operation { MOVE, MOVE_SECOND, SWAP, SWAP_SECOND, NONE }
  private boolean resolvePref( Tuple<Assignable, Slot> pref ) {
    // Note: treating the second type as a union; too tired to care about correctness at the moment.
    Tuple<Operation, Tuple<Assignable, Slot>> bestMove = new Tuple<Operation, Tuple<Assignable, Slot>>( Operation.MOVE, new Tuple<Assignable, Slot>( null, pref.second ));
    int bestDecrease = assignment.stageAction( pref.first, pref.second );
    for( Assignable c : assignment.getAssignmentsBySlot().get( pref.second ) ) {
      int stage = assignment.stageAction( pref.first, c );
      if( stage > bestDecrease ) {
        bestDecrease = stage;
        bestMove = new Tuple<Operation, Tuple<Assignable, Slot>>( Operation.SWAP, new Tuple<Assignable, Slot>( c, null ) );
      }
    }

    if( bestDecrease > 0 ) {
      if( bestMove.first == Operation.SWAP ) {
        assignment.swap( pref.first, bestMove.second.first );
        return true;
      }
      else if( bestMove.first == Operation.MOVE ) {
        assignment.move( pref.first, pref.second );
        return true;
      }
    }
    return false;
  }

  private boolean resolvePair( Tuple<Assignable, Assignable> pair ) {
    Slot firstSlot = assignment.getAssignmentsByCourse().get( pair.first );
    Slot secondSlot = assignment.getAssignmentsByCourse().get( pair.second );
    Tuple<Operation, Tuple<Assignable, Slot>> bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>(Operation.NONE, new Tuple<Assignable,Slot>(null, null));
    int bestDecrease = 0;
    //Try to move the first course together with the second
    for( Assignable a : assignment.getAssignmentsBySlot().get( firstSlot ) ) {
      if( !a.equals( pair.second ) ) {
        int stage = assignment.stageAction( pair.first, a );
        if( stage > bestDecrease ) {
          bestDecrease = stage;
          bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>(Operation.SWAP, new Tuple<Assignable,Slot>( a, null ) );
        }
      }
    }
    //Try to move the second course together with the first
    for( Assignable a : assignment.getAssignmentsBySlot().get( secondSlot ) ) {
      if( !a.equals( pair.first ) ) {
        int stage = assignment.stageAction( pair.second, a );
        if( stage > bestDecrease ) {
          bestDecrease = stage;
          bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>(Operation.SWAP_SECOND, new Tuple<Assignable, Slot>( a, null ) );
        }
      }
    }

    int stage = assignment.stageAction( pair.first, secondSlot );
    if( stage > bestDecrease ) {
      bestDecrease = stage;
      bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>(Operation.MOVE, new Tuple<Assignable, Slot>( null, secondSlot ) );
    }
    stage = assignment.stageAction( pair.second, firstSlot );
    if( stage > bestDecrease ) {
      bestDecrease = stage;
      bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>(Operation.MOVE_SECOND, new Tuple<Assignable, Slot>( null, firstSlot ) );
    }

    if( bestDecrease > 0 ) {
      switch( bestMove.first ) {
        case MOVE:
          assignment.move( pair.first, secondSlot );
          break;
        case MOVE_SECOND:
          assignment.move( pair.second, firstSlot );
          break;
        case SWAP:
          assignment.swap( pair.first, bestMove.second.first );
          break;
        case SWAP_SECOND:
          assignment.swap( pair.second, bestMove.second.first );
          break;
      }
    }
    return false;
  }

  private boolean resolveSection( Tuple<Assignable, Assignable> section ) {
    // Both slots are the same;
    Slot slot = assignment.getAssignmentsByCourse().get( section.first );
    Tuple<Operation, Tuple<Assignable, Slot>> bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>(Operation.NONE, null);
    int bestDecrease = 0;
    for( Slot s : assignment.getAssignmentsBySlot().keySet() ) {
      if( !s.equals( slot ) ) {
        int stage = assignment.stageAction( section.first, s );
        if( stage > bestDecrease ) {
          bestDecrease = stage;
          bestMove = new Tuple<Operation,Tuple<Assignable,Slot>>( Operation.MOVE, new Tuple<Assignable,Slot>( null, s ) );
        }

        stage = assignment.stageAction( section.second, s );
        if( stage > bestDecrease ) {
          bestDecrease = stage;
          bestMove = new Tuple<Operation,Tuple<Assignable,Slot>>( Operation.MOVE_SECOND, new Tuple<Assignable,Slot>( null, s ) );
        }

        for( Assignable assign : assignment.getAssignmentsBySlot().get( s ) ) {
          stage = assignment.stageAction( section.first, assign );
          if( stage > bestDecrease ) {
            bestDecrease = stage;
            bestMove = new Tuple<Operation,Tuple<Assignable,Slot>>( Operation.SWAP, new Tuple<Assignable,Slot>( assign, null ) );
          }

          stage = assignment.stageAction( section.second, assign );
          if( stage > bestDecrease ) {
            bestDecrease = stage;
            bestMove = new Tuple<Operation,Tuple<Assignable,Slot>>( Operation.SWAP_SECOND, new Tuple<Assignable,Slot>( assign, null ) );
          }
        }
      }
    }

    if( bestDecrease > 0 ) {
      switch( bestMove.first ) {
        case MOVE:
          assignment.move( section.first, bestMove.second.second );
          break;
        case MOVE_SECOND:
          assignment.move( section.second, bestMove.second.second );
          break;
        case SWAP:
          assignment.swap( section.first, bestMove.second.first );
          break;
        case SWAP_SECOND:
          assignment.swap( section.second, bestMove.second.first );
          break;
      }
    }
    return false;
  }

  private boolean resolveMin( Slot slot ) {
    int bestDecrease = 0;
    Tuple<Operation, Tuple<Assignable, Slot>> bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>( Operation.NONE, null);
    for( Slot s : assignment.getAssignmentsBySlot().keySet() ) {
      if( !s.equals( slot ) ) {
        for( Assignable a : assignment.getAssignmentsBySlot().get( s ) ) {
          int stage = assignment.stageAction( a, slot );
          if( stage > bestDecrease ) {
            bestDecrease = stage;
            bestMove = new Tuple<Operation,Tuple<Assignable,Slot>>( Operation.MOVE, new Tuple<Assignable,Slot>( a, null ) );
          }
        }
      }
    }

    if( bestDecrease > 0 ) {
      if( bestMove.first == Operation.MOVE ) {
        assignment.move( bestMove.second.first, slot );
        return true;
      }
    }
    return false;
  }
}
