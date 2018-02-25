/**
 * Created by smallufo on 2018-02-25.
 */
package destiny.FengShui.SanYuan

import destiny.iching.Symbol
import destiny.iching.SymbolAcquired

import java.io.Serializable

/**
 * 三元盤的 presentation model
 */
class Chart(
  // 元運 , 其值只能為 1~9
  val period: Int,
  // 座山
  val mountain: Mountain,
  // 從哪個卦看去 ? 意即，九宮格的底邊，是哪個卦（後天）
  val view: Symbol) : Serializable {

  private val blocks = arrayOfNulls<ChartBlock>(10) // 0 不用

  private val 地盤 = EarthlyCompass()

  private val 後天八卦盤 = AcquiredSymbolCompass()

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
    var mountainStart = 0
    var directionStart = 0


    for (i in 1..9) {
      val blockSymbol = getBlockSymbol(i)
      if (blockSymbol === 飛佈山盤卦)
        mountainStart = getBlockPeriod(i)
      if (blockSymbol === 飛佈向盤卦)
        directionStart = getBlockPeriod(i)
    }

    val 原始山盤卦 = SymbolAcquired.getSymbolNullable(mountainStart)
    val 原始向盤卦 = SymbolAcquired.getSymbolNullable(directionStart)


    for (i in 1..9) {
      val period = normalize(period+i-1)
      val symbol = getBlockSymbol(i)

      val mnt = getMountain(mountainStart, isConverse(原始山盤卦, 飛佈山盤卦, mountain), i)
      val dst = getDirection(directionStart, isConverse(原始向盤卦, 飛佈向盤卦, mountain.opposite), i)

      blocks[i] = ChartBlock(symbol, mnt, dst, period)
    }
  } //init



  /**
   * 方向（以卦來表示），尚未實作轉盤
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

  private fun getBlockPeriod(blockIndex: Int): Int {
    val 元運 = period
    return normalize(元運 + blockIndex - 1)
  }

  /**
   * 查詢某卦方位裡面的資料結構 (ChartBlock) , 如果查的是 null , 則傳回中宮
   */
  fun getChartBlock(s: Symbol): ChartBlock {
    return blocks.first { b -> b?.symbol === s }!!
  }

  private fun isConverse(原始卦: Symbol?, 飛佈卦: Symbol, m: Mountain): Boolean {
    val isConverse: Boolean
    isConverse = if (原始卦 == null) {
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
   * 傳入座山 , 以及是否逆飛
   *
   * @param blockIndex 1 to 9
   */
  private fun getMountain(start: Int, converse: Boolean, blockIndex: Int): Int {
    return if (!converse) {
      // 順飛
      normalize(start + blockIndex + 8)
    } else {
      // 逆飛
      normalize(start - 1 + 10)
    }
  }


  /**
   * 傳入向山 , 以及是否逆飛
   *
   * @param blockIndex 1 to 9
   */
  private fun getDirection(start: Int, converse: Boolean, blockIndex: Int): Int {
    return if (!converse) {
      // 順飛
      normalize(start + blockIndex + 8)
    } else {
      // 逆飛
      normalize(start - blockIndex + 10)
    }
  }

}
