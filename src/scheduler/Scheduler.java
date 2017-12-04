/**
 * 
 */
package scheduler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

import common.*;
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
	
	public Scheduler(Instance i) {
		this.inst = i;
		assign = new Assignment(i);
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
		
		int courseSlotSize = inst.getCourseSlots().size();
		for (Course c : inst.getCourses()) {
			int nextSlotIndex = rand.nextInt() % courseSlotSize;
			Slot courseSlot = inst.getCourseSlots().get(nextSlotIndex);
			if (maintainsHardConstraints(courseSlot, c)) {
				
				
				// TODO assign course to slot
				
			}
				
		}
		
	}
	
	/**
	 * 
	 * @param s slot to be tested
	 * @param a class/lab to be tested
	 * @return true if a class/lab can be assigned to that slot without violating a hard constraint 
	 */
	private boolean maintainsHardConstraints(Slot s, Assignable a) {
		
		try {
			
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
		return false;
	}
	
	
	

}
