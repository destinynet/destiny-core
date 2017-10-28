/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.chrono.ChronoLocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/** 排盤結果 , 作為 DTO */

public class Plate implements Serializable {

  private transient static Logger logger = LoggerFactory.getLogger(Plate.class);

  /** 名稱 */
  @Nullable
  protected final String name;

  /** 設定資料 */
  //protected final ZContext context;

  /** 出生資料 , 陰曆 */
  protected final ChineseDate chineseDate;

  /** 出生資料 , 陽曆 , 精確到「分、秒」 */
  @Nullable
  protected final ChronoLocalDateTime localDateTime;

  /** 出生地點 */
  @Nullable
  protected final Location location;

  /** 地點名稱 */
  @Nullable
  protected final String place;

  /** 性別 */
  protected final Gender gender;

  /** 命宮 */
  protected final StemBranch mainHouse;

  /** 身宮 */
  protected final StemBranch bodyHouse;

  /** 命主 */
  protected final ZStar mainStar;

  /** 身主 */
  protected final ZStar bodyStar;

  /** 五行 */
  protected final FiveElement fiveElement;

  /** 五行第幾局 */
  protected final int set;

  /** 納音 */
  //protected final String naYin;

  /** 12個宮位，每個宮位內的資料 */
  protected final Set<HouseData> houseDataSet;

  /** 四化星 的列表
   * 存放著「這顆星」在 [本命、大限、流年、...] 的四化 結果為何
   * */
  protected final Map<ZStar , Map<FlowType, ITransFour.Value>> transFourMap;// = new HashMap<>();

  /** 取得此地支，在各個流運類型， 宮位名稱 是什麼 */
  protected final Map<Branch, Map<FlowType, House>> branchFlowHouseMap;

  /** 計算的流運資料 */
  protected final Map<FlowType , StemBranch> flowBranchMap;

  /** 星體強弱表 */
  protected final Map<ZStar , Integer> starStrengthMap;

  /** 註解列表 */
  private final List<String> notes;

  /** 虛歲，每歲的起訖時分 (in GMT) */
  private final Map<Integer , Tuple2<Double , Double>> vageMap;

  /**
   * 命盤
   */
  protected Plate(@Nullable String name, ChineseDate chineseDate, @Nullable ChronoLocalDateTime localDateTime, @Nullable Location location, @Nullable String place, Gender gender, StemBranch mainHouse, StemBranch bodyHouse, ZStar mainStar, ZStar bodyStar, FiveElement fiveElement, int set, Set<HouseData> houseDataSet, Map<ZStar, Map<FlowType, ITransFour.Value>> transFourMap, Map<Branch, Map<FlowType, House>> branchFlowHouseMap, Map<FlowType, StemBranch> flowBranchMap, Map<ZStar, Integer> starStrengthMap, List<String> notes, Map<Integer, Tuple2<Double, Double>> vageMap) {
    this.name = name;
    this.chineseDate = chineseDate;
    this.localDateTime = localDateTime;
    this.location = location;
    this.place = place;
    this.gender = gender;
    this.mainHouse = mainHouse;
    this.bodyHouse = bodyHouse;
    this.mainStar = mainStar;
    this.bodyStar = bodyStar;
    this.fiveElement = fiveElement;
    this.set = set;
    this.houseDataSet = houseDataSet;
    this.transFourMap = transFourMap;
    this.branchFlowHouseMap = branchFlowHouseMap;
    this.flowBranchMap = flowBranchMap;
    this.starStrengthMap = starStrengthMap;
    this.notes = notes;
    this.vageMap = vageMap;
  }

  public Optional<String> getName() {
    return Optional.ofNullable(name);
  }

  public ChineseDate getChineseDate() {
    return chineseDate;
  }

  public Optional<ChronoLocalDateTime> getLocalDateTime() {
    return Optional.ofNullable(localDateTime);
  }

  public Optional<Location> getLocation() {
    return Optional.ofNullable(location);
  }

  public Optional<String> getPlace() {
    if (StringUtils.isBlank(place)) {
      return Optional.empty();
    } else {
      return Optional.of(place);
    }
  }

  public Gender getGender() {
    return gender;
  }

  public StemBranch getMainHouse() {
    return mainHouse;
  }

  public StemBranch getBodyHouse() {
    return bodyHouse;
  }

  /** 五行 */
  public FiveElement getFiveElement() {
    return fiveElement;
  }

  /** 第幾局 */
  public int getSet() {
    return set;
  }

  /** 宮位名稱 -> 宮位資料 */
  public Map<House, HouseData> getHouseMap() {
    return houseDataSet.stream().collect(Collectors.toMap(HouseData::getHouse, hd -> hd));
  }

  /** 星體 -> 宮位資料 */
  public Map<ZStar, HouseData> getStarMap() {
    return houseDataSet.stream()
      .flatMap(hd -> hd.getStars().stream().map(star -> Tuple.tuple(star , hd)))
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
  }

  /** 宮位地支 -> 星體s */
  public Map<Branch, Set<ZStar>> getBranchStarMap() {
    return houseDataSet.stream().collect(Collectors.toMap(hd -> hd.getStemBranch().getBranch() , HouseData::getStars));
  }

  /** 宮位名稱 -> 星體s */
  public Map<House, Set<ZStar>> getHouseStarMap() {
    return houseDataSet.stream().collect(Collectors.toMap(HouseData::getHouse, HouseData::getStars));
  }

  /** 取得每個宮位、詳細資料 , 按照 [命宮 , 兄弟 , 夫妻...] 排序下來 */
  public Set<HouseData> getHouseDataSet() {
    return new TreeSet<>(houseDataSet);
  }

  /** 取得此地支的宮位資訊 */
  @SuppressWarnings("ConstantConditions")
  public HouseData getHouseDataOf(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch).findFirst().get();
  }

  /** 取得此命盤，包含哪些流運資訊 */
  public Map<FlowType, StemBranch> getFlowBranchMap() {
    return flowBranchMap;
  }

  /** 取得 星體的四化列表 */
  public Map<ZStar, Map<FlowType, ITransFour.Value>> getTranFours() {
    return transFourMap;
  }

  /** 取得此顆星，的四化列表 */
  public List<Tuple2<FlowType, ITransFour.Value>> getTransFourOf(ZStar star) {
    return
      transFourMap.getOrDefault(star , new HashMap<>())
      .entrySet()
      .stream()
      .map(e -> Tuple.tuple(e.getKey() , e.getValue()))
      .collect(Collectors.toList());
  }

  /** 取得此地支，在各個流運類型， 宮位是什麼 */
  public Map<Branch, Map<FlowType, House>> getBranchFlowHouseMap() {
    return branchFlowHouseMap;
  }

  /** 本命盤中，此地支的宮位名稱是什麼 */
  public Map<Branch , House> getBranchHouseMap() {
    return getBranchFlowHouseMap().entrySet().stream()
      .map(e -> Tuple.tuple(e.getKey() , e.getValue().get(FlowType.本命)))
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
  }

  /** 取得命盤的星體強弱表 */
  public Map<ZStar, Integer> getStarStrengthMap() {
    return starStrengthMap;
  }

  /** 取得在此地支宮位的主星 */
  public List<ZStar> getMainStarsIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarMain)
      .collect(Collectors.toList());
  }

  /** 吉星 */
  public List<ZStar> getLuckyStarsIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarLucky)
      .collect(Collectors.toList());
  }

  /** 凶星 */
  public List<ZStar> getUnluckyStarsIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarUnlucky)
      .collect(Collectors.toList());
  }

  /** 雜曜 */
  public List<ZStar> getMinorStarsIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarMinor)
      .collect(Collectors.toList());
  }

  /** 博士12神煞 */
  public Optional<ZStar> getDoctorStarIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarDoctor)
      .findFirst();
  }

  /** 長生12神煞 */
  public Optional<ZStar> getLongevityStarIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarLongevity)
      .findFirst();
  }

  /** 將前 12星 */
  public Optional<ZStar> getGeneralFrontStarIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarGeneralFront)
      .findFirst();
  }

  /** 歲前 12星 */
  public Optional<ZStar> getYearFrontStarIn(Branch branch) {
    return houseDataSet.stream().filter(houseData -> houseData.getStemBranch().getBranch() == branch)
      .flatMap(houseData -> houseData.getStars().stream())
      .filter(star -> star instanceof StarYearFront)
      .findFirst();
  }

  /** 命主 */
  public ZStar getMainStar() {
    return mainStar;
  }

  /** 身主 */
  public ZStar getBodyStar() {
    return bodyStar;
  }


  /** 歲數 map (fromGMT , toGMT) */
  public Map<Integer, Tuple2<Double, Double>> getVageMap() {
    return vageMap;
  }

  /** 取得註解列表 */
  public List<String> getNotes() {
    return notes;
  }
}
