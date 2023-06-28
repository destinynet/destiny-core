/**
 * Created by smallufo on 2018-02-21.
 */
package destiny.core.iching.divine

import destiny.core.iching.canvas.DstCanvas
import destiny.core.iching.canvas.HexagramPlateCanvas
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 繪製 [ICombinedWithMetaName]
 * 合併卦象，只有卦名，沒有其他卦辭、爻辭等文字，也沒有日期時間等資料 (for 經文易排盤後對照)
 * 高度 9 x 寬度 (32 + 2 + 8 + 24 = 66 )
 *
<pre>

　　　　　乾金一世卦：天風姤  　　　　　　　坎水三世卦：水火既濟　
　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
【伏　　神】　　【　本　　卦　】　　　　　　　　【　變　　卦　】　
　　　　　　　　父母▇▇▇壬戌土　○──→戊子水　子孫▇　▇應兄弟
　　　　　　　　兄弟▇▇▇壬申金　　　　　戊戌土　父母▇▇▇　官鬼
　　　　　　　應官鬼▇▇▇壬午火　○──→戊申金　兄弟▇　▇　父母
　　　　　　　　兄弟▇▇▇辛酉金　　　　　己亥水　子孫▇▇▇世兄弟
妻財　甲寅木　　子孫▇▇▇辛亥水　○──→己丑土　父母▇　▇　官鬼
　　　　　　　世父母▇　▇辛丑土　╳──→己卯木　妻財▇▇▇　子孫


　　　　　離火三世卦：火水未濟　　　　　　　坎水三世卦：水火既濟　
　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
【伏　　神】　　【　本　　卦　】　　　　　　　　【　變　　卦　】　
兄弟　乙巳火　應兄弟▇▇▇辛巳火　○──→丙寅木　父母▇　▇應子孫
子孫　癸未土　　父母▇　▇癸卯木　╳──→戊辰土　子孫▇▇▇　官鬼
妻財　辛酉金　　子孫▇▇▇乙丑土　○──→甲午火　兄弟▇　▇　妻財
官鬼　丁亥水　世妻財▇　▇壬申金　╳──→丁亥水　官鬼▇▇▇世兄弟
妻財　己酉金　　子孫▇▇▇庚戌土　○──→己酉金　妻財▇　▇　父母
子孫　辛未土　　官鬼▇　▇丙子水　╳──→辛未土　子孫▇▇▇　官鬼
</pre>
 * */
class CombinedWithMetaNameCanvas(plate: ICombinedWithMetaName) : ColorCanvas(9, 66, ChineseStringTools.NULL_CHAR) {
  init {
    // 本卦 : 高度 9 x 寬度 32
    val srcCanvas = HexagramPlateCanvas(plate.srcModel)

    // 中間空一 column (寬度2)

    // 箭頭 : 高度 6 x 寬度 8
    val arrow = ColorCanvas(6, 8, ChineseStringTools.NULL_CHAR).apply {
      for (i in 6 downTo 1) {
        if (plate.src.getBoolean(i) != plate.dst.getBoolean(i)) {
          if (plate.src.getBoolean(i))
            setText("○──→", 7 - i, 1)
          else
            setText("╳──→", 7 - i, 1)
        } else
          setText("　　　　", 7 - i, 1)
      }
    }

    // 變卦 : 高度 9 x 寬度 24
    val dstCanvas = DstCanvas(plate)

    add(srcCanvas, 1, 1)
    add(arrow, 4, 35)
    add(dstCanvas, 1, 43)
  }
}
