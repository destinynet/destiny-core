/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.impls

import destiny.core.chinese.Branch
import destiny.core.chinese.Branch.*
import destiny.core.chinese.ITianyi
import destiny.core.chinese.IYinYang
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.tools.Domain
import destiny.tools.Impl
import destiny.tools.converters.Domains
import destiny.tools.converters.Domains.Divine.KEY_DIVINE_TIANYI
import destiny.tools.converters.Domains.Pithy.KEY_LIUREN_PITHY_TIANYI
import destiny.tools.converters.Domains.Ziwei.KEY_TIANYI
import java.io.Serializable

/**
 * 紫微斗數全書 對於天乙貴人的設定
 * 甲戊庚牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，六辛逢馬虎，此是貴人鄉
 *
 * 與 [TianyiLiurenPithyImpl] 《大六壬金口訣》《六壬神課金口訣》 差別只在於「壬癸」
 *
 * 截圖 http://imgur.com/1rmn11a
 */
@Impl([
        Domain(KEY_LIUREN_PITHY_TIANYI, TianyiZiweiBookImpl.VALUE),
        Domain(KEY_TIANYI, TianyiZiweiBookImpl.VALUE , default = true),
        Domain(KEY_DIVINE_TIANYI, TianyiZiweiBookImpl.VALUE)
      ])
class TianyiZiweiBookImpl : ITianyi, Serializable {

  override fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch {
    return when (stem) {
      甲, 戊, 庚 -> if (yinYang.booleanValue) 丑 else 未

      乙, 己 -> if (yinYang.booleanValue) 子 else 申

      丙, 丁 -> if (yinYang.booleanValue) 亥 else 酉

      壬, 癸 -> if (yinYang.booleanValue) 卯 else 巳

      辛 -> if (yinYang.booleanValue) 午 else 寅
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
    const val VALUE = "ziweiBook"
  }


}
