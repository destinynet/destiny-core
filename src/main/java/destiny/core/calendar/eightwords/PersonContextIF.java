/**
 * Created by smallufo on 2015-06-21.
 */
package destiny.core.calendar.eightwords;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.Time;
import destiny.core.calendar.eightwords.personal.PersonContext;

public interface PersonContextIF extends EightWordsIF {

  PersonContext getPersonContext(Time lmt , Location location , Gender gender);
}
