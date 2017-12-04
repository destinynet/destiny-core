/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.chinese.ziwei;

import com.google.common.collect.Table;
import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.eightwords.personal.PersonContextModel;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import kotlin.Pair;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.text.MessageFormat;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

public class Builder implements Serializable {

  /** 名稱 */
  @Nullable
  private String name;

  /** 設定資料 */
  private final ZContext context;

  /** 陰曆生日 */
  private final ChineseDate chineseDate;

  /** 陽曆出生日期 */
  @Nullable
  private ChronoLocalDateTime localDateTime = null;

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

  private final Set<HouseData> houseDataSet;

  /** 干支 -> 宮位 的對照表 */
  private final Map<StemBranch, House> stemBranchHouseMap;

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

  /** 每個地支宮位，所代表的大限，「虛歲」從何時、到何時 */
  private final Map<StemBranch , Pair<Integer , Integer>> flowBigVageMap;

  /** 註解 builder */
  private final List<Tuple2<String, Object[]>> notesBuilder = new ArrayList<>();

  /** 註解列表 */
  private List<String> notes = new ArrayList<>();

  /** 歲數 (暫定虛歲），每歲的起訖時分 (in GMT) */
  private final Map<Integer , Pair<Double , Double>> vageMap;

  /** 本命盤 */
  public Builder(ZContext context, ChineseDate chineseDate, Gender gender, int birthMonthNum, Branch birthHour, StemBranch mainHouse, StemBranch bodyHouse, ZStar mainStar, ZStar bodyStar, FiveElement fiveElement, int set, Map<StemBranch, House> branchHouseMap, Map<ZStar, Branch> starBranchMap, Map<ZStar, Integer> starStrengthMap, Map<StemBranch, Pair<Integer, Integer>> flowBigVageMap, Map<Branch, List<Integer>> branchSmallRangesMap, Map<StemBranch, Table<ITransFour.Value, ZStar, Branch>> flyMap, Map<Integer, Pair<Double, Double>> vageMap) {
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
    this.starStrengthMap = starStrengthMap;
    this.flowBigVageMap = flowBigVageMap;
    this.flyMap = flyMap;
    this.vageMap = vageMap;

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

    // 可能有些 地支宮位裡面沒有星 , 因此建立出來的 Map 就無該 地支的 key 值 , 因此必須建立另一個 map , 確保裡面每個地支都存在，且 value 至少為 empty set

    Map<Branch , Set<ZStar>> branchStarMap2 = Arrays.stream(Branch.values()).map(branch -> {
      Set<ZStar> stars = branchStarMap.getOrDefault(branch , new HashSet<>());
      return Tuple.tuple(branch , stars);
    }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    this.stemBranchHouseMap = branchHouseMap;

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


      Pair<Integer , Integer> fromTo = flowBigVageMap.get(sb);
      List<Integer> smallRanges = branchSmallRangesMap.get(sb.getBranch());
      return new HouseData(house, sb
        , stars
        , branchFlowHouseMap.get(sb.getBranch())
        , flyMap.get(sb)
        , fromTo.getFirst() , fromTo.getSecond(), smallRanges);
    }).collect(Collectors.toSet());

  } // builder init

  public ZContext getContext() {
    return context;
  }

  public ChineseDate getChineseDate() {
    return chineseDate;
  }

  public Set<ZStar> getStars() {
    return starStrengthMap.keySet();
  }

  public int getBirthMonthNum() {
    return birthMonthNum;
  }

  public int getSet() {
    return set;
  }

  public Gender getGender() {
    return gender;
  }

  /**
   * 取出 本命盤 , 排序過的 , 每個地支的 大限 「虛歲」起訖 時刻
   */
  public Map<StemBranch, Pair<Integer , Integer>> getFlowBigMap() {
    return flowBigVageMap;
  }

  /** 傳回 干支 -> 宮位 的 mapping */
  public Map<StemBranch, House> getStemBranchHouseMap() {
    return stemBranchHouseMap;
  }

  /** 承上，只傳回「地支」 -> 宮位 的 mapping */
  public Map<Branch , House> getBranchHouseMap() {
    return getStemBranchHouseMap().entrySet().stream().collect(Collectors.toMap(e -> e.getKey().getBranch() , Map.Entry::getValue));
  }

  public Branch getBirthHour() {
    return birthHour;
  }

  /** 歲數 map */
  public Map<Integer, Pair<Double, Double>> getVageMap() {
    return vageMap;
  }

  public Builder withName(String name) {
    this.name = name;
    return this;
  }

  public Builder withLocalDateTime(ChronoLocalDateTime localDateTime) {
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

    // 以流年的 將前12星 取代本命盤中的位置
    // 先檢查，本命盤中，是否已經存在任何 將前12星 , 若有，代表設定要計算
    boolean showGeneralFront = houseDataSet.stream()
      .flatMap(houseData -> houseData.getStars().stream())
      .anyMatch(star -> star instanceof StarGeneralFront);

    if (showGeneralFront) {
      // 若有的話，就清除掉現有紀錄
      houseDataSet.forEach(houseData -> houseData.getStars().removeIf(star -> star instanceof StarGeneralFront));

      // 接著，以「流年」的將前12星，塞入
      Arrays.stream(StarGeneralFront.Companion.getValues())
        .map(star -> {
          Branch b = StarGeneralFront.Companion.getFunMap().get(star).invoke(flowYear.getBranch());
          return Tuple.tuple(star , b);
        }).forEach(t -> {
          houseDataSet.stream()
            .filter(houseData -> houseData.getStemBranch().getBranch() == t.v2()).findFirst()
            .ifPresent(houseData -> houseData.getStars().add(t.v1()));
      });
    }

    // 以流年的 歲前12星 取代本命盤中的位置
    // 先檢查，本命盤中，是否已經存在任何 歲前12星。 若有，代表設定要計算
    boolean showYearFront = houseDataSet.stream()
      .flatMap(houseData -> houseData.getStars().stream())
      .anyMatch(star -> star instanceof StarYearFront);

    if (showYearFront) {
      // 若有的話，就清除掉現有的紀錄
      houseDataSet.forEach(houseData -> houseData.getStars().removeIf(star -> star instanceof StarYearFront));

      // 接著，以「流年」的 歲前12星，塞入
      Arrays.stream(StarYearFront.values)
        .map(star -> {
          Branch b = StarYearFront.funMap.get(star).apply(flowYear.getBranch());
          return Tuple.tuple(star , b);
        }).forEach(t -> {
          houseDataSet.stream()
            .filter(houseData -> houseData.getStemBranch().getBranch() == t.v2()).findFirst()
            .ifPresent(houseData -> houseData.getStars().add(t.v1()));
      });
    }

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

  public Builder appendNotesBuilders(List<Tuple2<String , Object[]>> notesBuilders) {
    this.notesBuilder.addAll(notesBuilders);
    return this;
  }


  public Builder withNotes(@NotNull Class resourceBundleClazz , @NotNull Locale locale) {
    this.notes = buildNotes(resourceBundleClazz , locale);
    return this;
  }

  public Builder withNotes(@NotNull Class resourceBundleClazz) {
    this.notes = buildNotes(resourceBundleClazz , Locale.getDefault());
    return this;
  }

  private List<String> buildNotes(@NotNull Class resourceBundleClazz , @NotNull Locale locale) {
    return notesBuilder.stream().map(t -> {
      String pattern = ResourceBundle.getBundle(resourceBundleClazz.getName() , locale).getString(t.v1());
      return MessageFormat.format(pattern , t.v2());
    }).collect(Collectors.toList());
  }

  public Plate build() {
    if (personModel == null) {
      return new Plate(name, chineseDate, localDateTime, location, place, gender, mainHouse , bodyHouse , mainStar, bodyStar, fiveElement , set , houseDataSet , transFourMap, branchFlowHouseMap, flowBranchMap, starStrengthMap, notes, vageMap);
    } else {
      return new PlateWithEightWords(name , chineseDate, localDateTime, location, place, gender, mainHouse , bodyHouse , mainStar, bodyStar, fiveElement , set , houseDataSet , transFourMap, branchFlowHouseMap, flowBranchMap, starStrengthMap, notes , vageMap, personModel);
    }
  }



} // class Builder
