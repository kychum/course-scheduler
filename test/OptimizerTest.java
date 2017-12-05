import common.*;
import optimizer.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.*;
import java.util.Random;

class OptimizerTest {
  static Random rand = new Random();
  @Test
  @DisplayName( "Optimal instance should have no changes" )
  void testAlreadyOptimal() {
    Instance i = new Instance();
    Slot s = new Slot( "MO", "14:00", 1, 1, false );
    Slot s2 = new Slot( "MO", "17:00", 1, 1, false );
    Course c = new Course( "CPSC", 100, 1 );
    i.addCourseSlot( s );
    i.addCourseSlot( s2 );
    i.addCourse( c );
    Assignment a = new Assignment( i );
    a.add( c, s );
    Optimizer o = new Optimizer( a, rand );
    o.optimize();
    assertEquals( s, o.getAssignment().getAssignmentsByCourse().get( c ) );
  }

  @Test
  @DisplayName( "Satisfy coursemin" )
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
    Optimizer o = new Optimizer( a, rand );
    o.optimize();
    System.out.println( o.getAssignment().toString() );
    assertEquals( 1, o.getAssignment().getAssignmentsBySlot().get( s2 ).size(), "A course should have been assigned to the courseslot violating coursemin" );
    assertEquals( 1, o.getAssignment().getAssignmentsBySlot().get( s4 ).size(), "A lab should have been assigned to the labslot violating labmin" );
  }

  @Test
  @DisplayName( "Fix pair violations" )
  void testPair() {
    Instance i = new Instance();
    Slot s1 = new Slot( "MO", "8:00", 2, 0, false );
    Slot s2 = new Slot( "MO", "9:00", 2, 0, false );
    Course c1 = new Course( "CPSC", 100, 1 );
    Course c2 = new Course( "CPSC", 101, 1 );
    i.addCourseSlot(s1);
    i.addCourseSlot(s2);
    i.addCourse(c1);
    i.addCourse(c2);
    i.addPair( c1, c2 );
    Assignment a = new Assignment( i );
    a.add( c1, s1 );
    a.add( c2, s2 );
    Optimizer o = new Optimizer( a, rand );
    o.optimize();
    Assignment last = o.getAssignment();
    assertTrue( last.getAssignmentsByCourse().get( c1 ).equals( last.getAssignmentsByCourse().get( c2 )), "Pair violation should have been fixed by optimizer" );
  }

  @Test
  @DisplayName( "Fix section violations" )
  void testSection() {
    Instance i = new Instance();
    Slot s1 = new Slot( "MO", "8:00", 2, 0, false );
    Slot s2 = new Slot( "MO", "9:00", 2, 0, false );
    Course c1 = new Course( "CPSC", 100, 1 );
    Course c2 = new Course( "CPSC", 100, 2 );
    i.addCourseSlot(s1);
    i.addCourseSlot(s2);
    i.addCourse(c1);
    i.addCourse(c2);
    Assignment a = new Assignment( i );
    a.add( c1, s1 );
    a.add( c2, s1 );
    assertTrue( a.getAssignmentsByCourse().get( c1 ).equals( a.getAssignmentsByCourse().get( c2 )) );
    assertEquals( 10, a.eval() );

    Optimizer o = new Optimizer( a, rand );
    o.optimize();
    Assignment last = o.getAssignment();
    assertFalse( last.getAssignmentsByCourse().get( c1 ).equals( last.getAssignmentsByCourse().get( c2 )), "Section violation should have been fixed by optimizer" );
    assertEquals( 0, last.eval() );
  }

  @Test
  @DisplayName( "Fix preference violations" )
  void testPref() {
    Instance i = new Instance();
    Slot s1 = new Slot( "MO", "8:00", 2, 0, false );
    Slot s2 = new Slot( "MO", "9:00", 2, 0, false );
    Course c1 = new Course( "CPSC", 100, 1 );
    i.addCourseSlot(s1);
    i.addCourseSlot(s2);
    i.addCourse(c1);
    i.addPreference( c1, s2, 100 );
    Assignment a = new Assignment( i );
    a.add( c1, s1 );
    assertEquals( 1, a.getPrefViolations().size() );
    assertEquals( 1000, a.eval() );

    Optimizer o = new Optimizer( a, rand );
    o.optimize();
    Assignment last = o.getAssignment();
    assertEquals( s2, last.getAssignmentsByCourse().get( c1 ) );
    assertEquals( 0, last.eval() );
  }
}
