/**
 * @author smallufo
 * Created on 2011/1/24 at 下午3:50:21
 */
package destiny.iching

import java.util.*

/** 歸藏卦序  */
class HexagramGuiCangComparator : AbstractHexagramSequence(), Comparator<IHexagram> {

  override val map: Map<Hexagram, Int>
    get() = mapOf(
      Hexagram.坤 to 1 ,
      Hexagram.乾 to 2 ,
      Hexagram.屯 to 3 ,
      Hexagram.蒙 to 4 ,
      Hexagram.需 to 5 ,
      Hexagram.訟 to 6 ,
      Hexagram.師 to 7 ,
      Hexagram.比 to 8 ,
      Hexagram.小畜 to 9 ,
      Hexagram.履 to 10 ,
      Hexagram.泰 to 11 ,
      Hexagram.否 to 12 ,
      Hexagram.同人 to 13 ,
      Hexagram.大有 to 14 ,
      Hexagram.艮 to 15 ,
      Hexagram.震 to 16 ,
      Hexagram.大過 to 17 ,
      Hexagram.頤 to 18 ,
      Hexagram.困 to 19 ,
      Hexagram.井 to 20 ,
      Hexagram.革 to 21 ,
      Hexagram.鼎 to 22 ,
      Hexagram.旅 to 23 ,
      Hexagram.豐 to 24 ,
      Hexagram.中孚 to 25 ,
      Hexagram.小過 to 26 ,
      Hexagram.臨 to 27 ,
      Hexagram.觀 to 28 ,
      Hexagram.萃 to 29 ,
      Hexagram.升 to 30 ,
      Hexagram.剝 to 31 ,
      Hexagram.復 to 32 ,
      Hexagram.大畜 to 33 ,
      Hexagram.無妄 to 34 ,
      Hexagram.睽 to 35 ,
      Hexagram.家人 to 36 ,
      Hexagram.節 to 37 ,
      Hexagram.渙 to 38 ,
      Hexagram.蹇 to 39 ,
      Hexagram.解 to 40 ,
      Hexagram.損 to 41 ,
      Hexagram.益 to 42 ,
      Hexagram.咸 to 43 ,
      Hexagram.恆 to 44 ,
      Hexagram.夬 to 45 ,
      Hexagram.姤 to 46 ,
      Hexagram.巽 to 47 ,
      Hexagram.兌 to 48 ,
      Hexagram.離 to 49 ,
      Hexagram.坎 to 50 ,
      Hexagram.謙 to 51 ,
      Hexagram.豫 to 52 ,
      Hexagram.歸妹 to 53 ,
      Hexagram.漸 to 54 ,
      Hexagram.晉 to 55 ,
      Hexagram.明夷 to 56 ,
      Hexagram.賁 to 57 ,
      Hexagram.噬嗑 to 58 ,
      Hexagram.既濟 to 59 ,
      Hexagram.未濟 to 60 ,
      Hexagram.遯 to 61 ,
      Hexagram.大壯 to 62 ,
      Hexagram.蠱 to 63 ,
      Hexagram.隨 to 64 )


  override fun compare(h1: IHexagram, h2: IHexagram): Int {
    return getIndex(h1) - getIndex(h2)
  }


}
