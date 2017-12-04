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
    HashSet<Slot> min = assignment.getMinViolations();
    HashSet<Tuple<Assignable, Assignable>> pair = assignment.getPairViolations();
    return original;
  }
}
