/**
 * Created by smallufo on 2017-06-22.
 */
package destiny.core.chinese.ziwei;

import destiny.core.calendar.Location;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.chinese.IFinalMonthNumber;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.calendar.eightwords.YearMonthIF;
import destiny.core.chinese.Branch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;

import java.io.Serializable;
import java.time.LocalDateTime;

import static destiny.core.chinese.ziwei.ZContext.YearType;

/**
 * 傳統紫微計算命宮
 *
 * 命宮計算依據，是「年干」以及「時支」
 * 而「年干」要分「陰曆」或是「節氣」
 * 因此必須傳入一大堆參數，才能計算出「陰曆」或是「節氣」的「年」
 * 再由 {@link YearType} 來決定要挑哪一個
 */
public class MainBodyHouseTradImpl implements IMainBodyHouse, Serializable {

  private final YearMonthIF yearMonthImpl;
  private final DayIF dayImpl;

  private final ChineseDateIF chineseDateImpl;
  private final HourIF hourImpl;
  private final MidnightIF midnightImpl;
  private final boolean changeDayAfterZi;
  private final IFinalMonthNumber.MonthAlgo mainStarsAlgo;

  public MainBodyHouseTradImpl(YearMonthIF yearMonthImpl, DayIF dayImpl, ChineseDateIF chineseDateImpl, HourIF hourImpl,
                               MidnightIF midnightImpl, boolean changeDayAfterZi, IFinalMonthNumber.MonthAlgo mainStarsAlgo) {
    this.hourImpl = hourImpl;
    this.midnightImpl = midnightImpl;
    this.changeDayAfterZi = changeDayAfterZi;
    this.chineseDateImpl = chineseDateImpl;
    this.yearMonthImpl = yearMonthImpl;
    this.dayImpl = dayImpl;
    this.mainStarsAlgo = mainStarsAlgo;
  }

  @Override
  public Tuple2<Branch , Branch> getMainBodyHouse(LocalDateTime lmt, Location loc) {

    ChineseDate cDate = chineseDateImpl.getChineseDate(lmt , loc , dayImpl , hourImpl , midnightImpl , changeDayAfterZi);

    Branch monthBranch = yearMonthImpl.getMonth(lmt , loc).getBranch();

    int lunarMonth = cDate.getMonth();
    int days = cDate.getDay();

    Branch hour = hourImpl.getHour(lmt , loc);

    // 最終要計算的「月份」數字 , for 主星
    final int finalMonthNumForMainStars = IFinalMonthNumber.getFinalMonthNumber(lunarMonth, cDate.isLeapMonth() , monthBranch , days , mainStarsAlgo);

    Branch mainHouse = IZiwei.getMainHouseBranch(finalMonthNumForMainStars , hour);
    Branch bodyHouse = IZiwei.getBodyHouseBranch(finalMonthNumForMainStars , hour);

    return Tuple.tuple(mainHouse , bodyHouse);
  }
}
