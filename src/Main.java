import common.*;
import parser.*;
import scheduler.*;
import java.util.logging.*;
import java.util.*;

public class Main {
  public static void main( String[] args ) {
    Handler h = new ConsoleHandler();
    h.setLevel(Level.ALL);
    Logger.getLogger("Parser").addHandler( h );
    Logger.getLogger("Parser").setLevel(Level.ALL);
    Logger.getLogger("Parser").fine("test");
    Parser p = new Parser();
    Instance i;
    if( args.length > 0 )
      i = p.parseFile( args[0] );
    else
      i = p.parseFile( "test/input/deptinst2.txt" );
    System.out.println(i.toString());
  }
}
