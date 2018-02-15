/**
 * Created by smallufo on 2017-10-27.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.IStarPosition;
import destiny.core.calendar.Location;
import destiny.core.calendar.ISolarTerms;
import destiny.core.calendar.chinese.IChineseDate;

import java.time.chrono.ChronoLocalDateTime;

/**
 * 取代 {@link EightWordsContext}
 */
public interface IEightWordsDetail {

  EightWordsContextModel getDetails(ChronoLocalDateTime lmt, Location location, String place , IEightWords eightWordsImpl, IYearMonth yearMonthImpl, IChineseDate chineseDateImpl, IDay dayImpl, IHour hourImpl, IMidnight midnightImpl, boolean changeDayAfterZi, IRisingSign risingSignImpl, IStarPosition starPositionImpl , ISolarTerms solarTermsImpl);

}
