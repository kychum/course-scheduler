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
		
		this.rand = r;
		
		//currentNode = new Node(null);
	}
	
	public boolean makeTree() {
		
		
		return makeTreeRec(0);
	}
	
	private boolean makeTreeRec(int assignIndex) {
		//System.out.println("Node index: " + assignIndex + ".");
		
		if (assignIndex == assignables.size())
			return true;
		
		ArrayList<Slot> remainingCourseSlots = new ArrayList<Slot>(courseSlots);
		ArrayList<Slot> remainingLabSlots = new ArrayList<Slot>(labSlots);
		
		
		Assignable currentAssign = assignables.get(assignIndex);
		boolean validSubTree = false;
		while(!validSubTree) {
			if (!currentAssign.isLab())
			{
				if (remainingCourseSlots.size() == 0)
				{
					return false;
				}
				
				int nextCourseSlotIndex = rand.nextInt(remainingCourseSlots.size());
				
				try {
					Slot s = remainingCourseSlots.get(nextCourseSlotIndex);
					assign.add(s, currentAssign);
					validSubTree = makeTreeRec(assignIndex + 1);
					if (!validSubTree) {
						assign.remove(currentAssign);
					}
					remainingCourseSlots.remove(nextCourseSlotIndex);
					
					
				} catch (HardConstraintViolationException e) {
					remainingCourseSlots.remove(nextCourseSlotIndex);
					validSubTree = false;
				}
				
				
			} else {
				
				if (remainingLabSlots.size() == 0)
				{
					return false;
				}
				
				int nextLabSlotIndex = rand.nextInt(remainingLabSlots.size());
				
				try {
					Slot s = remainingLabSlots.get(nextLabSlotIndex);
					assign.add(s, currentAssign);
					validSubTree = makeTreeRec(assignIndex + 1);
					if (!validSubTree) {
						assign.remove(currentAssign);
					}
					remainingLabSlots.remove(nextLabSlotIndex);
					
					
				} catch (HardConstraintViolationException e) {
					remainingLabSlots.remove(nextLabSlotIndex);
					validSubTree = false;
				}
				
			}
			
		}
		
		return true;
	}

}
