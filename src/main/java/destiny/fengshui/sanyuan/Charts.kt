/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.fengshui.sanyuan

import destiny.core.TriGrid
import destiny.fengshui.MountainYinYangEmptyImpl
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import java.io.Serializable


interface IPeriod {
  // 元運 , 其值只能為 1~9
  val period: Int
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
  fun getRobbery() : ChartPattern.七星打劫? {
    return ChartMntRules.robbery(this)
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

