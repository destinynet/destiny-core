package destiny.core.chinese.holo

import destiny.astrology.IZodiacSign
import destiny.astrology.Planet
import destiny.core.BirthDataNamePlace
import destiny.core.Gender
import destiny.core.calendar.*
import destiny.core.calendar.eightwords.EightWordsImpl
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.fengshui.sanyuan.IYuan
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.*
import destiny.iching.Symbol.*
import destiny.iching.divine.ISettingsOfStemBranch
import mu.KotlinLogging
import java.io.Serializable
import java.time.chrono.ChronoLocalDateTime

/**
 * @param threeKings : 是否考量三至尊卦 : [Hexagram.蹇] [Hexagram.坎] [Hexagram.屯]
 */
class HoloContext(val eightWordsImpl: IEightWordsFactory,
                  val yuanImpl: IYuan,
                  val numberize: INumberize,
                  val yuanGenderImpl: IYuanGender,
                  val zodiacSignImpl: IZodiacSign,
                  val yearSplitterImpl: IYearSplitterBySign,
                  val seasonalSymbolImpl: ISeasonalSymbol,
                  val solarTermsImpl: ISolarTerms,
                  val settings: ISettingsOfStemBranch,
                  private val hexSolidImpl: IHexSolid,
                  val goldenKeyProvider: IGoldenKeyProvider,
                  override val monthlyHexagramImpl: IMonthlyHexagram = MonthlyHexagramSignImpl.instance,
                  val dailyHexagramService: IDailyHexagramService,
                  override val threeKings: IHoloContext.ThreeKingsAlgo? = IHoloContext.ThreeKingsAlgo.HALF_YEAR,
                  override val hexChange: IHoloContext.HexChange = IHoloContext.HexChange.DST
) : IHoloContext, Serializable {

  private fun Int.isOdd(): Boolean {
    return this % 2 == 1
  }

  private fun Int.isEven(): Boolean {
    return this % 2 == 0
  }

  /** 將 爻 限制在 1~6 之間 */
  private fun confine(line: Int): Int {
    return (line % 6).let { r -> if (r == 0) 6 else r }
  }

  /** @param line 第幾爻變換 (1~6) */
  private fun switch(hex: IHexagram, line: Int): Pair<Hexagram, Int> {
    val confinedLine = confine(line)
    return Hexagram.of(hex.getTargetYinYangs(confinedLine)) to confinedLine
  }

  /**
   * @param hex 先天卦 or 後天卦
   * @param yuanTangIndexFrom1 元堂 (1~6)
   * @return Holo卦象 以及 出生之後每個立春的GMT時刻(亦即：原歲數截止時刻 & 新歲數開始時刻 ）
   */
  fun getHoloHexagramAndAgeList(hex: Hexagram, yuanTangIndexFrom1: Int, initGmtJulDay: Double, initStemBranch: IStemBranch): List<HoloLine> {
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
        val startFortuneGmtJulDay = if (lineIndex_holoLine.isEmpty()) initGmtJulDay else lineIndex_holoLine.last().second.endExclusive

        val stemBranchOf1stYear: IStemBranch = lineIndex_holoLine.lastOrNull()?.second?.hexagrams?.last()?.stemBranch?.next
          ?: initStemBranch

        // 每年 卦象 、元堂 以及 該年的干支
        val yearlySeq: Sequence<Triple<Hexagram, Int, IStemBranch>> = getYearlyHexagrams(hex, yuanTangIndexFrom1 + indexFrom0to5, stemBranchOf1stYear)

        // 6 or 9 條流年資訊
        val yearly: List<HoloHexagramWithStemBranch> = generateSequence(startFortuneGmtJulDay) {
          solarTermsImpl.getSolarTermsTime(SolarTerms.立春, it + 0.1, true)
        }
          .take(if (lineIndex_boolean.second.booleanValue) 9 + 1 else 6 + 1)
          .windowed(2, 1, false)
          .map { it[0] to it[1] }
          .zip(yearlySeq)
          .map { (from_to, hex_lineIndex_stemBranch) ->
            val fromGmt = from_to.first
            val toGmt = from_to.second
            val yearlyHex: Hexagram = hex_lineIndex_stemBranch.first
            val lineIndex = hex_lineIndex_stemBranch.second
            val stemBranch = hex_lineIndex_stemBranch.third
            val stemBranches = (1..6).map { settings.getStemBranch(yearlyHex, it) }.toList()
            val holoHexagram: IHoloHexagram = HoloHexagram(IHoloHexagram.Scale.YEAR, yearlyHex, lineIndex, stemBranches, fromGmt, toGmt)
            HoloHexagramWithStemBranch(holoHexagram, stemBranch)
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
   * @param stemBranch 第一年的干支
   */
  fun getYearlyHexagrams(hex: Hexagram, lineIndex: Int, stemBranch: IStemBranch): Sequence<Triple<Hexagram, Int, IStemBranch>> {

    logger.debug("getYearlyHexagrams() , hex = {} , lineIndex = {} , 年 = {}", Hexagram.of(hex), confine(lineIndex), stemBranch)

    val confinedLine = confine(lineIndex)

    return if (hex.getBoolean(confinedLine)) {
      // 陽爻
      val firstYear: Triple<Hexagram, Int, Int> = if (!stemBranch.stem.booleanValue) {
        logger.debug("陽爻，元堂，流年逢陰年 必變")
        if (hexChange == IHoloContext.HexChange.SRC) {
          Triple(switch(hex, confinedLine).first, confine(confinedLine + 3), 1)
        } else {
          switch(hex, confinedLine).let { Triple(it.first, it.second, 1) }
        }
      } else {
        logger.debug("陽爻，元堂，流年仍陽年 ") // 第一年不用變
        Triple(Hexagram.of(hex), confinedLine, 1)
      }


      /**  [IHoloContext.HexChange.SRC] 設定 */
      val srcSeq = generateSequence(firstYear) { triple ->
        when (triple.third) {
          1 -> {
            if (!stemBranch.stem.booleanValue) // 陰年
              Triple(switch(triple.first, triple.second).first, confine(triple.second + 3), triple.third + 1)
            else
              Triple(triple.first, confine(triple.second + 3), triple.third + 1) // 陽年
          }
          2 -> Triple(switch(triple.first, triple.second).first, confine(triple.second + 3), triple.third + 1)
          else -> Triple(switch(triple.first, triple.second).first, confine(triple.second + 1), triple.third + 1)
        }
      }

      /**  [IHoloContext.HexChange.DST] 設定 */


      val dstSeq = generateSequence(firstYear) { triple ->
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
      }

      val seq = if (hexChange == IHoloContext.HexChange.SRC) srcSeq else dstSeq
      seq.take(9)
        .map { triple: Triple<Hexagram, Int, Int> ->
          Triple(triple.first, triple.second, stemBranch.next(triple.third - 1))
        }
    } else {
      // 陰爻
      generateSequence(switch(hex, confinedLine)) { pair ->
        switch(pair.first, pair.second + 1)
      }
        .take(6)
        .mapIndexed { indexFrom0, hexAndLine ->
          Triple(hexAndLine.first, hexAndLine.second, stemBranch.next(indexFrom0))
        }.map { triple: Triple<Hexagram, Int, IStemBranch> ->
          if (hexChange == IHoloContext.HexChange.SRC) {
            val yearHex = switch(triple.first, triple.second).first
            Triple(yearHex, triple.second, triple.third)
          } else {
            triple
          }
        }
    }
  }

  /** 先天卦 + 後天卦 */
  override fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, name: String?, place: String?): IHolo {
    val yuan = yuanImpl.getYuan(lmt, loc)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt, loc)
    val ew: IEightWords = eightWordsImpl.getEightWords(lmt, loc)

    // 天數
    val heavenNumber = getHeavenNumber(ew)
    val heavenSymbol = getHeavenSymbol(ew, gender, yuan)
    // 地數
    val earthNumber = getEarthNumber(ew)
    val earthSymbol = getEarthSymbol(ew, gender, yuan)

    val sign = zodiacSignImpl.getSign(Planet.SUN, gmtJulDay)
    val yearHalfYinYang: IYinYang = yearSplitterImpl.getYinYang(sign)

    // 當年立春時刻
    val startOfYear = solarTermsImpl.getSolarTermsTime(SolarTerms.立春, gmtJulDay, false)

    // 先天命卦 , 以及六爻大運、流年 等資訊
    val hexagramCongenital: ILifeHoloHexagram = getHexagramCongenital(ew, gender, yuan, yearHalfYinYang).let { (hex, yuanTang) ->
      val lines: List<HoloLine> = getHoloHexagramAndAgeList(hex, yuanTang, gmtJulDay, ew.year)
      val stemBranches = (1..6).map { settings.getStemBranch(hex, it) }.toList()
      val dutyDaysMap = dailyHexagramService.getDutyDays(hex, startOfYear - 0.01, true)
      val hexSolid = hexSolidImpl.getHexagram(hex)
      LifeHoloHexagram(lines, stemBranches, dutyDaysMap, hexSolid)
    }

    val yinYang: IYinYang = threeKings?.let { algo ->
      when (algo) {
        IHoloContext.ThreeKingsAlgo.HALF_YEAR -> yearHalfYinYang
        IHoloContext.ThreeKingsAlgo.MONTH_BRANCH -> SimpleBranch[ew.month.branch]
      }
    } ?: yearHalfYinYang


    // 後天命卦 , 以及六爻大運、流年 等資訊
    val hexagramAcquired: ILifeHoloHexagram = getHexagramAcquired(hexagramCongenital, hexagramCongenital.yuanTang, yinYang).let { (hex, yuanTang) ->
      // 先天卦的最後一爻
      val congMaxLine: HoloLine = hexagramCongenital.lines.maxBy { it.endExclusive }!!

      val lines: List<HoloLine> = getHoloHexagramAndAgeList(hex, yuanTang, congMaxLine.endExclusive, congMaxLine.hexagrams.last().stemBranch.next)
      val stemBranches = (1..6).map { settings.getStemBranch(hex, it) }.toList()
      val dutyDaysMap = dailyHexagramService.getDutyDays(hex, startOfYear - 0.01, true)
      val hexSolid = hexSolidImpl.getHexagram(hex)
      LifeHoloHexagram(lines, stemBranches, dutyDaysMap, hexSolid)
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

    // 位於哪兩個「節或氣」之間
    val solarTermsPos: SolarTermsTimePos = solarTermsImpl.getSolarTermsPosition(gmtJulDay)


    val birthData = BirthDataNamePlace(gender, lmt, loc, name, place)

    // 四節氣卦
    val seasonalHexagram: Pair<Hexagram, Int> = seasonHexMap.getValue(solarTermsPos.solarTerms).let { pair ->
      Hexagram.of(pair.first, pair.first) to pair.second
    }

    // 12消息卦
    val monthlyHexagram: Hexagram = monthlyHexagramImpl.getHexagram(solarTermsPos.solarTerms).first

    // 當下值日卦列表
    val dailyHexagramMap: Map<IDailyHexagram, Hexagram> = dailyHexagramService.getHexagramMap(gmtJulDay).map { (k, v) ->
      k to v.first
    }.toMap()

    // 金鎖銀匙歌 參評歌訣
    val goldenKey = goldenKeyProvider.getBaseGoldenKey(NaYin.getFiveElement(ew.year), ew.day.branch, ew.hour.branch, gender)

    return Holo(birthData, ew, gender, yuan, solarTermsPos,
      heavenNumber, heavenSymbol,
      earthNumber, earthSymbol,
      hexagramCongenital, hexagramAcquired,
      vigorousSymbolFromStem, vigorousSymbolFromBranch,
      vigorlessSymbolFromStem, vigorlessSymbolFromBranch,
      seasonalSymbols, seasonlessSymbols,
      seasonalHexagram, monthlyHexagram,
      dailyHexagramMap,
      goldenKey)
  } // getHolo(inner)

  /**
   * 列出當年 12 個月的流月卦象
   * @param yearHexagram 當年卦象
   */
  override fun getMonthlyHexagrams(yearHexagram: IHoloHexagramWithStemBranch): List<IHoloHexagramWithStemBranch> {
    // 立春開始
    val springStart = yearHexagram.start
    return SolarTerms.values()
      .filter { it.major }  // 只要「節」即可 , 共取出 12 節 , from 立春 to 小寒
      .asSequence()
      .foldIndexed(mutableListOf()) { indexFrom0, list, solarTerms ->
        val lastOddHex: IHoloHexagramWithStemBranch = if (list.isEmpty()) yearHexagram else {
          if (list.size % 2 == 1)
            list.last()
          else
            list.dropLast(1).last()
        }

        val lastHex = if (list.isEmpty()) yearHexagram else list.last()

        val (hex, yuanTang, start) = if (indexFrom0 % 2 == 0) {
          // 單月 (立春 ...)
          val (hex, yuanTang) = switch(lastOddHex, lastOddHex.yuanTang + 1)
          val start: Double = if (list.isEmpty()) springStart else list.last().endExclusive
          Triple(hex, yuanTang, start)
        } else {
          // 雙月 (驚蟄 ...)
          val (hex, yuanTang) = switch(lastHex, lastHex.yuanTang + 3)
          val start: Double = list.last().endExclusive
          Triple(hex, yuanTang, start)
        }

        val stemBranches: List<StemBranch> = (1..6).map { settings.getStemBranch(hex, it) }.toList()
        val end: Double = solarTermsImpl.getSolarTermsTime(solarTerms.next().next(), start, true)
        val holoHex = HoloHexagram(IHoloHexagram.Scale.MONTH, hex, yuanTang, stemBranches, start, end)
        // 五虎遁年起月訣
        val monthStem = StemBranchUtils.getMonthStem(yearHexagram.stemBranch.stem, solarTerms.branch)
        list.add(HoloHexagramWithStemBranch(holoHex, StemBranch[monthStem, solarTerms.branch]))
        list
      }
  }


  /**
   * 取得當下 gmt 時刻的「月份」卦象
   * @param yearStem 該年天干
   * @param yearHexagram 該年卦象
   * @param yearYuanTang 該年元堂
   */
  override fun getMonthlyHexagram(yearStem: Stem, yearHexagram: IHexagram, yearYuanTang: Int, gmt: Double): IHoloHexagramWithStemBranch {
    val solarTerms: SolarTerms = solarTermsImpl.getSolarTermsFromGMT(gmt)

    val monthNum = solarTerms.branch.getAheadOf(寅) + 1 // 1~12

    val list: List<Pair<IHexagram, Int>> = SolarTerms.values()
      .filter { it.major }  // 只要「節」即可 , 共取出 12 節 , from 立春 to 小寒
      .asSequence()
      .foldIndexed(mutableListOf<Pair<IHexagram, Int>>()) { indexFrom0, list, _ ->
        val lastOddHex: Pair<IHexagram, Int> = if (list.isEmpty()) yearHexagram to yearYuanTang else {
          if (list.size % 2 == 1)
            list.last()
          else
            list.dropLast(1).last()
        }

        val lastHex: Pair<IHexagram, Int> = if (list.isEmpty()) yearHexagram to yearYuanTang else list.last()

        val (hex, yuanTang) = if (indexFrom0 % 2 == 0) {
          // 單月 (立春 ...)
          switch(lastOddHex.first, lastOddHex.second + 1)
        } else {
          // 雙月 (驚蟄 ...)
          switch(lastHex.first, lastHex.second + 3)
        }
        list.add(hex to yuanTang)
        list
      }.toList()

    val (monthHex: IHexagram, monthYuanTang: Int) = list[monthNum - 1]

    val stemBranches = (1..6).map { settings.getStemBranch(monthHex, it) }
    val (start, end) = solarTermsImpl.getMajorSolarTermsGmtBetween(gmt).let { (from, to) ->
      from.second to to.second
    }

    val holoHexagram = HoloHexagram(IHoloHexagram.Scale.MONTH, monthHex, monthYuanTang, stemBranches, start, end)
    // 五虎遁年起月訣
    val monthStem = StemBranchUtils.getMonthStem(yearStem, solarTerms.branch)
    return HoloHexagramWithStemBranch(holoHexagram, StemBranch[monthStem, solarTerms.branch])
  } // 流月


  /**
   * 取得當下時刻的流日卦象
   * @param monthHexagram 流月卦
   * @param monthYuanTang 流月元堂 (index start from 1)
   * @param viewGmt 當下的 GMT 時刻
   */
  override fun getDailyHexagram(monthHexagram: IHexagram, monthYuanTang: Int, viewGmt: Double, loc: ILocation): IHoloHexagramWithStemBranch {

    // 計算此時刻，處於何節氣中 , 開始為何時
    val (solarTerms, startGmt) = solarTermsImpl.getMajorSolarTermsGmtBetween(viewGmt).first

    val dayImpl = (eightWordsImpl as EightWordsImpl).dayHourImpl
    val startSB = dayImpl.getDay(startGmt, loc)
    val viewSB = dayImpl.getDay(viewGmt, loc)
    val diffDays: Int = viewSB.getAheadOf(startSB) + 1 // 沒有第零日 , 「節」當日也算第一日
    logger.debug("從 {} 到 {} , diffDays = {}", startSB, viewSB, diffDays)

    val (dayHex, dayYuanTang) = generateSequence(monthHexagram to confine(monthYuanTang + 1)) {
      Pair(Hexagram.of(monthHexagram), confine(it.second + 1))
    }.flatMap { pair ->
      logger.debug("pair = {}", pair)
      generateSequence(switch(pair.first, pair.second).first to 1) {
        it.first to confine(it.second + 1)
      }.take(6)
    }.take(diffDays).last()

    val stemBranches = (1..6).map { settings.getStemBranch(dayHex, it) }

    val hourImpl = eightWordsImpl.dayHourImpl.hourImpl

    val start = if (dayImpl.changeDayAfterZi) {
      hourImpl.getGmtPrevStartOf(viewGmt, loc, 子)
    } else {
      dayImpl.midnightImpl.getPrevMidnight(viewGmt, loc)
    }
    val end = if (dayImpl.changeDayAfterZi) {
      hourImpl.getGmtNextStartOf(viewGmt, loc, 子)
    } else {
      dayImpl.midnightImpl.getNextMidnight(viewGmt, loc)
    }

    val holoHex = HoloHexagram(IHoloHexagram.Scale.DAY, dayHex, dayYuanTang, stemBranches, start, end)

    val daySb = dayImpl.getDay(viewGmt, loc)

    return HoloHexagramWithStemBranch(holoHex, daySb)
  } // 流日

  /** 傳回 本命先後天卦、以及此 gmt 時刻 的大運、流年、流月 等資訊 */
  override fun getHoloWithTime(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender, gmt: Double, name: String?, place: String?): Pair<IHolo, List<IHoloHexagram>> {

    val holo = getHolo(lmt, loc, gender, name, place)
    val congenitalLines: List<HoloLine> = holo.hexagramCongenital.lines
    val acquiredLines: List<HoloLine> = holo.hexagramAcquired.lines

    // 現在處於 先天卦 or 後天卦 當中 , FIXME : 排久遠之前的盤，會有問題
    val mainHexagram: IHoloHexagram? = holo.hexagramCongenital.takeIf {
      it.contains(gmt)
    } ?: holo.hexagramAcquired.takeIf {
      it.contains(gmt)
    }

    // 此時刻的大運 (6 or 9年) 的 卦象(with 元堂)
    val majorHexagram: IHoloHexagramWithStemBranch? = (holo.hexagramCongenital.let { holoHexagram: IHoloHexagram ->
      // 先從先天卦找起
      val lineIndex = congenitalLines.indexOfFirst {
        it.contains(gmt)
      } + 1
      Triple(holoHexagram, lineIndex, if (lineIndex > 0) congenitalLines[lineIndex - 1] else null)

    }.takeIf { (_, lineIndex) -> lineIndex > 0 }
      ?: holo.hexagramAcquired.let { holoHexagram: IHoloHexagram ->
        // 再到後天卦尋找
        val lineIndex = acquiredLines.indexOfFirst {
          it.contains(gmt)
        } + 1
        Triple(holoHexagram, lineIndex, if (lineIndex > 0) acquiredLines[lineIndex - 1] else null)
      }.takeIf { (_, lineIndex) -> lineIndex > 0 }
      )?.let { (hex, lineIndex, line: HoloLine?) ->
      // 大運的干支 , 指的是 先後天卦，走到哪一爻, 該爻的納甲
      val stemBranch = settings.getStemBranch(hex, lineIndex)
      val stemBranches = (1..6).map { settings.getStemBranch(hex, it) }.toList()
      val start = line!!.hexagrams.minBy { it.start }!!.start
      val end = line.hexagrams.maxBy { it.endExclusive }!!.endExclusive
      val holoHex = HoloHexagram(IHoloHexagram.Scale.MAJOR, hex, lineIndex, stemBranches, start, end)
      HoloHexagramWithStemBranch(holoHex, stemBranch)
    }

    // 此時刻的流年卦象 (with 元堂)
    val yearlyHexagram: IHoloHexagramWithStemBranch? = congenitalLines.plus(acquiredLines).flatMap { holoLine ->
      // 每條 holoLine 存放 6 or 9 條 流年資訊
      holoLine.hexagrams
    }.firstOrNull { hex: IHoloHexagramWithStemBranch ->
      hex.contains(gmt)
    }?.let { hex: IHoloHexagramWithStemBranch ->
      // 流年干支
      val stemBranch = hex.stemBranch
      val stemBranches = (1..6).map { settings.getStemBranch(hex, it) }.toList()
      val holoHex = HoloHexagram(IHoloHexagram.Scale.YEAR, hex, hex.yuanTang, stemBranches, hex.start, hex.endExclusive)
      HoloHexagramWithStemBranch(holoHex, stemBranch)
    }

    // 流月 (depends on 流年)
    val monthlyHexagram: IHoloHexagramWithStemBranch? = yearlyHexagram?.let { yearly: IHoloHexagramWithStemBranch ->
      getMonthlyHexagram(yearly.stemBranch.stem, yearly, yearly.yuanTang, gmt)
    }

    // 流日
    val dailyHexagram: IHoloHexagramWithStemBranch? = monthlyHexagram?.let { monthly ->
      getDailyHexagram(monthly, monthly.yuanTang, gmt, loc).also {
        logger.debug("流日 , start = {}", it.start)
      }
    }

    val list: List<IHoloHexagram> = mutableListOf<IHoloHexagram>().apply {
      mainHexagram?.also { this.add(it) }
      majorHexagram?.also { this.add(it) }
      yearlyHexagram?.also { this.add(it) }
      monthlyHexagram?.also { this.add(it) }
      dailyHexagram?.also { this.add(it) }
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
        else -> (value % 10).let { reminder ->
          if (reminder != 0)
            reminder
          else
            value / 10
        }
        //else -> (value % 10)
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
    private val logger = KotlinLogging.logger { }
  }
}
