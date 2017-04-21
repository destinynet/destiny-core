/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.DayIF;
import destiny.core.calendar.eightwords.HourIF;
import destiny.core.calendar.eightwords.MidnightIF;
import destiny.core.calendar.eightwords.MonthIF;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public class ZiweiImpl implements IZiwei, Serializable {

  /** 本命盤 */
  @Override
  public Plate.Builder getBirthPlate(StemBranch year, int monthNum, boolean leapMonth, Branch monthBranch, SolarTerms solarTerms, int days, Branch hour, @NotNull Collection<ZStar> stars, Gender gender, Settings settings) {
    IMainHouse mainHouseImpl = getMainHouseImpl(settings.getMainHouse());
    StemBranch mainHouse = IZiwei.getMainHouse(year.getStem() , monthNum , hour , solarTerms , mainHouseImpl);
    StemBranch bodyHouse = IZiwei.getBodyHouse(year.getStem() , monthNum , hour);

    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());

    Tuple3<String , FiveElement , Integer> t3 = IZiwei.getNaYin(mainHouse);
    int set = t3.v3();

    // 地支 -> 宮位 的 mapping
    Map<StemBranch , House> branchHouseMap =
      Arrays.stream(houseSeq.getHouses()).map(house -> {
        StemBranch sb = getHouse(year.getStem() , monthNum, hour , house , houseSeq , solarTerms , mainHouseImpl);
        return Tuple.tuple(sb , house);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    // 寅 的天干
    Stem stemOf寅 = IZiwei.getStemOf寅(year.getStem());

    Map<ZStar , StemBranch> starBranchMap =
    stars.stream()
      .map(star -> Optional.ofNullable(HouseFunctions.map.get(star))
        .map(iHouse -> {
          Branch branch = iHouse.getBranch(year , monthBranch , monthNum, solarTerms , days, hour, set, gender, settings);
          StemBranch sb = IZiwei.getStemBranchOf(branch , stemOf寅);
          return Tuple.tuple(star , sb);
        })
      )
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2))
      ;

    // 本命四化
    Map<Tuple2<ZStar , FlowType> , ITransFour.Value> trans4Map = getTrans4Map(stars , FlowType.本命 , year.getStem() , settings);

    // 星體強弱表
    IStrength strengthImpl = getStrengthImpl(settings.getStrength());

    Map<ZStar , Integer> starStrengthMap = stars.stream()
      .map(star -> Tuple.tuple(star, strengthImpl.getStrengthOf(star, starBranchMap.get(star).getBranch())))
      .filter(t -> t.v2().isPresent())
      .collect(Collectors.toMap(Tuple2::v1, t2 -> t2.v2().orElse(0))); // 這裡其實不會傳 0 , 因為前面已經 filter 過了

    ChineseDate chineseDate = new ChineseDate(null , year , monthNum , leapMonth , days);

    return new Plate.Builder(chineseDate, gender, monthNum, hour, mainHouse , bodyHouse , t3.v2() , set , branchHouseMap , starBranchMap, starStrengthMap)
      .appendTrans4Map(trans4Map)
      ;
  } // 計算本命盤

  /** 最完整的計算命盤方式 */
  @Override
  public Plate.Builder getBirthPlate(LocalDateTime lmt, Location location, String place, @NotNull Collection<ZStar> stars, Gender gender, Settings settings, ChineseDateIF chineseDateImpl, SolarTermsIF solarTermsImpl, MonthIF monthImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi) {
    ChineseDate cDate = chineseDateImpl.getChineseDate(lmt , location , dayImpl , hourImpl , midnightImpl , changeDayAfterZi);
    StemBranch year = cDate.getYear();
    Branch monthBranch = monthImpl.getMonth(lmt , location).getBranch();
    int monthNum = cDate.getMonth();
    SolarTerms solarTerms = solarTermsImpl.getSolarTerms(lmt , location);
    int days = cDate.getDay();
    Branch hour = hourImpl.getHour(lmt , location);
    if (cDate.isLeapMonth()) {
      // 閏月
      switch (settings.getLeapMonth()) {
        case NEXT_MONTH: monthNum = monthNum+1; break;
        case SPLIT_15: {
          if (days > 15) {
            monthNum = monthNum+1;
            break;
          }
        }
      }
    }
    return getBirthPlate(year , monthNum, cDate.isLeapMonth() , monthBranch , solarTerms , days , hour , stars , gender , settings)
      .withLocalDateTime(lmt)
      .withLocation(location)
      .withPlace(place)
      ;
  }

  /** 計算 大限盤 */
  @Override
  public Plate.Builder getFlowBig(Plate.Builder builder, Settings settings, StemBranch flowBig) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());

    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map( branch -> {
        int steps = branch.getAheadOf(flowBig.getBranch());
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    // 大限四化
    Map<Tuple2<ZStar , FlowType> , ITransFour.Value> trans4Map = getTrans4Map(builder.getStars() , FlowType.大限 , flowBig.getStem() , settings);
    return builder
      .withFlowBig(flowBig.getBranch() , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 計算 流年盤 */
  @Override
  public Plate.Builder getFlowYear(Plate.Builder builder, Settings settings, StemBranch flowBig, StemBranch flowYear) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());
    IFlowYear flowYearImpl = getFlowYearImpl(settings.getFlowYear());

    Branch 流年命宮 = flowYearImpl.getFlowYear(flowYear.getBranch() , builder.getBirthMonthNum() , builder.getBirthHour());
    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map(branch -> {
        int steps = branch.getAheadOf(流年命宮);
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    // 流年四化
    Map<Tuple2<ZStar , FlowType> , ITransFour.Value> trans4Map = getTrans4Map(builder.getStars() , FlowType.流年 , flowYear.getStem() , settings);

    return getFlowBig(builder , settings , flowBig)
      .withFlowYear(flowYear.getBranch() , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 計算 流月盤 */
  @Override
  public Plate.Builder getFlowMonth(Plate.Builder builder, Settings settings, StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());
    IFlowMonth flowMonthImpl = getFlowMonthImpl(settings.getFlowMonth());

    Branch 流月命宮 = flowMonthImpl.getFlowMonth(flowYear.getBranch() , flowMonth.getBranch() , builder.getBirthMonthNum() , builder.getBirthHour());
    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map(branch -> {
        int steps = branch.getAheadOf(流月命宮);
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1 , Tuple2::v2));

    // 流月四化
    Map<Tuple2<ZStar , FlowType> , ITransFour.Value> trans4Map = getTrans4Map(builder.getStars() , FlowType.流月 , flowMonth.getStem() , settings);

    return getFlowYear(builder , settings , flowBig , flowYear)
      .withFlowMonth(flowMonth.getBranch() , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 計算 流日盤 */
  @Override
  public Plate.Builder getFlowDay(Plate.Builder builder, Settings settings, StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());
    IFlowMonth flowMonthImpl = getFlowMonthImpl(settings.getFlowMonth());
    Branch 流月命宮 = flowMonthImpl.getFlowMonth(flowYear.getBranch() , flowMonth.getBranch() , builder.getBirthMonthNum() , builder.getBirthHour());
    IFlowDay flowDayImpl = getFlowDayImpl(settings.getFlowDay());

    Branch 流日命宮 = flowDayImpl.getFlowDay(flowDay.getBranch() , flowDayNum , 流月命宮);
    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map(branch -> {
        int steps = branch.getAheadOf(流日命宮);
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1 , Tuple2::v2));

    // 流日四化
    Map<Tuple2<ZStar , FlowType> , ITransFour.Value> trans4Map = getTrans4Map(builder.getStars() , FlowType.流日 , flowDay.getStem() , settings);
    return getFlowMonth(builder , settings , flowBig , flowYear , flowMonth)
      .withFlowDay(flowDay.getBranch() ,branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 流時盤 */
  @Override
  public Plate.Builder getFlowHour(Plate.Builder builder, Settings settings, StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum, StemBranch flowHour) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());
    IFlowMonth flowMonthImpl = getFlowMonthImpl(settings.getFlowMonth());
    Branch 流月命宮 = flowMonthImpl.getFlowMonth(flowYear.getBranch() , flowMonth.getBranch() , builder.getBirthMonthNum() , builder.getBirthHour());
    IFlowDay flowDayImpl = getFlowDayImpl(settings.getFlowDay());
    Branch 流日命宮 = flowDayImpl.getFlowDay(flowDay.getBranch() , flowDayNum , 流月命宮);

    IFlowHour flowHourImpl = getFlowHourImpl(settings.getFlowHour());
    Branch 流時命宮 = flowHourImpl.getFlowHour(flowHour.getBranch() , 流日命宮);

    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map(branch -> {
        int steps = branch.getAheadOf(流時命宮);
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1 , Tuple2::v2));

    // 流時四化
    Map<Tuple2<ZStar , FlowType> , ITransFour.Value> trans4Map = getTrans4Map(builder.getStars() , FlowType.流時 , flowHour.getStem() , settings);

    return getFlowDay(builder , settings , flowBig , flowYear , flowMonth , flowDay , flowDayNum)
      .withFlowHour(flowHour.getBranch() , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }


  /**
   * @param stars     取得這些星體
   * @param flowType  在[本命、大限、流年]... (之一)
   * @param flowStem  天干為
   * @return 傳回四化 (若有的話)
   */
  private Map<Tuple2<ZStar , FlowType> , ITransFour.Value> getTrans4Map(Collection<ZStar> stars , FlowType flowType , Stem flowStem , Settings settings) {
    ITransFour transFourImpl = getTranFourImpl(settings.getTransFour());
    return stars.stream()
      .map(star -> {
        Tuple2<ZStar , FlowType> key = Tuple.tuple(star , flowType);
        return Tuple.tuple(key , transFourImpl.getValueOf(star , flowStem));
      })
      .filter(t -> t.v2().isPresent())
      .collect(
        Collectors.toMap(
          Tuple2::v1
          ,t -> t.v2().orElse(null)   // 其實這裡不會 null , 因為之前已經 filter 過了
          //,(v1 , v2) -> v1
          //, TreeMap::new
        )
      );
  }

  private IFlowHour getFlowHourImpl(Settings.FlowHour flowHour) {
    switch (flowHour) {
      case DAY_DEP: return new FlowHourDayMainHouseDepImpl();
      case FIXED  : return new FlowHourBranchImpl();
      default: throw new AssertionError("Error : " + flowHour);
    }
  }

  private IFlowDay getFlowDayImpl(Settings.FlowDay flowDay) {
    switch (flowDay) {
      case MONTH_DEP: return new FlowDayFlowMonthMainHouseDepImpl();
      case FIXED: return new FlowDayBranchImpl();
      default: throw new AssertionError("Error : " + flowDay);
    }
  }

  private IFlowMonth getFlowMonthImpl(Settings.FlowMonth flowMonth) {
    switch (flowMonth) {
      case DEFAULT: return new FlowMonthDefaultImpl();
      case FIXED:   return new FlowMonthFixedImpl();
      case YEAR_DEP: return new FlowMonthYearMainHouseDepImpl();
      default: throw new AssertionError("Error : " + flowMonth);
    }
  }

  private IFlowYear getFlowYearImpl(Settings.FlowYear flowYear) {
    switch (flowYear) {
      case BRANCH: return new FlowYearBranchImpl();
      case ANCHOR:  return new FlowYearAnchorImpl();
      default: throw new AssertionError("Error : " + flowYear);
    }
  }

  private IHouseSeq getHouseSeq(Settings.HouseSeq houseSeq) {
    switch (houseSeq) {
      case DEFAULT: return new HouseSeqDefaultImpl();
      case TAIYI:   return new HouseSeqTaiyiImpl();
      default: throw new AssertionError("Error : " + houseSeq);
    }
  }

  private ITransFour getTranFourImpl(Settings.TransFour transFour) {
    switch (transFour) {
      case DEFAULT: return new TransFourDefaultImpl();
      case NORTH: return new TransFourNorthImpl();
      case SOUTH: return new TransFourSouthImpl();
      case MIDDLE: return new TransFourMiddleImpl();
      case DIVINE: return new TransFourDivineImpl();
      case ZIYUN: return new TransFourZiyunImpl();
      default: throw new AssertionError("Error : " + transFour);
    }
  }

  private IMainHouse getMainHouseImpl(Settings.MainHouse mainHouse) {
    switch (mainHouse) {
      case DEFAULT: return new MainHouseDefaultImpl();
      case SOLAR_TERMS: return new MainHouseSolarTermsImpl();
      default: throw new AssertionError("Error : " + mainHouse);
    }
  }

  private IStrength getStrengthImpl(Settings.Strength strength) {
    switch (strength) {
      case MIDDLE: return new StrengthMiddleImpl();
      default: throw new AssertionError("Error : " + strength);
    }
  }

}
