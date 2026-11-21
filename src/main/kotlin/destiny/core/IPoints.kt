/**
 * Created by smallufo on 2021-10-18.
 */
package destiny.core

import destiny.tools.Lang
import destiny.tools.toLang
import java.util.Locale
import kotlin.reflect.KClass


interface IPoints<T: Point> {

  val type : KClass<out Point>

  val values: Array<T>

  /** 依名稱找點；[lang] 決定拿哪一種語言的名稱來比對 */
  fun fromString(value: String, lang: Lang = Lang.EN): T?

  /** `Locale` 橋接（給 JVM 端既有的呼叫端），勿覆寫。轉 KMP 時移到 jvmMain */
  fun fromString(value: String, locale: Locale): T? = fromString(value, locale.toLang())

  fun valueOf(nameKey: String): T? {
    return values.firstOrNull {
      it.nameKey == nameKey
    }
  }
}
