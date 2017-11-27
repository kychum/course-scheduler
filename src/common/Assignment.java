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
  
  	// Assume constraint check is performed in calling function
  public void addAssignment(Slot slot, Assignable assignment) {
	  ArrayList<Assignable> currentList = this.assignments.get(slot);
	  currentList.add(assignment);
	  this.assignments.put(slot, currentList);
  }
  
  public void removeAssignment(Slot slot, Assignable assignment) {
	  ArrayList<Assignable> currentList = this.assignments.get(slot);
	  currentList.remove(assignment);
	  this.assignments.put(slot, currentList);
  }
  
  // Assume identification of which slot an assignable is associated with is done in calling function
  public void swapAssignment(Slot s1, Assignable a1, Slot s2, Assignable a2) {
	  ArrayList<Assignable> s1List, s2List;
	  s1List=this.assignments.get(s1);
	  s1List.remove(a1);
	  s1List.add(a2);
	  s2List=this.assignments.get(s2);
	  s2List.remove(a2);
	  s2List.add(a1);
	  this.assignments.put(s1, s1List);
	  this.assignments.put(s2, s2List);
  }
}
