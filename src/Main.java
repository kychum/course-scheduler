import common.*;
import parser.*;
import java.util.logging.*;
import java.util.*;

public class Main {
  public static void main( String[] args ) {
	// setup handler for logging
    System.setProperty( "java.util.logging.SimpleFormatter.format",
        "[%1$tF %1$tT] [%4$s] [%2$s]  %5$s%6$s%n");
    Handler h = new ConsoleHandler();
    h.setLevel(Level.ALL);
    Logger.getLogger("").addHandler( h );
    Logger.getLogger("").setLevel(Level.ALL);
    // create a parser so we can begin reading input file
    Parser p = new Parser();
    // parseFile constructs an instance from a file for us
    Instance i;
    if ( args.length > 0 ) {
      i = p.parseFile( args[0] );
    }
    else {
      i = p.parseFile( "test.txt" );
    }
    // finalize the instance, this adds relevant hard constraints from the assignment spec
    
    i.finalizeInstance();
    Scheduler s = new Scheduler(i);
  }
  
  // Here we need to start out, we'll have one instance per run of main, but we should generate some number of assignments here
  // do a loop of 50 or however many then compare the results, finding the best, or choosing random
  
  // First thing here, fire up and parse the input file
  // TODO: For now we assume all weights for constraint violations are 1
}
