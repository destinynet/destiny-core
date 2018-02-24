/**
 * @author smallufo
 * @date 2002/8/19 , @date 2009/6/23 改寫
 */
package destiny.iching.divine

import destiny.iching.Hexagram
import destiny.iching.IHexagram
import destiny.iching.IHexagramSequence

/**
 * 京房卦序：乾為天,天風姤,天山遯,天地否...
 */
class HexagramDivinationComparator : Comparator<IHexagram>, IHexagramSequence {

  /**
   * 實作 HexagramSequenceIF
   * @return 傳回六爻卦序, 乾=1 , 姤=2 , 遯=3 , 否=4 ...
   */
  override fun getIndex(hexagram: IHexagram): Int {
    val h = Hexagram.getHexagram(hexagram.upperSymbol, hexagram.lowerSymbol)
    return hexagramIndexMap[h]!!
  }

  override fun getHexagram(index: Int): Hexagram {
    val i = if (index > 64) index % 64 else if (index <= 0) 64 - (0-index) %64 else index

    return indexHexagramMap[i]!!
  }

  override fun compare(h1: IHexagram, h2: IHexagram): Int {
    return getIndex(h1) - getIndex(h2)
  }

  companion object {
    private val hexagramIndexMap = mapOf(
      Hexagram.乾 to 1,
      Hexagram.姤 to 2,
      Hexagram.遯 to 3,
      Hexagram.否 to 4,
      Hexagram.觀 to 5,
      Hexagram.剝 to 6,
      Hexagram.晉 to 7,
      Hexagram.大有 to 8,
      Hexagram.震 to 9,
      Hexagram.豫 to 10,
      Hexagram.解 to 11,
      Hexagram.恆 to 12,
      Hexagram.升 to 13,
      Hexagram.井 to 14,
      Hexagram.大過 to 15,
      Hexagram.隨 to 16,
      Hexagram.坎 to 17,
      Hexagram.節 to 18,
      Hexagram.屯 to 19,
      Hexagram.既濟 to 20,
      Hexagram.革 to 21,
      Hexagram.豐 to 22,
      Hexagram.明夷 to 23,
      Hexagram.師 to 24,
      Hexagram.艮 to 25,
      Hexagram.賁 to 26,
      Hexagram.大畜 to 27,
      Hexagram.損 to 28,
      Hexagram.睽 to 29,
      Hexagram.履 to 30,
      Hexagram.中孚 to 31,
      Hexagram.漸 to 32,
      Hexagram.坤 to 33,
      Hexagram.復 to 34,
      Hexagram.臨 to 35,
      Hexagram.泰 to 36,
      Hexagram.大壯 to 37,
      Hexagram.夬 to 38,
      Hexagram.需 to 39,
      Hexagram.比 to 40,
      Hexagram.巽 to 41,
      Hexagram.小畜 to 42,
      Hexagram.家人 to 43,
      Hexagram.益 to 44,
      Hexagram.無妄 to 45,
      Hexagram.噬嗑 to 46,
      Hexagram.頤 to 47,
      Hexagram.蠱 to 48,
      Hexagram.離 to 49,
      Hexagram.旅 to 50,
      Hexagram.鼎 to 51,
      Hexagram.未濟 to 52,
      Hexagram.蒙 to 53,
      Hexagram.渙 to 54,
      Hexagram.訟 to 55,
      Hexagram.同人 to 56,
      Hexagram.兌 to 57,
      Hexagram.困 to 58,
      Hexagram.萃 to 59,
      Hexagram.咸 to 60,
      Hexagram.蹇 to 61,
      Hexagram.謙 to 62,
      Hexagram.小過 to 63,
      Hexagram.歸妹 to 64)

    private val indexHexagramMap = hexagramIndexMap.map { (k,v) -> v to k }.toMap()
  }
}
