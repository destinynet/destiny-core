/**
 * Created by smallufo on 2017-04-25.
 */
package destiny.core.chinese.ziwei

import destiny.core.Gender
import destiny.core.calendar.Location
import destiny.core.calendar.chinese.ChineseDate
import destiny.core.calendar.eightwords.personal.PersonContextModel
import destiny.core.chinese.Branch
import destiny.core.chinese.FiveElement
import destiny.core.chinese.StemBranch

import java.time.chrono.ChronoLocalDateTime

/**
 * 紫微盤為主，八字盤為輔
 */
class PlateWithEightWords internal constructor(
  name: String, chineseDate: ChineseDate,
  localDateTime: ChronoLocalDateTime<*>?,
  location: Location?, place: String?,
  gender: Gender,
  mainHouse: StemBranch, bodyHouse: StemBranch,
  mainStar: ZStar, bodyStar: ZStar,
  fiveElement: FiveElement, set: Int,
  houseDataSet: Set<HouseData>,
  transFourMap: Map<ZStar, Map<FlowType, ITransFour.Value>>,
  branchFlowHouseMap: Map<Branch, Map<FlowType, House>>,
  flowBranchMap: Map<FlowType, StemBranch>,
  starStrengthMap: Map<ZStar, Int>,
  notes: List<String>, vageMap: Map<Int, Pair<Double, Double>>,
  /** 八字資料  */
  val personModel: PersonContextModel) : Plate(name, chineseDate, localDateTime, location, place, gender, mainHouse, bodyHouse, mainStar, bodyStar, fiveElement, set, houseDataSet, transFourMap, branchFlowHouseMap, flowBranchMap, starStrengthMap, notes, vageMap)
