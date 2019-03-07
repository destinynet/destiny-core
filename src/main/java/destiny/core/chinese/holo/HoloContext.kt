package destiny.core.chinese.holo

import destiny.astrology.IZodiacSign
import destiny.astrology.Planet
import destiny.core.Gender
import destiny.core.calendar.*
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.fengshui.sanyuan.IYuan
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.*
import destiny.iching.Symbol.*
import org.slf4j.LoggerFactory
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
                  override val threeKings: IHoloContext.ThreeKingsAlgo? = IHoloContext.ThreeKingsAlgo.HALF_YEAR) : IHoloContext, Serializable {

  /** 流年卦 */
  private data class HoloYearlyHexagram(
    val stemBranch: IStemBranch,
    val hexagram: Hexagram,
    val yuanTang: Int,
    val start: Double,
    val end: Double) : IHexagram by hexagram

  /** 先天卦 or 後天卦 的單一爻 , 內含 6年 or 9年 的流年資料結構 */
  private data class HoloLine(val yinYang: IYinYang,
                              val yuanTang: Boolean,
                              val yearly: List<HoloYearlyHexagram>) {
    val startGmtJulDay: Double
      get() = yearly.minBy { it.start }!!.start

    val endGmtJulDay: Double
      get() = yearly.maxBy { it.end }!!.end
  }

  private fun Int.isOdd(): Boolean {
    return this % 2 == 1
  }

  private fun Int.isEven(): Boolean {
    return this % 2 == 0
  }

  /**
   * @param hex 先天卦 or 後天卦
   * @param yuanTangIndexFrom1 元堂 (1~6)
   * @return Holo卦象 以及 出生之後每個立春的GMT時刻(亦即：原歲數截止時刻 & 新歲數開始時刻 ）
   */
  private fun getHoloHexagramAndAgeList(hex: Hexagram, yuanTangIndexFrom1: Int, initGmtJulDay: Double, initStemBranch: IStemBranch): List<HoloLine> {
    return generateSequence { hex.yinYangs }
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
      .foldIndexed(mutableListOf<Pair<Int, HoloLine>>()) { indexFrom0to5, lineIndex_holoLine, lineIndex_boolean ->
        val yinYang = lineIndex_boolean.second
        val yuanTang: Boolean = (indexFrom0to5 == 0)
        val startFortuneGmtJulDay = if (lineIndex_holoLine.isEmpty()) initGmtJulDay else lineIndex_holoLine.last().second.endGmtJulDay

        val stemBranchOf1stYear: IStemBranch = lineIndex_holoLine.lastOrNull()?.second?.yearly?.last()?.stemBranch?.next
          ?: initStemBranch

        val yearlySeq: Sequence<Triple<Hexagram, Int, IStemBranch>> = getYearlyHexagrams(hex, yuanTangIndexFrom1 + indexFrom0to5, stemBranchOf1stYear)

        val yearly: List<HoloYearlyHexagram> = generateSequence(startFortuneGmtJulDay) {
          solarTermsImpl.getSolarTermsTime(SolarTerms.立春, it + 0.1, true)
        }
          .take(if (lineIndex_boolean.second.booleanValue) 9 + 1 else 6 + 1)
          .windowed(2, 1, false)
          .map { it[0] to it[1] }
          .zip(yearlySeq)
          .map { (from_to, hex_lineIndex_stemBranch) ->
            val fromGmt = from_to.first
            val toGmt = from_to.second
            val yearlyHex = hex_lineIndex_stemBranch.first
            val lineIndex = hex_lineIndex_stemBranch.second
            val stemBranch = hex_lineIndex_stemBranch.third
            HoloYearlyHexagram(stemBranch, yearlyHex, lineIndex, fromGmt, toGmt)
          }.toList()

        val line = HoloLine(yinYang, yuanTang, yearly)
        lineIndex_holoLine.add(lineIndex_boolean.first to line)
        lineIndex_holoLine
      }
      .sortedBy { (lineIndex, _) -> lineIndex }
      .map { (_, holoLine) -> holoLine }
  }


  /**
   * 取得流年卦 , 陽爻 取 9 年， 陰爻 取 6 年
   * @param hex 先天卦
   * @param lineIndex 元堂 (1~6)
   */
  fun getYearlyHexagrams(hex: Hexagram, lineIndex: Int, stemBranch: IStemBranch): Sequence<Triple<Hexagram, Int, IStemBranch>> {

    // 將 爻 限制在 1~6 之間
    fun confine(line: Int): Int {
      return (line % 6).let { r -> if (r == 0) 6 else r }
    }

    logger.debug("getYearlyHexagrams() , hex = {} , lineIndex = {} , 年 = {}", Hexagram.of(hex), confine(lineIndex), stemBranch)

    // 第幾爻變換
    fun switch(hex: Hexagram, line: Int): Pair<Hexagram, Int> {
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

    val confinedLine = confine(lineIndex)

    return if (hex.getLine(confinedLine)) {
      // 陽爻
      val firstYear: Triple<Hexagram, Int, Int> = if (!stemBranch.stem.booleanValue) {
        logger.debug("陽爻，元堂，流年逢陰年 ")
        switch(hex, confinedLine).let { Triple(it.first, it.second, 1) }
      } else {
        logger.debug("陽爻，元堂，流年仍陽年 ")
        Triple(Hexagram.of(hex), confinedLine, 1)
      }

      generateSequence(firstYear) { triple ->
        if (triple.third < 9) {
          val toAddLine = when (triple.third) {
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
        .map { triple: Triple<Hexagram, Int, Int> -> Triple(triple.first, triple.second, stemBranch.next(triple.third - 1)) }
    } else {
      // 陰爻
      generateSequence(switch(hex, confinedLine)) { pair ->
        switch(pair.first, pair.second + 1)
      }.take(6)
        .mapIndexed { indexFrom0, hexAndLine -> Triple(hexAndLine.first, hexAndLine.second, stemBranch.next(indexFrom0)) }
    }
  }

  /** 先天卦 (with 元堂) */
  private fun getHoloInner(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender): Triple<IHolo, List<HoloLine>, List<HoloLine>> {
    val yuan = yuanImpl.getYuan(lmt, loc)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val ew: IEightWords = eightWordsImpl.getEightWords(lmt, loc)

    // 天數
    val heavenNumber = getHeavenNumber(ew)
    // 地數
    val earthNumber = getEarthNumber(ew)

    val sign = zodiacSignImpl.getSign(Planet.SUN, gmtJulDay)
    val yearHalfYinYang = yearSplitterImpl.getYinYang(sign)

    // 先天命卦 , 以及六爻大運、流年 等資訊
    val (hexagramCongenital: IHoloHexagram, congenitalLines: List<HoloLine>) = getHexagramCongenital(ew, gender, yuan, yearHalfYinYang).let { (hex, yuanTang) ->
      val lines: List<HoloLine> = getHoloHexagramAndAgeList(hex, yuanTang, gmtJulDay, ew.year)
      val start = lines.minBy { line -> line.startGmtJulDay }!!.startGmtJulDay
      val end = lines.maxBy { line -> line.endGmtJulDay }!!.endGmtJulDay
      HoloHexagram(IHoloHexagram.Scale.LIFE, hex, yuanTang, start, end) to lines
    }

    val yinYang: IYinYang = threeKings?.let { algo ->
      when (algo) {
        IHoloContext.ThreeKingsAlgo.HALF_YEAR -> yearHalfYinYang
        IHoloContext.ThreeKingsAlgo.MONTH_BRANCH -> SimpleBranch[ew.month.branch]
      }
    } ?: yearHalfYinYang


    // 後天命卦 , 以及六爻大運、流年 等資訊
    val (hexagramAcquired: IHoloHexagram, acquiredLines: List<HoloLine>) = getHexagramAcquired(hexagramCongenital, hexagramCongenital.yuanTang, yinYang).let { (hex, yuanTang) ->
      val maxLine: HoloLine = congenitalLines.maxBy { holoLine: HoloLine -> holoLine.endGmtJulDay }!!

      val lines: List<HoloLine> = getHoloHexagramAndAgeList(hex, yuanTang, maxLine.endGmtJulDay, maxLine.yearly.last().stemBranch.next)

      val start = lines.minBy { line -> line.startGmtJulDay }!!.startGmtJulDay
      val end = lines.maxBy { line -> line.endGmtJulDay }!!.endGmtJulDay
      HoloHexagram(IHoloHexagram.Scale.LIFE, hex, yuanTang, start, end) to lines
    }


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

    val holo = Holo(ew, gender, yuan, heavenNumber, earthNumber, hexagramCongenital, hexagramAcquired, vigorousSymbolFromStem, vigorousSymbolFromBranch, vigorlessSymbolFromStem, vigorlessSymbolFromBranch, seasonalSymbols, seasonlessSymbols)
    return Triple(holo, congenitalLines, acquiredLines)
  } // getHolo(inner)


  override fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender): IHolo {
    return getHoloInner(lmt, loc, gender).first
  }

  /** 包含 大運、流年、流月 等資訊 */
  override fun getHoloWithTime(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, gmt: Double): Pair<IHolo, List<IHoloHexagram>> {

    val (holo, congenitalLines, acquiredLines) = getHoloInner(lmt, loc, gender)

    // 現在處於 先天卦 or 後天卦 當中
    val mainHexagram: IHoloHexagram? = holo.hexagramCongenital.takeIf {
      it.start <= gmt && gmt < it.end
    } ?: holo.hexagramAcquired.takeIf {
      it.start <= gmt && gmt < it.end
    }

    // 此時刻的大運 (6 or 9年) 的 卦象(with 元堂)
    val majorHexagram: HoloHexagram? = (holo.hexagramCongenital.let { holoHexagram: IHoloHexagram ->
      // 先從先天卦找起
      val lineIndex = congenitalLines.indexOfFirst {
        it.startGmtJulDay <= gmt && gmt < it.endGmtJulDay
      } + 1
      Triple(holoHexagram.hexagram, lineIndex, if (lineIndex > 0) congenitalLines[lineIndex - 1] else null)

    }.takeIf { (_, lineIndex) -> lineIndex > 0 }
      ?: holo.hexagramAcquired.let { holoHexagram: IHoloHexagram ->
        // 再到後天卦尋找
        val lineIndex = acquiredLines.indexOfFirst {
          it.startGmtJulDay <= gmt && gmt < it.endGmtJulDay
        } + 1
        Triple(holoHexagram.hexagram, lineIndex, if (lineIndex > 0) congenitalLines[lineIndex - 1] else null)
      }.takeIf { (_, lineIndex) -> lineIndex > 0 }
      )?.let { (hex, lineIndex, line) ->
      val start = line!!.yearly.minBy { it.start }!!.start
      val end = line.yearly.maxBy { it.end }!!.end
      HoloHexagram(IHoloHexagram.Scale.MAJOR, hex, lineIndex, start, end)
    }

    // 此時刻的流年卦象 (with 元堂)
    val yearlyHexagram: HoloHexagram? = congenitalLines.plus(acquiredLines).flatMap { holoLine ->
      holoLine.yearly
    }.firstOrNull { yearly ->
      yearly.start <= gmt && gmt < yearly.end
    }?.let { yearly ->
      HoloHexagram(IHoloHexagram.Scale.YEAR, yearly.hexagram, yearly.yuanTang, yearly.start, yearly.end)
    }

    val list: List<IHoloHexagram> = mutableListOf<IHoloHexagram>().apply {
      mainHexagram?.also { this.add(it) }
      majorHexagram?.also { this.add(it) }
      yearlyHexagram?.also { this.add(it) }
    }.toList()

    return holo to list
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

  companion object {
    private val logger = LoggerFactory.getLogger(HoloContext::class.java)
  }
}