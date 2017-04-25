/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import destiny.astrology.StarPositionIF;
import destiny.astrology.StarTransitIF;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.SolarTerms;
import destiny.core.calendar.SolarTermsIF;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.chinese.ChineseDateIF;
import destiny.core.calendar.eightwords.*;
import destiny.core.calendar.eightwords.personal.FortuneDirectionDefaultImpl;
import destiny.core.calendar.eightwords.personal.FortuneDirectionIF;
import destiny.core.calendar.eightwords.personal.PersonContext;
import destiny.core.chinese.*;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class ZiweiImpl implements IZiwei, Serializable {

  /** 本命盤 */
  @Override
  public Builder getBirthPlate(StemBranch year, int monthNum, boolean leapMonth, Branch monthBranch, SolarTerms solarTerms, int days, Branch hour, @NotNull Collection<ZStar> stars, Gender gender, ZSettings settings) {

    // 最終要計算的「月份」數字
    int finalMonthNum;
    if (leapMonth) {
      // 閏月
      switch (settings.getLeapMonth()) {
        case LEAP_NEXT_MONTH: finalMonthNum = monthNum+1; break;
        case LEAP_SPLIT_15: {
          if (days > 15) {
            finalMonthNum = monthNum+1;
            break;
          }
        }
        default: finalMonthNum = monthNum;
      }
    } else {
      finalMonthNum = monthNum;
    }

    if (monthNum != finalMonthNum) {
      logger.warn("閏月設定為 : {} 造成原本月份為閏 {} 月，以月數 {} 計算之" , settings.getLeapMonth() , monthNum , finalMonthNum);
    }
    IMainHouse mainHouseImpl = getMainHouseImpl(settings.getMainHouse());
    StemBranch mainHouse = IZiwei.getMainHouse(year.getStem() , finalMonthNum , hour , solarTerms , mainHouseImpl);
    logger.debug("命宮在 : {}"  , mainHouse);
    StemBranch bodyHouse = IZiwei.getBodyHouse(year.getStem() , finalMonthNum , hour);

    // 取命主 : 命宮所在地支安星
    ZStar mainStar = IZiwei.getMainStar(mainHouse.getBranch());

    // 取身主 : 以出生年之地支安星
    ZStar bodyStar = IZiwei.getBodyStar(year.getBranch());

    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());

    Tuple3<String , FiveElement , Integer> t3 = IZiwei.getNaYin(mainHouse);
    int set = t3.v3();

    // 地支 -> 宮位 的 mapping
    Map<StemBranch , House> branchHouseMap =
      Arrays.stream(houseSeq.getHouses()).map(house -> {
        StemBranch sb = getHouse(year.getStem() , finalMonthNum, hour , house , houseSeq , solarTerms , mainHouseImpl);
        return Tuple.tuple(sb , house);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    // 地支 <-> 宮位 的 雙向 mapping
    BiMap<Branch , House> branchHouseBiMap = HashBiMap.create();
    branchHouseMap.forEach((sb , house) -> branchHouseBiMap.put(sb.getBranch() , house));

    // 寅 的天干
    Stem stemOf寅 = IZiwei.getStemOf寅(year.getStem());

    Map<ZStar , StemBranch> starBranchMap =
    stars.stream()
      .map(star -> Optional.ofNullable(HouseFunctions.map.get(star))
        .map(iHouse -> {
          Branch branch = iHouse.getBranch(year , monthBranch , finalMonthNum, solarTerms , days, hour, set, gender, settings);
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

    // 大限 mapping
    IBigRange bigRangeImpl = getBigRangeImpl(settings.getBigRange());

    Map<Branch , Tuple2<Double , Double>> bigRangeMap = Arrays.stream(Branch.values())
      .map(branch -> {
        Tuple2<Double , Double> t2 = bigRangeImpl.getRange(branchHouseBiMap.get(branch), set, year.getStem(), gender, settings.getRangeOutput(), houseSeq);
        return Tuple.tuple(branch , t2);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    // 小限 mapping
    Map<Branch, List<Double>> branchSmallRangesMap = Arrays.stream(Branch.values())
      .map(branch -> {
        List<Double> doubles = ISmallRange.getRanges(branch , year.getBranch() , gender)
          .stream()
          .map(Double::valueOf)
          .collect(Collectors.toList());
        return Tuple.tuple(branch , doubles);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    return new Builder(settings, chineseDate, gender, finalMonthNum, hour, mainHouse , bodyHouse , mainStar, bodyStar, t3.v2() , set , t3.v1(), branchHouseMap , starBranchMap, starStrengthMap, bigRangeMap , branchSmallRangesMap)
      .appendTrans4Map(trans4Map)
      ;
  } // 計算本命盤

  /** 最完整的計算命盤方式 */
  @Override
  public Builder getBirthPlate(LocalDateTime lmt, Location location, String place, @NotNull Collection<ZStar> stars, Gender gender, ZSettings settings, ChineseDateIF chineseDateImpl, StarTransitIF starTransitImpl, SolarTermsIF solarTermsImpl, YearMonthIF yearMonthImpl, DayIF dayImpl, HourIF hourImpl, MidnightIF midnightImpl, boolean changeDayAfterZi, RisingSignIF risingSignImpl, StarPositionIF starPositionImpl) {
    ChineseDate cDate = chineseDateImpl.getChineseDate(lmt , location , dayImpl , hourImpl , midnightImpl , changeDayAfterZi);
    StemBranch year = cDate.getYear();
    Branch monthBranch = yearMonthImpl.getMonth(lmt , location).getBranch();
    int monthNum = cDate.getMonth();
    SolarTerms solarTerms = solarTermsImpl.getSolarTerms(lmt , location);
    int days = cDate.getDay();
    Branch hour = hourImpl.getHour(lmt , location);

    /** 大運的順逆，內定採用『陽男陰女順排；陰男陽女逆排』的演算法 */
    FortuneDirectionIF fortuneDirectionImpl = new FortuneDirectionDefaultImpl();


    EightWordsIF eightWordsImpl = new EightWordsImpl(yearMonthImpl , dayImpl , hourImpl , midnightImpl , changeDayAfterZi);

    PersonContext context = new PersonContext(eightWordsImpl , chineseDateImpl, yearMonthImpl, dayImpl, hourImpl,
      midnightImpl, false, solarTermsImpl, starTransitImpl, lmt, location, place, gender,
      120.0, fortuneDirectionImpl, risingSignImpl, starPositionImpl, FortuneOutput.西元);

    return getBirthPlate(year , monthNum, cDate.isLeapMonth() , monthBranch , solarTerms , days , hour , stars , gender , settings)
      .withLocalDateTime(lmt)
      .withLocation(location)
      .withPlace(place)
      //.withEightWords(context.getEightWords())
      ;
  }

  /** 計算 大限盤 */
  @Override
  public Builder getFlowBig(Builder builder, ZSettings settings, StemBranch flowBig) {
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
      .withFlowBig(flowBig , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 計算 流年盤 */
  @Override
  public Builder getFlowYear(Builder builder, ZSettings settings, StemBranch flowBig, StemBranch flowYear) {
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
      .withFlowYear(flowYear , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 計算 流月盤 */
  @Override
  public Builder getFlowMonth(Builder builder, ZSettings settings, StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth) {
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
      .withFlowMonth(flowMonth , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 計算 流日盤 */
  @Override
  public Builder getFlowDay(Builder builder, ZSettings settings, StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum) {
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
      .withFlowDay(flowDay ,branchHouseMap)
      .appendTrans4Map(trans4Map);
  }

  /** 流時盤 */
  @Override
  public Builder getFlowHour(Builder builder, ZSettings settings, StemBranch flowBig, StemBranch flowYear, StemBranch flowMonth, StemBranch flowDay, int flowDayNum, StemBranch flowHour) {
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
      .withFlowHour(flowHour , branchHouseMap)
      .appendTrans4Map(trans4Map);
  }


  /**
   * @param stars     取得這些星體
   * @param flowType  在[本命、大限、流年]... (之一)
   * @param flowStem  天干為
   * @return 傳回四化 (若有的話)
   */
  private Map<Tuple2<ZStar , FlowType> , ITransFour.Value> getTrans4Map(Collection<ZStar> stars , FlowType flowType , Stem flowStem , ZSettings settings) {
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

  protected IFlowHour getFlowHourImpl(ZSettings.FlowHour flowHour) {
    switch (flowHour) {
      case FLOW_HOUR_DAY_DEP: return new FlowHourDayMainHouseDepImpl();
      case FLOW_HOUR_FIXED: return new FlowHourBranchImpl();
      default: throw new AssertionError("Error : " + flowHour);
    }
  }

  protected IFlowDay getFlowDayImpl(ZSettings.FlowDay flowDay) {
    switch (flowDay) {
      case FLOW_DAY_MONTH_DEP: return new FlowDayFlowMonthMainHouseDepImpl();
      case FLOW_DAY_FIXED: return new FlowDayBranchImpl();
      default: throw new AssertionError("Error : " + flowDay);
    }
  }

  protected IFlowMonth getFlowMonthImpl(ZSettings.FlowMonth flowMonth) {
    switch (flowMonth) {
      case FLOW_MONTH_DEFAULT: return new FlowMonthDefaultImpl();
      case FLOW_MONTH_FIXED:   return new FlowMonthFixedImpl();
      case FLOW_MONTH_YEAR_DEP: return new FlowMonthYearMainHouseDepImpl();
      default: throw new AssertionError("Error : " + flowMonth);
    }
  }

  protected IFlowYear getFlowYearImpl(ZSettings.FlowYear flowYear) {
    switch (flowYear) {
      case FLOW_YEAR_BRANCH: return new FlowYearBranchImpl();
      case FLOW_YEAR_ANCHOR: return new FlowYearAnchorImpl();
      default: throw new AssertionError("Error : " + flowYear);
    }
  }

  protected IHouseSeq getHouseSeq(ZSettings.HouseSeq houseSeq) {
    switch (houseSeq) {
      case HOUSE_DEFAULT: return new HouseSeqDefaultImpl();
      case HOUSE_TAIYI:   return new HouseSeqTaiyiImpl();
      case HOUSE_ASTRO:   return new HouseSeqAstroImpl();
      default: throw new AssertionError("Error : " + houseSeq);
    }
  }

  protected ITransFour getTranFourImpl(ZSettings.TransFour transFour) {
    switch (transFour) {
      case TRANSFOUR_FULL_COLLECT: return new TransFourFullCollectImpl();
      case TRANSFOUR_NORTH: return new TransFourNorthImpl();
      case TRANSFOUR_FULL_BOOK: return new TransFourFullBookImpl();
      case TRANSFOUR_MIDDLE: return new TransFourMiddleImpl();
      case TRANSFOUR_DIVINE: return new TransFourDivineImpl();
      case TRANSFOUR_ZIYUN: return new TransFourZiyunImpl();
      default: throw new AssertionError("Error : " + transFour);
    }
  }

  protected IMainHouse getMainHouseImpl(ZSettings.MainHouse mainHouse) {
    switch (mainHouse) {
      case MAIN_HOUSE_DEFAULT: return new MainHouseDefaultImpl();
      case MAIN_HOUSE_SOLAR: return new MainHouseSolarTermsImpl();
      default: throw new AssertionError("Error : " + mainHouse);
    }
  }

  protected IStrength getStrengthImpl(ZSettings.Strength strength) {
    switch (strength) {
      case STRENGTH_MIDDLE: return new StrengthMiddleImpl();
      case STRENGTH_NORTH: return new StrengthNorthImpl();
      default: throw new AssertionError("Error : " + strength);
    }
  }

  protected IBigRange getBigRangeImpl(ZSettings.BigRange bigRange) {
    switch (bigRange) {
      case BIG_RANGE_FROM_MAIN: return new BigRangeFromMain();
      case BIG_RANGE_SKIP_MAIN: return new BigRangeSkipMain();
      default: throw new AssertionError("Error : " + bigRange);
    }
  }

}
