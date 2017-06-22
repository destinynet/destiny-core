/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.calendar.eightwords.YearMonthIF;
import destiny.core.chinese.Branch;
import destiny.core.chinese.StemBranch;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 傳統紫微計算命宮
 */
public class MainHouseTradImpl implements IMainHouse , Serializable {

  private final ZContextMore context;
  private final YearMonthIF yearMonthImpl;
  private final DayIF dayImpl;
  private final SolarTermsIF solarTermsImpl;

  public MainHouseTradImpl(ZContextMore context, YearMonthIF yearMonthImpl, DayIF dayImpl, SolarTermsIF solarTermsImpl) {
    this.context = context;
    this.yearMonthImpl = yearMonthImpl;
    this.dayImpl = dayImpl;
    this.solarTermsImpl = solarTermsImpl;
  }

  @Override
  public Branch getMainHouse(LocalDateTime lmt, Location loc) {
    ChineseDateIF chineseDateImpl = context.getChineseDateImpl();
    HourIF hourImpl = context.getHourImpl();
    MidnightIF midnightImpl = context.getMidnightImpl();

    ChineseDate cDate = chineseDateImpl.getChineseDate(lmt , loc , dayImpl , hourImpl , midnightImpl , context.isChangeDayAfterZi());
    StemBranch lunarYear = cDate.getYear();

    Branch monthBranch = yearMonthImpl.getMonth(lmt , loc).getBranch();
    StemBranch solarYear = yearMonthImpl.getYear(lmt , loc);

    int lunarMonth = cDate.getMonth();
    int days = cDate.getDay();

    Branch hour = hourImpl.getHour(lmt , loc);

    // 最終要計算的「月份」數字 , for 主星
    final int finalMonthNumForMainStars = IZiwei.getFinalMonthNumber(lunarMonth, cDate.isLeapMonth() , monthBranch , days , context.getMainStarsAlgo());

    // 命宮所參考的「年干」，同時依據「年系星」的類型來決定
    StemBranch year = context.getYearType() == ZContext.YearType.YEAR_LUNAR ? lunarYear : solarYear;
    StemBranch mainHouse = IZiwei.getMainHouse(year.getStem() , finalMonthNumForMainStars , hour);
    return mainHouse.getBranch();
  }
}
