/**
 * Created by smallufo on 2017-04-15.
 */
package destiny.core.chinese.impls

import destiny.core.Descriptive
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.tools.asDescriptive
import java.io.Serializable

/**
 * 紫微斗數全書 對於天乙貴人的設定
 * 甲戊庚牛羊，乙己鼠猴鄉，丙丁豬雞位，壬癸兔蛇藏，六辛逢馬虎，此是貴人鄉
 *
 * 與 [TianyiLiurenPithyImpl] 《大六壬金口訣》《六壬神課金口訣》 差別只在於「壬癸」
 *
 * 截圖 http://imgur.com/1rmn11a
 */
class TianyiZiweiBookImpl : ITianyi,
                            Descriptive by Tianyi.ZiweiBook.asDescriptive(),
                            Serializable {

  override fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch {
    return when (stem) {
      甲, 戊, 庚 -> if (yinYang.booleanValue) 丑 else 未

      乙, 己    -> if (yinYang.booleanValue) 子 else 申

      丙, 丁    -> if (yinYang.booleanValue) 亥 else 酉

      壬, 癸    -> if (yinYang.booleanValue) 卯 else 巳

      辛       -> if (yinYang.booleanValue) 午 else 寅
    }
  }
}
