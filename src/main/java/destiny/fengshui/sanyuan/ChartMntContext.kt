/**
 * Created by smallufo on 2018-03-02.
 */
package destiny.fengshui.sanyuan

import destiny.core.TriGrid
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired

object ChartMntContext {

  fun getChartMnt(period: Int,
                  mountain: Mountain,
                  replacementImpl: IReplacement?): IChartMnt {

    val (mntStart, mntReversed) = getStart(period, mountain, replacementImpl)
    val (dirStart, dirReversed) = getStart(period, mountain.opposite, replacementImpl)


    val blocks = arrayOfNulls<ChartBlock>(10) // 0 不用
    for (i in 1..9) {
      val p = normalize(period + i - 1)
      val symbol = getBlockSymbol(i)

      val mnt = getNumber(mntStart, mntReversed, i)
      val dir = getNumber(dirStart, dirReversed, i)

      blocks[i] = ChartBlock(symbol, mnt, dir, p)
    }

    val useReplacement = replacementImpl != null

    return ChartMnt(period , mountain , useReplacement , blocks.filterNotNull())
  } // getChartMnt


  fun getPositionMap(view: Symbol): Map<TriGrid, Symbol?> {

    val grids: List<TriGrid> = generateSequence(TriGrid.B, { it: TriGrid -> it.clockWise()!! }).take(8).toList()
    val chartBlocks: List<Symbol?> =
      generateSequence(view, { it: Symbol -> SymbolAcquired.getClockwiseSymbol(it) }).take(8)
        .toList()

    return grids.zip(chartBlocks).plusElement(Pair(TriGrid.C, null)).toMap()
  } // getPositionMap


  fun getChartPresenter(period: Int ,
                        mnt : Mountain ,
                        view: Symbol,
                        replacementImpl: IReplacement? = null) : IChartMntPresenter {
    val blocks : List<ChartBlock> = getChartMnt(period, mnt, replacementImpl).blocks
    val useReplacement = replacementImpl != null
    return ChartMntPresenter(period , mnt , view , useReplacement , blocks , getPositionMap(view) )
  }


  /**
   *  取得 山、向 的運 (入中宮者) , 以及，是否逆行
   */
  private fun getStart(period: Int, mountain: Mountain, replacementImpl: IReplacement?): Pair<Int, Boolean> {
    val symbol = 地盤.getSymbol(mountain)
    val defaultStart = (1..9).first { getBlockSymbol(it) === symbol }.let { getBlockPeriod(it, period) }
    val 原始配卦: Symbol? = SymbolAcquired.getSymbolNullable(defaultStart)
    val reversed = isReversed(原始配卦, symbol, mountain)

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


  private fun isReversed(原始卦: Symbol?, 飛佈卦: Symbol, m: Mountain): Boolean {
    val reversed: Boolean
    reversed = if (原始卦 == null) {
      /**
       * 當五黃如中時，因為五黃本身沒有山向，五黃是中宮戊己土，那麼，
       * 五入中的順飛還是逆飛則是由五所在的宮位的山向的陰陽，性質決定。
       * 如一運子山午向，運星五到離，五入中為向星後，按五所在的「午」的性質決定，故逆飛。
       */
      !玄空陰陽.getYinYang(m)
    } else {
      !玄空陰陽.getYinYang(VoidFunctions.getMappingMountain(m, 原始卦))
    }
    return reversed
  }


  private fun getChartBlock(symbol: Symbol?,
                            blocks: Iterable<ChartBlock?>): ChartBlock {
    return if (symbol == null)
      blocks.first { it?.symbol === null }!!
    //blocks[1]!!
    else {
      blocks.first { it?.symbol === symbol }!!
    }
  }

  /**
   * 方向（以卦來表示）
   * |-----------------|
   * | 9巽 | 5離 | 7坤 |
   * |[0,0]|[0,1]|[0,2]|
   * |-----|-----|-----|
   * | 8震 |  1  | 3兌 |
   * |[1,0]|[1,1]|[1,2]|
   * |-----|-----|-----|
   * | 4艮 | 6坎 | 2乾 |
   * |[2,0]|[2,1]|[2,2]|
   * |-----------------|
   */
  private fun getBlockSymbol(blockIndex: Int): Symbol? {
    return when (blockIndex) {
      9 -> Symbol.巽
      8 -> Symbol.震
      4 -> Symbol.艮

      5 -> Symbol.離
      6 -> Symbol.坎

      7 -> Symbol.坤
      3 -> Symbol.兌
      2 -> Symbol.乾
      else -> null
    }
  }

  private fun getBlockPeriod(blockIndex: Int, period: Int): Int {
    return normalize(period + blockIndex - 1)
  }

  /**
   * 傳入中宮的 山 或 向  , 以及是否逆飛
   *
   * @param blockIndex 1 to 9
   */
  private fun getNumber(start: Int, converse: Boolean, blockIndex: Int): Int {
    return if (!converse) {
      // 順飛
      normalize(start + blockIndex + 8)
    } else {
      // 逆飛
      normalize(start - blockIndex + 10)
    }
  }

  /**
   * 將 int 值侷限在 1 到 9 之間
   */
  private fun normalize(value: Int): Int {
    return when {
      value > 9 -> normalize(value - 9)
      value < 1 -> normalize(value + 9)
      else -> value
    }
  }


  private val 地盤 = EarthlyCompass()
  private val 玄空陰陽 = MountainYinYangEmptyImpl()

}