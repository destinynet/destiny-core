/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import destiny.iching.Symbol.*
import destiny.iching.SymbolAcquired
import java.io.Serializable

/**
 * 三元盤的 presentation model
 * 挨星下卦
 */
class Chart(

  // 元運 , 其值只能為 1~9
  val period: Int,

  // 座山
  val mountain: Mountain,

  // 從哪個卦看去 ? 意即，九宮格的底邊，是哪個卦（後天）
  val view: Symbol) : Serializable {

  private val blocks = arrayOfNulls<ChartBlock>(10) // 0 不用


  init {

    //決定座山/向 是位於哪一卦中
    //詢問此山/向 的中心點度數為：
    val midMountain: Double = if (Math.abs(地盤.getEndDegree(mountain) - 地盤.getStartDegree(mountain)) > 180)
      0.0
    else
      (地盤.getEndDegree(mountain) + 地盤.getStartDegree(mountain)) / 2

    val midDirection = if (midMountain == 180.0)
      0.0
    else
      (地盤.getEndDegree(mountain.opposite) + 地盤.getStartDegree(mountain.opposite)) / 2

    val 飛佈山盤卦 = 後天八卦盤.getSymbol(midMountain)
    val 飛佈向盤卦 = 後天八卦盤.getSymbol(midDirection)

    //搜尋 blocks[1~9] , 分別找尋 飛佈山盤卦 以及 飛佈向盤卦 , 取得其 period 值
    val mntStart: Int = (1..9).first { getBlockSymbol(it) === 飛佈山盤卦 }.let { getBlockPeriod(it) }
    val dirStart: Int = (1..9).first { getBlockSymbol(it) === 飛佈向盤卦 }.let { getBlockPeriod(it) }

    // 原始 == 中宮
    val 原始山盤卦: Symbol? = SymbolAcquired.getSymbolNullable(mntStart)
    val 原始向盤卦: Symbol? = SymbolAcquired.getSymbolNullable(dirStart)

//    println("${飛佈山盤卦}山 ($midMountain) ${飛佈向盤卦}向")
//    println("\t$飛佈山盤卦 = $mntStart , from 後天八卦 $原始山盤卦")
//    println("\t$飛佈向盤卦 = $dirStart , from 後天八卦 $原始向盤卦")


    val mntConverse = isConverse(原始山盤卦, 飛佈山盤卦, mountain)
    val dirConverse = isConverse(原始向盤卦, 飛佈向盤卦, mountain.opposite)

    for (i in 1..9) {
      val period = normalize(period + i - 1)
      val symbol = getBlockSymbol(i)

      val mnt = getNumber(mntStart, mntConverse, i)
//      println("[$i] , mntStart = $mntStart , mntConverse = $mntConverse")
      val dir = getNumber(dirStart, dirConverse, i)

      blocks[i] = ChartBlock(symbol, mnt, dir, period)
    }
  } //init


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
      9 -> 巽
      8 -> 震
      4 -> 艮

      5 -> 離
      6 -> 坎

      7 -> 坤
      3 -> 兌
      2 -> 乾
      else -> null
    }
  }

  private fun getBlockPeriod(blockIndex: Int): Int {
    val 元運 = period
    return normalize(元運 + blockIndex - 1)
  }

  /**
   * 查詢某卦方位裡面的資料結構 (ChartBlock)
   */
  fun getChartBlock(s: Symbol): ChartBlock {
    return blocks.first { cb: ChartBlock? -> cb?.symbol === s }!!
  }

  /**
   * 則傳回中宮
   */
  fun getCenterBlock() : ChartBlock {
    //blocks.forEach { println(it) }
    return blocks.filter { it != null }.first { cb -> cb?.symbol === null }!!
  }

  private fun isConverse(原始卦: Symbol?, 飛佈卦: Symbol, m: Mountain): Boolean {
    val isConverse: Boolean
    isConverse = if (原始卦 == null) {
      /**
       * 當五黃如中時，因為五黃本身沒有山向，五黃是中宮戊己土，那麼，
       * 五入中的順飛還是逆飛則是由五所在的宮位的山向的陰陽，性質決定。
       * 如一運子山午向，運星五到離，五入中為向星後，按五所在的「午」的性質決定，故逆飛。
       */
      !地盤.getYinYang(m)
    } else {
      val index = ((地盤.getStartDegree(m) - 後天八卦盤.getStartDegree(飛佈卦)) / 15).toInt()
      /**
       * index = 0 => 地元
       * index = 1 => 天元
       * index = 2 => 人元
       */
      val degree = 後天八卦盤.getStartDegree(原始卦) + (index * 15).toDouble() + 1.0 //最後的 +1 是確保結果能坐落於該山中
      !地盤.getYinYang(地盤.getMnt(degree))
    }
    return isConverse
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


  companion object {
    private val 地盤 = EarthlyCompass()
    private val 後天八卦盤 = AcquiredSymbolCompass()
  }

}
