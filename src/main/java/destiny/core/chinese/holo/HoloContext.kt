package destiny.core.chinese.holo

import destiny.astrology.IZodiacSign
import destiny.astrology.Planet
import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.ISolarTerms
import destiny.core.calendar.SolarTerms
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.fengshui.sanyuan.IYuan
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.*
import destiny.iching.Symbol.*
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * @param threeKings : 是否考量三至尊卦 : [Hexagram.蹇] [Hexagram.坎] [Hexagram.屯]
 */
class HoloContext(private val eightWordsImpl: IEightWordsFactory,
                  private val yuanImpl: IYuan,
                  private val numberize: INumberize,
                  private val yuanGenderImpl: IYuanGander,
                  private val zodiacSignImpl: IZodiacSign,
                  private val yearSplitterImpl: IYearSplitterBySign,
                  private val seasonalSymbolImpl: ISeasonalSymbol,
                  private val solarTermsImpl: ISolarTerms,
                  override val threeKings: IHoloContext.ThreeKingsAlgo? = IHoloContext.ThreeKingsAlgo.HALF_YEAR
) : IHoloContext, Serializable {

  private fun Int.isOdd(): Boolean {
    return this % 2 == 1
  }

  private fun Int.isEven(): Boolean {
    return this % 2 == 0
  }

  @Deprecated("")
  private fun getHoloHexagram(yinYangs: List<Boolean>, yuanTangIndexFrom1: Int, initGmtJulDay: Double): HoloHexagram {
    return generateSequence { yinYangs }
      .flatten()
      .drop(yuanTangIndexFrom1 - 1) // 從「元堂」爻開始
      .withIndex()
      .map {
        // 把原本的 zero-based list index 改為 1~6
        // index : 第幾爻 (1~6)
        val index = ((it.index + yuanTangIndexFrom1) % 6).let { r -> if (r == 0) 6 else r }
        index to it.value
      }
      .take(6)
      .foldIndexed(mutableListOf<Pair<Int, HoloLine>>() ) { indexFrom0, holoLines, lineIndexAndBoolean ->
        val yinYang = if (lineIndexAndBoolean.second) YinYang.陽 else YinYang.陰
        val yuanTang: Boolean = (indexFrom0 == 0)
        val startFortuneGmtJulDay = if (holoLines.isEmpty()) initGmtJulDay else holoLines.last().second.endFortuneGmtDay

        val endFortuneGmtJulDay = generateSequence(startFortuneGmtJulDay + 0.1) {
          solarTermsImpl.getSolarTermsTime(SolarTerms.立春, it + 0.1, true)
        }
          .drop(1).take(if (lineIndexAndBoolean.second) 9 else 6)
          .last()
        val line = HoloLine(yinYang, yuanTang, startFortuneGmtJulDay, endFortuneGmtJulDay)
        holoLines.add(lineIndexAndBoolean.first to line)
        holoLines
      }
      .sortedBy { (lineIndex, line) -> lineIndex }
      .map { (lineIndex, line) -> line }
      .let { lines -> HoloHexagram(lines) }
  }

  private fun getHoloHexagramAndAgeList(yinYangs: List<Boolean>, yuanTangIndexFrom1: Int, initGmtJulDay: Double): Pair<HoloHexagram, List<Double>> {
    return generateSequence { yinYangs }
      .flatten()
      .drop(yuanTangIndexFrom1 - 1) // 從「元堂」爻開始
      .withIndex()
      .map {
        // 把原本的 zero-based list index 改為 1~6
        // index : 第幾爻 (1~6)
        val index = ((it.index + yuanTangIndexFrom1) % 6).let { r -> if (r == 0) 6 else r }
        index to it.value
      }
      .take(6)
      .foldIndexed(mutableListOf<Pair<Int, HoloLine>>() to mutableListOf<Double>()) { indexFrom0, holoLinesAndAgeEnds, lineIndexAndBoolean ->
        val yinYang = if (lineIndexAndBoolean.second) YinYang.陽 else YinYang.陰
        val yuanTang: Boolean = (indexFrom0 == 0)
        val startFortuneGmtJulDay = if (holoLinesAndAgeEnds.first.isEmpty()) initGmtJulDay else holoLinesAndAgeEnds.first.last().second.endFortuneGmtDay


        val ageEnds: List<Double> = generateSequence(startFortuneGmtJulDay + 0.1) {
          solarTermsImpl.getSolarTermsTime(SolarTerms.立春, it + 0.1, true)
        }
          .drop(1).take(if (lineIndexAndBoolean.second) 9 else 6)
          .toList()

        holoLinesAndAgeEnds.second.addAll(ageEnds)

        val endFortuneGmtJulDay = ageEnds.last()

        val line = HoloLine(yinYang, yuanTang, startFortuneGmtJulDay, endFortuneGmtJulDay)
        holoLinesAndAgeEnds.first.add(lineIndexAndBoolean.first to line)
        holoLinesAndAgeEnds
      }.let { (indexLinePair, yearlyEnds) ->

        val holoHexagram = indexLinePair.sortedBy { (lineIndex, line) -> lineIndex }
          .map { (lineIndex, line) -> line }
          .let { lines -> HoloHexagram(lines) }

        holoHexagram to yearlyEnds
      }
  }


  /**
   * 取得歲流年卦
   * @param hex 先天卦
   * @param lineIndex 元堂 (1~6)
   */
  fun getYearlyHexagrams(hex: IHexagram , lineIndex : Int) : Sequence<Pair<IHexagram, Int>> {


    // 將 爻 限制在 1~6 之間
    fun confine(line: Int): Int {
      return (line % 6).let { r -> if (r == 0) 6 else r }
    }

    fun switch(hex: IHexagram, line: Int): Pair<IHexagram, Int> {

      return (1..6)
        .map { index ->
          hex.getLine(index).let {
            if (confine(line) == index)
              !it
            else
              it
          }
        }
        .toList().let { Hexagram.of(it) to confine(line) }
    }

    return if (hex.getLine(lineIndex)) {
      // 陽爻
      generateSequence (Triple(hex , lineIndex , 1)) { triple ->
        if (triple.third < 9) {
          val toAddLine = when(triple.third) {
            1 -> 3
            2 -> 3
            else -> 1
          }
          val (a, b) = switch(triple.first, triple.second + toAddLine)
          Triple(a, b, triple.third + 1)
        } else {
          null
        }

      }.take(9)
        .map { triple -> triple.first to triple.second }
        .plus( getYearlyHexagrams(hex , confine(lineIndex+1)) )
    } else {
      // 陰爻
      generateSequence(switch(hex , lineIndex)) { pair ->
        switch(pair.first , pair.second+1)
      }.take(6).plus( getYearlyHexagrams(hex , confine(lineIndex+1)) )
    }
  }


  override fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender): Holo {

    val yuan = yuanImpl.getYuan(lmt, loc)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val ew: IEightWords = eightWordsImpl.getEightWords(lmt, loc)

    // 天數
    val heavenNumber = getHeavenNumber(ew)
    // 地數
    val earthNumber = getEarthNumber(ew)

    val sign = zodiacSignImpl.getSign(Planet.SUN, gmtJulDay)
    val yearHalfYinYang = yearSplitterImpl.getYinYang(sign)

    // 先天命卦
    val (hexagramCongenital: HoloHexagram, ageEnds1: List<Double>) = getHexagramCongenital(ew, gender, yuan, yearHalfYinYang).let { (hex, indexFrom1) ->
      getHoloHexagramAndAgeList(hex.yinYangs, indexFrom1, gmtJulDay)
    }

    val yinYang: IYinYang = threeKings?.let { algo ->
      when (algo) {
        IHoloContext.ThreeKingsAlgo.HALF_YEAR -> yearHalfYinYang
        IHoloContext.ThreeKingsAlgo.MONTH_BRANCH -> SimpleBranch[ew.month.branch]
      }
    } ?: yearHalfYinYang


    // 後天命卦
    val (hexagramAcquired: HoloHexagram, ageEnds2: List<Double>) = getHexagramAcquired(hexagramCongenital, hexagramCongenital.yuanTang, yinYang).let { (hex, indexFrom1) ->
      getHoloHexagramAndAgeList(hex.yinYangs, indexFrom1, hexagramCongenital.lines.maxBy { holoLine: HoloLine -> holoLine.endFortuneGmtDay }!!.endFortuneGmtDay)
    }

    // 每歲的結尾 , sorted
    val ageEnds = ageEnds1.plus(ageEnds2)

    // 天元氣
    val vigorousSymbolFromStem = SymbolAcquired.getSymbol(numberize.getNumber(ew.year.stem))!!
    // 地元氣
    val vigorousSymbolFromBranch = ew.year.branch.let { branch ->
      when (branch) {
        子 -> 坎
        丑, 寅 -> 艮
        卯 -> 震
        辰, 巳 -> 巽
        午 -> 離
        未, 申 -> 坤
        酉 -> 兌
        戌, 亥 -> 乾
      }
    }
    // 天元氣相反
    val vigorlessSymbolFromStem = vigorousSymbolFromStem.let { symbol -> SymbolCongenital.getOppositeSymbol(symbol) }
    // 地元氣相反
    val vigorlessSymbolFromBranch = vigorousSymbolFromBranch.let { symbol -> SymbolCongenital.getOppositeSymbol(symbol) }

    // 化工 : 得到季節力量者 , 這裡的季節，與中國的季節不同（遲了一個半月），反而與西洋的季節定義相同 : 二分二至 為 春夏秋冬的起點
    // 另外考慮了季月 艮坤 旺 18日
    val seasonalSymbols: Set<Symbol> = seasonalSymbolImpl.getSeasonalSymbol(lmt, loc)

    // 化工反例
    val seasonlessSymbols = seasonalSymbols.map { SymbolCongenital.getOppositeSymbol(it) }.toSet()

    return Holo(ew, gender, yuan, heavenNumber, earthNumber,
      hexagramCongenital, hexagramAcquired,
      vigorousSymbolFromStem, vigorousSymbolFromBranch,
      vigorlessSymbolFromStem, vigorlessSymbolFromBranch,
      seasonalSymbols, seasonlessSymbols
    )
  }

  override fun getHeavenNumber(ew: IEightWords): Int {
    return ew.stemBranches.map { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isOdd() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isOdd() }.sum()
    }.sum()
  }

  /** 天數 -> 卦 */
  override fun getHeavenSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol {

    fun shrink(value: Int): Int {
      return when {
        value > 25 -> shrink(value % 25)
        value == 25 -> 5
        else -> value % 10
      }
    }

    val value = shrink(getHeavenNumber(ew))

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, ew.year.stem)

  }

  override fun getEarthNumber(ew: IEightWords): Int {
    return ew.stemBranches.map { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isEven() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isEven() }.sum()
    }.sum()
  }

  /** 地數 -> 卦 */
  override fun getEarthSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol {

    fun shrink(value: Int): Int {
      return if (value > 30) {
        shrink(value % 30)
      } else {
        (value % 10).let { reminder ->
          if (reminder != 0)
            reminder
          else
            value / 10
        }
      }
    }

    val value = shrink(getEarthNumber(ew))

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, ew.year.stem)
  }
}