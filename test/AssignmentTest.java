import common.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.HashSet;

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
    assertEquals( "Assignment would cause slot to be over capacity.", e.getMessage() );
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
    assertEquals( "Assignment would cause slot to be over capacity.", e.getMessage() );
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
    assertEquals( "Assignment would result in incompatibility violation.", e.getMessage() );
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
    assertEquals( "Assignment is of a course to an unwanted slot.", e.getMessage() );
  }

  @Test
  @DisplayName( "Test swapping which causes violation" )
  void testSwapIncomp() {
    Instance i = new Instance();
    Slot s1 = new Slot( "MO", "8:00", 100, 100, false );
    Slot s2 = new Slot( "MO", "9:00", 100, 100, false );
    Course c1 = new Course( "CPSC", 100, 1 );
    Course c2 = new Course( "CPSC", 100, 2 );
    Course c3 = new Course( "CPSC", 101, 1 );
    Course c4 = new Course( "CPSC", 102, 1 );
    i.addCourseSlot( s1 );
    i.addCourseSlot( s2 );
    i.addCourse( c1 );
    i.addCourse( c2 );
    i.addCourse( c3 );
    i.addCourse( c4 );
    i.addIncomp( c1, c3 );
    i.addUnwanted( c4, s1 );
    Assignment a = new Assignment( i );
    a.add( c1, s1 );
    a.add( c2, s1 );
    a.add( c3, s2 );
    a.add( c4, s2 );
    HardConstraintViolationException e = assertThrows( HardConstraintViolationException.class,
        () -> a.swap( c2, c3 ), "Swap should have caused an incompatability" );
    assertEquals( "Assignment would result in incompatibility violation.", e.getMessage() );
    e = assertThrows( HardConstraintViolationException.class, () -> a.swap( c2, c4 ),
        "Swap should have caused an 'unwanted' violation" );
    assertEquals( "Assignment is of a course to an unwanted slot.", e.getMessage() );
  }

  @Test
  @DisplayName( "Test swapping causing overloaded maxAssign" )
  void testSwapMax() {
    Instance i = new Instance();
    Slot s1 = new Slot( "MO", "8:00", 1, 1, false );
    Slot s2 = new Slot( "MO", "8:00", 1, 1, true );
    Slot s3 = new Slot( "MO", "9:00", 1, 1, false );
    Slot s4 = new Slot( "MO", "9:00", 1, 1, true );
    Course c1 = new Course( "CPSC", 100, 1 );
    Course c2 = new Course( "CPSC", 101, 1 );
    Lab l = new Lab( "CPSC", 103, 1, 1, true );
    i.addCourseSlot( s1 );
    i.addCourseSlot( s3 );
    i.addLabSlot( s2 );
    i.addLabSlot( s4 );
    i.addCourse( c1 );
    i.addCourse( c2 );
    i.addLab( l );
    Assignment a = new Assignment( i );
    a.add( c1, s1 );
    a.add( l, s2 );
    a.add( c2, s3 );
    HardConstraintViolationException e = assertThrows( HardConstraintViolationException.class,
        () -> a.swap( c2, l ), "Swap should have caused maxAssign error" );
    assertEquals( "Assignment would cause slot to be over capacity.", e.getMessage() );
  }

  @Test
  @DisplayName( "Test swapping invalid slots" )
  void testSwapInvalidSlot() {
    Instance i = new Instance();
    Slot s1 = new Slot( "MO", "8:00", 1, 1, false );
    Slot s2 = new Slot( "TU", "9:00", 1, 1, true );
    Course c1 = new Course( "CPSC", 100, 1 );
    Lab l1 = new Lab( "CPSC", 100, 1, 1, false );
    i.addCourseSlot( s1 );
    i.addLabSlot( s2 );
    i.addCourse( c1 );
    i.addLab( l1 );
    Assignment a = new Assignment( i );
    a.add( c1, s1 );
    a.add( l1, s2 );
    HardConstraintViolationException e = assertThrows( HardConstraintViolationException.class,
        () -> a.swap( c1, l1 ), "Swap should have caused error due to differing slot types" );
    assertEquals( "Could not swap lab and course due to a corresponding slot not existing", e.getMessage() );
  }

  @Test
  @DisplayName( "Test getting slotmin violations" )
  void testSlotMin() {
    Instance i = new Instance();
    Slot s1 = new Slot( "MO", "8:00", 2, 1, false );
    Slot s2 = new Slot( "TU", "8:00", 2, 1, false );
    Slot s3 = new Slot( "MO", "10:00", 2, 1, true );
    Slot s4 = new Slot( "FR", "12:00", 2, 1, true );
    Course c1 = new Course( "CPSC", 100, 1 );
    Course c2 = new Course( "CPSC", 101, 1 );
    Lab l1 = new Lab( "CPSC", 100, 1, 1, false );
    Lab l2 = new Lab( "CPSC", 101, 1, 1, true );
    i.addCourseSlot( s1 );
    i.addCourseSlot( s2 );
    i.addLabSlot( s3 );
    i.addLabSlot( s4 );
    i.addCourse( c1 );
    i.addCourse( c2 );
    i.addLab( l1 );
    i.addLab( l2 );
    Assignment a = new Assignment( i );
    a.add( c1, s1 );
    a.add( c2, s1 );
    a.add( l1, s3 );
    a.add( l2, s3 );
    HashSet<Slot> min = a.getMinViolations();
    assertTrue( min.size() == 2, "There should be one lab slot and one course slot having min violations" );
    assertTrue( min.contains( s2 ), "The second course slot should be in violation" );
    assertTrue( min.contains( s4 ), "The second lab slot should be in violation" );
  }

  @Test
  @DisplayName( "Optimal instance should have no violations" )
  void testOptimal() {
    Instance i = new Instance();
    Slot s = new Slot( "MO", "14:00", 1, 1, false );
    Slot s2 = new Slot( "MO", "17:00", 1, 1, false );
    Course c = new Course( "CPSC", 100, 1 );
    i.addCourseSlot( s );
    i.addCourseSlot( s2 );
    i.addCourse( c );
    Assignment a = new Assignment( i );
    a.add( c, s );
    assertEquals( 0, a.eval() );
    assertNull( a.getMinViolations() );
  }
}
