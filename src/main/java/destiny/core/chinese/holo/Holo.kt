/**
 * Created by smallufo on 2019-02-27.
 */
package destiny.core.chinese.holo

import destiny.core.Gender
import destiny.core.calendar.eightwords.IEightWords
import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.IYuanGander
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.fengshui.sanyuan.Yuan
import destiny.iching.Hexagram
import destiny.iching.IHexagram
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import java.io.Serializable
import java.lang.RuntimeException

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

interface IHoloContext {
  // 天數
  fun getYangSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol

  // 地數
  fun getYinSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol

  fun getHexagram(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): IHexagram {
    val yangSymbol = getYangSymbol(ew, gender, yuan, yinYang)
    val yinSymbol = getYinSymbol(ew, gender, yuan, yinYang)
    return if ((gender == Gender.男 && yinYang) || (gender == Gender.女 && !yinYang)) {
      Hexagram.getHexagram(yangSymbol, yinSymbol)
    } else {
      Hexagram.getHexagram(yinSymbol, yangSymbol)
    }
  }

  /** 元堂 , return 1(incl.) to 6(incl.) */
  fun getYuanTang(hexagram: IHexagram, hour: Branch): Int {
    val yangCount = hexagram.yinYangs.count { it }
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
        else -> TODO()
      }
    } else {
      // 午~亥 , 後六時 陰
      when (yinCount) {
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
        else -> TODO()
      }
    }


  }
}

class HoloContext(private val numberize: INumberize,
                  private val yuanGenderImpl: IYuanGander) : IHoloContext, Serializable {

  private fun Int.isOdd(): Boolean {
    return this % 2 == 1
  }

  private fun Int.isEven(): Boolean {
    return this % 2 == 0
  }

  override fun getYangSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol {
    val sum = ew.stemBranches.map { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isOdd() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isOdd() }.sum()
    }.sum()

    val value = when {
      sum > 25 -> sum % 25
      sum == 25 -> 5
      else -> sum % 10
    }

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, yinYang)

  }

  override fun getYinSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol {
    val sum = ew.stemBranches.map { sb ->
      (numberize.getNumber(sb.stem).takeIf { it.isEven() } ?: 0) +
        numberize.getNumber(sb.branch).filter { it.isEven() }.sum()
    }.sum()

    val value = when {
      sum > 20 -> sum % 30
      sum == 30 -> 3
      else -> sum % 10
    }

    return SymbolAcquired.getSymbol(value) ?: yuanGenderImpl.getSymbol(gender, yuan, yinYang)
  }


}