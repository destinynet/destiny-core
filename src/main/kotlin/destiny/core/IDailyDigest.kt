package destiny.core

import java.time.LocalDate
import java.util.*

interface IDailyDigest<M, T> {

  fun digest(model: M, date: LocalDate, locale: Locale = Locale.getDefault()): T?
}
