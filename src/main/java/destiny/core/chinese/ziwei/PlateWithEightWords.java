/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.chinese.ziwei;

import destiny.core.Gender;
import destiny.core.calendar.Location;
import destiny.core.calendar.chinese.ChineseDate;
import destiny.core.calendar.eightwords.personal.PersonContextModel;
import destiny.core.chinese.Branch;
import destiny.core.chinese.FiveElement;
import destiny.core.chinese.StemBranch;
import org.jetbrains.annotations.Nullable;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 紫微盤為主，八字盤為輔
 */
public class PlateWithEightWords extends Plate {

  /** 八字資料 */
  private final PersonContextModel personModel;

  PlateWithEightWords(ZContext context, ChineseDate chineseDate, @Nullable LocalDateTime localDateTime, @Nullable Location location, @Nullable String place, Gender gender, StemBranch mainHouse, StemBranch bodyHouse, ZStar mainStar, ZStar bodyStar, FiveElement fiveElement, int set, Set<HouseData> houseDataSet, Map<ZStar, Map<FlowType, ITransFour.Value>> transFourMap, Map<Branch, Map<FlowType, House>> branchFlowHouseMap, Map<FlowType, StemBranch> flowBranchMap, Map<ZStar, Integer> starStrengthMap, List<String> notes , PersonContextModel personModel) {
    super(context, chineseDate, localDateTime, location, place, gender, mainHouse, bodyHouse, mainStar, bodyStar, fiveElement, set, houseDataSet, transFourMap, branchFlowHouseMap, flowBranchMap, starStrengthMap, notes);
    this.personModel = personModel;
  }


  public PersonContextModel getPersonModel() {
    return personModel;
  }
}
