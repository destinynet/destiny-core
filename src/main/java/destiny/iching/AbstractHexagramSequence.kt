/**
 * Created by smallufo on 2017-05-10.
 */
package destiny.iching

import java.io.Serializable

/** 以 Map 實作 64卦的卦序  */
abstract class AbstractHexagramSequence : IHexagramSequence, Serializable {

  protected abstract val map: Map<Hexagram, Int>

  override fun getIndex(hexagram: IHexagram): Int {
    val h = Hexagram.getHexagram(hexagram.upperSymbol, hexagram.lowerSymbol)
    return map[h]!!
  }


  override fun getHexagram(index: Int): Hexagram {
    val i = if (index > 64) index % 64 else if (index <= 0) 64 - (0 - index) % 64 else index

    return map.filter { (_,v) -> v == i }.keys.first()
    //return map.map { (k,v) -> v to k }.toMap()[i]!!
  }

}
