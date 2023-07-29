/**
 * Created by smallufo on 2018-02-20.
 */
package destiny.core.iching.divine

import destiny.core.iching.Hexagram
import destiny.core.iching.IHexagram
import destiny.core.iching.IHexagramText
import destiny.core.iching.ILineNameDecoratorService
import destiny.tools.canvas.ColorCanvas
import destiny.tools.getTitle
import java.util.*

/** 將 [ICombinedFull] 包裝成 HTML */
object CombinedFullColorCanvasWrapper {

  context(ILineNameDecoratorService)
  fun render(plate: ICombinedFull): String {

    // 13 x 74 , 只剩下起卦方式還沒填
    val c = CombinedWithMetaNameDayMonthCanvas(plate)

    val 起卦方式: String? = plate.approach?.let {
      when (it) {
        DivineApproach.RANDOM -> "電腦起卦"
        DivineApproach.MANUAL -> "手動起卦"
      }
    }

    起卦方式?.also {
      c.setText(it, 2, 27)
    }

    val metaCanvas: ColorCanvas = ColorCanvas(0, 75, " ").apply {

      plate.run {
        gender?.also { appendLine("性別：$it", null, null, " ") }
        question?.also { appendLine("問題：$it", null, null, " ") }
        decoratedDateTime?.also { appendLine(it, null, null, " ") }
        place?.also { appendLine("地點：$it", null, null, " ") }
        val 納甲伏神 = "納甲系統：${settings.getTitle(Locale.TAIWAN)}，伏神系統：${hiddenEnergy.getTitle(Locale.TAIWAN)}"

        納甲伏神.also { appendLine(it, null, null, " ", null, null) }
      }
    }

    val mainCanvas = ColorCanvas(13 + metaCanvas.height, 75, " ").apply {
      add(metaCanvas, 1, 1)
      add(c, metaCanvas.height + 1, 1)
    }

    plate.pairTexts.also {
      val srcText: IHexagramText = it.first
      val dstText: IHexagramText = it.second

      mainCanvas.run {
        srcText.run {
          appendEmptyLine()
          appendLine("本卦：$fullName", "000", null, " ")
          appendHexagramText(mainCanvas, plate.src, this)
        } // 本卦

        dstText.run {
          appendEmptyLine()
          appendLine("變卦：$fullName", "000", null, " ")
          appendHexagramText(mainCanvas, plate.dst, this)
        } // 變卦
      }
    }

    return mainCanvas.htmlOutput
  } // render
}

context(ILineNameDecoratorService)
private fun appendHexagramText(colorCanvas: ColorCanvas, hex: IHexagram, text: IHexagramText) {
  colorCanvas.run {
    text.run {
      appendLine("卦辭：$expression", "F00", null, " ", null, null)
      appendLine("象曰：$image", "00F", null, " ", null, null)
      appendLine("彖曰：$judgement", "green", null, " ", null, null)
      extraLine?.run {
        val use96 = with(StringBuffer()) {
          append("用")
          append(if (hex === Hexagram.乾) "九" else "六")
          append("：")
          append(expression)
        }.toString()
        appendLine(use96, "00F", null, " ", null, null)
        appendLine("象曰：$image", "green", null, " ", null, null)
      } // 用九、用六


      (6 downTo 1).forEach { lineIndex ->
        val lineName = getName(hex, lineIndex, Locale.TAIWAN)
        appendLine(lineName + "：" + getLineFromOne(lineIndex).expression, "00F", null, " ", null, null)
        appendLine("象曰：" + getLineFromOne(lineIndex).image, "green", null, " ", null, null)
      }
    }
  }
}
