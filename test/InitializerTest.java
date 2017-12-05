import common.*;
import optimizer.Optimizer;
import parser.Parser;
import scheduler.Scheduler;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

class InitializerTest {
	static Parser parser;

	@BeforeAll
	static void test() {
		parser = new Parser();
	}
	
	@ParameterizedTest
	@DisplayName("Test Initializer")
	@ValueSource( strings = { "deptinst1.txt", "deptinst2.txt", "example1", "minnumber.txt",
							  "parallelpen.txt", "prefexamp.txt"} )
	  void doInit(String filename) {
	  Instance i = parser.parseFile("test/input/" + filename);
	
	  // finalize the instance, this adds relevant hard constraints from the assignment spec
	  i.finalizeInstance();
	  Assignment best = null;
	  Random rand = new Random();
	  long startTime = System.currentTimeMillis();
	  int ctr;
	  Scheduler s = new Scheduler(i, rand);
	  Assignment assign = s.makeSchedule(); // Or otherwise get assignment from scheduler
	}

}
