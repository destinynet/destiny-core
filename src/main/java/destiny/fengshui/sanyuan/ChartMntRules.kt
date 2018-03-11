/**
 * Created by smallufo on 2018-03-06.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import kotlin.math.abs

object ChartMntRules {


  fun contTriplet(chart: IChartMnt): ChartRule.連珠三般卦? {
    fun distance(v1:Int , v2:Int) : Int {
      return abs(v1 - v2).let { abs ->
        return@let when {
          abs <= 6 ->  abs
          abs == 8 -> 1 // 1 & 9
          else -> 2 // 7(1,8 or 2,9)
        }
      }
    }

    return Symbol.values().all {
      chart.getChartBlockFromSymbol(it).let { block ->
        distance(block.period , block.mnt) <=2 && distance(block.period , block.dir) <= 2 && distance(block.mnt , block.dir) <=2
      }
    }.let {
      if (it) ChartRule.連珠三般卦 else null
    }
  }

  fun parentTriplet(chart: IChartMnt): ChartRule.父母三般卦? {
    val set1 = setOf(1,4,7)
    val set2 = setOf(2,5,8)
    val set3 = setOf(3,6,9)

    return Symbol.values().all  {
      val blockNums: Set<Int> = chart.getChartBlockFromSymbol(it).let {
        setOf(it.period , it.mnt , it.dir)
      }
      blockNums.containsAll(set1) || blockNums.containsAll(set2) || blockNums.containsAll(set3)
    }.let {
      if (it) ChartRule.父母三般卦 else null
    }
  }

  /**
   * 全局合十 :
   * 各宮得天盤 與 山盤(或向盤) 相加都為 10
   *
   * 範例：
   * 七運，子山午向 , 山盤 與 天盤 相加 均為 10
  ４１　８６　６８
  巽六　離二　坤四
  　　　　　　　　
  ５９　３２　１４
  震五　中七　兌九
  　　　　　　　　
  ９５　７７　２３
  艮一　坎三　乾八
   *  */
  fun match10(chart: IChartMnt): ChartRule.合十? {
    return when {
      chart.blocks.all { block -> block.period + block.mnt == 10 } -> ChartRule.合十(MntDir.山)
      chart.blocks.all { block -> block.period + block.dir == 10 } -> ChartRule.合十(MntDir.向)
      else -> null
    }
  }


  /**
   * 全局伏吟 (元旦盤) :
   * 五入中 順飛 , 形成 與後天八卦 相同的盤勢， 稱為 伏吟
   * 例如：
   *
  7 運 , 庚山 甲向 , 向盤分佈 呈現與洛書相同 , 故 , 全局伏吟(元旦盤)

  ８４　４９　６２
  巽六　離二　坤四
  　　　　　　　　
  ７３　９５　２７
  震五　中七　兌九
  　　　　　　　　
  ３８　５１　１６
  艮一　坎三　乾八
   * */
  fun beneathSameOrigin(chart: IChartMnt): ChartRule.伏吟元旦盤? {
    return when {
      chart.blocks.all { block -> SymbolAcquired.getSymbolNullable(block.mnt) == block.symbol } ->
        ChartRule.伏吟元旦盤(MntDir.山)
      chart.blocks.all { block ->
        SymbolAcquired.getSymbolNullable(block.dir) == block.symbol
      } -> ChartRule.伏吟元旦盤(
        MntDir.向)
      else -> null
    }
  }


  /**
   * 全局反吟 : (山或向) 五入中 , 逆飛 , 形成 山盤（或向盤）全局反吟
   * 例如：
   *
  七運，卯山酉向 , 山盤 5入中 , 逆飛 , 形成 山盤與洛書盤對沖

  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八
   * */
  fun reversed(chart: IChartMnt): ChartRule.反吟? {
    return chart.getCenterBlock().let { cb ->
      val block巽 = chart.getChartBlockFromSymbol(Symbol.巽)
      if (cb.mnt == 5 && block巽.mnt == 6)
        ChartRule.反吟(MntDir.山)
      else if (cb.dir == 5 && block巽.dir == 6)
        ChartRule.反吟(MntDir.向)
      else
        null
    }
  }


  // ================================ 單宮 ================================

  /**
   * 單宮合十
   */
  fun match10(chartBlock: ChartBlock) : BlockRule.合十? {
    return chartBlock.period.let {
      when {
        it + chartBlock.mnt == 10 -> BlockRule.合十(MntDir.山)
        it + chartBlock.dir == 10 -> BlockRule.合十(MntDir.向)
        else -> null
      }
    }
  }

  /**
   * 單宮伏吟(元旦盤) :
   * 山盤 或 向盤 飛星數 與 座落宮位的洛書數相同
   *
   * 七運，卯山酉向

  |--山盤伏吟(這裡不討論) , 由 [beneathSameSky] 實作
  V
  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７ <-- 7 即為 兌宮洛書數， 伏吟(元旦盤)
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八  <-- 向盤 伏吟 (這裡不討論) , 由 [beneathSameSky] 實作

   */
  fun beneathSameOrigin(chartBlock: ChartBlock): BlockRule.伏吟元旦盤? {
    return chartBlock.let {
      return@let when {
        SymbolAcquired.getSymbol(it.mnt) === it.symbol -> BlockRule.伏吟元旦盤(MntDir.山)
        SymbolAcquired.getSymbol(it.dir) === it.symbol -> BlockRule.伏吟元旦盤(MntDir.向)
        else -> null
      }
    }
  }

  /**
   * 單宮伏吟(天盤) :
   * 天盤的數字與 山盤 或 向盤 飛星相同
   *
   * 七運，卯山酉向

  |--山盤伏吟
  V
  ６１　１５　８３
  巽六　離二　坤四
  　　　　　　　　
  ７２　５９　３７ <-- 7 即為 兌宮洛書數，這裡也是另一種伏吟 , 由 [beneathSameOrigin] 實作
  震五　中七　兌九
  　　　　　　　　
  ２６　９４　４８
  艮一　坎三　乾八  <-- 向盤 伏吟

   * */
  fun beneathSameSky(chartBlock: ChartBlock): BlockRule.伏吟天盤? {
    return chartBlock.let {
      return@let when {
        it.period == it.mnt -> BlockRule.伏吟天盤(MntDir.山)
        it.period == it.dir -> BlockRule.伏吟天盤(MntDir.向)
        else -> null
      }
    }
  }


  /**
   * 單宮 反吟 (元旦盤) :
   * 某宮位內， 山 或 向 的飛星與元旦盤（洛書數）合十
   *
   * 八運，卯山酉向
   *
  　　　|-- 山星 1 + 離(9) = 10 , 山盤 反吟
  　　　v
  ５２　１６　３４
  巽七　離三　坤五
  　　　　　　　　
  ４３　６１　８８
  震六　中八　兌一
  　　　　　　　　
  ９７　２５　７９
  艮二　坎四　乾九
   */
  fun reversed(block: ChartBlock): BlockRule.反吟元旦盤? {
    return block.symbol?.let { symbol ->
      when {
        (SymbolAcquired.getIndex(symbol) + block.mnt) == 10 -> BlockRule.反吟元旦盤(MntDir.山)
        (SymbolAcquired.getIndex(symbol) + block.dir) == 10 -> BlockRule.反吟元旦盤(MntDir.向)
        else -> null
      }
    }
  }

}