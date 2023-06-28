/**
 * @author smallufo
 * Created on 2005/4/6 at 下午 04:07:42
 */
package destiny.core.calendar.eightwords


import java.util.*

/**
 * 十神 , 天干之間互相的互動關係 : 比間，劫財，正財...
 */
enum class Reaction  {
  比肩,
  劫財,
  正財,
  偏財,
  正印,
  偏印,
  食神,
  傷官,
  正官,
  七殺;

  /** 比肩.getPairReaction() == 劫財  */
  val pairReaction: Reaction
    get() {
      return when (this) {
        比肩 -> 劫財
        劫財 -> 比肩
        正印 -> 偏印
        偏印 -> 正印
        正官 -> 七殺
        七殺 -> 正官
        食神 -> 傷官
        傷官 -> 食神
        正財 -> 偏財
        偏財 -> 正財
      }
    }


  /** 縮寫  */
  fun getAbbreviation(locale: Locale): String {
    return ResourceBundle.getBundle(Reaction::class.java.name, locale).getString(name + "_ABBR")
  }

}
