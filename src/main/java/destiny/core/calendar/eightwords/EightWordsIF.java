/**
 * @author smallufo
 * Created on 2011/5/24 at 上午12:13:25
 */
package destiny.core.calendar.eightwords;

import destiny.core.calendar.Location;
import org.jetbrains.annotations.NotNull;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 計算八字的介面
 */
public interface EightWordsIF {

  @NotNull
  EightWords getEightWords(ChronoLocalDateTime lmt, Location location);

}
