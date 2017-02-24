/**
 * Created by smallufo on 2015-06-21.
 */
package destiny.core.calendar.eightwords;

import destiny.astrology.StarTransitIF;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.Time;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.personal.FortuneDirectionIF;
import destiny.core.calendar.eightwords.personal.PersonContext;

import java.io.Serializable;

/**
 * 方便快速產生 PersonContext 的 工具
 */
public class PersonContextDefaultImpl extends EightWordsImpl implements PersonContextIF, Serializable {

  private final RisingSignIF risingSignImpl;

  private final ChineseDateIF chineseDateImpl;

  private final SolarTermsIF solarTermsImpl;

  private final StarTransitIF starTransitImpl;

  private final FortuneDirectionIF fortuneDirectionImpl;


  public PersonContextDefaultImpl(YearMonthIF yearMonthImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi, RisingSignIF risingSignImpl, ChineseDateIF chineseDateImpl, SolarTermsIF solarTermsImpl, StarTransitIF starTransitImpl, FortuneDirectionIF fortuneDirectionImpl) {
    super(yearMonthImpl, dayImpl, hourImpl, midnightImpl, changeDayAfterZi);
    this.risingSignImpl = risingSignImpl;
    this.chineseDateImpl = chineseDateImpl;
    this.solarTermsImpl = solarTermsImpl;
    this.starTransitImpl = starTransitImpl;
    this.fortuneDirectionImpl = fortuneDirectionImpl;
  }

  @Override
  public PersonContext getPersonContext(Time lmt, Location location, Gender gender) {
    return new PersonContext(chineseDateImpl , yearMonthImpl , dayImpl , hourImpl , midnightImpl ,
      changeDayAfterZi , solarTermsImpl , starTransitImpl , lmt.toLocalDateTime() , location , gender , 120.0 , fortuneDirectionImpl , risingSignImpl);
  }

}
