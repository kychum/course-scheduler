import common.*;
import parser.Parser;
import scheduler.Scheduler;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

class InitializerTest {
	static Parser parser;

	@BeforeAll
	static void test() {
		parser = new Parser();
	}
	
	@ParameterizedTest
	@DisplayName("Test Initializer")
	@ValueSource( strings = { "deptinst2.txt", "example1", "gehtnicht3.txt",
		    "gehtnicht4.txt", "gehtnicht5.txt", "gehtnicht6.txt", "gehtnicht10.txt", "gehtnicht11.txt", "gehtnicht12.txt", "minnumber.txt",
		    "pairing.txt", "parallelpen.txt", "prefexamp.txt", "deptinst1.txt" } )
	void doInit(String filename) {
		String file = "test/input/" + filename;
		System.out.println("Starting file: " + filename);
		Instance i = parser.parseFile(file);
		i.finalizeInstance();
		//Scheduler s = new Scheduler(i);
		String expected = filename + ".expected";
		//String out = s.getAssignment().getCourseAssignments().toString();
	    //BufferedWriter writer;
	    //try {
	    	//writer = new BufferedWriter(new FileWriter(expected));
	    	//writer.write("foo");
	    	//writer.close();
	    //} catch (IOException e) {
	    //	e.printStackTrace();
	    //}
	}

}
