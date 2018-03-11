/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.fengshui.sanyuan

import destiny.core.TriGrid
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import java.io.Serializable


interface IPeriod {
  // 元運 , 其值只能為 1~9
  val period: Int
}

enum class MntDirSpec {
  到山到向 ,
  上山下水 ,
  雙星到山 ,
  雙星到向
}

enum class Gate {
  正城門,
  副城門
}

interface IChartMnt : IPeriod {
  // 座山
  val mnt: Mountain
  // 是否用替
  val replacement: Boolean
  /** 9個 [ChartBlock] */
  val blocks: List<ChartBlock>

  fun getChartBlockFromSymbol(symbol: Symbol?): ChartBlock {
    return blocks.first { it.symbol === symbol }
  }

  fun getCenterBlock() : ChartBlock {
    return getChartBlockFromSymbol(null)
  }

  /** 取得 四種 山向格局 (or null if 替星) */
  fun getMntDirSpec() : MntDirSpec? {
    val 地盤 = EarthlyCompass()
    val mntBlock: ChartBlock = getChartBlockFromSymbol(地盤.getSymbol(mnt))
    val dirBlock: ChartBlock = getChartBlockFromSymbol(地盤.getSymbol(mnt.opposite))

    return if (mntBlock.mnt == period && dirBlock.dir == period)
      MntDirSpec.到山到向
    else if (mntBlock.dir == period && dirBlock.mnt == period)
      MntDirSpec.上山下水
    else if (mntBlock.mnt == period && mntBlock.dir == period)
      MntDirSpec.雙星到山
    else if (dirBlock.mnt == period && dirBlock.dir == period)
      MntDirSpec.雙星到向
    else
      null // 只有替星盤 才有可能為 null , 正常的 挨星下卦，一定有值
  }


  /** 城門訣 , 傳回 正城門、副城門 ， 以及各自在此運是否 enabled */
  fun getGates(): Map<Gate , Pair<Mountain , Boolean>> {

    val gateMap: Map<Gate, Mountain> = VoidFunctions.getGates(mnt)

    val 地盤 = EarthlyCompass()
    val 玄空陰陽 = MountainYinYangEmptyImpl()
    return gateMap.mapValues { (_,m) ->
      val symbol = 地盤.getSymbol(m)
      val start: Int = getChartBlockFromSymbol(symbol).period

      val mappingMountain = SymbolAcquired.getSymbol(start)?.let {
        VoidFunctions.getMappingMountain(m,it)
      }?:m

      val reversed = !玄空陰陽.getYinYang(mappingMountain)

      val steps = FlyingStar.symbolPeriods.indexOf(symbol)
      val finalValue: Int = FlyingStar.getValue(start , steps , reversed)
      m to (finalValue == period)
    }
  } // 城門訣

  /** 七星打劫 */
  fun getRobbery() : Robbery? {
    return getMntDirSpec()?.takeIf { it === MntDirSpec.雙星到向 || it === MntDirSpec.到山到向 }?.let {
      val 離乾震 = listOf(Symbol.離 , Symbol.乾, Symbol.震)
      val 離宮ints: Set<Int> = 離乾震
        .map { getChartBlockFromSymbol(it) }
        .map { it.dir }
        .toSet()

      val 坎兌巽 = listOf(Symbol.坎 , Symbol.兌 , Symbol.巽)
      val 坎宮ints = 坎兌巽
        .map { getChartBlockFromSymbol(it) }
        .map { it.dir }
        .toSet()

        return@let if (離宮ints.containsAll(listOf(1, 4, 7)) || 離宮ints.containsAll(listOf(2, 5, 8)) || 離宮ints.containsAll(listOf(3, 6, 9)) ) {
          val map = 離乾震.map { symbol -> symbol to getChartBlockFromSymbol(symbol).dir }.toMap()
          Robbery(Symbol.離 , map)
        } else if (坎宮ints.containsAll(listOf(1,4,7)) || 坎宮ints.containsAll(listOf(2,5,8)) || 坎宮ints.containsAll(listOf(3,6,9))) {
          val map = 坎兌巽.map { symbol -> symbol to getChartBlockFromSymbol(symbol).dir }.toMap()
          Robbery(Symbol.坎 , map)
        } else
          null
    }
  } // 七星打劫
}

interface IChartDegree : IChartMnt {
  // 詳細的度數 ,  可用來排兼向替卦
  val degree: Double
}

/** 具備描述九宮格的資料 , 九宮格內，每個 block 存放哪個 [ChartBlock] */
interface IChartPresenter {
  val gridMap: Map<TriGrid, Symbol?>
}

interface IChartMntPresenter : IChartMnt, IChartPresenter {
  fun getChartBlockFromGrid(grid: TriGrid): ChartBlock {
    val symbol: Symbol? = gridMap[grid]
    return getChartBlockFromSymbol(symbol)
  }
}

/** 元運 + 何山（何向）+ 是否用替 */
data class ChartMnt(override val period: Int,
                    override val mnt: Mountain,
                    override val replacement: Boolean,
                    override val blocks: List<ChartBlock>) : IChartMnt, Serializable

/** 元運 + 座山的度數 （可推導出 座山)  */
data class ChartDegree(override val period: Int,
                       override val degree: Double,
                       override val replacement: Boolean,
                       override val blocks: List<ChartBlock>) : IChartDegree, Serializable {

  override val mnt: Mountain
    get() = EarthlyCompass().getMnt(degree)
}

/**
 * 描述一個三元玄空挨星、九宮格盤
 */
data class ChartMntPresenter(override val period: Int,
                             override val mnt: Mountain,
                             val view: Symbol,
                             override val replacement: Boolean,
                             override val blocks: List<ChartBlock>,
                             override val gridMap: Map<TriGrid, Symbol?>) : Serializable, IChartMntPresenter

