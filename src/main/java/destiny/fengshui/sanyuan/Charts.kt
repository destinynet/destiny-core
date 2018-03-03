/**
 * Created by smallufo on 2018-02-28.
 */
package destiny.fengshui.sanyuan

import destiny.iching.Symbol
import java.io.Serializable


interface IPeriod {
  // 元運 , 其值只能為 1~9
  val period: Int
}

interface IChartMnt : IPeriod {
  // 座山
  val mnt: Mountain
  // 是否用替
  val replacement: Boolean
}

interface IChartDegree : IChartMnt {
  // 詳細的度數 ,  可用來排兼向替卦
  val degree: Double
}

enum class Position {
  B,   // 底
  LB,  // 左下
  L, // 左
  LU,  // 左上
  U, // 上
  RU, // 右上
  R, // 右
  RB, // 右下
  C; // 中間

  /** 順時針 */
  fun clockWise(): Position? {
    return when (this) {
      C -> null
      B -> LB
      LB -> L
      L -> LU
      LU -> U
      U -> RU
      RU -> R
      R -> RB
      RB -> B
    }
  }
}

/** 具備描述九宮格的資料 , 九宮格內，每個 block 存放哪個 [ChartBlock] */
interface IChartPresenter {
  val posMap: Map<Position, ChartBlock>

  //  fun getChartBlock(symbol: Symbol) : ChartBlock {
  //    return posMap.values.first { cb -> cb.symbol === symbol }
  //  }
}

interface IChartMntPresenter : IChartMnt, IChartPresenter

/** 元運 + 何山（何向）+ 是否用替 */
data class ChartMnt(override val period: Int,
                    override val mnt: Mountain,
                    override val replacement: Boolean) : IChartMnt, Serializable

/** 元運 + 座山的度數 （可推導出 座山)  */
data class ChartDegree(override val period: Int,
                       override val degree: Double,
                       override val replacement: Boolean) : IChartDegree,
  IChartMnt by degToMnt(period, degree, replacement), Serializable

fun degToMnt(period: Int, degree: Double, replacement: Boolean): IChartMnt {
  val mnt = EarthlyCompass().getMnt(degree)
  return ChartMnt(period, mnt, replacement)
}

data class ChartMntPresenter(override val period: Int,
                             override val mnt: Mountain,
                             val view: Symbol,
                             override val replacement: Boolean) : Serializable  ,
  IChartMntPresenter,
  IChartPresenter by ChartPresenter(period, mnt, view , replacement) {

  fun getChartBlock(symbol: Symbol): ChartBlock {
    return posMap.values.first { cb -> cb.symbol === symbol }
  }

}



