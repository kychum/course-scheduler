package common;
import java.time.LocalTime;
import java.util.Objects;

/**
 * Representation of a time slot used to schedule courses. This class handles
 * all the minute details about valid time and day combinations, and provides
 * an interface to check if two time slots take up some common times.
 *
 * Does not differentiate between a course slot or a lab slot.
 *
 * @author Kevin
 * @version 1.00
 */
public class Slot {
  private static final long durationMWF = 60;
  private static final long durationTR  = 90;
  private static final long durationF   = 120;
  private static final LocalTime eveningTime = LocalTime.of( 18, 00 );
  private LocalTime startTime;
  private LocalTime endTime;
  private int minAssign;
  private int maxAssign;

  public enum Day{
    MO ( "Mo" ),
    TU ( "Tu" ),
    FR ( "Fr" );

    private final String dayName;
    private Day( String dayName ) {
      this.dayName = dayName;
    }

    public static Day of( String day ) {
      if( day.matches( "(?i)(MO)(NDAY)?" ) ) {
        return MO;
      }
      else if( day.matches( "(?i)(TU)(ESDAY)?" ) ) {
        return TU;
      }
      else {
        //TODO: sanity and error checking
        return FR;
      }
    }

    public String toString() {
      return dayName;
    }
  }
  private Day day;

  /**
   * Constructs a new slot with the specified days and time.
   * The end time is automatically calculated from the given day according to
   * the assignment specifications.
   *
   * @param day the days of the time slot; can be Day.MO, Day.TU. or Day.FR
   * @param time the starting time of the slot
   * @param maxAssign the maximum number of allowed Assignables assigned to this slot
   * @param minAssign the minimum required number of Assignables assigned to this slot
   */
  public Slot( Day day, LocalTime time, int maxAssign, int minAssign ) {
    // TODO: verify that the given day/time combination is valid. e.g., reject TU 11:00
    this.day = day;
    this.startTime = time;
    if( day == Day.TU ) {
      this.endTime = startTime.plusMinutes( durationTR );
    }
    else if( day == Day.FR ){
      this.endTime = startTime.plusMinutes( durationF );
    }
    else {
      this.endTime = startTime.plusMinutes( durationMWF );
    }

    this.maxAssign = maxAssign;
    this.minAssign = minAssign;
  }

  /**
   * Constructs a slot object from string parameters.
   *
   * @param day string representation of the day
   * @param time string representation of the time, e.g. "10:00"
   * @param hour the starting hour of the time slot
   * @param minute the starting minutes of the time slot
   */
  public Slot( String day, String time, int maxAssign, int minAssign ) {
    this( Day.of( day ), LocalTime.parse( time ), maxAssign, minAssign );
  }

  /**
   * Determines if two slots overlap in time by checking if the days coincide
   * as well as the time range.
   *
   * @param other the other slot to test against
   * @return True if the slots overlap, false otherwise
   */
  public boolean overlaps( Slot other ) {
    if( (this.day == Day.MO || this.day == Day.FR) &&
        (other.day == Day.MO || other.day == Day.FR) ) {
      return timeOverlaps( other );
    }
    else if( this.day == other.day ) {
      return timeOverlaps( other );
    }
    return false;
  }

  /**
   * Determines if two times overlap. Does not check the day.
   *
   * @param other The other slot to test against
   * @return True if the times of the slots overlap, false otherwise
   */
  private boolean timeOverlaps( Slot other ) {
      if( this.startTime.compareTo( other.startTime ) <= 0 ) {
        return this.endTime.compareTo( other.endTime ) > 0;
      }
      else {
        return this.startTime.compareTo( other.endTime ) < 0;
      }
  }

  /**
   * Determines if the slot is classified as an evening time slot.
   * Evening slots are any slots that start at 18:00 or later.
   *
   * @return True if the slot is an evening slot, false otherwise.
   */
  public boolean isEveningSlot() {
    return this.startTime.compareTo( eveningTime ) >= 0;
  }

  /**
   * Determines if this slot is equivalent to a different one.
   * Slots are equivalent if they occur on the same day and time ranges.
   * The min/max assign may differ depending on if the slot is for labs or
   * courses
   *
   * @param other The other slot to test against
   * @return True if the two slots are equivalent, false otherwise.
   */
  public boolean equivalent( Slot other ) {
    return ( this.day == other.day ) && ( this.startTime.equals( other.startTime ) );
  }

  public int getMaxAssign() {
    return maxAssign;
  }

  public int getMinAssign() {
    return minAssign;
  }

  /**
   * Transforms the slot into a string.
   *
   * @return The string representation of a slot, for example, "FR, 10:00"
   */
  public String toString() {
    return String.format("%s, %s", day.toString(), startTime.toString());
  }

  public int hashCode() {
    return Objects.hash(day, startTime, endTime, maxAssign, minAssign);
  }
}

