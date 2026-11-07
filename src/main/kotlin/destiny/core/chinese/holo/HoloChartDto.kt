/**
 * Created by smallufo on 2026-11-06.
 */
package destiny.core.chinese.holo

import destiny.core.calendar.GmtJulDay
import destiny.core.chinese.StemBranch
import destiny.core.chinese.StemBranchYearTool
import destiny.core.iching.Hexagram
import destiny.core.iching.IHexData
import destiny.core.iching.Symbol
import kotlinx.serialization.Serializable

/**
 * 河洛理數給前端的盤：天地數、元氣、化工、先天卦與後天卦（各含六爻大運與逐年流年）以及詩詞、終身卦斷語。
 *
 * 卦名、干支等字樣都已填好；前端只負責畫，不自己對照（卦畫由 [HoloHexDto.lines] 決定）。
 * 先天＋後天只涵蓋有限的歲數（陽爻 9 年、陰爻 6 年，合計約 75–105 年），之後沒有資料。
 */
@Serializable
data class HoloChartDto(
  /** 天數（奇數和）與其所化之卦 */
  val heavenNumber: Int,
  val heavenSymbol: Symbol,
  /** 地數（偶數和）與其所化之卦 */
  val earthNumber: Int,
  val earthSymbol: Symbol,
  /** 天元氣（年干）、地元氣（年支） */
  val vigorousFromStem: Symbol,
  val vigorousFromBranch: Symbol,
  /** 天元氣、地元氣之反 */
  val vigorlessFromStem: Symbol,
  val vigorlessFromBranch: Symbol,
  /** 化工：得季節之力的卦，及其反 */
  val seasonal: List<Symbol>,
  val seasonless: List<Symbol>,
  /** 先天卦 */
  val congenital: HoloLifeDto,
  /** 後天卦 */
  val acquired: HoloLifeDto,
)

/** 一個卦象 */
@Serializable
data class HoloHexDto(
  val hexagram: Hexagram,
  /** 卦的全名（「乾為天」） */
  val name: String,
  /** 六爻陰陽，初爻在前；true ＝ 陽 */
  val lines: List<Boolean>,
  /** 元堂在第幾爻（1–6） */
  val yuanTang: Int,
)

/** 先天卦或後天卦 */
@Serializable
data class HoloLifeDto(
  val hex: HoloHexDto,
  /** 河洛理數 64 卦訣：卦詩 */
  val poem: List<String>,
  /** 六爻的爻詩，初爻在前（size = 6） */
  val linePoems: List<List<String>>,
  /** 終身卦斷語：卦 */
  val lifeDesc: String,
  /** 終身卦斷語：六爻，初爻在前（size = 6） */
  val lineDescs: List<String>,
  /** 六爻大運，依時間先後（由元堂爻起） */
  val majors: List<HoloMajorDto>,
)

/** 一爻的大運：陽爻 9 年、陰爻 6 年 */
@Serializable
data class HoloMajorDto(
  /** 第幾爻（1–6） */
  val line: Int,
  val yang: Boolean,
  /** 起訖西元年（含） */
  val startYear: Int,
  val endYear: Int,
  /** 起訖虛歲（含） */
  val startAge: Int,
  val endAge: Int,
  /** 觀看時刻落在這一運 */
  val current: Boolean,
  val years: List<HoloYearDto>,
)

/** 一年的流年卦 */
@Serializable
data class HoloYearDto(
  /** 立春起算的西元年 */
  val westYear: Int,
  /** 虛歲：出生那年（出生到第一個立春）為 1 */
  val age: Int,
  /** 流年干支 */
  val stemBranch: StemBranch,
  val hex: HoloHexDto,
  /** 流年卦元堂爻的爻詩 */
  val poem: List<String>,
  /** 觀看時刻落在這一年 */
  val current: Boolean,
)

/**
 * [IHolo] → [HoloChartDto]。
 *
 * @param birthYear 出生時刻（當地）的西元年；與八字年柱比對，得出「立春起算」的出生年
 * @param now 觀看時刻，用來標示當前大運與流年
 * @param fullName 卦的全名（`IHexNameFull`）
 * @param poemOf 河洛理數 64 卦訣（`IPoemProvider`）
 * @param lifeDescOf 終身卦斷語（`ILifeDescProvider`）
 */
fun IHolo.toHoloChartDto(
  birthYear: Int,
  now: GmtJulDay,
  fullName: (Hexagram) -> String,
  poemOf: (Hexagram) -> IHexData<List<String>, ILinePoem>,
  lifeDescOf: (Hexagram) -> IHexData<String, String>,
): HoloChartDto {

  // 出生在立春前（一月、二月初），年柱還是前一年
  val firstYear = if (StemBranchYearTool.getYearStemBranch(birthYear) == StemBranch[ew.year.stem, ew.year.branch]) birthYear else birthYear - 1

  // 先天、後天所有流年依時間排，第 k 年 ⇒ 西元 firstYear + k、虛歲 k + 1
  val ordinal: Map<GmtJulDay, Int> = (hexagramCongenital.lines + hexagramAcquired.lines)
    .flatMap { it.hexagrams }
    .map { it.start }
    .sorted()
    .withIndex()
    .associate { (i, start) -> start to i }

  fun hexDto(hex: Hexagram, yuanTang: Int) = HoloHexDto(hex, fullName(hex), hex.booleans, yuanTang)

  fun yearDto(y: IHoloHexagramWithStemBranch): HoloYearDto {
    val k = ordinal.getValue(y.start)
    val hex = Hexagram.of(y)
    return HoloYearDto(
      westYear = firstYear + k,
      age = k + 1,
      stemBranch = StemBranch[y.stemBranch.stem, y.stemBranch.branch],
      hex = hexDto(hex, y.yuanTang),
      poem = poemOf(hex).getLine(y.yuanTang).poems,
      current = y.contains(now),
    )
  }

  fun lifeDto(life: ILifeHoloHexagram): HoloLifeDto {
    val hex = Hexagram.of(life)
    val poem = poemOf(hex)
    val desc = lifeDescOf(hex)
    val majors = life.lines.withIndex()
      .sortedBy { (_, line) -> line.start }
      .map { (i, line) ->
        val years = line.hexagrams.sortedBy { it.start }.map(::yearDto)
        HoloMajorDto(
          line = i + 1,
          yang = line.booleanValue,
          startYear = years.first().westYear,
          endYear = years.last().westYear,
          startAge = years.first().age,
          endAge = years.last().age,
          current = line.contains(now),
          years = years,
        )
      }
    return HoloLifeDto(
      hex = hexDto(hex, life.yuanTang),
      poem = poem.hexValue,
      linePoems = (1..6).map { poem.getLine(it).poems },
      lifeDesc = desc.hexValue,
      lineDescs = (1..6).map { desc.getLine(it) },
      majors = majors,
    )
  }

  return HoloChartDto(
    heavenNumber = heavenNumber,
    heavenSymbol = heavenSymbol,
    earthNumber = earthNumber,
    earthSymbol = earthSymbol,
    vigorousFromStem = vigorousSymbolFromStem,
    vigorousFromBranch = vigorousSymbolFromBranch,
    vigorlessFromStem = vigorlessSymbolFromStem,
    vigorlessFromBranch = vigorlessSymbolFromBranch,
    seasonal = seasonalSymbols.toList(),
    seasonless = seasonlessSymbols.toList(),
    congenital = lifeDto(hexagramCongenital),
    acquired = lifeDto(hexagramAcquired),
  )
}
