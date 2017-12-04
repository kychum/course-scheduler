import common.*;
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
  @ValueSource( ints = { 1, 2, 7, 8, 9 } )
  void testBadInstance(int i) {
    Instance inst = parser.parseFile( String.format( "test/input/gehtnicht%d.txt", i ) );
    assertThrows( HardConstraintViolationException.class, () -> inst.finalizeInstance(),
        String.format( "File [gehtnicht%d.txt] did not cause an exception when finalizing instance!", i) );
  }

  @Test
  @DisplayName( "Implicit constraints on finalized instance" )
  void testFinalizing() {
    Instance instance = new Instance();
    Course c1 = new Course( "CPSC", 100, 1 );
    Lab l11 = new Lab( "CPSC", 100, 1, 1, true );
    Lab l12 = new Lab( "CPSC", 100, 1, 2, false );
    Course c2 = new Course( "CPSC", 501, 1 );
    Course c3 = new Course( "CPSC", 502, 1 );
    Course c4 = new Course( "CPSC", 313, 1 );
    Lab l41 = new Lab( "CPSC", 313, 1, 1, true );
    Course c5 = new Course( "CPSC", 413, 1 );
    Lab l51 = new Lab( "CPSC", 413, 1, 1, true );
    Slot s = new Slot( "MO", "8:00", 100, 100, false );
    Slot s2 = new Slot( "MO", "9:00", 100, 100, false );
    Slot s3 = new Slot( "TU", "8:00", 100, 100, true );
    instance.addCourseSlot( s );
    instance.addCourseSlot( s2 );
    instance.addLabSlot( s3 );
    instance.addCourse( c1 );
    instance.addCourse( c2 );
    instance.addCourse( c3 );
    instance.addCourse( c4 );
    instance.addCourse( c5 );
    instance.addLab( l11 );
    instance.addLab( l12 );
    instance.addLab( l41 );
    instance.addLab( l51 );
    instance.finalizeInstance();

    Constraints c = instance.getConstraints();
    assertTrue( c.checkIncomp( c1, l11 ), "Course should be incompatible with its labs" );
    assertTrue( c.checkIncomp( c1, l12 ), "Course should be incompatible with its labs" );
    assertTrue( c.checkIncomp( c2, c3 ), "500-level courses should be incompatible" );
    assertTrue( c.checkIncomp( c4, Course.getCPSC813() ), "CPSC813 should be incompatible with CPSC313" );
    assertTrue( c.checkIncomp( c4, l41 ), "CPSC313 should be incompatible with its labs" );
    assertTrue( c.checkIncomp( l41, Course.getCPSC813() ), "CPSC813 should be incompatible with CPSC313's labs" );
    assertTrue( c.checkIncomp( c5, Course.getCPSC913() ), "CPSC913 should be incompatible with CPSC413" );
    assertTrue( c.checkIncomp( c5, l51 ), "CPSC413 should be incompatible with its labs" );
    assertTrue( c.checkIncomp( l51, Course.getCPSC913() ), "CPSC913 should be incompatible with CPSC413's labs" );
  }
}
