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
		//Scanner scan = new Scanner(System.in);
		//System.out.print("Enter the name of your input file: ");
		//String filename = scan.nextLine();
		String filename = "gehtnicht4.txt";
		try {
			BufferedReader br = new BufferedReader(new FileReader(filename));
			String line;
			while((line = br.readLine()) != null) {
				if (line.equals("Name:")) {
					System.out.println(line);
				}
			}
		} catch (java.io.FileNotFoundException e) {
			System.out.println("File " + filename + " does not exist!");
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

}
