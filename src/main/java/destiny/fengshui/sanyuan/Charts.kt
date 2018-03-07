/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.fengshui.sanyuan

import destiny.core.TriGrid
import destiny.iching.Symbol
import destiny.iching.SymbolAcquired
import java.io.Serializable
import kotlin.math.abs


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
    return blocks.first { it.symbol == null }
  }

  /** 取得 四種 山向格局 (or null) */
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
    // 先取出「向」兩旁的卦
    val 地盤 = EarthlyCompass()

    val dirSymbol: Symbol = 地盤.getSymbol(mnt.opposite)
    /*
    取出 「向」卦，順逆兩卦，相對應（天元、人元、地元龍）的兩個山
    例如：子山午向，「向」為離卦。 離卦 兩旁分別是 巽、坤兩卦
    而巽、坤兩卦內，的天元龍就是「巽」「坤」兩山
     */
    val mountains: Set<Mountain> = setOf(
      VoidFunctions.getMappingMountain(mnt , SymbolAcquired.getClockwiseSymbol(dirSymbol)) ,
      VoidFunctions.getMappingMountain(mnt , SymbolAcquired.getCounterClockwiseSymbol(dirSymbol)))

    // 目前有兩個山，其中一個為「正城門」、另一個為「副城門」.
    // 正者：其卦 與「向」卦 的落書數，相差5 , 另一個為負
    // 例如：子山午向，「向卦」為離，洛書數為9
    // 旁邊兩山，「巽=4」「坤=2」 , 「巽」的4 與 「離」的9 相差5 , 故，「巽」為正城門、「坤」為副城門

    // 正城門
    val mntPrimary = mountains.first {
      val mntSymbol: Symbol = 地盤.getSymbol(it)
      abs(SymbolAcquired.getIndex(mntSymbol) - SymbolAcquired.getIndex(dirSymbol)) == 5
    }
    // 副城門
    val mntSecondary = mountains.minus(mntPrimary).first()

    //println("正 = $mntPrimary , 副 = $mntSecondary")

    return mapOf(
      Gate.正城門 to Pair(mntPrimary , true),
      Gate.副城門 to Pair(mntSecondary , true))

  }
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

