
/**
 * Created by smallufo on 2018-02-20.
 */
package destiny.core.iching.canvas

import destiny.core.iching.IHexagram
import destiny.tools.canvas.ColorCanvas

/** 將 [IHexagram] 繪製成六條槓 , 高x寬 = 6x6 */
class HexagramCanvas(hex : IHexagram) : ColorCanvas( 6 , 6 ) {
  init {
    for (i in 6 downTo 1) {
      if (hex.getBoolean(i)) {
        setText("▇▇▇", 7 - i, 1)
      } else {
        setText("▇　▇", 7 - i, 1)
      }
    }
  }
}
