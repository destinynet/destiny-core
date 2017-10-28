/**
 * Created by smallufo on 2017-10-27.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.StarPositionIF;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.chinese.ChineseDateIF;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 取代 {@link EightWordsContext}
 */
public interface IEightWordsDetail {

  EightWordsContextModel getDetails(ChronoLocalDateTime lmt, Location location, String place , EightWordsIF eightWordsImpl, YearMonthIF yearMonthImpl, ChineseDateIF chineseDateImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi, RisingSignIF risingSignImpl, StarPositionIF starPositionImpl , SolarTermsIF solarTermsImpl);

}
