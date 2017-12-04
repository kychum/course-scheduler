import common.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

class AssignmentTest{
  @Test
  @DisplayName( "Test maxCourse violation" )
  void testMaxCourse() {
    Instance i = new Instance();
    Slot s = new Slot( "TU", "9:00", 0, 0, false );
    Course c = new Course( "CPSC", 100, 1 );
    i.addCourseSlot( s );
    i.addCourse( c );
    Assignment a = new Assignment( i );
    HardConstraintViolationException e = assertThrows( HardConstraintViolationException.class, () -> a.add( s, c ) );
    assertEquals( e.getMessage(), "Assignment would cause slot to be over capacity." );
  }

  @Test
  @DisplayName( "Test maxLab violation" )
  void testMaxLab() {
    Instance i = new Instance();
    Slot s = new Slot( "TU", "9:00", 0, 0, true );
    Lab l = new Lab( "CPSC", 100, 1, 1, true );
    i.addLabSlot( s );
    i.addLab( l );
    Assignment a = new Assignment( i );
    HardConstraintViolationException e = assertThrows( HardConstraintViolationException.class, () -> a.add( s, l ) );
    assertEquals( e.getMessage(), "Assignment would cause slot to be over capacity." );
  }

  @Test
  @DisplayName( "Test not-compatible violation" )
  void testIncomp() {
    Instance i = new Instance();
    Slot s = new Slot( "TU", "9:00", 100, 100, false );
    Course c1 = new Course( "CPSC", 100, 1 );
    Course c2 = new Course( "CPSC", 101, 1 );
    i.addCourseSlot( s );
    i.addCourse( c1 );
    i.addCourse( c2 );
    i.addIncomp( c1, c2 );
    Assignment a = new Assignment( i );
    a.add( s, c1 );
    HardConstraintViolationException e = assertThrows( HardConstraintViolationException.class, () -> a.add( s, c2 ) );
    assertEquals( e.getMessage(), "Assignment would result in incompatibility violation." );
  }

  @Test
  @DisplayName( "Test unwanted violation" )
  void testUnwanted() {
    Instance i = new Instance();
    Slot s = new Slot( "MO", "8:00", 100, 100, false );
    Course c = new Course( "CPSC", 100, 1 );
    i.addCourseSlot( s );
    i.addCourse( c );
    i.addUnwanted( c, s );
    Assignment a = new Assignment( i );
    HardConstraintViolationException e = assertThrows( HardConstraintViolationException.class, () -> a.add( s, c ) );
    assertEquals( e.getMessage(), "Assignment is of a course to an unwanted slot." );
  }
}
