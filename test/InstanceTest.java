import common.Instance;
import common.HardConstraintViolationException;
import parser.Parser;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;

class InstanceTest {
  static Parser parser;

  @BeforeAll
  static void initialize() {
    parser = new Parser();
  }

  @ParameterizedTest( name = "gehtnicht{0}.txt" )
  @DisplayName( "Check instance validation when finalizeInstance is called" )
  @ValueSource( ints = { 1, 2, 6, 7, 8, 9, 10 } )
  void testBadInstance(int i) {
    Instance inst = parser.parseFile( String.format( "test/input/gehtnicht%d.txt", i ) );
    assertThrows( HardConstraintViolationException.class, () -> inst.finalizeInstance(),
        String.format( "File [gehtnicht%d.txt] did not cause an exception when finalizing instance!", i) );
  }
}
