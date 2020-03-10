/**
 * Created by smallufo on 2015-05-27.
 */
package destiny.core.chinese.impls

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.core.chinese.ITianyi
import destiny.core.chinese.IYinYang
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import destiny.tools.converters.Domains.Divine.KEY_DIVINE_TIANYI
import destiny.tools.converters.Domains.Pithy.KEY_LIUREN_PITHY_TIANYI
import destiny.tools.converters.Domains.Ziwei.KEY_TIANYI
import java.io.Serializable

/**
 * 《淵海子平》《六壬視斯》《六壬大占》《大六壬金口訣》(部份版本用此法)
 *
 *
 * 《六壬視斯》說歌曰：甲戊兼牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，庚辛逢虎馬，永定貴人方。
 * 並釋說：“日用上一字，夜用下一字。
 * 如甲戊日日占應用牛字，便從天盤丑上起貴人，是為陽貴。甲戊日夜占，應用羊字，便從天盤未上起貴人，是為陰貴……”。
 */
@Impl([
      Domain(KEY_LIUREN_PITHY_TIANYI , TianyiOceanImpl.VALUE),
      Domain(KEY_TIANYI , TianyiOceanImpl.VALUE),
      Domain(KEY_DIVINE_TIANYI, TianyiOceanImpl.VALUE)
      ])
class TianyiOceanImpl : ITianyi, Serializable {


  /**
   * 甲戊兼牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，
   * 庚辛逢馬虎，此是貴人方，命中如遇此，定作紫薇郎。
   */
  override fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch {
    return when (stem) {
      甲, 戊 -> if (yinYang.booleanValue) 丑 else 未

      乙, 己 -> if (yinYang.booleanValue) 子 else 申

      丙, 丁 -> if (yinYang.booleanValue) 亥 else 酉

      壬, 癸 -> if (yinYang.booleanValue) 卯 else 巳

      庚, 辛 -> if (yinYang.booleanValue) 午 else 寅

    }
  }

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false
    return true
  }

  override fun hashCode(): Int {
    return javaClass.hashCode()
  }

  companion object {
    const val VALUE = "ocean"
  }

}
