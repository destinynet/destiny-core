/**
 * Created by smallufo on 2018-04-07.
 */
package destiny.core.chinese.impls

import destiny.core.Descriptive
import destiny.core.chinese.*
import destiny.core.chinese.Branch.*
import destiny.core.chinese.Stem.*
import destiny.tools.asDescriptive
import java.io.Serializable


/**
 * 甲戊庚牛羊
 * 乙己鼠猴鄉
 * 丙丁豬雞位
 * 壬癸兔蛇藏
 * 六辛逢虎馬
 * 此是貴人方
 *
 * https://destiny.to/ubbthreads/ubbthreads.php/topics/1977088#Post1977088
 *
 * 前一字為晝貴，後一字為夜貴。
 * 前述天乙貴人歌訣係明代先賢根據「劉基」所留傳而著述
 */
class TianyiLiuBowenImpl : ITianyi,
                           Descriptive by Tianyi.LiuBowen.asDescriptive(),
                           Serializable {

  override fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch {
    return when (stem) {
      甲, 戊, 庚 -> if (yinYang.booleanValue) 丑 else 未

      乙, 己    -> if (yinYang.booleanValue) 子 else 申

      丙, 丁    -> if (yinYang.booleanValue) 亥 else 酉

      壬, 癸    -> if (yinYang.booleanValue) 卯 else 巳

      辛       -> if (yinYang.booleanValue) 寅 else 午
    }
  }
}
