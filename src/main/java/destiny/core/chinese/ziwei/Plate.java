/**
 * Created by smallufo on 2017-04-13.
 */
package destiny.core.chinese.ziwei;

import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/** 排盤結果 , 作為 DTO */

public class Plate implements Serializable {

  /** 命宮 */
  private final StemBranch mainHouse;

  /** 身宮 */
  private final StemBranch bodyHouse;

  /** 五行 */
  private final FiveElement fiveElement;

  /** 五行第幾局 */
  private final int set;

  /** 12個宮位，每個宮位內的資料 */
  private final Set<HouseData> houseDataSet;

  private transient static Logger logger = LoggerFactory.getLogger(Plate.class);

  /** 四化星 的列表 */
  private final Map<ZStar , Map<ITransFour.Type , ITransFour.Value>> tranFourMap;// = new HashMap<>();

  /**
   * 本命盤
   */
  public Plate(StemBranch mainHouse, StemBranch bodyHouse, FiveElement fiveElement,
               int set,
               Map<House, StemBranch> houseMap,
               Map<ZStar, StemBranch> starBranchMap,
               Map<ZStar, Map<ITransFour.Type, ITransFour.Value>> transFourMap) {
    this.mainHouse = mainHouse;
    this.bodyHouse = bodyHouse;
    this.fiveElement = fiveElement;
    this.set = set;
    this.tranFourMap = transFourMap;

    // 哪個地支 裡面 有哪些星體
    Map<Branch , Set<ZStar>> branchStarMap = starBranchMap.entrySet().stream()
      .collect(
        Collectors.groupingBy(
          entry -> entry.getValue().getBranch(),
          TreeMap::new,   // 保留地支順序
          Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
        )
      );

    houseDataSet = houseMap.entrySet().stream().map(e -> {
      House house = e.getKey();
      StemBranch sb = e.getValue();
      Set<ZStar> stars = branchStarMap.get(sb.getBranch());
      return new HouseData(house , sb , stars);
    }).collect(Collectors.toSet());

  }

  public StemBranch getMainHouse() {
    return mainHouse;
  }

  public StemBranch getBodyHouse() {
    return bodyHouse;
  }

  public FiveElement getFiveElement() {
    return fiveElement;
  }

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

  /** 取得 星體的四化列表 */
  public Map<ZStar, Map<ITransFour.Type, ITransFour.Value>> getTranFours() {
    return tranFourMap;
  }

  /** 取得此顆星，的四化列表 */
  public List<Tuple2<ITransFour.Type , ITransFour.Value>> getTransFourOf(ZStar star) {
    return tranFourMap.get(star)
      .entrySet()
      .stream()
      .map(e -> Tuple.tuple(e.getKey() , e.getValue()))
      .collect(Collectors.toList());
  }
}
