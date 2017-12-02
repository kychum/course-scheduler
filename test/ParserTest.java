import parser.Parser;
import common.Instance;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.nio.file.Files;
import java.nio.file.FileSystems;
import java.io.IOException;

class ParserTest{
  static Parser parser;

  @BeforeAll
  static void initialize() {
    parser = new Parser();
  }

  @ParameterizedTest
  @ValueSource( strings = { "test/input/example1" } )
  void parseExample(String file) {
    String expectedOutputFile = file.replace("input", "expected");
    String expected = "";
    try{ 
     expected = new String( Files.readAllBytes( FileSystems.getDefault().getPath( expectedOutputFile ) ) );
    }
    catch( IOException e ) {
      fail( "Unable to read the expected output file! (" + expectedOutputFile + ")" );
    }
    Instance i = parser.parseFile( file );
    System.out.println( i.toString() );
    assertEquals( expected, i.toString() );
  }
}

