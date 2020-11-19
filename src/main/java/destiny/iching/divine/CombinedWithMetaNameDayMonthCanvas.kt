/**
 * Created by smallufo on 2018-02-21.
 */
package destiny.iching.divine

import destiny.core.calendar.eightwords.Direction
import destiny.core.calendar.eightwords.EightWordsNullableCanvas
import destiny.core.chinese.SixAnimal
import destiny.tools.ChineseStringTools
import destiny.tools.canvas.ColorCanvas

/**
 * 具備「日干支」「月令」 , 可以排出六獸 [SixAnimal] 以及神煞 , 但不具備完整時間，也沒有起卦方法 ( [DivineApproach] )
 * 高度 13 x 寬度 74
<pre>
┌─────────────────┐時日月年　　空　驛　桃　貴　羊　　　
│　ＤＥＳＴＩＮＹ命理網　　　　　　│　　　　　　亡　馬　花　人　刃　　　
│　　　ｄｅｓｔｉｎｙ．ｔｏ　　　　│　丙　　　　戌　申　卯　酉　午　　　
└─────────────────┘　寅丑　　　亥　　　　　亥　　　　　
　　　　　　　　　乾金一世卦：天風姤  　　　　　　　坎水三世卦：水火既濟　
　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　　
【六獸】【伏　　神】　　【　本　　卦　】　　　　　　　　【　變　　卦　】　
　青龍　　　　　　　　　父母▇▇▇壬戌土　○──→戊子水　子孫▇　▇應兄弟
　玄武　　　　　　　　　兄弟▇▇▇壬申金　　　　　戊戌土　父母▇▇▇　官鬼
　白虎　　　　　　　　應官鬼▇▇▇壬午火　○──→戊申金　兄弟▇　▇　父母
　螣蛇　　　　　　　　　兄弟▇▇▇辛酉金　　　　　己亥水　子孫▇▇▇世兄弟
　勾陳　妻財　甲寅木　　子孫▇▇▇辛亥水　○──→己丑土　父母▇　▇　官鬼
　朱雀　　　　　　　　世父母▇　▇辛丑土　╳──→己卯木　妻財▇▇▇　子孫
</pre>
 * */
class CombinedWithMetaNameDayMonthCanvas(plate: ICombinedWithMetaNameDayMonth)
  : ColorCanvas(13, 74, ChineseStringTools.NULL_CHAR) {

  init {

    val url = "https://destiny.to"

    val banner = ColorCanvas(4, 38, " ").apply {
      setText("┌─────────────────┐", 1, 1, txtUrl = url, wrap = false)
      setText("│　ＤＥＳＴＩＮＹ命理網　　　　　　│", 2, 1, txtUrl = url, wrap = false)
      setText("│　　　ｄｅｓｔｉｎｙ．ｔｏ　　　　│", 3, 1, txtUrl = url, wrap = false)
      setText("└─────────────────┘", 4, 1, txtUrl = url, wrap = false)
    }

    // 八字 : 高度 4 x 寬度 8
    val ewCanvas = EightWordsNullableCanvas(plate.eightWordsNullable, Direction.R2L)

    val 神煞 = ColorCanvas(4, 18).apply {
      setText("空　驛　桃　貴　羊", 1, 1)
      setText("亡　馬　花　人　刃", 2, 1)
      setText("　　　　　　　　　", 3, 1)
      setText("　　　　　　　　　", 4, 1)

      plate.voids.takeIf { it.size == 2 }?.toList()?.also {
        setText(it[0].toString(), 3, 1, "brown", wrap = false)
        setText(it[1].toString(), 4, 1, "brown", wrap = false)
      }

      plate.horse?.also {
        setText(it.toString(), 3, 5, "brown", wrap = false)
      }

      plate.flower?.also {
        setText(it.toString(), 3, 9, "brown", wrap = false)
      }

      plate.tianyis.takeIf { it.size == 2 }?.toList()?.also {
        setText(it[0].toString(), 3, 13, "brown", wrap = false)
        setText(it[1].toString(), 4, 13, "brown", wrap = false)
      }

      plate.yangBlade?.also {
        setText(it.toString(), 3, 17, "brown", wrap = false)
      }
    }

    // 7 x 8
    val 六獸canvas = ColorCanvas(7, 8).apply {
      plate.sixAnimals.takeIf { it.isNotEmpty() }?.also {
        setText("【六獸】", 1, 1)
        for (i in 6 downTo 1) {
          setText("　" + it[i - 1] + "　", 8 - i, 1)
        }
      }
    }

    // 高度 9 x 寬度 66
    val embedded = CombinedWithMetaNameCanvas(plate)

    add(banner, 1, 1)
    add(ewCanvas, 1, 39)
    add(神煞, 1, 51)
    add(六獸canvas, 7, 1)
    add(embedded, 5, 9)
  }
}
