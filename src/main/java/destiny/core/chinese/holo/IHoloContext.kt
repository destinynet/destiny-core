package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.calendar.ILocation
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.Branch
import destiny.core.chinese.IYinYang
import destiny.core.chinese.Stem
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.Hexagram
import destiny.iching.IHexagram
import destiny.iching.Symbol
import java.lang.RuntimeException
import java.time.chrono.ChronoLocalDateTime

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

  /** 取得 先天卦、後天卦 , 元氣、化工 等資訊 */
  fun getHolo(lmt: ChronoLocalDateTime<*>, loc:ILocation, gender: Gender) : IHolo


  /**
   * 列出當年 12 個月的流月卦象
   * @param yearHexagram 當年卦象
   */
  fun getMonthlyHexagrams(yearHexagram: IHoloHexagramWithStemBranch): List<IHoloHexagramWithStemBranch>

  /**
   * 取得當下 gmt 時刻的「月份」卦象
   * @param yearStem 該年天干
   * @param yearHexagram 該年卦象
   * @param yearYuanTang 該年元堂
   */
  fun getMonthlyHexagram(yearStem : Stem, yearHexagram : IHexagram, yearYuanTang : Int, gmt: Double) : IHoloHexagramWithStemBranch

  /** 除了傳回 本命先後天卦，另外傳回 以及此 gmt 時刻 的大運、流年、流月 等資訊 */
  fun getHoloWithTime(lmt: ChronoLocalDateTime<*> , loc:ILocation , gender: Gender , gmt:Double) : Pair<IHolo , List<IHoloHexagram>>

  /** 天數 */
  fun getHeavenNumber(ew: IEightWords): Int

  /** 天數 -> 卦 */
  fun getHeavenSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol

  /** 地數 */
  fun getEarthNumber(ew: IEightWords): Int

  /** 地數 -> 卦 */
  fun getEarthSymbol(ew: IEightWords, gender: Gender, yuan: Yuan): Symbol

  /**
   * 本命卦 (先天卦 without 元堂)
   * */
  fun getHexagram(ew: IEightWords, gender: Gender, yuan: Yuan): Hexagram {
    val heavenSymbol = getHeavenSymbol(ew, gender, yuan)
    val earthSymbol = getEarthSymbol(ew, gender, yuan)

    return if ((gender == Gender.男 && ew.year.stem.booleanValue) || (gender == Gender.女 && !ew.year.stem.booleanValue)) {
      Hexagram.of(heavenSymbol, earthSymbol)
    } else {
      Hexagram.of(earthSymbol, heavenSymbol)
    }
  }



  /**
   * 元堂 , return 1(incl.) to 6(incl.)
   * @param yearHalfYinYang 切割年份的陰陽， 常例為 前半年(冬至 to 夏至) 為 陽 , 後半年(夏至 to 冬至) 為 陰
   * */
  fun getYuanTang(hexagram: IHexagram, hour: Branch, gender: Gender, yearHalfYinYang: IYinYang): Int {
    // 幾個陽爻
    val yangCount = hexagram.booleans.count { it }
    // 幾個陰爻
    val yinCount = hexagram.booleans.count { !it }

    val yangSeq: Sequence<Int> = hexagram.booleans.zip(1..6)
      .filter { (yinYang, indexFrom1) -> yinYang }
      .map { pair -> pair.second }
      .let { list -> generateSequence { list }.flatten() }

    val yinSeq = hexagram.booleans.zip(1..6)
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
          in Branch.子..Branch.丑 -> yangSeq.take(hour.index + 1).last()
          else -> yinSeq.take(hour.index - 1).last()
        }
        2 -> when (hour) {
          in Branch.子..Branch.卯 -> yangSeq.take(hour.index + 1).last()
          else -> yinSeq.take(hour.index + 1).last()
        }
        3 -> when (hour) {
          in Branch.子..Branch.巳 -> yangSeq.take(hour.index + 1).last()
          else -> throw RuntimeException("error")
        }
        4 -> when (hour) {
          in Branch.子..Branch.卯 -> yangSeq.take(hour.index + 1).last()
          else -> yinSeq.take(hour.index + 1).last()
        }
        5 -> when (hour) {
          in Branch.子..Branch.辰 -> yangSeq.take(hour.index + 1).last()
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
          in Branch.午..Branch.未 -> yinSeq.first()
          else -> yangSeq.take(hour.index - 7).last()
        }
        2 -> when (hour) {
          in Branch.午..Branch.酉 -> yinSeq.take(hour.index - 5).last()
          else -> yangSeq.take(hour.index - 9).last()
        }
        3 -> when (hour) {
          in Branch.午..Branch.亥 -> yinSeq.take(hour.index - 5).last()
          else -> throw RuntimeException("error")
        }
        4 -> when (hour) {
          in Branch.午..Branch.酉 -> yinSeq.take(hour.index - 5).last()
          else -> yangSeq.take(hour.index - 9).last()
        }
        5 -> when (hour) {
          in Branch.午..Branch.戌 -> yinSeq.take(hour.index - 5).last()
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

  /** 先天卦 (with 元堂 : 1~6) */
  fun getHexagramCongenital(ew: IEightWords, gender: Gender, yuan: Yuan, yearHalfYinYang: IYinYang): Pair<Hexagram, Int> {
    if (yearHalfYinYang.booleanValue) {
      require(!(Branch.未..Branch.亥).contains(ew.month.branch)) {
        "前半年，月支 ${ew.month.branch} 不可以出現在 未~亥 當中"
      }
    } else {
      require(!(Branch.丑..Branch.巳).contains(ew.month.branch)) {
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
  fun getHexagramAcquired(hexagramCongenital: IHexagram, yuanTang: Int, yinYang: IYinYang): Pair<Hexagram, Int> {

    val newYuanTang = ((yuanTang + 3) % 6).let {
      if (it == 0)
        6
      else
        it
    }

    return hexagramCongenital.booleans.mapIndexed { index, booleanValue ->
      if (index == yuanTang - 1)
        !booleanValue
      else
        booleanValue
    }.let { list ->
      Hexagram.of(list).let { hex ->
        val upperSymbol = hex.upperSymbol
        val lowerSymbol = hex.lowerSymbol

        if (threeKings != null && threeKingHexagrams.contains(hexagramCongenital) && (yuanTang == 5 || yuanTang == 6)) {
          /** 三至尊卦 : [Hexagram.坎] [Hexagram.屯] [Hexagram.蹇] , 且 , 元堂 為 5 or 6 */
          if (yuanTang == 5) {
            if (yinYang.booleanValue) {
              Hexagram.of(lowerSymbol, upperSymbol) to newYuanTang
            } else {
              // 爻變，上下不動
              Hexagram.of(upperSymbol, lowerSymbol) to yuanTang
            }
          } else {
            // 6
            if (yinYang.booleanValue) {
              // 爻變，上下不動
              Hexagram.of(upperSymbol, lowerSymbol) to yuanTang
            } else {
              Hexagram.of(lowerSymbol, upperSymbol) to newYuanTang
            }
          }

        } else {
          // 正常狀況 上下交換
          Hexagram.of(lowerSymbol, upperSymbol) to newYuanTang
        }
      }
    }
  } // 後天卦




  companion object {
    val threeKingHexagrams = setOf(Hexagram.坎, Hexagram.屯, Hexagram.蹇)
  }

}