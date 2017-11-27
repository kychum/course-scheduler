package common;
import java.util.TreeMap;
import java.util.ArrayList;

public class Assignment {
  private TreeMap<Slot, ArrayList<Assignable>> assignments;
  
  public Assignment() {
    assignments = new TreeMap<Slot, ArrayList<Assignable>>();
  }

  public TreeMap<Slot, ArrayList<Assignable>> getAssignments() {
    return assignments;
  }

  public void setAssignments(TreeMap<Slot, ArrayList<Assignable>> assignments) {
    this.assignments = assignments;
  }
}
