import optimizer.*;
import common.*;
import parser.*;
import scheduler.Scheduler;
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
  static int maxRuns = 50;
  static boolean timed = false;
  static int timeout = 600;
  static Logger log;
  public static void main( String[] args ) {
	// setup handler for logging
    System.setProperty( "java.util.logging.SimpleFormatter.format",
        "[%1$tF %1$tT] [%4$s] [%2$s]  %5$s%6$s%n");
    for( Handler h : Logger.getLogger("").getHandlers() ) {
      // Remove the default handler to get rid of duplicate infos
      Logger.getLogger("").removeHandler(h);
    }
    Handler h = new ConsoleHandler();
    h.setLevel(Level.ALL);
    Logger.getLogger("").addHandler( h );
    Logger.getLogger("").setLevel(Level.INFO);
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
    	//i = p.parseFile( "test.txt" );
    	//i = p.parseFile("test/input/gehtnicht3.txt");
    	i = p.parseFile("test/input/deptinst2.txt");
    }
    // finalize the instance, this adds relevant hard constraints from the assignment spec
    
    try{
      log.info( "Running search on instance:\n" + i.toString() );
      i.finalizeInstance();
      Assignment best = null;
      Random rand = new Random(0);
      long startTime = System.currentTimeMillis();
      int ctr;
      for( ctr = 0; ctr < maxRuns; ++ctr ) {
        log.info( "Starting iteration " + ctr );
        Scheduler s = new Scheduler(i, rand);
        Assignment assign = s.makeSchedule(); // Or otherwise get assignment from scheduler
        Optimizer optimizer = new Optimizer( assign, minfilled, pref, pair, secdiff );
        Assignment optimized = optimizer.optimize();
        if( best == null || optimized.eval() < best.eval() ) {
          best = optimized;
        }
      
        if( timed && (( System.currentTimeMillis() - startTime )/1000) >= timeout ) {
          log.warning( "Search has run past the configured timeout; terminating with the current best solution." );
          break;
        }
      }
      System.out.println( "The best solution after " + ctr + " runs is:" );
      System.out.println( best.toString() );
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
          case "maxRuns":
            maxRuns = Integer.parseInt( kvPair[1] );
            break;
          case "timed":
            timed = Integer.parseInt( kvPair[1] ) != 0;
          case "timeout":
            timeout = Integer.parseInt( kvPair[1] );
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
}
