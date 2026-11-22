/**
 * Created by smallufo on 2026-11-22.
 */
package destiny.core.calendar

import destiny.tools.toLang
import java.time.ZoneId
import java.util.*

/**
 * [Location] 的 JVM 橋接（KMP quarantine 上刻意保留；轉 KMP 時移到 jvmMain）。
 * 與原本的成員同名、同 package，呼叫端 `import destiny.core.calendar.locationOf` 這類既有 import 照樣解析。
 */

/** 實際採用的時區，見 [ILocation.effectiveTzid] */
val ILocation.zoneId: ZoneId
  get() = ZoneId.of(effectiveTzid)

/** 依語系挑預設地點，見 `locationOf(Lang)` */
fun locationOf(locale: Locale): Location = locationOf(locale.toLang())
