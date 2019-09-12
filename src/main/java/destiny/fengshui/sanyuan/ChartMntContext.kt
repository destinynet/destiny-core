/**
 * Created by smallufo on 2018-03-02.
 */
package destiny.fengshui.sanyuan

import destiny.core.TriGrid
import destiny.fengshui.sanyuan.FlyingStar.symbolPeriods
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired

object ChartMntContext {

  /** 取得 [IChartMnt] 的實作 [ChartMnt] */
  fun getChartMnt(period: Int,
                  mountain: Mountain,
                  replacementImpl: IReplacement? = null): IChartMnt {

    val (mntStart, mntReversed) = getStart(period, mountain, replacementImpl)
    val (dirStart, dirReversed) = getStart(period, mountain.opposite, replacementImpl)

    val blocks = symbolPeriods.filterNotNull().map { symbol ->
      val steps = symbolPeriods.indexOf(symbol)
      val p = FlyingStar.getValue(period , steps , false)
      val mnt = FlyingStar.getValue(mntStart , steps , mntReversed)
      val dir = FlyingStar.getValue(dirStart , steps , dirReversed)
      ChartBlock(symbol , mnt , dir , p)
    }.toList()
      .plus(ChartBlock(null , mntStart , dirStart , period)) // 中宮

    val useReplacement = replacementImpl != null

    return ChartMnt(period, mountain, useReplacement, blocks)
  } // getChartMnt


  private fun getGridMap(view: Symbol): Map<TriGrid, Symbol?> {

    val grids: List<TriGrid> = generateSequence(TriGrid.B) { it.clockWise()!! }.take(8).toList()
    val chartBlocks: List<Symbol?> =
      generateSequence(view) { SymbolAcquired.getClockwiseSymbol(it) }.take(8)
        .toList()

    return grids.zip(chartBlocks).plusElement(Pair(TriGrid.C, null)).toMap()
  } // getGridMap


  /** 取得 [IChartMntPresenter] 的實作 [ChartMntPresenter] */
  fun getChartPresenter(period: Int ,
                        mnt : Mountain ,
                        view: Symbol,
                        replacementImpl: IReplacement? = null) : IChartMntPresenter {
    val blocks : List<ChartBlock> = getChartMnt(period, mnt, replacementImpl).blocks
    val useReplacement = replacementImpl != null
    return ChartMntPresenter(period, mnt, view, useReplacement, blocks, getGridMap(view))
  }


  /**
   *  取得 山、向 的運 (入中宮者) , 以及，是否逆行
   */
  private fun getStart(period: Int, mountain: Mountain, replacementImpl: IReplacement?): Pair<Int, Boolean> {
    val symbol: Symbol = 地盤.getSymbol(mountain)

    val defaultStart = (period + symbolPeriods.indexOf(symbol)).let {
      if (it > 9)
        return@let it - 9
      else
        return@let it
    }

    val 原始配卦: Symbol? = SymbolAcquired.getSymbolNullable(defaultStart)
    val reversed = isReversed(原始配卦, mountain)

    if (replacementImpl == null) {
      // 不用替星
      return Pair(defaultStart, reversed)
    } else {
      // 用替星
      return if (原始配卦 == null) {
        Pair(defaultStart, reversed)
      } else {
        val mnt = VoidFunctions.getMappingMountain(mountain, 原始配卦)
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


  private fun isReversed(原始卦: Symbol?, m: Mountain): Boolean {
    return if (原始卦 == null) {
      /**
       * 當五黃如中時，因為五黃本身沒有山向，五黃是中宮戊己土，那麼，
       * 五入中的順飛還是逆飛則是由五所在的宮位的山向的陰陽，性質決定。
       * 如一運子山午向，運星五到離，五入中為向星後，按五所在的「午」的性質決定，故逆飛。
       */
      !玄空陰陽.getYinYang(m)
    } else {
      !玄空陰陽.getYinYang(VoidFunctions.getMappingMountain(m, 原始卦))
    }
  }




  private val 地盤 = EarthlyCompass()
  private val 玄空陰陽 = MountainYinYangEmptyImpl()

}