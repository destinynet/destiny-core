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
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import java.io.Serializable

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
  fun getUpperSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol
  fun getLowerSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol
}

class HoloContext(private val numberize: INumberize,
                  private val yuanGenderImpl: IYuanGander) : IHoloContext, Serializable {

  private fun Int.isOdd(): Boolean {
    return this % 2 == 1
  }

  private fun Int.isEven(): Boolean {
    return this % 2 == 0
  }

  override fun getUpperSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol {
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

  override fun getLowerSymbol(ew: IEightWords, gender: Gender, yuan: Yuan, yinYang: Boolean): Symbol {
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