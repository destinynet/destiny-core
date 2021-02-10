/**
 * Created by smallufo on 2018-03-02.
 */
package destiny.core.fengshui.sanyuan

import destiny.core.TriGrid
import destiny.core.fengshui.EarthlyCompass
import destiny.core.fengshui.Mountain
import destiny.core.fengshui.MountainYinYangEmptyImpl
import destiny.core.fengshui.sanyuan.FlyingStar.symbolPeriods
import destiny.core.iching.Symbol
import destiny.core.iching.SymbolAcquired

object ChartMntContext {

  /** 取得 [IChartMnt] 的實作 [ChartMnt] */
  fun getChartMnt(period: Int,
                  mountain: Mountain,
                  replacementImpl: IReplacement? = null): IChartMnt {

    val (mntStart, mntReversed) = getStart(period, mountain, replacementImpl)
    val (dirStart, dirReversed) = getStart(period, mountain.opposite, replacementImpl)

    val blocks = symbolPeriods.filterNotNull().map { symbol ->
      val steps = symbolPeriods.indexOf(symbol)
      val p = FlyingStar.getValue(period, steps, false)
      val mnt = FlyingStar.getValue(mntStart, steps, mntReversed)
      val dir = FlyingStar.getValue(dirStart, steps, dirReversed)
      ChartBlock(symbol , mnt , dir , p)
    }.toList()
      .plus(ChartBlock(null , mntStart , dirStart , period)) // 中宮

    val useReplacement = replacementImpl != null

    return ChartMnt(period, mountain, useReplacement, blocks)
  } // getChartMnt


  /** 取得 [IChartMntPresenter] 的實作 [ChartMntPresenter] */
  fun getChartPresenter(period: Int,
                        mnt : Mountain,
                        view: Symbol,
                        replacementImpl: IReplacement? = null) : IChartMntPresenter {
    val blocks : List<ChartBlock> = getChartMnt(period, mnt, replacementImpl).blocks
    val useReplacement = replacementImpl != null
    return ChartMntPresenter(period, mnt, view, useReplacement, blocks, TriGrid.getGridMap(view , SymbolAcquired))
  }


  /**
   *  取得 山、向 的運 (入中宮者) , 以及，是否逆行
   */
  private fun getStart(period: Int, mountain: Mountain, replacementImpl: IReplacement?): Pair<Int, Boolean> {
    val symbol: Symbol = 地盤.getSymbol(mountain)

    val defaultStart = (period + symbolPeriods.indexOf(symbol)).let {
      if (it > 9)
        it - 9
      else
        it
    }

    // 原始配卦
    val srcSymbol: Symbol? = SymbolAcquired.getSymbolNullable(defaultStart)
    val reversed = isReversed(srcSymbol, mountain)

    if (replacementImpl == null) {
      // 不用替星
      return Pair(defaultStart, reversed)
    } else {
      // 用替星
      return if (srcSymbol == null) {
        Pair(defaultStart, reversed)
      } else {
        val mnt = VoidFunctions.getMappingMountain(mountain, srcSymbol)
        val repStar: ReplacementStar = replacementImpl.getReplacementStar(mnt)
        if (!repStar.enabled) {
          Pair(defaultStart, reversed)
        } else {
          val newStart = repStar.period
          Pair(newStart, reversed)
        }
      }
    }
  }


  private fun isReversed(
    /** 原始卦 */
    sourceSymbol: Symbol?,
    m: Mountain
  ): Boolean {
    return if (sourceSymbol == null) {
      /**
       * 當五黃如中時，因為五黃本身沒有山向，五黃是中宮戊己土，那麼，
       * 五入中的順飛還是逆飛則是由五所在的宮位的山向的陰陽，性質決定。
       * 如一運子山午向，運星五到離，五入中為向星後，按五所在的「午」的性質決定，故逆飛。
       */
      !玄空陰陽.getYinYang(m)
    } else {
      !玄空陰陽.getYinYang(VoidFunctions.getMappingMountain(m, sourceSymbol))
    }
  }




  private val 地盤 = EarthlyCompass()
  private val 玄空陰陽 = MountainYinYangEmptyImpl()

}
