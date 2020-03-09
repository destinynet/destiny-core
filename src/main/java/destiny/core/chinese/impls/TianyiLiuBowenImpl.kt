/**
 * Created by smallufo on 2018-04-07.
 */
package destiny.core.chinese.impls

import destiny.core.chinese.Branch
import destiny.core.chinese.ITianyi
import destiny.core.chinese.IYinYang
import destiny.core.chinese.Stem
import destiny.core.chinese.Stem.*
import destiny.tools.Impl
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
@Impl(value = TianyiLiuBowenImpl.VALUE)
class TianyiLiuBowenImpl : ITianyi, Serializable {
  override fun getFirstTianyi(stem: Stem, yinYang: IYinYang): Branch {
    return when (stem) {
      甲, 戊, 庚 -> if (yinYang.booleanValue) Branch.丑 else Branch.未

      乙, 己 -> if (yinYang.booleanValue) Branch.子 else Branch.申

      丙, 丁 -> if (yinYang.booleanValue) Branch.亥 else Branch.酉

      壬, 癸 -> if (yinYang.booleanValue) Branch.卯 else Branch.巳

      辛 -> if (yinYang.booleanValue) Branch.寅 else Branch.午
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
    const val VALUE = "liuBowen"
  }


}
