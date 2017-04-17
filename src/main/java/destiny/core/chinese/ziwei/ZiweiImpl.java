/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.Stem;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.NotNull;
import org.jooq.lambda.tuple.Tuple;
import org.jooq.lambda.tuple.Tuple2;
import org.jooq.lambda.tuple.Tuple3;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class ZiweiImpl implements IZiwei, Serializable {

  @Override
  public Plate.Builder getPlate(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour,
                        @NotNull Collection<ZStar> stars, Gender gender,
                        Map<FlowType, Stem> transFourTypes, Settings settings) {
    StemBranch mainHouse = IZiwei.getMainHouse(year.getStem() , monthNum , hour);
    StemBranch bodyHouse = IZiwei.getBodyHouse(year.getStem() , monthNum , hour);

    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());

    Tuple3<String , FiveElement , Integer> t3 = getNaYin(year.getStem() , monthNum , hour);
    int set = t3.v3();

    // 地支 -> 宮位 的 mapping
    Map<StemBranch , House> branchHouseMap =
      Arrays.stream(houseSeq.getHouses()).map(house -> {
        StemBranch sb = getHouse(year.getStem() , monthNum, hour , house , houseSeq);
        return Tuple.tuple(sb , house);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    // 寅 的天干
    Stem stemOf寅 = IZiwei.getStemOf寅(year.getStem());

    Map<ZStar , StemBranch> starBranchMap =
    stars.stream()
      .map(star -> Optional.ofNullable(HouseFunctions.map.get(star))
        .map(iHouse -> {
          Branch branch = iHouse.getBranch(year , monthBranch , monthNum , days , hour , set , gender , settings);
          StemBranch sb = IZiwei.getStemBranchOf(branch , stemOf寅);
          return Tuple.tuple(star , sb);
        })
      )
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2))
      ;

    // 欲計算的 (運的類型,天干) 對照表
    Map<FlowType, Stem> calculatingTransFourMap = new HashMap<>();
    calculatingTransFourMap.put(FlowType.本命 , year.getStem());

    transFourTypes.entrySet().stream()
      .filter(entry -> entry.getKey() != FlowType.本命) // 如果原本有傳入本命，移除之（因為可能帶入不正確的天干）
      .forEachOrdered(e -> calculatingTransFourMap.put(e.getKey() , e.getValue()));

    // 每顆星體，於每個[流運的類型] -> 四化結果 , 的對照表
    Map<ZStar , Map<FlowType, ITransFour.Value>> transFourMap =
      stars.stream().map(star -> {
        Map<FlowType, ITransFour.Value> resultMap =
          calculatingTransFourMap.entrySet().stream()
            .map(e -> Tuple.tuple(
              e.getKey() ,
              getTranFourImpl(settings.getTransFour()).getValueOf(star , e.getValue())
            ))
            .filter(tuple -> tuple.v2().isPresent())
            .collect(Collectors.toMap(
              Tuple2::v1,
              t -> t.v2().orElse(null),  // 其實這裡不會 null , 因為之前已經 filter 過了
              (v1, v2) -> v1,
              TreeMap::new
            ));
        return Tuple.tuple(star , resultMap);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    return new Plate.Builder(monthNum, hour, mainHouse , bodyHouse , t3.v2() , set , branchHouseMap , starBranchMap)
      .withTransFourMap(transFourMap)
      ;
  } // 計算命盤

  /** 計算 大限盤 */
  @Override
  public Plate.Builder getPlate(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, @NotNull Collection<ZStar> stars, Gender gender, Map<FlowType, Stem> transFourTypes, Settings settings, Branch flowBranch) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());
    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map( branch -> {
        int steps = branch.getAheadOf(flowBranch);
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    return getPlate(year , monthBranch , monthNum , days , hour , stars , gender , transFourTypes , settings)
      .withFlowMain(flowBranch , branchHouseMap);
  }

  /** 計算 流年盤 */
  @Override
  public Plate.Builder getPlate(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, @NotNull Collection<ZStar> stars, Gender gender, Map<FlowType, Stem> transFourTypes, Settings settings, Branch flowBranch, Branch flowYear) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());
    IFlowYear flowYearImpl = getFlowYearImpl(settings.getFlowYear());

    Branch 流年命宮 = flowYearImpl.getFlowYear(flowYear , monthNum , hour);
    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map(branch -> {
        int steps = branch.getAheadOf(流年命宮);
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    return getPlate(year , monthBranch , monthNum , days , hour , stars , gender , transFourTypes , settings , flowBranch)
      .withFlowYear(flowYear , branchHouseMap)
      ;
  }

  /** 計算 流月盤 */
  @Override
  public Plate.Builder getPlate(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour,
                         @NotNull Collection<ZStar> stars, Gender gender,
                         Map<FlowType, Stem> transFourTypes, Settings settings,
                         Branch flowBranch , Branch flowYear , Branch flowMonth) {
    IHouseSeq houseSeq = getHouseSeq(settings.getHouseSeq());
    IFlowMonth flowMonthImpl = getFlowMonthImpl(settings.getFlowMonth());

    Branch 流月命宮 = flowMonthImpl.getFlowMonth(flowYear , flowMonth , monthNum , hour);
    Map<Branch , House> branchHouseMap =
      Arrays.stream(Branch.values()).map(branch -> {
        int steps = branch.getAheadOf(流月命宮);
        House house = houseSeq.prev(House.命宮 , steps);
        return Tuple.tuple(branch , house);
      }).collect(Collectors.toMap(Tuple2::v1 , Tuple2::v2));

    return getPlate(year , monthBranch , monthNum , days , hour , stars , gender , transFourTypes , settings , flowBranch , flowYear)
      .withFlowMonth(flowMonth , branchHouseMap);
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
      case DEFAULT: return new FlowYearDefaultImpl();
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

}
