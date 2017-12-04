/**
 * 
 */
package scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.TreeMap;

import common.*;

import java.util.List;
import java.util.Random;
import parser.*;

/**
 * @author dengel
 * @author tnmelan
 *
 */
public class Scheduler {

Instance inst;
Random rand;
Assignment assign;
Constraints constraints;
	
	public Scheduler(Instance i) {
		this.inst = i;
		assign = new Assignment(i);
		this.constraints = this.inst.getConstraints();
		makeSchedule();
	}



	public void makeSchedule() {
		
		rand = new Random(0);
		initialize();
		//hillClimb();
	}
	


	/**
	 * Create a random initial schedule without violating hard constraints
	 */
	private void initialize() {
		boolean assigned = false;
		int courseSlotSize = inst.getCourseSlots().size();
		int labSlotSize = inst.getLabSlots().size();
		
		// NOTE: Current iteration is using addPartAssign to the instance to simulate adding an actual assignment, as a quick-and-dirty sort of thing.
		for (Course c : inst.getCourses()) {
			assigned=false;
			TreeMap curAssignments = assign.getCourseAssignments();
			while(!assigned) {	// Might need an additional limiter to the while loop.
				for(int i = 0; i<curAssignments.size(); i++)
				{
					if (curAssignments.containsKey(c)){
						assigned=true;
						break;
					}
				}
				if (!assigned) {
					int nextSlotIndex = rand.nextInt(courseSlotSize);
					Slot courseSlot = inst.getCourseSlots().get(nextSlotIndex);
					if (maintainsHardConstraints(courseSlot, c)) {
						assign.add(courseSlot, c);
						System.out.println("DEBUG: Assigned course " + c.toString() + " to " + courseSlot.toString() + "." ); // debug message
						assigned=true;					
					}
					else {
						System.out.println("DEBUG: Failed to assign course " + c.toString() + " to " + courseSlot.toString() + ".");
					}
				}
			}
		}
		for (Lab l : inst.getLabs()) {
			assigned=false;
			while(!assigned) {	// Might need an additional limiter to the while loop.
				int nextSlotIndex = rand.nextInt(labSlotSize);
				Slot labSlot = inst.getLabSlots().get(nextSlotIndex);
				if (maintainsHardConstraints(labSlot, l)) {
					assign.add(labSlot, l);
					System.out.println("DEBUG: Assigned course " + l.toString() + " to " + labSlot.toString() + "." ); // debug message
					assigned=true;					
				}
			}
		}
		System.out.println("DEBUG: finished init.");
	}
	
	private void hillClimb() {
		List courseSlots = inst.getCourseSlots();
		List labSlots = inst.getLabSlots();
		
		int courseSlotSize = courseSlots.size();
		int labSlotSize = labSlots.size();
		
		int maxHillClimb = 5; // change as necessary
		
		
		
	}
	
	/**
	 * 
	 * @param s slot to be tested
	 * @param a class/lab to be tested
	 * @return true if a class/lab can be assigned to that slot without violating a hard constraint 
	 */
	private boolean maintainsHardConstraints(Slot s, Assignable a) {
		boolean isValid = false;
		Assignment tempAssign = assign;
		try {
			tempAssign.add(s, a);
			// TODO: Figure out why exception thrown within Assignment class terminates entire program.
			isValid = true;
			return isValid;
		} catch (HardConstraintViolationException e) {
			System.out.println("DEBUG: Attempted assignment " + a.toString() + " to " + s.toString() + " failed.");
			isValid = false;
			return isValid;
		}
	}
	
	// hard constraints checker methods
	
	

}
