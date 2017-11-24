/**
 * 
 */
package scheduler;

import common.*;
import java.util.Random;

/**
 * @author dengel
 * @author tnmelan
 *
 */
public class Scheduler {

Instance inst;
Random rand;
Assignment assign;
	
	public void makeSchedule() {
		
		Instance inst = parseFile();
		assign = new Assignment();
		rand = new Random(0);
		initialize();
		//hillClimb();
	}
	


	/**
	 * Create a random initial schedule without violating hard constraints
	 */
	private void initialize() {
		
		int courseSlotSize = inst.getCourseSlots().size();
		for (Course s : inst.getCourses()) {
			int nextSlotIndex = rand.nextInt() % courseSlotSize;
			Slot courseSlot = inst.getCourseSlots().get(nextSlotIndex);
			if (maintainsHardConstraints(courseSlot, s)) {
				
				
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
		
		// TODO Auto-generated method stub
		
		return false;
	}
	
	
	
	private Instance parseFile() {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	public static void main(String[] args) {
		
		Scheduler s = new Scheduler();
		s.makeSchedule();
		
	}

}
