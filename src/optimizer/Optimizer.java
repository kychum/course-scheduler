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
import java.util.logging.Logger;
import java.util.Map;
import java.util.Random;

public class Optimizer{
  private Assignment assignment;
  private Instance instance;
  private static Logger log = Logger.getLogger("Optimizer");
  private static Random rand = new Random();

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

  public Assignment getAssignment() {
    return assignment;
  }

  // Note that the current solution lacks randomness
  public void optimize() {
    if( assignment.eval() == 0 ) return;

    log.info( "Beginning optimization" );
    HashSet<Slot> min = assignment.getMinViolations();
    HashSet<Tuple<Assignable, Assignable>> pair = assignment.getPairViolations();
    HashMap<Tuple<Assignable, Slot>, Integer> pref = assignment.getPrefViolations();
    HashSet<Tuple<Assignable, Assignable>> secdiff = assignment.getSectionViolations();

    // Sorted by the values, descending order
    List<Tuple<Assignable, Slot>> sortedPref = pref.entrySet().stream().sorted( Map.Entry.comparingByValue( Comparator.reverseOrder() ) )
      .map( e -> e.getKey() )
      .collect( Collectors.toList() );

    while( !min.isEmpty() || !pair.isEmpty() || !sortedPref.isEmpty() || !secdiff.isEmpty() ) {
      // Determine which course(s) would give the greatest decrease in eval
      // Rather than a strict best-move, we start with this heuristic, where
      // fixing the violation with the largest eval should give a fairly good result.
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

      if( biggest == ConstraintType.NONE ) {
        log.fine( "Found no more solvable constraint violations" );
        break;
      }
      log.fine("Determined biggest violator is of type" + biggest.toString());
      log.fine(String.format("Eval before attempting resolution is [%d]", assignment.eval()));

      // Try to fix the violation
      boolean resolved = false;
      switch( biggest ) {
        case PAIR:
          List<Tuple<Assignable, Assignable>> pairList = pair.stream().collect( Collectors.toList() );
          Tuple<Assignable, Assignable> pairViolator = pairList.get( rand.nextInt( pairList.size() ) );
          if( !resolvePair( pairViolator ) ){
            // Note that this action should not be tried again for the current iteration
            pair.remove(pairViolator);
          }
          else { 
            resolved = true;
          }
          break;
        case SECTION:
          List<Tuple<Assignable, Assignable>> secdiffList = secdiff.stream().collect( Collectors.toList() );
          Tuple<Assignable, Assignable> secViolator = secdiffList.get( rand.nextInt( secdiffList.size() ) );
          if( !resolveSection( secViolator ) ) {
            secdiff.remove( secViolator );
            resolved = false;
          }
          else {
            resolved = true;
          }
          break;
        case PREF:
          final HashMap<Tuple<Assignable, Slot>, Integer> finalPref = assignment.getPrefViolations();
          int maxPref = pref.get( sortedPref.get( 0 ) );
          List<Tuple<Assignable, Slot>> prefList = sortedPref.stream().filter( p -> finalPref.get(p) == maxPref ).collect( Collectors.toList() );
          Tuple<Assignable, Slot> prefViolator = prefList.get( rand.nextInt( prefList.size() ) );
          if( !resolvePref( prefViolator ) ){
            sortedPref.remove( prefViolator );
            pref.remove( prefViolator );
            resolved = false;
          }
          else {
            resolved = true;
          }
          break;
        case MIN:
          Slot[] minArray = min.stream().toArray( Slot[]::new );
          Slot minViolator = minArray[ rand.nextInt( minArray.length ) ];
          if( minViolator != null && !resolveMin( minViolator ) ){
            min.remove(minViolator);
            resolved = false;
          }
          else {
            resolved = true;
          }
          break;
        default:
          break;
      }

      if( resolved ) {
        log.finer("updating violations");
        min = assignment.getMinViolations();
        pair = assignment.getPairViolations();
        pref = assignment.getPrefViolations();
        secdiff = assignment.getSectionViolations();

        // Sorted by the values, descending order
        sortedPref = pref.entrySet().stream().sorted( Map.Entry.comparingByValue( Comparator.reverseOrder() ) )
          .map( e -> e.getKey() )
          .collect( Collectors.toList() );
      }

      log.finer( String.format("At the end of the loop, min[%d], pair[%d], pref[%d], secdiff[%d]", min.size(), pair.size(), pref.size(), secdiff.size()));
    }
  }

  enum Operation { MOVE, MOVE_SECOND, SWAP, SWAP_SECOND, NONE }
  private boolean resolvePref( Tuple<Assignable, Slot> pref ) {
    if( pref == null ) return false;
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
        log.info( String.format("Swapping courses [%s] and [%s] for an eval-decrease of [%d]. Post-swap eval is [%d]", pref.first.toString(), bestMove.second.first.toString(), bestDecrease, assignment.eval() ) );
        return true;
      }
      else if( bestMove.first == Operation.MOVE ) {
        assignment.move( pref.first, pref.second );
        log.info( String.format("Moving course [%s] to slot [%s] for an eval-decrease of [%d]. Post-move eval is [%d]", pref.first.toString(), pref.second.toString(), bestDecrease, assignment.eval() ) );
        return true;
      }
    }
    return false;
  }

  private boolean resolvePair( Tuple<Assignable, Assignable> pair ) {
    if( pair == null ) return false;
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
          log.info( String.format( "Moving [%s] to [%s] for a decrease of [%d]. Post-move eval=[%d]", pair.first.toString(), secondSlot.toString(), bestDecrease, assignment.eval() ) );
          return true;
        case MOVE_SECOND:
          assignment.move( pair.second, firstSlot );
          log.info( String.format( "Moving [%s] to [%s] for a decrease of [%d]. Post-move eval=[%d]", pair.second.toString(), firstSlot.toString(), bestDecrease, assignment.eval() ) );
          return true;
        case SWAP:
          assignment.swap( pair.first, bestMove.second.first );
          log.info( String.format( "Swapping [%s] with [%s] for a decrease of [%d]. Post-move eval=[%d]", pair.first.toString(), bestMove.second.first.toString(), bestDecrease, assignment.eval() ) );
          return true;
        case SWAP_SECOND:
          assignment.swap( pair.second, bestMove.second.first );
          log.info( String.format( "Swapping [%s] with [%s] for a decrease of [%d]. Post-move eval=[%d]", pair.second.toString(), bestMove.second.first.toString(), bestDecrease, assignment.eval() ) );
          return true;
      }
    }
    return false;
  }

  private boolean resolveSection( Tuple<Assignable, Assignable> section ) {
    if( section == null ) return false;
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
          log.info( String.format("Moving [%s] to [%s] for a decrease of [%d]. Final eval is [%d]", section.first.toString(), bestMove.second.second.toString(), bestDecrease, assignment.eval() ));
          return true;
        case MOVE_SECOND:
          assignment.move( section.second, bestMove.second.second );
          log.info( String.format("Moving [%s] to [%s] for a decrease of [%d]. Final eval is [%d]", section.second.toString(), bestMove.second.second.toString(), bestDecrease, assignment.eval() ));
          return true;
        case SWAP:
          assignment.swap( section.first, bestMove.second.first );
          log.info( String.format("Swapping [%s] to [%s] for a decrease of [%d]. Final eval is [%d]", section.first.toString(), bestMove.second.first.toString(), bestDecrease, assignment.eval() ));
          return true;
        case SWAP_SECOND:
          assignment.swap( section.second, bestMove.second.first );
          log.info( String.format("Swapping [%s] to [%s] for a decrease of [%d]. Final eval is [%d]", section.second.toString(), bestMove.second.first.toString(), bestDecrease, assignment.eval() ));
          return true;
      }
    }
    return false;
  }

  private boolean resolveMin( Slot slot ) {
    if( slot == null ) return false;
    int bestDecrease = 0;
    Tuple<Operation, Tuple<Assignable, Slot>> bestMove = new Tuple<Operation, Tuple<Assignable,Slot>>( Operation.NONE, null );
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
        log.info( String.format( "Moving [%s] to [%s] for decrease of [%d], final eval is [%d]", bestMove.second.first.toString(), slot.toString(), bestDecrease, assignment.eval()));
        return true;
      }
    }
    return false;
  }
}
