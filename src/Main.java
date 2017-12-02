import common.*;
import parser.*;
import scheduler.*;
import java.util.logging.*;

public class Main {
  public static void main( String[] args ) {
    Handler h = new ConsoleHandler();
    h.setLevel(Level.ALL);
    Logger.getLogger("Parser").addHandler( h );
    Logger.getLogger("Parser").setLevel(Level.ALL);
    Logger.getLogger("Parser").fine("test");
    Parser p = new Parser();
    Instance i = p.parseFile( "test/input/example1" );
    System.out.println(i.toString());
  }
}
