package destiny.iching.contentProviders

import destiny.iching.IHexData
import destiny.iching.IHexagram
import java.util.*

/** 針對「卦」所做的註解 */
interface IHex<HexT> {

  fun getHexagram(hex: IHexagram, locale: Locale = Locale.getDefault()): HexT
}

/** 針對「爻」所做的註解 */
interface IHexLine<LineT> {

  /**
   * @param lineIndex 1 <= lineIndex <= 6
   */
  fun getLine(hex: IHexagram, lineIndex: Int, locale: Locale = Locale.getDefault()): LineT

  fun getExtraLine(hex: IHexagram, locale: Locale = Locale.getDefault()): LineT?
}


interface IHexagramProvider<T : IHexagram> {

  fun getHexagram(hex: IHexagram, locale: Locale = Locale.getDefault()): T
}

/** 卦 [IHex] + 爻 [IHexLine] , 此 interface 不實作 [IHexagram] 有其考量，避免 subclass 出現 diamond problem */
interface IHexProvider<HexT, LineT> : IHex<HexT>, IHexLine<LineT> {

  fun getHexagramData(hex: IHexagram, locale: Locale = Locale.getDefault()): IHexData<HexT, LineT>
}

/** 短卦名 (中文為 一或兩字元) */
interface IHexNameShort : IHex<String> {
  /** 從卦的「短卦名」，反查回 Hexagram  */
  fun reverse(name: String, locale: Locale): IHexagram
}

/** 完整卦名 (中文為 三或四字元) */
interface IHexNameFull : IHex<String> {
  /** 從卦的「長卦名」，反查回 Hexagram  */
  fun reverse(name: String, locale: Locale): IHexagram
}

/** 彖曰 , 只有卦，才有彖曰 */
interface IHexJudgement : IHex<String>