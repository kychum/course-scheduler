import common.*;
import parser.*;
<<<<<<< HEAD
import scheduler.Scheduler;

=======
import scheduler.*;
>>>>>>> Add config to read in weights on runtime
import java.util.logging.*;
import java.util.*;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.io.BufferedReader;
import java.io.IOException;

public class Main {
  static int minfilled = 10;
  static int pref = 10;
  static int pair = 10;
  static int secdiff = 10;
  static Logger log;
  public static void main( String[] args ) {
	// setup handler for logging
    System.setProperty( "java.util.logging.SimpleFormatter.format",
        "[%1$tF %1$tT] [%4$s] [%2$s]  %5$s%6$s%n");
    Handler h = new ConsoleHandler();
    h.setLevel(Level.ALL);
    Logger.getLogger("").addHandler( h );
    Logger.getLogger("").setLevel(Level.ALL);
    log = Logger.getLogger("Main");

    loadConfig();

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
    
    try{
      i.finalizeInstance();
      System.out.println(i.toString());
      Scheduler s = new Scheduler(i);
    }
    catch( HardConstraintViolationException e ) {
      System.out.println( String.format( "Unable to find a solution for the given instance! Reason: %s", e.getMessage() ) );
    }
  }

  static void loadConfig() {
    BufferedReader reader;
    try{
      reader = Files.newBufferedReader( FileSystems.getDefault().getPath( "config.ini" ) );
      while( reader.ready() ) {
        String line = reader.readLine();
        if( line.equals( "" ) || line.charAt(0) == ';' ) {
          continue;
        }

        String[] kvPair = line.split( "=" );
        switch( kvPair[0] ) {
          case "minfilled":
            minfilled = Integer.parseInt( kvPair[1] );
            break;
          case "pref":
            pref = Integer.parseInt( kvPair[1] );
            break;
          case "pair":
            pair = Integer.parseInt( kvPair[1] );
            break;
          case "secdiff":
            secdiff = Integer.parseInt( kvPair[1] );
            break;
          default:
            log.warning( String.format( "Unknown setting [%s=%s]. Ignoring..", kvPair[0], kvPair[1] ) );
            break;
        }
      }
    }
    catch( IOException e ) {
      log.warning( "Failed to read config file; will proceed with default values." );
    }
  }
  
  // Here we need to start out, we'll have one instance per run of main, but we should generate some number of assignments here
  // do a loop of 50 or however many then compare the results, finding the best, or choosing random
  
  // First thing here, fire up and parse the input file
  // TODO: For now we assume all weights for constraint violations are 1
}
