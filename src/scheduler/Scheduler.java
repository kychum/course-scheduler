/**
 * 
 */
package scheduler;

import common.*;

/**
 * @author dengel
 *
 */
public class Scheduler {

Instance inst;
	
	
	public void makeSchedule() {
		
		Instance inst = parseFile();
		initialize();
		//hillClimb();
	}
	


	/**
	 * Create a random schedule without violating hard constraints
	 */
	private void initialize() {
		
		
	}
	
	
	private boolean violatesHard() {
		
		
		
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
