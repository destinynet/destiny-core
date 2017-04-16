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
  public Plate getPlate(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour,
                        IHouseSeq houseSeq, @NotNull Collection<ZStar> stars, Gender gender,
                        Map<ITransFour.Type , Stem> transFourTypes, Settings settings) {
    StemBranch mainHouse = IZiwei.getMainHouse(year.getStem() , monthNum , hour);
    StemBranch bodyHouse = IZiwei.getBodyHouse(year.getStem() , monthNum , hour);

    Tuple3<String , FiveElement , Integer> t3 = getNaYin(year.getStem() , monthNum , hour);
    int set = t3.v3();

    Map<House , StemBranch> houseMap =
      Arrays.stream(houseSeq.getHouses())
        .map( house -> Tuple.tuple(house , getHouse(year.getStem() , monthNum, hour , house , houseSeq)))
        .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

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

    // 欲計算的四化類型
    Map<ITransFour.Type , Stem> calculatingMap = new HashMap<>();
    calculatingMap.put(ITransFour.Type.本命 , year.getStem());

    transFourTypes.entrySet().stream()
      .filter(entry -> entry.getKey() != ITransFour.Type.本命) // 如果原本有傳入本命，移除之（因為可能帶入不正確的天干）
      .forEachOrdered(e -> calculatingMap.put(e.getKey() , e.getValue()));

    Map<ZStar , Map<ITransFour.Type , ITransFour.Value>> tranFourMap =
      stars.stream().map(star -> {
        Map<ITransFour.Type, ITransFour.Value> resultMap =
          calculatingMap.entrySet().stream()
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
    return new Plate(mainHouse , bodyHouse, t3.v2(), set, houseMap, starBranchMap, tranFourMap);
  } // 計算本命盤


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
