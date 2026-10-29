/**
 * Created by smallufo on 2026-10-29.
 */
package destiny.core.chinese.ziwei

import destiny.core.calendar.TimeTools
import destiny.core.calendar.chinese.ChineseDateConfig
import destiny.core.calendar.chinese.IChineseDateFeature
import destiny.core.calendar.chinese.LunisolarPreset
import destiny.core.calendar.chinese.display
import destiny.core.calendar.chinese.toChineseMonthString
import destiny.core.calendar.toLocalDateTime
import destiny.tools.Lang
import destiny.tools.getTitle

/**
 * 紫微盤面農曆後的標注：換月規則不是萬年曆時，寫出規則；日期與萬年曆不同時，再寫萬年曆的日期與「下個月自何時起」（出生地鐘面時）。
 */
object ZiweiLunisolarNote {

  /** 萬年曆設定（或盤沒有時間地點）回 null */
  fun of(plate: IPlate, config: IZiweiConfig, chineseDateFeature: IChineseDateFeature, lang: Lang): String? {
    val preset = LunisolarPreset.of(config.lunisolar)
    if (preset == LunisolarPreset.CIVIL) return null
    val lmt = plate.localDateTime ?: return null
    val loc = plate.location ?: return null
    val title = preset?.getTitle(lang) ?: config.lunisolar.toString()

    val gmt = TimeTools.getGmtJulDay(lmt, loc)
    // 萬年曆：預設的陰陽曆規則，日界同八字日柱
    val civil = chineseDateFeature.getModel(gmt, loc, ChineseDateConfig(dayHourConfig = config.ewConfig.dayHourConfig))
    if (civil == plate.chineseDate) return "（依「$title」，與萬年曆相同）"

    val dateConfig = ChineseDateConfig(config.lunisolar, config.ewConfig.dayHourConfig)
    val next = chineseDateFeature.nextMonthOf(chineseDateFeature.monthOf(gmt, loc, dateConfig), loc, dateConfig)
    val start = chineseDateFeature.monthStart(next, loc, dateConfig).toLocalDateTime(loc.zoneId)
    val name = (if (next.leap) "閏" else "") + next.number.toChineseMonthString() + "初一"
    val nextStart = "${name}自 ${start.monthValue}月${start.dayOfMonth}日 ${"%02d:%02d".format(start.hour, start.minute)} 起"
    return "（依「$title」；萬年曆為${civil.display(appendDay = true)}；$nextStart）"
  }
}
