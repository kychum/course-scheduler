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
  static int pen_labsmin = 1;
  static int pen_coursemin = 1;
  static int pen_notpaired = 1;
  static int pen_section = 1;
  static boolean timed = false;
  static boolean doLogging = true;
  static int timeout = 600;
  static Logger log;
  static Assignment best = null;
  static boolean printed = false;

  public static void printUsage() {
    System.out.println( "Usage: java -jar Scheduler.jar <input file>" );
    System.out.println( "If running from compiled .class:\n        java Main <input file>" );
  }

  public static void main( String[] args ) {
	// setup handler for logging
    System.setProperty( "java.util.logging.SimpleFormatter.format",
        "[%1$tF %1$tT] [%4$s] [%2$s]  %5$s%6$s%n");

    // Print out the best solution if program is killed
    // Does not catch kill -9.
    Runtime.getRuntime().addShutdownHook( new Thread() {
      public void run() {
        if( best != null && !printed) {
          System.out.println( "The best solution found so far is:" );
          System.out.println( best.toString() );
        }
      }
    });

    // load the config containing weights
    loadConfig();

    for( Handler h : Logger.getLogger("").getHandlers() ) {
      // Remove the default handler to get rid of duplicate infos
      Logger.getLogger("").removeHandler(h);
    }
    Handler h = new ConsoleHandler();
    if( doLogging )
      h.setLevel(Level.INFO);
    else
      h.setLevel(Level.OFF);
    Logger.getLogger("").addHandler( h );
    log = Logger.getLogger("Main");

    // create a parser so we can begin reading input file
    Parser p = new Parser();

    // parseFile constructs an instance from a file for us
    Instance i;
    if ( args.length > 0 ) {
      i = p.parseFile( args[0] );
    }
    else {
      printUsage();
      return;
    }
    try{
      log.info( "Running search on instance:\n" + i.toString() );

      // finalize the instance, this adds relevant hard constraints from the assignment spec
      i.finalizeInstance();
      Random rand = new Random(0);
      long startTime = System.currentTimeMillis();
      int ctr;
      for( ctr = 0; ctr < maxRuns; ++ctr ) {
        log.info( "Starting iteration " + ctr );
        Scheduler s = new Scheduler(i, rand);
        Assignment assign = s.makeSchedule(); // Or otherwise get assignment from scheduler
        if( best == null ) best = assign;
        Optimizer optimizer = new Optimizer( assign, minfilled, pref, pair, secdiff, rand, pen_labsmin, pen_coursemin, pen_notpaired, pen_section );
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
      printed = true;
    }
    catch( HardConstraintViolationException e ) {
      System.out.println( String.format( "Unable to find a solution for the given instance! Reason: %s", e.getMessage() ) );
    }
  }

  // for loading the config.ini file
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
          case "w_minfilled":
            minfilled = Integer.parseInt( kvPair[1] );
            break;
          case "w_pref":
            pref = Integer.parseInt( kvPair[1] );
            break;
          case "w_pair":
            pair = Integer.parseInt( kvPair[1] );
            break;
          case "w_secdiff":
            secdiff = Integer.parseInt( kvPair[1] );
            break;
          case "maxRuns":
            maxRuns = Integer.parseInt( kvPair[1] );
            break;
          case "timed":
            timed = Integer.parseInt( kvPair[1] ) != 0;
            break;
          case "timeout":
            timeout = Integer.parseInt( kvPair[1] );
            break;
          case "pen_labsmin":
            pen_labsmin = Integer.parseInt( kvPair[1] );
            break;
          case "pen_coursemin":
            pen_coursemin = Integer.parseInt( kvPair[1] );
            break;
          case "pen_notpaired":
            pen_notpaired = Integer.parseInt( kvPair[1] );
            break;
          case "pen_section":
            pen_section = Integer.parseInt( kvPair[1] );
            break;
          case "suppress_log":
            doLogging = Integer.parseInt( kvPair[1] ) == 0;
            break;
          default:
            System.err.println( String.format( "Unknown setting [%s=%s]. Ignoring..", kvPair[0], kvPair[1] ) );
            break;
        }
      }
    }
    catch( IOException e ) {
      log.warning( "Failed to read config file; will proceed with default values." );
    }
  }
}
