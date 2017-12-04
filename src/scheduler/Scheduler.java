/**
 * 
 */
package scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

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
		for (Course c : inst.getCourses()) {
			assigned=false;
			while(!assigned) {	// Might need an additional limiter to the while loop.
				int nextSlotIndex = rand.nextInt(courseSlotSize);
				Slot courseSlot = inst.getCourseSlots().get(nextSlotIndex);
				if (maintainsHardConstraints(courseSlot, c)) {
					inst.addPartAssign(c, courseSlot);
					System.out.println("DEBUG: Assigned course " + c.toString() + " to " + courseSlot.toString() + "." ); // debug message
					assigned=true;
					// TODO assign course to slot
					
				}
			}
		}
		for (Lab l : inst.getLabs()) {
			assigned=false;
			while(!assigned) {	// Might need an additional limiter to the while loop.
				int nextSlotIndex = rand.nextInt(labSlotSize);
				Slot labSlot = inst.getLabSlots().get(nextSlotIndex);
				if (maintainsHardConstraints(labSlot, l)) {
					inst.addPartAssign(l, labSlot);
					System.out.println("DEBUG: Assigned course " + l.toString() + " to " + labSlot.toString() + "." ); // debug message
					assigned=true;
					// TODO assign course to slot
					
				}
			}
		}
		System.out.println("DEBUG: finished init.");
	}
	
	private void hillClimb() {
		int courseSlotSize = inst.getCourseSlots().size();
	}
	
	/**
	 * 
	 * @param s slot to be tested
	 * @param a class/lab to be tested
	 * @return true if a class/lab can be assigned to that slot without violating a hard constraint 
	 */
	private boolean maintainsHardConstraints(Slot s, Assignable a) {
		boolean isValid = false;
		Instance tempInst = inst;
		try {
			tempInst.addPartAssign(a, s);
			isValid = true;
		} catch (Exception e) {
			// TODO: handle exception
			System.out.println("DEBUG: Attempted assignment " + a.toString() + " to " + s.toString() + " failed.");
			isValid = false;
		}
		
		return isValid;
	}
	
	// hard constraints checker methods
	
	

}
