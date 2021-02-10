/**
 * Created by smallufo on 2017-04-10.
 */
package destiny.core.chinese.ziwei

import destiny.core.chinese.ziwei.StarMain.*
import destiny.core.iching.Symbol
import destiny.core.iching.Symbol.*
import java.io.Serializable

/**
 * 紫微斗數配卦
 *
 * 參考此圖 http://imgur.com/a/hD1tx
 *
 * 其餘系統參考這裡  http://skylight-hk.net/forum/forum.php?mod=viewthread&tid=607
 */
class StarSymbolDefaultImpl : IStarSymbol, Serializable {

  override fun getSymbolAcquired(star: StarMain): Symbol {
    return when(star) {
      紫微 , 天府 -> 艮
      天機 , 巨門 -> 震
      貪狼 -> 巽
      太陽 , 天相 -> 離
      武曲 , 破軍 -> 坤
      天同 , 天梁 -> 兌
      七殺 -> 乾
      廉貞 , 太陰 -> 坎
    }
  }
}
