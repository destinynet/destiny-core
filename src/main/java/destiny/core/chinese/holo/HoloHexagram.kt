/**
 * Created by smallufo on 2019-03-04.
 */
package destiny.core.chinese.holo

import destiny.core.chinese.IStemBranch
import destiny.core.chinese.IYinYang
import destiny.core.chinese.StemBranch
import destiny.iching.Hexagram
import destiny.iching.IHexagram
import destiny.iching.Symbol
import java.io.Serializable


interface IHoloHexagram : IHexagram, TimeRange<Double> {

  enum class Scale {
    LIFE,    // 半輩子，意味： 先天卦 or 後天卦
    MAJOR,   // 大運 : 陽爻9年 , 陰爻6年
    YEAR,    // 流年
    MONTH,   // 流月
  }

  val scale: Scale

  /** 卦象 */
  val hexagram: Hexagram

  /** 元堂 在第幾爻 (1~6) */
  val yuanTang: Int

  /** 六爻納甲 */
  val stemBranches : List<StemBranch>

  /** start of GMT JulianDay */
  override val start: Double

  /** end of GMT JulianDay */
  override val endExclusive: Double

  override val lowerSymbol: Symbol
    get() = hexagram.lowerSymbol

  override val upperSymbol: Symbol
    get() = hexagram.upperSymbol
}

data class HoloHexagram(
  override val scale: IHoloHexagram.Scale,
  override val hexagram: Hexagram,
  override val yuanTang: Int,
  override val stemBranches: List<StemBranch>,
  override val start: Double,
  override val endExclusive: Double) : IHoloHexagram, Serializable {

  override fun toString(): String {
    return "$hexagram 之 $yuanTang"
  }
}
/** 除了 卦象、元堂 之外，另外包含干支資訊 (並非元堂爻的納甲) */
interface IHoloHexagramWithStemBranch : IHoloHexagram {
  /**
   * 此干支 可能表示 大運、流年、流月、流日 or 流時 的干支
   * 大運 : 代表本命先後天卦中，走到哪一爻，該爻的納甲
   * 流年 : 流年干支
   * 流月 : 流月干支
   * ... 其餘類推
   * */
  val stemBranch: IStemBranch
}

/**
 * 流年、流月、流日 or 流時 卦象 ,
 * @param stemBranch 流年、流月、流日 or 流時 的干支  (並非元堂爻的納甲)
 **/
data class HoloHexagramWithStemBranch(
  val holoHexagram: IHoloHexagram,
  override val stemBranch: IStemBranch) : IHoloHexagramWithStemBranch, IHoloHexagram by holoHexagram

/** 先天卦 or 後天卦 的單一爻 , 內含 6年 or 9年 的流年資料結構 */
data class HoloLine(val yinYang: IYinYang,
                    val yuanTang: Boolean,
                    /**
                     * 每個爻 可以變化出多個卦 ,
                     * 大運的爻，可以變化出 6 or 9 個流年卦象
                     * 流年的爻，可以變化出 12 個流月卦象
                     * 流月的爻，可以變化出 30 個流日卦象
                     * 流日的爻，可以變化出 12 個流時卦象
                     * */
                    val hexagrams: List<IHoloHexagramWithStemBranch>) : IYinYang by yinYang , TimeRange<Double> {


  override val start: Double
    get() = hexagrams.minBy { it.start }!!.start

  override val endExclusive: Double
    get() = hexagrams.maxBy { it.endExclusive }!!.endExclusive
}




/** 純粹用於 先天卦 or 後天卦 , 包含六爻中，每爻的流年資訊 */
interface ILifeHoloHexagram : IHoloHexagram {
  val lines : List<HoloLine>
}

/** 先天卦 or 後天卦 */
data class LifeHoloHexagram(override val lines: List<HoloLine>,
                            override val stemBranches: List<StemBranch>) : ILifeHoloHexagram {

  init {
    require(lines.count { it.yuanTang } == 1) {
      "傳入的六爻，只能有一個 元堂，不能多也不能少。"
    }
  }

  override val scale: IHoloHexagram.Scale
    get() = IHoloHexagram.Scale.LIFE

  override val hexagram: Hexagram
    get() = Hexagram.ofYinYangs(lines)

  override val yuanTang: Int
    get() = lines.indexOfFirst { it.yuanTang }+1

  override val start: Double
    get() = lines.minBy { it.start }!!.start

  override val endExclusive: Double
    get() = lines.maxBy { it.endExclusive }!!.endExclusive


}
