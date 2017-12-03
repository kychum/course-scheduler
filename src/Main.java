import common.*;
import parser.*;
import scheduler.*;
import java.util.logging.*;
import java.util.*;

public class Main {
  public static void main( String[] args ) {
	// setup handler for logging
    Handler h = new ConsoleHandler();
    h.setLevel(Level.ALL);
    Logger.getLogger("Parser").addHandler( h );
    Logger.getLogger("Parser").setLevel(Level.ALL);
    Logger.getLogger("Parser").fine("test");
    // create a parser so we can begin reading input file
    Parser p = new Parser();
    // parseFile constructs an instance from a file for us
    Instance i;
    if ( args.length > 0 ) {
      i = p.parseFile( args[0] );
    }
    else {
      i = p.parseFile( "test/input/deptinst2.txt" );
    }
    // finalize the instance, this adds relevant hard constraints from the assignment spec
    i.finalizeInstance();
    // verify no hard constraints are currently violated
    i.verifyInstance();
    //System.out.println(i.toString());
  }
  
  // Here we need to start out, we'll have one instance per run of main, but we should generate some number of assignments here
  // do a loop of 50 or however many then compare the results, finding the best, or choosing random
  
  // First thing here, fire up and parse the input file
  // TODO: For now we assume all weights for constraint violations are 1
}
