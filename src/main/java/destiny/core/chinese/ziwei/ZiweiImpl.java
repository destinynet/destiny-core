/**
 * Created by smallufo on 2017-04-09.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;
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

public class ZiweiImpl implements ZiweiIF , Serializable {

  @Override
  public Plate getPlate(StemBranch year, Branch monthBranch, int monthNum, int days, Branch hour, HouseSeqIF houseSeq, @NotNull Collection<ZStar> stars, Gender gender, Settings settings) {
    StemBranch mainHouse = ZiweiIF.getMainHouse(year.getStem() , monthNum , hour);
    StemBranch bodyHouse = ZiweiIF.getBodyHouse(year.getStem() , monthNum , hour);

    Tuple3<String , FiveElement , Integer> t3 = getNaYin(year.getStem() , monthNum , hour);
    int set = t3.v3();

    Map<House , StemBranch> houseMap =
      Arrays.stream(houseSeq.getHouses())
        .map( house -> Tuple.tuple(house , getHouse(year.getStem() , monthNum, hour , house , houseSeq)))
        .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2));

    // 寅 的天干
    Stem stemOf寅 = ZiweiIF.getStemOf寅(year.getStem());

    Map<ZStar , StemBranch> starBranchMap =
    stars.stream()
      .map(star -> Optional.ofNullable(HouseFunctions.map.get(star))
        .map(iHouse -> {
          Branch branch = iHouse.getBranch(year , monthBranch , monthNum , days , hour , set , gender , settings);
          StemBranch sb = ZiweiIF.getStemBranchOf(branch , stemOf寅);
          return Tuple.tuple(star , sb);
        })
      )
      .filter(Optional::isPresent)
      .map(Optional::get)
      .collect(Collectors.toMap(Tuple2::v1, Tuple2::v2))
      ;

    Map<ZStar , Tuple2<ITransFour.Type , ITransFour.Value>> tranFourMap =
      stars.stream().map(star -> Tuple.tuple(star , getTranFourImpl(settings.getTransFour()).getValueOf(star , year.getStem())))
        .filter(t -> t.v2().isPresent())
        .collect(Collectors.toMap(
          Tuple2::v1,
          tuple -> Tuple.tuple(ITransFour.Type.本命 , tuple.v2().orElse(null))) // 前面已經 filter 過了，其實這裡不可能為 null
        );

    return new Plate(mainHouse , bodyHouse, t3.v2(), set, houseMap, starBranchMap , tranFourMap);
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

  @Override
  public void calculate(Gender gender, LocalDateTime time, Location loc) {

  }
}
