package parser;

public class Parser{
	
	private String cSlotPattern = "";
	
	public void Parser() {
		return;
	}
	
    // We can have a few different line formats
	// Name: -> next line is name of instance
	// Course Slots: - > next lines have a course day, time, max/min value
	// Eg:				 MO, 8:00, 2, 1
	// Lab Slots: -> next lines have a lab day, time, max/min value
	// Eg:				 MO, 8:00, 2, 1
	// Courses:
	// Labs:
	public int parseLine() {
		System.out.println("Parsed a line");
		return 0;
	}
}
