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

import static destiny.core.chinese.ziwei.ZContext.*;

/**
 * 傳統紫微計算命宮
 *
 * 命宮計算依據，是「年干」以及「時支」
 * 而「年干」要分「陰曆」或是「節氣」
 * 因此必須傳入一大堆參數，才能計算出「陰曆」或是「節氣」的「年」
 * 再由 {@link YearType} 來決定要挑哪一個
 */
public class MainBodyHouseTradImpl implements IMainBodyHouse, Serializable {

  private final ZContextMore context;
  private final YearMonthIF yearMonthImpl;
  private final DayIF dayImpl;
  private final SolarTermsIF solarTermsImpl;

  public MainBodyHouseTradImpl(ZContextMore context, YearMonthIF yearMonthImpl, DayIF dayImpl, SolarTermsIF solarTermsImpl) {
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
    StemBranch year = context.getYearType() == YearType.YEAR_LUNAR ? lunarYear : solarYear;
    StemBranch mainHouse = IZiwei.getMainHouse(year.getStem() , finalMonthNumForMainStars , hour);
    return mainHouse.getBranch();
  }
}
