package common;
import java.time.LocalTime;

/**
 * Representation of a time slot used to schedule courses. This class handles
 * all the minute details about valid time and day combinations, and provides
 * an interface to check if two time slots take up some common times.
 *
 * @author Kevin
 * @version 1.00
 */
public class Slot {
  private final long durationMWF = 60;
  private final long durationTR  = 90;
  private final long durationF   = 120;
  private LocalTime startTime;
  private LocalTime endTime;

  public enum Day{
    MO ( "Mo" ),
    TU ( "Tu" ),
    FR ( "Fr" );

    private final String dayName;
    private Day( String dayName ) {
      this.dayName = dayName;
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
   * @param hour the starting hour of the time slot
   * @param minute the starting minutes of the time slot
   */
  public Slot( Day day, int hour, int minute ) {
    this.day = day;
    this.startTime = LocalTime.of( hour, minute );
    if( day == Day.TU ) {
      this.endTime = startTime.plusMinutes( durationTR );
    }
    else if( day == Day.FR ){
      this.endTime = startTime.plusMinutes( durationF );
    }
    else {
      this.endTime = startTime.plusMinutes( durationMWF );
    }
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
   * Transforms the slot into a string.
   *
   * @return The string representation of a slot, for example, "FR, 10:00"
   */
  public String toString() {
    return String.format("%s, %s", day.toString(), startTime.toString());
  }
}
