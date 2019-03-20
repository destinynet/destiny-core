/**
 * Created by smallufo on 2019-03-20.
 */
package destiny.iching.contentProviders

import destiny.iching.IHexagram
import java.util.*

/** 針對「卦」所做的註解 */
interface IHex<HexT> {

  fun getHexagram(hex: IHexagram , locale: Locale = Locale.getDefault()): HexT
}

/** 針對「爻」所做的註解 */
interface IHexLine<LineT> {

  /**
   * @param lineIndex 1 <= lineIndex <= 6
   */
  fun getLine(hex: IHexagram, lineIndex: Int , locale: Locale = Locale.getDefault()): LineT

  fun getExtraLine(hex: IHexagram , locale: Locale = Locale.getDefault()): LineT?
}


interface IHexProvider<HexT, LineT> : IHex<HexT>, IHexLine<LineT>

/** 短卦名 (中文為 一或兩字元) */
interface IHexNameShort : IHex<String>

/** 完整卦名 (中文為 三或四字元) */
interface IHexNameFull : IHex<String>

/** 卦辭、爻辭 */
interface IHexExpression : IHexProvider<String , String>

/** 卦 或 爻 的象曰  */
interface IHexImage : IHexProvider<String , String>

/** 彖曰 , 只有卦，才有彖曰 */
interface IHexJudgement : IHex<String>