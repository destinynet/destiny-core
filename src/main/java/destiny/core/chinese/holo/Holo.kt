/**
 * Created by smallufo on 2019-02-27.
 */
package destiny.core.chinese.holo

import destiny.astrology.IZodiacSign
import destiny.astrology.Planet
import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.TimeTools
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.calendar.eightwords.IEightWordsFactory
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.fengshui.sanyuan.IYuan
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.*
import destiny.iching.Symbol.*
import java.io.Serializable
import java.lang.RuntimeException
import java.time.chrono.ChronoLocalDateTime

interface INumberize {
  fun getNumber(stem: Stem): Int
  fun getNumber(branch: Branch): Set<Int>
}

class NumberizeImpl : INumberize, Serializable {
  override fun getNumber(stem: Stem): Int {
    return when (stem) {
      甲, 壬 -> 6
      乙, 癸 -> 2
      丙 -> 8
      丁 -> 7
      戊 -> 1
      己 -> 9
      庚 -> 3
      辛 -> 4
    }
  }

  override fun getNumber(branch: Branch): Set<Int> {
    return when (branch) {
      子, 亥 -> setOf(1, 6)
      寅, 卯 -> setOf(3, 8)
      巳, 午 -> setOf(2, 7)
      申, 酉 -> setOf(4, 9)
      辰, 戌, 丑, 未 -> setOf(5, 10)
    }
  }
}


data class Holo(
  val ew: IEightWords,

  val gender: Gender,

  val yuan: Yuan,

  /** 先天卦 , with 元堂 (1~6) */
  val hexagramCongenital: Pair<IHexagram, Int>,

  /** 後天卦 , with 元堂 (1~6) */
  val hexagramAcquired: Pair<IHexagram, Int>,

  /** 天元氣 */
  val vigorousSymbolFromStem: Symbol,

  /** 地元氣 */
  val vigorousSymbolFromBranch: Symbol,

  /** 天元氣相反 */
  val vigorlessSymbolFromStem: Symbol,

  /** 地元氣相反 */
  val vigorlessSymbolFromBranch: Symbol,

  /**
   * 化工 : 得到季節力量者 , 這裡的季節，與中國的季節不同（遲了一個半月），反而與西洋的季節定義相同 : 二分二至 為 春夏秋冬的起點
   * 另外考慮了季月 艮坤 旺 18日
   */
  val seasonalSymbols: Set<Symbol>,

  /** 化工反例 */
  val seasonlessSymbols: Set<Symbol>
)

interface IHoloContext {

  enum class ThreeKingsAlgo {
    /** 前半年(冬至 to 夏至) 為 陽 , 後半年(夏至 to 冬至) 為 陰 */
    HALF_YEAR,
    /** 月令地支 的陰陽 */
    MONTH_BRANCH
  }

  /**
   * 取後天卦 [getHexagramAcquired] 時 ， 是否考量三至尊卦 : [Hexagram.坎] [Hexagram.屯] [Hexagram.蹇]
   */
  val threeKings: ThreeKingsAlgo?

  fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender): Holo

  /** 天數 -> 卦 */
  fun getYangSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol

  /** 地數 -> 卦 */
  fun getYinSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol

  /**
   * 本命卦
   * */
  fun getHexagram(ew: IEightWords, gender: Gender, yuan: Yuan): IHexagram {
    val yangSymbol = getYangSymbol(ew, gender, yuan)
    val yinSymbol = getYinSymbol(ew, gender, yuan)

    return if ((gender == Gender.男 && ew.year.stem.booleanValue) || (gender == Gender.女 && !ew.year.stem.booleanValue)) {
      Hexagram.getHexagram(yangSymbol, yinSymbol)
    } else {
      Hexagram.getHexagram(yinSymbol, yangSymbol)
    }
  }

  /**
   * 元堂 , return 1(incl.) to 6(incl.)
   * @param yearHalfYinYang 切割年份的陰陽， 常例為 前半年(冬至 to 夏至) 為 陽 , 後半年(夏至 to 冬至) 為 陰
   * */
  fun getYuanTang(hexagram: IHexagram, hour: Branch, gender: Gender, yearHalfYinYang: IYinYang): Int {
    // 幾個陽爻
    val yangCount = hexagram.yinYangs.count { it }
    // 幾個陰爻
    val yinCount = hexagram.yinYangs.count { !it }

    val yangSeq: Sequence<Int> = hexagram.yinYangs.zip(1..6)
      .filter { (yinYang, indexFrom1) -> yinYang }
      .map { pair -> pair.second }
      .let { list -> generateSequence { list }.flatten() }

    val yinSeq = hexagram.yinYangs.zip(1..6)
      .filter { (yinYang, indexFrom1) -> !yinYang }
      .map { pair -> pair.second }
      .let { list -> generateSequence { list }.flatten() }

    return if (hour.index <= 5) {
      // 子~巳 , 前六時 陽

      when (yangCount) {
        0 -> {
          // 六陰爻
          when (gender) {
            Gender.男 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(1, 2, 3, 1, 2, 3)[hour.index]
              } else {
                // 後半年
                listOf(6, 5, 4, 6, 5, 4)[hour.index]
              }
            }
            Gender.女 -> {
              listOf(1, 2, 3, 1, 2, 3)[hour.index]
            }
          }
        }
        1 -> when (hour) {
          in 子..丑 -> yangSeq.take(hour.index + 1).last()
          else -> yinSeq.take(hour.index - 1).last()
        }
        2 -> when (hour) {
          in 子..卯 -> yangSeq.take(hour.index + 1).last()
          else -> yinSeq.take(hour.index + 1).last()
        }
        3 -> when (hour) {
          in 子..巳 -> yangSeq.take(hour.index + 1).last()
          else -> throw RuntimeException("error")
        }
        4 -> when (hour) {
          in 子..卯 -> yangSeq.take(hour.index + 1).last()
          else -> yinSeq.take(hour.index + 1).last()
        }
        5 -> when (hour) {
          in 子..辰 -> yangSeq.take(hour.index + 1).last()
          else -> yinSeq.first()
        }
        else -> {
          // 六陽爻
          when (gender) {
            Gender.男 -> {
              listOf(1, 2, 3, 1, 2, 3)[hour.index]
            }
            Gender.女 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(6, 5, 4, 6, 5, 4)[hour.index]
              } else {
                // 後半年
                listOf(1, 2, 3, 1, 2, 3)[hour.index]
              }
            }
          }
        }
      }
    } else {
      // 午~亥 , 後六時 陰
      when (yinCount) {
        0 -> {
          // 六陽爻
          when (gender) {
            Gender.男 -> {
              listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
            }
            Gender.女 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(3, 2, 1, 3, 2, 1)[hour.index - 6]
              } else {
                // 後半年
                listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
              }
            }
          }
        }
        1 -> when (hour) {
          in 午..未 -> yinSeq.first()
          else -> yangSeq.take(hour.index - 7).last()
        }
        2 -> when (hour) {
          in 午..酉 -> yinSeq.take(hour.index - 5).last()
          else -> yangSeq.take(hour.index - 9).last()
        }
        3 -> when (hour) {
          in 午..亥 -> yinSeq.take(hour.index - 5).last()
          else -> throw RuntimeException("error")
        }
        4 -> when (hour) {
          in 午..酉 -> yinSeq.take(hour.index - 5).last()
          else -> yangSeq.take(hour.index - 9).last()
        }
        5 -> when (hour) {
          in 午..戌 -> yinSeq.take(hour.index - 5).last()
          else -> yangSeq.first()
        }
        else -> {
          // 六陰爻
          when (gender) {
            Gender.男 -> {
              if (yearHalfYinYang.booleanValue) {
                // 前半年
                listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
              } else {
                // 後半年
                listOf(3, 2, 1, 3, 2, 1)[hour.index - 6]
              }
            }
            Gender.女 -> {
              listOf(4, 5, 6, 4, 5, 6)[hour.index - 6]
            }
          }
        }
      }
    }


  }


  /** 先天卦 (with 元堂) */
  fun getHexagramCongenital(ew: IEightWords, gender: Gender, yuan: Yuan, yearHalfYinYang: IYinYang): Pair<IHexagram, Int> {
    if (yearHalfYinYang.booleanValue) {
      require(!(未..亥).contains(ew.month.branch)) {
        "前半年，月支 ${ew.month.branch} 不可以出現在 未~亥 當中"
      }
    } else {
      require(!(丑..巳).contains(ew.month.branch)) {
        "後半年，月支 ${ew.month.branch} 不可以出現在 丑~巳 當中"
      }
      // 冬至、夏至 平分 子月、午月 ，無法從八字中判斷
    }
    val hex = getHexagram(ew, gender, yuan)
    val yuanTang = getYuanTang(hex, ew.hour.branch, gender, yearHalfYinYang)
    return hex to yuanTang
  }

  /**
   * 後天卦 (with 元堂)
   * @param yinYang 在「三至尊卦」 [threeKingHexagrams] 的情形下，陰陽如何判別？ 是依據切割年份的陰陽 ( [ThreeKingsAlgo.HALF_YEAR] ) , 或是依據 月份地支 判斷的陰陽 ( [ThreeKingsAlgo.MONTH_BRANCH] )
   *  */
  fun getHexagramAcquired(hexagramCongenital: IHexagram, yuanTang: Int, yinYang: IYinYang): Pair<IHexagram, Int> {

    val newYuanTang = ((yuanTang + 3) % 6).let {
      if (it == 0)
        6
      else
        it
    }

    return hexagramCongenital.yinYangs.mapIndexed { index, booleanValue ->
      if (index == yuanTang - 1)
        !booleanValue
      else
        booleanValue
    }.let { list ->
      Hexagram.getHexagram(list).let { hex ->
        val upperSymbol = hex.upperSymbol
        val lowerSymbol = hex.lowerSymbol

        if (threeKings != null && threeKingHexagrams.contains(hexagramCongenital) && (yuanTang == 5 || yuanTang == 6)) {
          /** 三至尊卦 : [Hexagram.坎] [Hexagram.屯] [Hexagram.蹇] , 且 , 元堂 為 5 or 6 */
          if (yuanTang == 5) {
            if (yinYang.booleanValue) {
              Hexagram.getHexagram(lowerSymbol, upperSymbol) to newYuanTang
            } else {
              // 爻變，上下不動
              Hexagram.getHexagram(upperSymbol, lowerSymbol) to yuanTang
            }
          } else {
            // 6
            if (yinYang.booleanValue) {
              // 爻變，上下不動
              Hexagram.getHexagram(upperSymbol, lowerSymbol) to yuanTang
            } else {
              Hexagram.getHexagram(lowerSymbol, upperSymbol) to newYuanTang
            }
          }

        } else {
          // 正常狀況 上下交換
          Hexagram.getHexagram(lowerSymbol, upperSymbol) to newYuanTang
        }
      }
    }
  }

  companion object {
    val threeKingHexagrams = setOf(Hexagram.坎, Hexagram.屯, Hexagram.蹇)
  }

}


/**
 * @param threeKings : 是否考量三至尊卦 : [Hexagram.蹇] [Hexagram.坎] [Hexagram.屯]
 */
class HoloContext(private val eightWordsImpl: IEightWordsFactory,
                  private val yuanImpl : IYuan,
                  private val numberize: INumberize,
                  private val yuanGenderImpl: IYuanGander,
                  private val zodiacSignImpl : IZodiacSign,
                  private val yearSplitterImpl: IYearSplitterBySign,
                  private val seasonalSymbolImpl: ISeasonalSymbol,
                  override val threeKings: IHoloContext.ThreeKingsAlgo? = IHoloContext.ThreeKingsAlgo.HALF_YEAR
) : IHoloContext, Serializable {

  private fun Int.isOdd(): Boolean {
    return this % 2 == 1
  }

  private fun Int.isEven(): Boolean {
    return this % 2 == 0
  }

  override fun getHolo(lmt: ChronoLocalDateTime<*>, loc: ILocation, gender: Gender): Holo {

    val yuan = yuanImpl.getYuan(lmt , loc)

    val gmtJulDay = TimeTools.getGmtJulDay(lmt , loc)
    val ew: IEightWords = eightWordsImpl.getEightWords(lmt, loc)

    val sign = zodiacSignImpl.getSign(Planet.SUN, gmtJulDay)
    val yearHalfYinYang = yearSplitterImpl.getYinYang(sign)

    val hexagramCongenital: Pair<IHexagram, Int> = getHexagramCongenital(ew, gender, yuan, yearHalfYinYang)

    val yinYang: IYinYang = threeKings?.let { algo ->
      when (algo) {
        IHoloContext.ThreeKingsAlgo.HALF_YEAR -> yearHalfYinYang
        IHoloContext.ThreeKingsAlgo.MONTH_BRANCH -> SimpleBranch[ew.month.branch]
      }
    } ?: yearHalfYinYang

    val hexagramAcquired: Pair<IHexagram, Int> = getHexagramAcquired(hexagramCongenital.first, hexagramCongenital.second, yinYang)

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

    return Holo(ew, gender, yuan, hexagramCongenital, hexagramAcquired,
      vigorousSymbolFromStem, vigorousSymbolFromBranch, vigorlessSymbolFromStem, vigorlessSymbolFromBranch,
      seasonalSymbols , seasonlessSymbols
    )
  }

  /** 天數 -> 卦 */
  override fun getYangSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol {
    val sum = ew.stemBranches.map { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isOdd() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isOdd() }.sum()
    }.sum()


    fun shrink(value : Int) : Int {
      return when {
        value > 25 -> shrink(value % 25)
        value == 25 -> 5
        else -> value % 10
      }
    }

    val value = shrink(sum)

    println("天數 sum = $sum , value = $value")

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, ew.year.stem)

  }

  /** 地數 -> 卦 */
  override fun getYinSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol {
    val sum = ew.stemBranches.map { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isEven() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isEven() }.sum()
    }.sum()


    fun shrink(value : Int) : Int {
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

    val value = shrink(sum)

    println("地數 sum = $sum , value = $value")

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, ew.year.stem)
  }


}