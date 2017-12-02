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
  @ValueSource( strings = { "example1", "gehtnicht1.txt", "gehtnicht2.txt", "gehtnicht3.txt",
    "gehtnicht4.txt", "gehtnicht5.txt", "gehtnicht6.txt", "gehtnicht7.txt", "gehtnicht8.txt",
    "gehtnicht9.txt", "gehtnicht10.txt", "gehtnicht11.txt", "gehtnicht12.txt", "minnumber.txt",
    "pairing.txt", "parallelpen.txt", "prefexamp.txt", "deptinst1.txt", "deptinst2.txt"} )
  void parseExample(String filename) {
    String file = "test/input/" + filename;
    String expectedOutputFile = file.replace("input", "expected");
    String expected = "";
    try{ 
     expected = new String( Files.readAllBytes( FileSystems.getDefault().getPath( expectedOutputFile ) ) );
    }
    catch( IOException e ) {
      fail( "Unable to read the expected output file! (" + expectedOutputFile + ")" );
    }
    Instance i = parser.parseFile( file );
    String[] expectedSplit = expected.split("\n");
    String[] resultSplit = i.toString().split("\n");
    for( int line = 0; line < expectedSplit.length && line < resultSplit.length; ++line ) {
      assertEquals( expectedSplit[line], resultSplit[line], "on line " + (line+1) );
    }
    if( expectedSplit.length != resultSplit.length ) {
      assertEquals( expectedSplit.length, resultSplit.length, "Result lengths do not match" );
    }
  }
}

