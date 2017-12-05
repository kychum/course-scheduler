package parser;
import common.Assignable;
import common.Instance;
import common.Course;
import common.Slot;
import common.Lab;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Logger;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.io.BufferedReader;
import java.io.IOException;

public class Parser{
    private static Logger log = Logger.getLogger( "Parser" );
    // Parsing modes, what type of line are we reading?
    private enum Mode {
    	NAME,
    	CSLOT,
	    LSLOT,
	    COURSE,
	    LAB,
	    NOTCOMPAT,
	    UNWANTED,
	    PREF,
	    PAIR,
	    PARTIAL
    }
    // default to name, this is usually top line
    private Mode mode = Mode.NAME;
	
	public void parseLine( String line, Instance instance, ArrayList<String[]> prefCache ) {
	    log.fine( String.format( "Parsing line [%s] in mode [%s]", line, this.mode.name() ) );
	    switch( this.mode ) {
	      case NAME:
	        instance.setName( line );
	        break;
	      case CSLOT:
	        Slot courseSlot = parseSlot( line, false );
	        instance.addCourseSlot( courseSlot );
	        break;
	      case LSLOT:
	        Slot labSlot = parseSlot( line, true );
	        instance.addLabSlot( labSlot );
	        break;
	      case COURSE:
	        Course course = parseCourse( line );
	        instance.addCourse( course );
	        break;
	      case LAB:
	        Lab lab = parseLab( line );
	        instance.addLab( lab );
	        break;
	      case NOTCOMPAT:
	        Assignable[] ncAssigns = Arrays.stream( tokenize( line, "," ) )
	          .map( s -> parseAssignable( s ) )
	          .toArray( Assignable[]::new );
	        if( !instance.addIncomp( ncAssigns[0], ncAssigns[1] ) ) {
	          log.warning( String.format( "Failed to set [%s] and [%s] as incompatible. Was the entry already present?", ncAssigns[0].toString(), ncAssigns[1].toString() ) );
	        }
	        break;
	      case UNWANTED:
	        String[] uwParts = tokenize( line, ",", 2 );
	        Assignable uwAssigns = parseAssignable( uwParts[0] );
	        Slot uwSlot = parseSlot( uwParts[1], uwParts[0].contains("LAB") || uwParts[0].contains("TUT") );
	        instance.addUnwanted( uwAssigns, uwSlot );
	        break;
	      case PREF:
	        String[] prefParts = tokenize( line, "," );
	        Assignable prefAssigns = parseAssignable( prefParts[2] );
	        boolean prefIsLab = prefParts[2].contains("LAB") || prefParts[2].contains("TUT");
	        Slot prefSlot = parseSlot( String.join( ",", prefParts[0], prefParts[1] ), prefIsLab );
	        int prefValue = Integer.parseInt( prefParts[3] );
	        if( !instance.addPreference( prefAssigns, prefSlot, prefValue ) ){
	          log.warning( String.format( "Ignoring preference [%d] for class [%s] to slot [%s]. Is the slot a valid slot? Caching for later.", prefValue, prefAssigns.toString(), prefSlot.toString() ));
	          prefCache.add(prefParts);
	        }
	        break;
	      case PAIR:
	        Assignable[] pairAssigns = Arrays.stream( tokenize( line, "," ) )
	          .map( s -> parseAssignable( s ) )
	          .toArray( Assignable[]::new );
	        if( !instance.addPair( pairAssigns[0], pairAssigns[1] ) ) {
	          log.warning( String.format( "Unable to set [%s] and [%s] as paired. Was it already defined earlier in the file?", pairAssigns[0], pairAssigns[1] ) );
	        }
	        break;
	      case PARTIAL:
	        String[] partialParts = tokenize( line, ",", 2 );
	        log.fine( partialParts[0] );
	        log.fine( partialParts[1] );
	        Assignable partialAssigns = parseAssignable( partialParts[0] );
	        Slot partialSlot = parseSlot( partialParts[1], partialParts[0].contains("LAB") || partialParts[0].contains("TUT") );
	        instance.addPartAssign( partialAssigns, partialSlot );
	        break;
	    }
	}

	  private String[] tokenize( String str, String token ) {
	    return Arrays.stream( str.split( token ) )
	      .map( String::trim )
	      .toArray( String[]::new );
	  }
	
	  private String[] tokenize( String str, String token, int limit ) {
	    return Arrays.stream( str.split( token, limit ) )
	      .map( String::trim )
	      .toArray( String[]::new );
	  }
	
	  public Assignable parseAssignable( String str ) {
	    if( str.contains( "LAB" ) || str.contains( "TUT" ) ) {
	      return parseLab(str);
	    }
	    return parseCourse(str);
	  }
	
	  public Slot parseSlot( String slot ) {
	    return parseSlot( slot, false );
	  }
	
	  public Slot parseSlot( String slot, boolean isLab ) {
	    String[] parts = tokenize( slot, "," );
	    Slot output;
	    if( parts.length >= 4 ) {
	      output = new Slot( parts[0], parts[1], Integer.parseInt( parts[2] ), Integer.parseInt( parts[3] ), isLab );
	    }
	    else {
	      // Slot not used for assignment, just for comparing stuff
	      output = new Slot( parts[0], parts[1], 0, 0, isLab );
	    }
	    return output;
	  }
	
	  public Course parseCourse( String course ) {
	    String[] parts = tokenize( course, "LEC" );
	    String[] id = tokenize( parts[0], " " );
	    return new Course( id[0], Integer.parseInt( id[1] ), Integer.parseInt( parts[1] ) );
	  }
	
	  public Lab parseLab( String lab ) {
	    // TODO: sanity check the input
	    Lab output;
	    boolean isTutorial = lab.contains( "TUT" );
	    String[] parts = tokenize( lab, "((LEC)|(LAB)|(TUT))" );
	    String[] id = tokenize( parts[0], "\\s+" );
	    if( parts.length > 2 ) {
	      output = new Lab( id[0], Integer.parseInt( id[1] ), Integer.parseInt( parts[1] ), Integer.parseInt( parts[2] ), isTutorial );
	    }
	    else {
	      output = new Lab( id[0], Integer.parseInt( id[1] ), Integer.parseInt( parts[1] ), isTutorial );
	    }
	    return output;
	  }
	
	  public boolean changeMode( String line ) {
	    if( line.compareToIgnoreCase( "Name:" ) == 0 ) {
	      this.mode = Mode.NAME;
	    }
	    else if( line.compareToIgnoreCase( "Course slots:" ) == 0 ) {
	      this.mode = Mode.CSLOT;
	    }
	    else if( line.compareToIgnoreCase( "Lab slots:" ) == 0 ) {
	      this.mode = Mode.LSLOT;
	    }
	    else if( line.compareToIgnoreCase( "Courses:" ) == 0 ) {
	      this.mode = Mode.COURSE;
	    }
	    else if( line.compareToIgnoreCase( "Labs:" ) == 0 ) {
	      this.mode = Mode.LAB;
	    }
	    else if( line.compareToIgnoreCase( "Not compatible:" ) == 0 ) {
	      this.mode = Mode.NOTCOMPAT;
	    }
	    else if( line.compareToIgnoreCase( "Unwanted:" ) == 0 ) {
	      this.mode = Mode.UNWANTED;
	    }
	    else if( line.compareToIgnoreCase( "Preferences:" ) == 0 ) {
	      this.mode = Mode.PREF;
	    }
	    else if( line.compareToIgnoreCase( "Pair:" ) == 0 ) {
	      this.mode = Mode.PAIR;
	    }
	    else if( line.compareToIgnoreCase( "Partial assignments:" ) == 0 ) {
	      this.mode = Mode.PARTIAL;
	    }
	    else {
	      return false;
	    }
	
	    log.finest( String.format( "Changing mode to [%s].", this.mode.name() ) );
	    return true;
	  }
	
	  public Instance parseFile( String file ) {
	    Instance instance = new Instance();
	    BufferedReader inFile;
	    ArrayList<String[]> prefCache = new ArrayList<String[]>();
	    try{
	      inFile = Files.newBufferedReader( FileSystems.getDefault().getPath( file ) );
	      while( inFile.ready() ) {
	        String line = inFile.readLine().trim();
	        if( !changeMode( line ) && !line.equals( "" ) ) {
	          parseLine( line, instance, prefCache );
	        }
	      }
	    }
	    catch (java.io.FileNotFoundException e) {
	    	log.severe( String.format("Could not find the file [%s]", file));
	    }
	    catch(IOException e){
	      log.severe( String.format( "Encountered an error when parsing the file [%s]", file ) );
	      e.printStackTrace();
	    }
	    
	    for (String[] prefParts : prefCache) {
	    	Assignable prefAssigns = parseAssignable( prefParts[2] );
	        boolean prefIsLab = prefParts[2].contains("LAB") || prefParts[2].contains("TUT");
	        Slot prefSlot = parseSlot( String.join( ",", prefParts[0], prefParts[1] ), prefIsLab );
	        int prefValue = Integer.parseInt( prefParts[3] );
	        if( !instance.addPreference( prefAssigns, prefSlot, prefValue ) ){
	          log.warning( String.format( "Ignoring preference [%d] for class [%s] to slot [%s]. Is the slot a valid slot?", prefValue, prefAssigns.toString(), prefSlot.toString() ));
	        }
	    }
	
	    return instance;
	  }
}
