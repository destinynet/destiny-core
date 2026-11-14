/**
 * Created by smallufo on 2026-11-14.
 */
package destiny.core.chinese.onePalm

import destiny.core.Scale
import destiny.core.calendar.chinese.toChineseDayString
import destiny.core.calendar.chinese.toChineseMonthString
import destiny.core.chinese.Branch
import destiny.tools.getTitle
import kotlinx.serialization.Serializable
import destiny.tools.Lang

/**
 * 一掌經給前端的盤：十二宮（星、道、宮名、坐落的柱與斷語）、四柱、四大字、時柱詩與斷語、大運與當年小運。
 *
 * 字樣（星名、道名、宮名、柱名、閏月註記）都已填好，前端只負責擺位與呈現，不自己對照。
 * 一掌經的字樣只有中文，所以這裡不分語系。
 */
@Serializable
data class PalmChartDto(
  /** 四大字：年、月、日、時四星各取一字（「權文奸藝」） */
  val fourChars: String,
  /** 年、月、日、時四柱，依此順序 */
  val pillars: List<PalmPillarDto>,
  /** 十二宮，依地支順序（子 → 亥） */
  val houses: List<PalmHouseDto>,
  /** 時柱所落之星的七言詩 */
  val hourPoem: String,
  /** 時柱所落之星的斷語 */
  val hourContent: String,
  /** 大運：每運十年，從 1 歲起 */
  val majors: List<PalmFortuneDto>,
  /** 觀看時刻的小運（一歲一宮）；不知道觀看時的歲數 ⇒ null */
  val minor: PalmFortuneDto?,
  /** 盤的註記（例：閏月以哪個月起月） */
  val notes: List<String>,
)

/** 一柱 */
@Serializable
data class PalmPillarDto(
  val scale: Scale,
  /** 「年」「月」「日」「時」 */
  val title: String,
  val branch: Branch,
  /** 「天文星」 */
  val star: String,
)

/** 一宮 */
@Serializable
data class PalmHouseDto(
  val branch: Branch,
  val house: IPalmModel.House,
  /** 「命宮」「財帛宮」 */
  val houseName: String,
  /** 「天貴星」 */
  val star: String,
  /** 「佛道」 */
  val dao: String,
  /** 坐落此宮的柱（年、月、日、時的順序） */
  val pillars: List<Scale>,
  /** 此星的總論；沒有柱坐落 ⇒ null */
  val intro: String?,
  /** 坐落此宮的年、月、日柱各自的斷語（時柱另見 [PalmChartDto.hourContent]） */
  val readings: List<PalmReadingDto>,
)

@Serializable
data class PalmReadingDto(
  val scale: Scale,
  /** 「年」「月」「日」 */
  val title: String,
  val text: String,
)

/** 一段運：大運十年一宮、小運一年一宮；歲數是虛歲 */
@Serializable
data class PalmFortuneDto(
  val startAge: Int,
  val endAge: Int,
  val branch: Branch,
  /** 「天權星」 */
  val star: String,
  /** 觀看時刻落在這一段 */
  val current: Boolean,
)

/**
 * [IPalmMetaModel] → [PalmChartDto]。
 *
 * @param age 觀看時刻的虛歲；null ⇒ 不標當前大運、不給小運
 * @param majorCount 列幾步大運（每步十年）
 */
fun IPalmMetaModel.toPalmChartDto(desc: IBranchDesc, age: Int?, majorCount: Int = 12): PalmChartDto {
  val lang = Lang.ZH_TW
  fun starOf(b: Branch) = IPalmModel.getStar(b) + "星"
  fun titleOf(s: Scale) = s.getTitle(lang)

  val pillars = Scale.entries.map { s -> getBranch(s).let { b -> PalmPillarDto(s, titleOf(s), b, starOf(b)) } }

  val houses = Branch.entries.map { b ->
    val onIt = Scale.entries.filter { getBranch(it) == b }
    PalmHouseDto(
      branch = b,
      house = getHouse(b),
      houseName = "${getHouse(b)}宮",
      star = starOf(b),
      dao = IPalmModel.getDao(b) + "道",
      pillars = onIt,
      intro = if (onIt.isEmpty()) null else desc.getHouseIntro(b),
      readings = onIt.filter { it != Scale.HOUR }.map { s -> PalmReadingDto(s, titleOf(s), desc.getContent(s, b)) },
    )
  }

  val majors = getMajorFortunes(majorCount).map { (start, b) ->
    PalmFortuneDto(start, start + 9, b, starOf(b), age != null && age in start..start + 9)
  }

  val minor = age?.takeIf { it >= 1 }?.let { a -> getMinorFortunes(a).let { b -> PalmFortuneDto(a, a, b, starOf(b), true) } }

  val notes = buildList {
    if (monthNumber.leapMonth) {
      val counted = (monthNumber.number - 1).mod(12) + 1
      add("閏${monthNumber.lunarMonth.toChineseMonthString()}${chineseDateHour.day.toChineseDayString()}生，本盤以${counted.toChineseMonthString()}起月")
    }
  }

  val hour = getBranch(Scale.HOUR)
  return PalmChartDto(fourChars, pillars, houses, desc.getPoem(hour), desc.getContent(hour), majors, minor, notes)
}
