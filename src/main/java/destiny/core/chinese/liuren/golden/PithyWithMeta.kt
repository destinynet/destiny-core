/**
 * Created by smallufo on 2015-05-31.
 */
package destiny.core.chinese.liuren.golden

import destiny.astrology.DayNight
import destiny.astrology.DayNightDifferentiator
import destiny.core.Gender
import destiny.core.calendar.LocationWithName
import destiny.core.chinese.IClockwise
import destiny.core.chinese.IMonthMaster
import destiny.core.chinese.ITianyi
import destiny.core.chinese.liuren.General
import destiny.core.chinese.liuren.IGeneralStemBranch
import destiny.core.chinese.liuren.IGeneralSeq
import java.io.Serializable
import java.util.*

class PithyWithMeta(val pithy: Pithy, private val method: Method?, val gender: Gender, val question: String, val locationWithName: LocationWithName,
                    /** 月將  */
                    val monthMasterImpl: IMonthMaster,
                    /** 晝夜區分  */
                    val dayNightImpl: DayNightDifferentiator,
                    /** 天乙貴人  */
                    val tianyiImpl: ITianyi,
                    /** 貴神順逆  */
                    val clockwiseImpl: IClockwise,
                    /** 12天將順序  */
                    val seqImpl: IGeneralSeq,
                    /** 12天將干支  */
                    private val generalStemBranchImpl: IGeneralStemBranch) : Serializable {

  /** 起課方式  */
  enum class Method {
    RANDOM, MANUAL
  }


  override fun toString(): String {
    val sb = StringBuilder()
    val ew = pithy.eightWords
    sb.append("　日").append("\n")
    sb.append(ew.hourStem).append(ew.dayStem).append(ew.monthStem).append(ew.yearStem).append("\n")
    sb.append(ew.hourBranch).append(ew.dayBranch).append(ew.monthBranch).append(ew.yearBranch).append("\n")
    sb.append("\n")
    sb.append("月將：").append(pithy.monthSign).append("（").append(monthMasterImpl.getTitle(Locale.TAIWAN)).append("）").append("\n")
    sb.append("晝夜：").append(if (pithy.dayNight == DayNight.DAY) "日" else "夜").append("\n")
    sb.append("年空：").append(ew.year.empties.map { it.toString() }.joinToString("、")).append("\n")
    //sb.append("年空：").append(ew.year.empties.stream().map<String>(Function<Branch, String> { it.toString() }).collect<String, *>(Collectors.joining("、"))).append("\n")
    sb.append("日空：").append(ew.day.empties.map { it.toString() }.joinToString("、")).append("\n")
    //sb.append("日空：").append(ew.day.empties.stream().map<String>(Function<Branch, String> { it.toString() }).collect<String, *>(Collectors.joining("、"))).append("\n")
    sb.append("\n")
    sb.append("人元：").append(pithy.human).append("\n")
    val 貴神 = pithy.benefactor
    sb.append("貴神：").append(貴神).append("（").append(General.get(貴神.branch, generalStemBranchImpl)).append("）").append("\n")
    val 將神 = pithy.johnson
    sb.append("將神：").append(將神).append("（").append(IMonthMaster.getName(將神.branch)).append("）").append("\n")
    sb.append("地分：").append(pithy.direction)
    sb.append("\n\n")

    sb.append("性別：").append(gender).append("\n")
    sb.append("問題：").append(question).append("\n")
    sb.append("地點：").append(locationWithName.name).append("\n")

    if (method != null)
      sb.append("起課方式：").append(if (method == Method.RANDOM) "電腦起課" else "手動起課").append("\n")
    sb.append("晝夜設定：").append(dayNightImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("天乙貴人：").append(tianyiImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("順逆設定：").append(clockwiseImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("天將順序：").append(seqImpl.getTitle(Locale.TAIWAN)).append("\n")
    sb.append("天將干支：").append(generalStemBranchImpl.getTitle(Locale.TAIWAN)).append("\n")

    return sb.toString()
  }


}
