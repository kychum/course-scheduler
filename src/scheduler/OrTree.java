package scheduler;

import java.util.ArrayList;
import java.util.Random;

import common.Assignable;
import common.Assignment;
import common.HardConstraintViolationException;
import common.Instance;
import common.Slot;

public class OrTree {
	
	ArrayList<Assignable> assignables;
	ArrayList<Assignable> unassigned;
	ArrayList<Slot> courseSlots;
	ArrayList<Slot> labSlots;
	Random rand;
	
	//Node currentNode;
	
	Assignment assign;
	Instance inst;
	
	public OrTree(Assignment a, Instance i, Random r) {
		this.assign = a;
		this.inst = i;
		assignables = new ArrayList<Assignable>();
		assignables.addAll(inst.getCourses());
		assignables.addAll(inst.getLabs());
		courseSlots = inst.getCourseSlots();
		labSlots = inst.getLabSlots();
		unassigned = new ArrayList<Assignable>(assignables);
		
		this.rand = r;
		
		//currentNode = new Node(null);
	}
	
	public boolean makeTree(){
		return makeTreeRec(rand.nextInt(assignables.size()), unassigned);
	}
	
	private boolean makeTreeRec(int assignIndex, ArrayList<Assignable> stillUnassigned) {
		//System.out.println("Node index: " + assignIndex + ".");
		
		// NOTE TO SELF: Attempt to randomly choose root, rather than starting from index 0 for root.
		if (stillUnassigned.size()==0)
			return true;
		
		ArrayList<Slot> remainingCourseSlots = new ArrayList<Slot>(courseSlots);
		ArrayList<Slot> remainingLabSlots = new ArrayList<Slot>(labSlots);
		ArrayList<Assignable> localUnassigned = new ArrayList<Assignable>(stillUnassigned);
		
		// System.out.println("Assign index = " + assignIndex + ", numUnassigned = " + stillUnassigned.size());
		
		Assignable currentAssign = stillUnassigned.get(assignIndex);
		boolean validSubTree = false;
		while(!validSubTree) {
			if (!currentAssign.isLab())
			{
				if (remainingCourseSlots.size() == 0)
				{
					// System.out.println("Remaining course slots = 0");
					return false;
				}
				
				int nextCourseSlotIndex = rand.nextInt(remainingCourseSlots.size());
				
				try {
					Slot s = remainingCourseSlots.get(nextCourseSlotIndex);
					// System.out.println("Index: " + assignIndex + "; Attempting to assign " + currentAssign.toString() + " to " + s.toString() + ".");
					// System.out.println("Size of remaining course slots: " + remainingCourseSlots.size());
					localUnassigned.remove(assignIndex);
					assign.add(s, currentAssign);
					// System.out.println("numUnassigned = " + localUnassigned.size());
					validSubTree = makeTreeRec(rand.nextInt(localUnassigned.size()==0?1:localUnassigned.size()), localUnassigned);
					if (!validSubTree) {
						// System.out.println("Failed to assign " + currentAssign.toString() + " to " + s.toString() + ".");
						localUnassigned.add(assignIndex, currentAssign);
						assign.remove(currentAssign);
					}
					// System.out.println("Size before remove: " + remainingCourseSlots.size());
					remainingCourseSlots.remove(nextCourseSlotIndex);
					// System.out.println("Size after remove: " + remainingCourseSlots.size());
					
				} catch (HardConstraintViolationException e) {
					localUnassigned.add(assignIndex, currentAssign);
					remainingCourseSlots.remove(nextCourseSlotIndex);
					validSubTree = false;
				}
				// System.out.println("Size before re-loop: " + remainingCourseSlots.size());
				
				
			} else {
				
				if (remainingLabSlots.size() == 0)
				{
					return false;
				}
				
				int nextLabSlotIndex = rand.nextInt(remainingLabSlots.size());
				
				try {
					Slot s = remainingLabSlots.get(nextLabSlotIndex);
					// System.out.println("Index: " + assignIndex + "; Attempting to assign " + currentAssign.toString() + " to " + s.toString() + ".");
					// System.out.println("Size of remaining lab slots: " + remainingLabSlots.size());
					// System.out.println("Going to remove index " + assignIndex + " from something of size " + localUnassigned.size());
					localUnassigned.remove(assignIndex);
					assign.add(s, currentAssign);
					validSubTree = makeTreeRec(rand.nextInt(localUnassigned.size()==0?1:localUnassigned.size()), localUnassigned);
					if (!validSubTree) {
						// System.out.println("Failed to assign " + currentAssign.toString() + " to " + s.toString() + ".");
						localUnassigned.add(assignIndex, currentAssign);
						assign.remove(currentAssign);
					}
					else {
						localUnassigned.add(assignIndex, currentAssign);
					}
					// System.out.println("Size before remove: " + remainingLabSlots.size());
					remainingLabSlots.remove(nextLabSlotIndex);
					// System.out.println("Size after remove: " + remainingLabSlots.size());
					
				} catch (HardConstraintViolationException e) {
					localUnassigned.add(assignIndex, currentAssign);
					remainingLabSlots.remove(nextLabSlotIndex);
					validSubTree = false;
				}
				// System.out.println("Size before re-loop: " + remainingLabSlots.size());
				
			}
			
		}
		
		return true;
	}

}
