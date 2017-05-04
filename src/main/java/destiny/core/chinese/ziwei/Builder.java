/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.Table;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.eightwords.EightWords;
import destiny.core.calendar.eightwords.personal.PersonContextModel;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Builder implements Serializable {

  /** 設定資料 */
  private final ZContext context;

  /** 陰曆生日 */
  private final ChineseDate chineseDate;

  /** 陽曆出生日期 */
  @Nullable
  private LocalDateTime localDateTime = null;

  /** 出生地點 */
  @Nullable
  private Location location = null;

  /** 地點名稱 */
  @Nullable
  private String place = null;

  /** 性別 */
  private final Gender gender;

  /** 出生月份 */
  private final int birthMonthNum;

  /** 出生時辰 */
  private final Branch birthHour;

  /** 命宮 */
  private final StemBranch mainHouse;

  /** 身宮 */
  private final StemBranch bodyHouse;

  /** 命主 */
  private final ZStar mainStar;

  /** 身主 */
  private final ZStar bodyStar;

  /** 五行 */
  private final FiveElement fiveElement;

  /** 五行第幾局 */
  private final int set;

  /** 納音 */
  private final String naYin;

  /** 正確的八字（節氣推算）*/
  private EightWords eightWords = null;

  private final Set<HouseData> houseDataSet;

  /**
   * 四化星 的列表
   * 存放著「這顆星」在 [本命、大限、流年、...] 的四化 結果為何
   */
  private Map<ZStar, Map<FlowType, ITransFour.Value>> transFourMap = new HashMap<>();

  /**
   * 每個地支，在每種流運，稱為什麼宮位
   */
  private Map<Branch , Map<FlowType , House>> branchFlowHouseMap = new TreeMap<>();

  /** 星體強弱表 */
  private final Map<ZStar , Integer> starStrengthMap;

  /** 計算流運資料 */
  private Map<FlowType , StemBranch> flowBranchMap = new TreeMap<>();

  /** 八字命盤 */
  private PersonContextModel personModel;

  /** 宮干四化 */
  private final Map<StemBranch , Table<ITransFour.Value, ZStar, Branch>> flyMap;

  private Logger logger = LoggerFactory.getLogger(getClass());

  /** 本命盤 */
  public Builder(ZContext context, ChineseDate chineseDate, Gender gender, int birthMonthNum, Branch birthHour,
                 StemBranch mainHouse, StemBranch bodyHouse, ZStar mainStar, ZStar bodyStar,
                 FiveElement fiveElement, int set, String naYin,
                 Map<StemBranch, House> branchHouseMap,
                 Map<ZStar, StemBranch> starBranchMap,
                 Map<ZStar, Integer> starStrengthMap,
                 Map<Branch, Tuple2<Double, Double>> bigRangeMap, Map<Branch, List<Double>> branchSmallRangesMap,
                 Map<StemBranch, Table<ITransFour.Value, ZStar, Branch>> flyMap) {
    this.context = context;
    this.chineseDate = chineseDate;
    this.gender = gender;
    this.birthMonthNum = birthMonthNum;
    this.birthHour = birthHour;
    this.mainHouse = mainHouse;
    this.bodyHouse = bodyHouse;
    this.mainStar = mainStar;
    this.bodyStar = bodyStar;
    this.fiveElement = fiveElement;
    this.set = set;
    this.naYin = naYin;
    this.starStrengthMap = starStrengthMap;
    this.flyMap = flyMap;

    // 哪個地支 裡面 有哪些星體
    Map<Branch , Set<ZStar>> branchStarMap = starBranchMap.entrySet().stream()
      .collect(
        Collectors.groupingBy(
          entry -> entry.getValue().getBranch(),
          TreeMap::new,   // 保留地支順序
          Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
        )
      );
    logger.debug("branchStarMap = {}" , branchStarMap);

    // TODO 可能有些 地支宮位裡面沒有星 , 因此建立出來的 Map 就無該 地支的 key 值 , 因此必須建立另一個 map , 確保裡面每個地支都存在，且 value 至少為 empty set

    Map<Branch , Set<ZStar>> branchStarMap2 = Arrays.stream(Branch.values()).map(branch -> {
      Set<ZStar> stars = branchStarMap.getOrDefault(branch , new HashSet<>());
      return Tuple.tuple(branch , stars);
    }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    Map<Branch , Map<FlowType , House>> 本命地支HouseMapping =
      branchHouseMap.entrySet().stream().map(e -> {
        Map<FlowType , House> m = new HashMap<>();
        m.put(FlowType.本命 , branchHouseMap.get(e.getKey()));
        return Tuple.tuple(e.getKey().getBranch() , m);
      })
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
    branchFlowHouseMap.putAll(本命地支HouseMapping);



    houseDataSet = branchHouseMap.entrySet().stream().map(e -> {
      StemBranch sb = e.getKey();
      House house = e.getValue();
      Set<ZStar> stars = branchStarMap2.get(sb.getBranch());


      Tuple2<Double , Double> fromTo = bigRangeMap.get(sb.getBranch());
      List<Double> smallRanges = branchSmallRangesMap.get(sb.getBranch());
      return new HouseData(house, sb
        , stars
        , branchFlowHouseMap.get(sb.getBranch())
        , flyMap.get(sb)
        , context.getFortuneOutput(), fromTo.v1() , fromTo.v2(), smallRanges);
    }).collect(Collectors.toSet());
  } // builder init

  public ChineseDate getChineseDate() {
    return chineseDate;
  }

  public Set<ZStar> getStars() {
    return starStrengthMap.keySet();
  }

  public int getBirthMonthNum() {
    return birthMonthNum;
  }

  public Branch getBirthHour() {
    return birthHour;
  }

  public Builder withLocalDateTime(LocalDateTime localDateTime) {
    this.localDateTime = localDateTime;
    return this;
  }

  public Builder withLocation(Location location) {
    this.location = location;
    return this;
  }

  public Builder withPlace(String place) {
    this.place = place;
    return this;
  }

  /** 添加 四化 */
  public Builder appendTrans4Map(Map<Tuple2<ZStar , FlowType> , ITransFour.Value> map) {
    map.forEach((tuple , value) -> {
      ZStar star = tuple.v1();
      FlowType flowType = tuple.v2();

      this.transFourMap.computeIfPresent(star, (star1, flowTypeValueMap) -> {
        flowTypeValueMap.putIfAbsent(flowType , value);
        return flowTypeValueMap;
      });
      this.transFourMap.putIfAbsent(star , new TreeMap<FlowType , ITransFour.Value>(){{ put(flowType , value); }} );
    });
    return this;
  }

  /**
   * with 大限宮位對照
   *
   * @param flowBig 哪個大限
   * @param map     地支「在該大限」與宮位的對照表
   */
  public Builder withFlowBig(StemBranch flowBig , Map<Branch , House> map) {
    this.flowBranchMap.put(FlowType.大限 , flowBig);
    map.forEach((branch, house) -> {
      branchFlowHouseMap.computeIfPresent(branch , (branch1 , m) -> {
        m.put(FlowType.大限 , map.get(branch));
        return m;
      });
    });
    return this;
  }

  /**
   * with 流年宮位對照
   *
   * @param flowYear 哪個流年
   * @param map      地支「在該流年」與宮位的對照表
   */
  public Builder withFlowYear(StemBranch flowYear , Map<Branch , House> map) {
    this.flowBranchMap.put(FlowType.流年 , flowYear);
    map.forEach((branch, house) -> {
      branchFlowHouseMap.computeIfPresent(branch , (branch1 , m) -> {
        m.put(FlowType.流年 , map.get(branch));
        return m;
      });
    });
    return this;
  }

  /**
   * with 流月宮位對照
   *
   * @param flowMonth 哪個流月
   * @param map       地支「在該流月」與宮位的對照表
   */
  public Builder withFlowMonth(StemBranch flowMonth , Map<Branch , House> map) {
    this.flowBranchMap.put(FlowType.流月 , flowMonth);
    map.forEach((branch, house) -> {
      branchFlowHouseMap.computeIfPresent(branch , (branch1 , m) -> {
        m.put(FlowType.流月 , map.get(branch));
        return m;
      });
    });
    return this;
  }

  /**
   * with 流日宮位對照
   *
   * @param flowDay 哪個流日
   * @param map     地支「在該流日」與宮位的對照表
   */
  public Builder withFlowDay(StemBranch flowDay , Map<Branch , House> map) {
    this.flowBranchMap.put(FlowType.流日 , flowDay);
    map.forEach((branch, house) -> {
      branchFlowHouseMap.computeIfPresent(branch , (branch1 , m) -> {
        m.put(FlowType.流日 , map.get(branch));
        return m;
      });
    });
    return this;
  }

  /**
   * with 流時宮位對照
   *
   * @param flowHour 哪個流時
   * @param map      地支「在該流時」與宮位的對照表
   */
  public Builder withFlowHour(StemBranch flowHour , Map<Branch , House> map) {
    this.flowBranchMap.put(FlowType.流時 , flowHour);
    map.forEach((branch, house) -> {
      branchFlowHouseMap.computeIfPresent(branch , (branch1 , m) -> {
        m.put(FlowType.流時 , map.get(branch));
        return m;
      });
    });
    return this;
  }

  public Builder withPersonModel(PersonContextModel personModel) {
    this.personModel = personModel;
    return this;
  }




  public Plate build() {
    if (personModel == null) {
      return new Plate(context, chineseDate, localDateTime, location, place, gender, mainHouse , bodyHouse , mainStar, bodyStar, fiveElement , set , naYin, houseDataSet , transFourMap, branchFlowHouseMap, flowBranchMap, starStrengthMap, eightWords);
    } else {
      return new PlateWithEightWords(context, chineseDate, localDateTime, location, place, gender, mainHouse , bodyHouse , mainStar, bodyStar, fiveElement , set , naYin, houseDataSet , transFourMap, branchFlowHouseMap, flowBranchMap, starStrengthMap, eightWords , personModel);
    }
  }


} // class Builder
