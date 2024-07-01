package destiny.core

import java.util.*

interface IDailyDigest<out M : IDaily, T> : IDigest<@UnsafeVariance M, T> {

  override fun digest(model: @UnsafeVariance M, locale: Locale): T?

}
