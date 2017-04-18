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

  /** 四化星 的列表
   * 存放著「這顆星」在 [本命、大限、流年、...] 的四化 結果為何
   * */
  private final Map<ZStar , Map<FlowType, ITransFour.Value>> transFourMap;// = new HashMap<>();

  private final Map<Branch, Map<FlowType, House>> branchFlowHouseMap;

  /**
   * 命盤
   */
  private Plate(StemBranch mainHouse, StemBranch bodyHouse, FiveElement fiveElement, int set,
                Set<HouseData> houseDataSet,
                Map<ZStar, Map<FlowType, ITransFour.Value>> transFourMap,
                Map<Branch, Map<FlowType, House>> branchFlowHouseMap) {
    this.mainHouse = mainHouse;
    this.bodyHouse = bodyHouse;
    this.fiveElement = fiveElement;
    this.set = set;
    this.houseDataSet = houseDataSet;
    this.transFourMap = transFourMap;
    this.branchFlowHouseMap = branchFlowHouseMap;
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


  public static class Builder {

    /** 出生月份 */
    private final int birthMonthNum;

    /** 出生時辰 */
    private final Branch birthHour;

    /** 命宮 */
    private final StemBranch mainHouse;

    /** 身宮 */
    private final StemBranch bodyHouse;

    /** 五行 */
    private final FiveElement fiveElement;

    /** 五行第幾局 */
    private final int set;

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

    public Builder(int birthMonthNum, Branch birthHour, StemBranch mainHouse, StemBranch bodyHouse, FiveElement fiveElement, int set, Map<StemBranch, House> branchHouseMap, Map<ZStar, StemBranch> starBranchMap) {
      this.birthMonthNum = birthMonthNum;
      this.birthHour = birthHour;
      this.mainHouse = mainHouse;
      this.bodyHouse = bodyHouse;
      this.fiveElement = fiveElement;
      this.set = set;

      // 哪個地支 裡面 有哪些星體
      Map<Branch , Set<ZStar>> branchStarMap = starBranchMap.entrySet().stream()
        .collect(
          Collectors.groupingBy(
            entry -> entry.getValue().getBranch(),
            TreeMap::new,   // 保留地支順序
            Collectors.mapping(Map.Entry::getKey, Collectors.toSet())
          )
        );

      Map<Branch , Map<FlowType , House>> 本命地支HouseMapping =
        branchHouseMap.entrySet().stream().map(e -> {
          Map<FlowType , House> m = new HashMap<>();
          m.put(FlowType.本命 , branchHouseMap.get(e.getKey()));
          return Tuple.tuple(e.getKey().getBranch() , m);
        }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));
      branchFlowHouseMap.putAll(本命地支HouseMapping);


      houseDataSet = branchHouseMap.entrySet().stream().map(e -> {
        StemBranch sb = e.getKey();
        House house = e.getValue();
        Set<ZStar> stars = branchStarMap.get(sb.getBranch());
        return new HouseData(house, sb, stars);
      }).collect(Collectors.toSet());
    } // builder init


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
    public Builder withFlowBig(Branch flowBig , Map<Branch , House> map) {
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
    public Builder withFlowYear(Branch flowYear , Map<Branch , House> map) {
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
    public Builder withFlowMonth(Branch flowMonth , Map<Branch , House> map) {
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
    public Builder withFlowDay(Branch flowDay , Map<Branch , House> map) {
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
    public Builder withFlowHour(Branch flowHour , Map<Branch , House> map) {
      map.forEach((branch, house) -> {
        branchFlowHouseMap.computeIfPresent(branch , (branch1 , m) -> {
          m.put(FlowType.流時 , map.get(branch));
          return m;
        });
      });
      return this;
    }


    public Plate build() {
      return new Plate(mainHouse , bodyHouse , fiveElement , set , houseDataSet , transFourMap, branchFlowHouseMap);
    }


  } // class Builder
}
